package myactivityresult.book.com.parking;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class aeroview extends AppCompatActivity {
    WebView AeroView;
    Spinner floor;
    private boolean initSpinner = false;
    ArrayList<String> FloorData;
    HttpURLConnector conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aeroview);

        AeroView = (WebView)findViewById(R.id.AeroView);
        floor = (Spinner)findViewById(R.id.floor);

        FloorData = new ArrayList<>();
        FloorData.add("1층");
        FloorData.add("2층");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, FloorData);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        floor.setAdapter(adapter);

        AeroView.setWebViewClient(new WebViewClient());
        /* 스피너의 층에 따라서 */
        floor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                String url="https://";  // 기본 API 주소

                if(initSpinner == false){
                    initSpinner = true;
                    floor.setSelection(0);       // 1층을 디폴트 값으로
                    conn = new HttpURLConnector(url); // 1층 구분 표시 추가
                    conn.connect();
                    return;
                }

                switch (position){
                    case 0 :  // 1층 조감도
                        conn = new HttpURLConnector(url); // 1층 구분 표시 추가
                        conn.connect();
                        break;
                    case 1 :  // 2층 조감도
                        conn = new HttpURLConnector(url); // 2층 구분 표시 추가
                        conn.connect();
                        break;
                    default :
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent){
                Toast.makeText(getApplicationContext(),
                        "층을 다시 선택해주세요", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void BackToStartMenu(View v){
        finish(); // goto start_menu
    }
}
