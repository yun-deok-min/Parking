package myactivityresult.book.com.parking;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class NotificationNoMoney extends AppCompatActivity {
    Config mConfig;
    final static int GET = 1000;
    final static int POST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_no_money);

        Intent intent = getIntent();
        mConfig = (Config) intent.getSerializableExtra("Config");
        mConfig.setServiceOnOff(false); // 서비스 중지

        int NotificationID = intent.getIntExtra("NotificationID", 0);
        NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(NotificationID);  // 알림 닫기

        int virtual_money = intent.getIntExtra("VirtualMoney",0);
        TextView current_cash = (TextView)findViewById(R.id.current_cash);
        current_cash.setText("현재 충전된 금액 : " + String.valueOf(virtual_money));
    }
    
    public void Charge(View v){
        EditText charge_money = (EditText)findViewById(R.id.charge_money);
        int money = Integer.parseInt(charge_money.getText().toString() );
        SharedPreferences pref = getSharedPreferences("save01", Context.MODE_PRIVATE);
        String CarNumber = pref.getString("CarNumber","");
        String url="http://13.124.74.249:3000/charge/";  // API 요청
        HttpURLConnector conn = new HttpURLConnector(url + CarNumber, POST);
        conn.start();
        try{
            conn.join();
        } catch(InterruptedException e){};
    }

    @Override
    protected void onDestroy() {
        mConfig.setServiceOnOff(true); // 서비스 재시작
        super.onDestroy();
    }
}
