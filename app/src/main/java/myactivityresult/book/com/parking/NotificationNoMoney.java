package myactivityresult.book.com.parking;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/* 현재 보유중인 가상 머니가 요금 보다 적을 때 나오는 알람을 눌렀을 때 나오는 액티비티 */
public class NotificationNoMoney extends AppCompatActivity {
    final static int GET = 1000;
    final static int POST = 1001;
    private ServiceConnection mConnection;
    MoneyAlarmService mService;
    int money_gap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_no_money);

        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d("test","NotiNoMoney 에서 onServiceConnected 실행");
                MoneyAlarmService.LocalBinder binder = (MoneyAlarmService.LocalBinder) service;
                mService = binder.getService();
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }
        };
        Intent ServiceIntent = new Intent(this, MoneyAlarmService.class);
        bindService(ServiceIntent, mConnection, BIND_AUTO_CREATE);

        Intent intent = getIntent();
        int NotificationID = intent.getIntExtra("NotificationID", 0);
        NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(NotificationID);  // 알림을 눌러서 확인했으므로 알림을 제거

        int virtual_money = intent.getIntExtra("VirtualMoney",0);
        int fare = intent.getIntExtra("Fare",0);
        TextView current_cash = (TextView)findViewById(R.id.current_cash);
        current_cash.setText("현재 충전된 금액 : " + String.valueOf(virtual_money));

        TextView Txt_gap = (TextView)findViewById(R.id.gap);
        money_gap = fare - virtual_money;
        Txt_gap.setText(String.valueOf(money_gap) + "만큼 요금이 부족합니다");
    }

    public void Charge(View v){
        EditText charge_money = (EditText)findViewById(R.id.charge_money);
        SharedPreferences pref = getSharedPreferences("save01", Context.MODE_PRIVATE);
        String CarNumber = pref.getString("CarNumber","");
        String url="http://13.124.74.249:3000/cars/";

        JSONObject jsonObject = new JSONObject(); // 충전할 금액을 json 형태로 서버에게 보냄
        try {
            jsonObject.put("amount", Integer.parseInt(charge_money.getText().toString()));
        }catch (JSONException e){
            e.printStackTrace();
        }
        // Log.d("test","주소 : " + url + CarNumber + "/charge_money");
        HttpURLConnector conn = new HttpURLConnector(url + CarNumber + "/charge_money", POST, jsonObject);
        conn.start();
        try{
            conn.join();
        } catch(InterruptedException e){};

        if(money_gap <= Integer.parseInt(charge_money.getText().toString())) {
            if(pref.getBoolean("ServiceOnOff", false)) { // 설정화면 체크 상태 확인
                mService.setIsRun(true); // 서비스 재시작
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "추가로 충전해주세요", Toast.LENGTH_LONG).show();
        }

        Toast.makeText(getApplicationContext(), charge_money.getText().toString()
                + "원이 충전되었습니다", Toast.LENGTH_LONG).show();
    }

    public void End(View v){
        finish();
    }
}
