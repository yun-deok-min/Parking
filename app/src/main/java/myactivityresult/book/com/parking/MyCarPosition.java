package myactivityresult.book.com.parking;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MyCarPosition extends AppCompatActivity {
    private final String url = ""; // DB 서버 url
    private String result; // DB 데이터
    EditText EdtCarNumber;
    TextView CarPosition;
    WebView ViewMyCar;
    HttpURLConnector conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_car_position);

        Intent intent = getIntent();
        conn = (HttpURLConnector)intent.getSerializableExtra("conn");

        EdtCarNumber = (EditText)findViewById(R.id.EdtCarNumber);
        SharedPreferences pref = getSharedPreferences("save01", Activity.MODE_PRIVATE);
        EdtCarNumber.setText(pref.getString("CarNumber","")); // 차량 번호가 등록되어 있으면 자동으로 표시
    }

    public void SearchCar(View v){
        EdtCarNumber = (EditText)findViewById(R.id.EdtCarNumber);
        CarPosition = (TextView)findViewById(R.id.CarPosition);
        ViewMyCar = (WebView)findViewById(R.id.ViewMyCar);

        ViewMyCar.setWebViewClient(new WebViewClient());

        JSONParser jsonParser = new JSONParser(result);
        //jsonParser.parser();


        String CarPositionString = "Error";
        /* 차량 번호 입력 받아서 해당 차량의 위치 정보 표시 */
        boolean find = false;
        String search = EdtCarNumber.getText().toString();
        /*for(int i = 0; i < str_arr.size(); i++){
            if(search.equals(str_arr.get(i))){
                CarPositionString = String.valueOf(int_arr.get(i));
                CarPosition.setText("위치 : " + CarPositionString);
                find = true;
                break;
            }
        }*/
        if(!find){
            CarPosition.setText("해당 차량은 없습니다.");
        }

        int floor = Integer.parseInt( (CarPositionString.substring(0,1)) );
        // 받은 데이터 중에서 층 정보만 분리
        switch (floor){
            case 1 :
                ViewMyCar.loadUrl("");
                break;
            case 2 :
                ViewMyCar.loadUrl("");
                break;
            default :
                Toast.makeText(getApplicationContext(), "통신 에러", Toast.LENGTH_LONG).show();
        }
    }

    public void BackToStartMenu(View v){ finish(); }
}
