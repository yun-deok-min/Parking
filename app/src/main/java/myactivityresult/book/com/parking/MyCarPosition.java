package myactivityresult.book.com.parking;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/* 차량 번호로 주차장의 어느 위치에 있는지 보여주는 액티비티 */
public class MyCarPosition extends AppCompatActivity {
    private String result; // DB 데이터
    EditText EdtCarNumber;
    TextView CarPosition;
    WebView ViewMyCar;
    HttpURLConnector conn;
    JSONParser jsonParser;
    private String CarNumber;
    final static int NotFound = 1002;
    private String zone_name;
    private int zone_index;
    private int floor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_car_position);

        EdtCarNumber = (EditText)findViewById(R.id.EdtCarNumber);
        SharedPreferences pref = getSharedPreferences("save01", Activity.MODE_PRIVATE);
        CarNumber = pref.getString("CarNumber","");
        if(!CarNumber.equals("")){ // 차량 번호가 등록되어 있으면 자동으로 표시
            EdtCarNumber.setText(pref.getString("CarNumber",""));
            SearchCar(CarNumber);
        }
    }

    public void SearchCar(View v){ // 차량 위치 검색 버튼을 눌렀을 때
        EdtCarNumber = (EditText)findViewById(R.id.EdtCarNumber);
        CarNumber = EdtCarNumber.getText().toString();
        SearchCar(CarNumber);
    }

    public void SearchCar(String CarNumber){
        CarPosition = (TextView)findViewById(R.id.CarPosition);
        ViewMyCar = (WebView)findViewById(R.id.ViewMyCar);
        ViewMyCar.setWebViewClient(new WebViewClient());

        String url = "http://13.124.74.249:3000/cars/";
        conn = new HttpURLConnector(url +  CarNumber);
        conn.start();
        try{
            conn.join();
        } catch(InterruptedException e){};
        result = conn.getResult();
        // Log.d("test", "결과(result) : " + result);

        jsonParser = new JSONParser(result);
        jsonParser.parser(1);
        zone_index = jsonParser.getZone_index();
        zone_name = jsonParser.getZone_name();
        floor = jsonParser.getFloor();

        String space = String.valueOf(floor) + zone_name + String.valueOf(zone_index);

        if(floor != NotFound){
            CarPosition.setText("위치 : " + space);

            // 서버측에서 위치정보를 받아서 차량 위치를 조감도로 보여줌
            String view_url = "http://13.124.74.249:3000/places/dashboard?floor=" + floor;
            ViewMyCar.loadUrl(view_url);
        }
        else{
            Toast.makeText(getApplicationContext(),
                    "해당 차량은 없습니다.", Toast.LENGTH_LONG).show();
        }
    }

    public void BackToStartMenu(View v){ finish(); }
}
