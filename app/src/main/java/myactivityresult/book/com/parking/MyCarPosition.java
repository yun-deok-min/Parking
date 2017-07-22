package myactivityresult.book.com.parking;

import android.app.Activity;
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
    private String result; // DB 데이터
    EditText EdtCarNumber;
    TextView CarPosition;
    WebView ViewMyCar;
    HttpURLConnector conn;
    JSONParser jsonParser;
    private String CarNumber;
    final static int NotFound = 0;
    private String zone_name;
    private int zone_index;
    private int floor = NotFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

    public void SearchCar(View v){
        EdtCarNumber = (EditText)findViewById(R.id.EdtCarNumber);
        CarNumber = EdtCarNumber.getText().toString();
        SearchCar(CarNumber);
    }
    public void SearchCar(String CarNumber){
        CarPosition = (TextView)findViewById(R.id.CarPosition);
        ViewMyCar = (WebView)findViewById(R.id.ViewMyCar);
        ViewMyCar.setWebViewClient(new WebViewClient());

        String url = "https://";
        conn = new HttpURLConnector(url +  CarNumber);
        conn.start();
        result = conn.getResult();

        jsonParser = new JSONParser(result);
        // jsonParser.parser();
        zone_index = jsonParser.getZone_index();
        zone_name = jsonParser.getZone_name();
        floor = jsonParser.getFloor();

        boolean find = false;
        if(floor != NotFound){
            find = true;
            CarPosition.setText(String.valueOf(floor) + zone_name + String.valueOf(zone_index));

            String view_url = "https://"
                    + String.valueOf(floor); // 받은 데이터 중에서 층 정보만 분리
            switch (floor){
                case 1 :
                    ViewMyCar.loadUrl(view_url);
                    break;
                case 2 :
                    ViewMyCar.loadUrl(view_url);
                    break;
            }
        }

        if(!find){
            Toast.makeText(getApplicationContext(),
                    "해당 차량은 없습니다.", Toast.LENGTH_LONG).show();
        }
    }

    public void BackToStartMenu(View v){ finish(); }
}
