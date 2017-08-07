package myactivityresult.book.com.parking;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.reflect.Field;

public class Config extends AppCompatActivity {
    EditText EdtCarNumber;
    SharedPreferences pref;
    String carNumber;
    DatePicker datePicker;
    Button select_month;
    CheckBox ServiceOnOff;
    boolean service_on_off;
    private ServiceConnection mConnection;
    MoneyAlarmService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        pref = getSharedPreferences("save01", Context.MODE_PRIVATE);
        carNumber = pref.getString("CarNumber","");
        if(!carNumber.equals("")) {
           EdtCarNumber = (EditText) findViewById(R.id.EdtCarNumber);
           EdtCarNumber.setText(carNumber);
        }

        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d("test","Config에서 onServiceConnected 실행");
                MoneyAlarmService.LocalBinder binder = (MoneyAlarmService.LocalBinder) service;
                mService = binder.getService();
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }
        };
        Intent intent = new Intent(Config.this, MoneyAlarmService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);

        ServiceOnOff = (CheckBox)findViewById(R.id.ServiceOnOff);
        service_on_off = pref.getBoolean("ServiceOnOff", true);
        ServiceOnOff.setChecked(service_on_off);

        ServiceOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( ((CheckBox)v).isChecked()){  // 서비스 시작
                    mService.setIsRun(true);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("ServiceOnOff", true);
                    editor.commit();
                }
                else{   // 서비스 중지
                    mService.setIsRun(false);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("ServiceOnOff", false);
                    editor.commit();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        unbindService(mConnection);
        super.onStop();
    }

    public void EnrollCar(View v){
        EdtCarNumber = (EditText)findViewById(R.id.EdtCarNumber);
        pref = getSharedPreferences("save01", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();
        editor.putString("CarNumber", EdtCarNumber.getText().toString() );
        editor.commit();
        Toast.makeText(getApplicationContext(),
                "차량 번호가 등록되었습니다.", Toast.LENGTH_LONG).show();
    }

    public void ShowTimeLog(View v){
        carNumber = pref.getString("CarNumber","");
        if(carNumber.equals("")) {
            Toast.makeText(getApplicationContext(), "등록된 차량이 없습니다",
                    Toast.LENGTH_LONG).show();
        }
        else {
            Intent intent = new Intent(getApplicationContext(), TimeLogActivity.class);
            startActivity(intent);
        }
    }

    public void ShowCalendar(View v){
        datePicker = (DatePicker)findViewById(R.id.datePicker);
        datePicker.setVisibility(View.VISIBLE);

        try{
            Field[] f = datePicker.getClass().getDeclaredFields();
            for(int i =0; i<f.length; i++){
                Log.d("test", f[i].getName());
            }
            for (Field dateField : f) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if ("mDaySpinner".equals(dateField.getName())) {
                        dateField.setAccessible(true);
                        Object dayPicker = new Object();
                        dayPicker = dateField.get(datePicker);
                        ((View) dayPicker).setVisibility(View.GONE);
                    }
                }
                else{
                    if ("mDayPicker".equals(dateField.getName())) {
                        dateField.setAccessible(true);
                        Object dayPicker = new Object();
                        dayPicker = dateField.get(datePicker);
                        ((View) dayPicker).setVisibility(View.GONE);
                    }
                }
            }
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        select_month = (Button)findViewById(R.id.select_month);
        select_month.setVisibility(View.VISIBLE);
    }

    public void SumDailyMoney(View v){
        int year = datePicker.getYear();
        int month = datePicker.getMonth() + 1;
        //Toast.makeText(getApplicationContext(), year + "년 " + month +"월", Toast.LENGTH_LONG).show();

        int monthly_fare = 0;
        int daily_fare;
        Cursor cursor;
        carNumber = pref.getString("CarNumber","");

        SQLiteHelper sqh = new SQLiteHelper(Config.this);
        cursor = sqh.getMonthlyFare(carNumber, year, month);

        while(cursor.moveToNext()){
            daily_fare = cursor.getInt(cursor.getColumnIndex(MoneyLogTable.Fare));
            monthly_fare = monthly_fare + daily_fare;
        }
        Toast.makeText(getApplicationContext(), "월 합계 : " + monthly_fare, Toast.LENGTH_LONG).show();
    }

    public void setServiceOnOff(boolean sw){  // true 면 서비스 시작
        ServiceOnOff = (CheckBox)findViewById(R.id.ServiceOnOff);
        ServiceOnOff.setChecked(sw);
    }

    public void BackToStartMenu(View v){
        finish();
    }
}
