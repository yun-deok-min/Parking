package myactivityresult.book.com.parking;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;

/* 입차 시간과 출차 시간을 통해서 요금을 산출하는 액티비티
*  디비와 현재 시간으로 구할 수 있고 유저가 임의의 시간을 입력해서 확인할 수 도 있음
* */
public class CalculateFare extends AppCompatActivity {
    EditText EdtCarNumber, EdtStartSec, EdtStartMinute, EdtEndSec, EdtEndMinute;
    TextView Fare;
    String StartSec, StartMinute, EndSec, EndMinute;
    int BetweenSec, BetweenMinute;
    private String result; // 서버에게 받은 json 데이터 전체
    HttpURLConnector conn;
    JSONParser jsonParser;
    final static int NotFound = 1002;
    final static int BaseMoney = 1000;
    int start_at;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
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
        EdtEndSec = (EditText)findViewById(R.id.EdtEndSec);
        EdtEndMinute = (EditText)findViewById(R.id.EdtEndMinute);
        Calendar now = Calendar.getInstance();
        int currentMin = now.get(Calendar.MINUTE);
        int currentSec = now.get(Calendar.SECOND);
        EdtEndMinute.setText(String.valueOf(currentMin));
        EdtEndSec.setText(String.valueOf(currentSec));
    }

    /* 요금 계산 하는 메소드 */
    public void Calculate(View v){
        EdtStartSec = (EditText)findViewById(R.id.EdtStartSec);
        EdtStartMinute = (EditText)findViewById(R.id.EdtStartMinute);
        EdtEndSec = (EditText)findViewById(R.id.EdtEndSec);
        EdtEndMinute = (EditText)findViewById(R.id.EdtEndMinute);
        Fare = (TextView) findViewById(R.id.Fare);

        StartSec = EdtStartSec.getText().toString();
        StartMinute = EdtStartMinute.getText().toString();
        EndSec = EdtEndSec.getText().toString();
        EndMinute = EdtEndMinute.getText().toString();

        // 입출차 시간 사이의 간격을 계산
        BetweenSec = Integer.parseInt(EndSec) - Integer.parseInt(StartSec);
        BetweenMinute = Integer.parseInt(EndMinute) - Integer.parseInt(StartMinute);

        int fare;
        if((BetweenMinute < 1) && (BetweenSec < 10)){
            fare = BaseMoney;  // 기본요금
            Fare.setText("요금 : " + fare);
        }
        else{
            fare = BaseMoney + (int)(BetweenSec / 10) * 1000 + BetweenMinute * 6000 ;
            Fare.setText("요금 : " + fare);
        }
    }

    /* 수동으로 차량 번호를 입력해서 입차 시간 검색 */
    public void SearchTime(View v){
        EdtCarNumber = (EditText)findViewById(R.id.EdtCarNumber);
        String CarNumber = EdtCarNumber.getText().toString();
        ShowStartTime(CarNumber);
    }

    /* 서버에게 차번호를 보내서 디비에서 해당 차의 입차시간을 받아옴 */
    public void ShowStartTime(String CarNumber){
        EdtStartSec = (EditText)findViewById(R.id.EdtStartSec);
        EdtStartMinute = (EditText)findViewById(R.id.EdtStartMinute);

        String url="http://13.124.74.249:3000/cars/";
        conn = new HttpURLConnector(url + CarNumber);
        conn.start();
        try{
            conn.join();
        } catch(InterruptedException e){};
        result = conn.getResult();
        // Log.d("test", "서버에게 받은 결과 : " + result);

        jsonParser = new JSONParser(result);
        jsonParser.parser(1);

        start_at = jsonParser.getStarted_at();

        if(start_at != NotFound){
            getTime(start_at);
        }
        else{
            Toast.makeText(getApplicationContext(),
                    "해당 차량은 없습니다.", Toast.LENGTH_LONG).show();
        }
    }

    // 서버에게 받은 입차 시간을 앱에서 사용할 형식으로 변환해서 화면에 보여줌
    public void getTime(int millisec){
        String pattern = "mm:ss";
        long millisec_1 = (long)millisec * 1000L;
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        String date = format.format(millisec_1);
        StringTokenizer tokenizer = new StringTokenizer(date, ":");
        EdtStartMinute.setText(tokenizer.nextToken());
        EdtStartSec.setText(tokenizer.nextToken());
    }

    public void BackToStartMenu(View v){
        finish();
    }
}
