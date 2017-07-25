package myactivityresult.book.com.parking;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class CalculateFare extends AppCompatActivity {
    EditText EdtCarNumber, EdtStartHour, EdtStartMinute, EdtEndHour, EdtEndMinute;
    TextView Fare;
    String StartHour, StartMinute, EndHour, EndMinute;
    int BetweenHour, BetweenMinute;
    private String result; // DB 데이터
    HttpURLConnector conn;
    JSONParser jsonParser;
    final static int NotFound = 0;
    int start_at = NotFound ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_fare);

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

    public void SearchTime(View v){  // 수동으로 차량 번호를 입력해서 입차 시간 검색
        EdtCarNumber = (EditText)findViewById(R.id.EdtCarNumber);
        String CarNumber = EdtCarNumber.getText().toString();
        ShowStartTime(CarNumber);
    }

    public void ShowStartTime(String CarNumber){
        // 서버에 차번호를 보내서 디비에서 해당 차의 입차시간을 확인해서 서버가 앱에게 입차시간을 전송해서 보여줌
        EdtStartHour = (EditText)findViewById(R.id.EdtStartHour);
        EdtStartMinute = (EditText)findViewById(R.id.EdtStartMinute);

        String url="https://";
        conn = new HttpURLConnector(url + CarNumber);
        conn.start();
        LoadingDialog dialog = new LoadingDialog(CalculateFare.this);
        dialog.execute();
        try{
            conn.join();
        } catch(InterruptedException e){};
        dialog.setIsEnd();
        result = conn.getResult();

        jsonParser = new JSONParser(result);
        jsonParser.parser(1);

        start_at = jsonParser.getStarted_at(); // substring 으로 시간 단위 분할

        boolean find = false;
        if(start_at != NotFound){
            find = true;
            EdtStartHour.setText("");
            EdtStartMinute.setText("");
        }

        if(!find){
            Toast.makeText(getApplicationContext(),
                    "해당 차량은 없습니다.", Toast.LENGTH_LONG).show();
        }
    }

    public void BackToStartMenu(View v){
        finish();
    }
}
