package myactivityresult.book.com.parking;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class CalculateFare extends AppCompatActivity {
    EditText EdtCarNumber, EdtStartHour, EdtStartMinute, EdtEndHour, EdtEndMinute;
    TextView Fare;
    String StartHour, StartMinute, EndHour, EndMinute;
    int BetweenHour, BetweenMinute;
    private final String url = "http://14.44.125.19/db.php"; // DB 서버 url
    private String result; // DB 데이터
    private ArrayList<String> str_arr;  // 차량 번호
    private ArrayList<String> StartTime_arr;  // 입차시간
    HttpURLConnector conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_fare);

        Intent intent = getIntent();
        conn = (HttpURLConnector)intent.getSerializableExtra("conn");

        /* 차량 번호가 등록되어 있으면 자동으로 표시하고 입차 시간도 보여줌 */
        SharedPreferences pref = getSharedPreferences("save01", Activity.MODE_PRIVATE);
        String CarNumber = pref.getString("CarNumber",""); // 등록된 차량이 없으면 null 입력
        EdtCarNumber = (EditText)findViewById(R.id.EdtCarNumber);
        EdtCarNumber.setText(CarNumber);
        if(!CarNumber.equals("")){
            ShowStartTime(CarNumber);
        }

        /* 현재 시간을 출차 시간으로 표시 */
        EdtEndHour = (EditText)findViewById(R.id.EdtEndHour);
        EdtEndMinute = (EditText)findViewById(R.id.EdtEndMinute);
        Calendar now = Calendar.getInstance();
        int currentHour = now.get(Calendar.HOUR_OF_DAY);
        int currentMin = now.get(Calendar.MINUTE);
        EdtEndHour.setText(String.valueOf(currentHour));
        EdtEndMinute.setText(String.valueOf(currentMin));
    }

    public void Calculate(View v){
        EdtStartHour = (EditText)findViewById(R.id.EdtStartHour);
        EdtStartMinute = (EditText)findViewById(R.id.EdtStartMinute);
        EdtEndHour = (EditText)findViewById(R.id.EdtEndHour);
        EdtEndMinute = (EditText)findViewById(R.id.EdtEndMinute);
        Fare = (TextView) findViewById(R.id.Fare);

        StartHour = EdtStartHour.getText().toString();
        StartMinute = EdtStartMinute.getText().toString();
        EndHour = EdtEndHour.getText().toString();
        EndMinute = EdtEndMinute.getText().toString();

        BetweenHour = Integer.parseInt(EndHour) - Integer.parseInt(StartHour);
        BetweenMinute = Integer.parseInt(EndMinute) - Integer.parseInt(StartMinute);

        if(BetweenMinute <= 30){
            int fare = 10000 * BetweenHour + 5000;  // 5000, 15000
            Fare.setText("요금 : " + fare);
        }
        else{
            int fare = 10000 * (BetweenHour + 1) ;  // 10000, 20000
            Fare.setText("요금 : " + fare);
        }
    }

    public void SearchTime(View v){
        EdtCarNumber = (EditText)findViewById(R.id.EdtCarNumber);
        String CarNumber = EdtCarNumber.getText().toString();
        ShowStartTime(CarNumber);
    }

    public void ShowStartTime(String CarNumber){
        // 서버에 차번호를 보내서 디비에서 해당 차의 입차시간을 확인해서 서버가 앱에게 입차시간을 전송해서 보여줌
        EdtStartHour = (EditText)findViewById(R.id.EdtStartHour);
        EdtStartMinute = (EditText)findViewById(R.id.EdtStartMinute);

        /*
        JSONParser jsonParser = new JSONParser(result);
        jsonParser.parser();

        str_arr = jsonParser.getStr_arr();
        StartTime_arr = jsonParser.getStartTime_arr();

        boolean find = false;
        for(int i = 0; i < str_arr.size(); i++){
            if(CarNumber.equals(str_arr.get(i))){
                StartHour = (StartTime_arr.get(i)).substring(0,1);   // 시간만 분리
                StartMinute = (StartTime_arr.get(i)).substring(3,4); // 2는 콜론 표시
                EdtStartHour.setText(StartHour);
                EdtStartMinute.setText(StartMinute);
                find = true;
                break;
            }
        }
        if(!find){
            Toast.makeText(getApplicationContext(),
                    "해당 차량은 없습니다.", Toast.LENGTH_LONG).show();
        }
        */
    }

    public void BackToStartMenu(View v){
        finish();
    }
}
