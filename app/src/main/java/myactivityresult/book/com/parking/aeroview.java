package myactivityresult.book.com.parking;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

/* 조감도(주차장 주차 현황 사진)를 보여주는 액티비티 */
public class aeroview extends AppCompatActivity {
    WebView AeroView;
    Spinner floor;
    private boolean initSpinner = false;  // 스피너에 접근한 적이 있는지 판단
    ArrayList<String> FloorData;    // 스피너 목록 리스트
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aeroview);

        AeroView = (WebView)findViewById(R.id.AeroView);
        floor = (Spinner)findViewById(R.id.floor);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        FloorData = new ArrayList<>();
        FloorData.add("1층");
        FloorData.add("2층");

        // 스피너 이미지를 설정하고 세팅
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, FloorData);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        floor.setAdapter(adapter);

        AeroView.setWebViewClient(new WebViewClient());
        // 안드로이드에서는 디폴트로, 다른 링크로 이동하고자 할때는 안드로이드의 디폴트
        // 외부 웹 브라우져를 통해서 이동하기 때문에 내부 웹뷰 클라이언트 지정

        // 스피너 항목 선택 시 처리 메소드
        floor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                String url="http://13.124.74.249:3000/places/dashboard";  // 기본 API 주소
                ProgressBarTask task;

                if(initSpinner == false){  // 스피너에 처음 접근할 때 설정
                    initSpinner = true;
                    floor.setSelection(0);       // 1층을 디폴트 값으로
                    AeroView.loadUrl(url + "?floor=1");
                    // task = new ProgressBarTask(url + "1", AeroView, progressBar);
                    // task.execute();
                    return;
                }

                switch (position){
                    case 0 :  // 1층 조감도
                        AeroView.loadUrl(url + "?floor=1");
                        // task = new ProgressBarTask(url + "/1", AeroView, progressBar);
                        // task.execute();
                        break;
                    case 1 :  // 2층 조감도
                        AeroView.loadUrl(url + "?floor=2");
                        // task = new ProgressBarTask(url + "/2", AeroView, progressBar);
                        // task.execute();
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
