package myactivityresult.book.com.parking;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NotificationNoMoney extends AppCompatActivity {
    final static int GET = 1000;
    final static int POST = 1001;
    private ServiceConnection mConnection;
    MoneyAlarmService mService;

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
        nm.cancel(NotificationID);  // 알림 닫기

        int virtual_money = intent.getIntExtra("VirtualMoney",0);
        TextView current_cash = (TextView)findViewById(R.id.current_cash);
        current_cash.setText("현재 충전된 금액 : " + String.valueOf(virtual_money));
    }

    public void Charge(View v){
        EditText charge_money = (EditText)findViewById(R.id.charge_money);
        /*SharedPreferences pref = getSharedPreferences("save01", Context.MODE_PRIVATE);
        String CarNumber = pref.getString("CarNumber","");
        String url="http://13.124.74.249:3000/cars/";  // API 요청
        String message_json = "\"car\":{\"numbering\":\""+ CarNumber + "\","
                + "\"money\":" + charge_money.getText().toString() + "}";
        HttpURLConnector conn = new HttpURLConnector(url + CarNumber + "/charge_money", POST, message_json);
        conn.start();
        try{
            conn.join();
        } catch(InterruptedException e){};*/

        Toast.makeText(getApplicationContext(), charge_money.getText().toString()
                + "원이 충전되었습니다", Toast.LENGTH_LONG).show();
    }

    public void End(View v){
        finish();
    }

    @Override
    protected void onDestroy() {
        mService.setIsRun(true); // 서비스 재시작
        super.onDestroy();
    }
}
