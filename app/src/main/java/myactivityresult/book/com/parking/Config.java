package myactivityresult.book.com.parking;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

/* 다양한 설정과 부가적인 기능을 처리하는 액티비티 */
public class Config extends AppCompatActivity {
    EditText EdtCarNumber;
    SharedPreferences pref;
    String carNumber;
    DatePicker datePicker; // 날짜를 선택할 수 있는 UI
    Button select_month;
    CheckBox ServiceOnOff;  // 보유 머니와 요금을 비교하는 서비스를 켜고 끄는 것을 설정
    boolean service_on_off;
    private ServiceConnection mConnection;  // 서비스를 bind 하기 위해서
    MoneyAlarmService mService; // 보유 머니와 요금을 비교하는 서비스 객체
    final static int GET = 1000;
    final static int POST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        /* 차량 번호가 등록되어 있다면 표시 */
        pref = getSharedPreferences("save01", Context.MODE_PRIVATE);
        carNumber = pref.getString("CarNumber","");
        if(!carNumber.equals("")) {
           EdtCarNumber = (EditText) findViewById(R.id.EdtCarNumber);
           EdtCarNumber.setText(carNumber);
        }

        /* 체크 박스에 접근한 적이 없으면 체크한 상태로 설정 */
        ServiceOnOff = (CheckBox)findViewById(R.id.ServiceOnOff);
        service_on_off = pref.getBoolean("ServiceOnOff", true);
        ServiceOnOff.setChecked(service_on_off);

        /* 서비스 bind 설정 */
        mConnection = new ServiceConnection() {
            @Override // bindService 메소드 실행 시 호출되는 콜백 메소드
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d("test","Config 에서 onServiceConnected 실행");
                MoneyAlarmService.LocalBinder binder = (MoneyAlarmService.LocalBinder) service;
                mService = binder.getService();
            }
            @Override // 비정상적으로 종료되었 을 때 호출되는 콜백 메소드
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }
        };

        /* 서비스 연결 */
        Intent intent = new Intent(Config.this, MoneyAlarmService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);

        /* 체크박스 클릭 시 처리 메소드 */
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
        if(service_on_off) {
            unbindService(mConnection);
        }
        super.onStop();
    }

    /* 차량 번호 등록 버튼 누르면 SharedPreferences 로 저장 */
    public void EnrollCar(View v){
        EdtCarNumber = (EditText)findViewById(R.id.EdtCarNumber);
        String car_number = EdtCarNumber.getText().toString();

        pref = getSharedPreferences("save01", Context.MODE_PRIVATE);

        String latest_car_number = pref.getString("CarNumber", "");
        if(car_number.equals(latest_car_number)){
            Toast.makeText(getApplicationContext(),
                    "현재 등록되어 있는 차량입니다.", Toast.LENGTH_LONG).show();
        }
        else {
            SQLiteHelper sqh = new SQLiteHelper(this);
        /* 과거에 등록했던 적이 있는지 확인 */
            boolean find = false;
            Cursor cursor = sqh.getAllDatabase(CarTable.TABLE_NAME);
            while (cursor.moveToNext()) {
                String old_car_number = cursor.getString(cursor.getColumnIndex(CarTable.CarNumber));
                if (car_number.equals(old_car_number)) {
                    find = true;
                }
            }
        /* 예전에 등록된 적 없는 차량일 경우에만 DB에 추가, SharedPreferences 는 무조건 변경 */
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("CarNumber", car_number);
            editor.commit();
            if (!find) {
                sqh.addCar(car_number, "in", 0);  // 주차장에 처음 방문해서 주차한 상태에서 차량 등록

                Toast.makeText(getApplicationContext(),
                        "차량 번호가 등록되었습니다.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "등록된 차량이 변경되었습니다.", Toast.LENGTH_LONG).show();
            }
        }
    }

    /* 현재 등록된 차량의 입출차 로그를 보여줌 */
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

    /* 어느 달의 요금 합계를 보여줄지 정하기 위해서 달력 스피너(datePicker)를 보여줌 */
    public void ShowCalendar(View v){
        datePicker = (DatePicker)findViewById(R.id.datePicker);
        datePicker.setVisibility(View.VISIBLE);

        /* '일' 스피너를 지우고 '년', '월'만 선택할 수 있게 변경 */
        try{
            Field[] f = datePicker.getClass().getDeclaredFields();
            for (Field dateField : f) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    int daySpinnerID = Resources.getSystem().getIdentifier("day","id","android");
                    if(daySpinnerID != 0){
                        View daySpinner = datePicker.findViewById(daySpinnerID);
                        if(daySpinner != null)
                            daySpinner.setVisibility(View.GONE);
                    }
                }
                else{
                    if ("mDaySpinner".equals(dateField.getName())) {
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

    /* 달력 스피너(datePicker)에서 선택한 년, 월에 해당하는 날짜들의 주차 요금 합계를 보여줌 */
    public void SumDailyMoney(View v){
        int year = datePicker.getYear();
        int month = datePicker.getMonth() + 1;

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

    public void ChargeMoney(View v){
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View textEntryView = layoutInflater.inflate(R.layout.cash_input_dialog,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("가상머니 충전");
        builder.setMessage("금액을 입력해주세요");
        builder.setView(textEntryView);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("충전", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                String CarNumber = pref.getString("CarNumber","");
                String url="http://13.124.74.249:3000/cars/";

                EditText testText = (EditText) textEntryView.findViewById(R.id.cashInputText);

                JSONObject jsonObject = new JSONObject(); // 충전할 금액을 json 형태로 서버에게 보냄
                try {
                    jsonObject.put("amount", Integer.parseInt(testText.getText().toString()));
                }catch (JSONException e){
                    e.printStackTrace();
                }
                // Log.d("test","주소 : " + url + CarNumber + "/charge_money");
                HttpURLConnector conn = new HttpURLConnector(url + CarNumber + "/charge_money", POST, jsonObject);
                conn.start();
                try{
                    conn.join();
                } catch(InterruptedException e){};

                Toast.makeText(getApplicationContext(), testText.getText().toString()
                        + "원이 충전되었습니다", Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){ }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void BackToStartMenu(View v){
        finish();
    }
}
