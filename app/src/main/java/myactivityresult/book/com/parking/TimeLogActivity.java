package myactivityresult.book.com.parking;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class TimeLogActivity extends AppCompatActivity {
    ListView logView;
    TextView visit_count_txt;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_log);

         /* DB를 보여주기전에 서버와 통신해서 최신화 */
        pref = getSharedPreferences("save01", Context.MODE_PRIVATE);
        String carNumber = pref.getString("CarNumber",null);

        HttpURLConnector conn = new HttpURLConnector("http://13.124.74.249:3000/entering_logs/" + carNumber);
        conn.start();
        try{
            conn.join();
        } catch(InterruptedException e){}
        JSONParser parser = new JSONParser(conn.getResult());
        parser.parser_array(pref);

        ArrayList<Integer> entered_array = parser.getEntered_array();
        ArrayList<Integer> exited_array = parser.getExited_array();

        SQLiteHelper sqh = new SQLiteHelper(TimeLogActivity.this);
        sqh.setLog(entered_array, exited_array, carNumber);

        /* 최신화가 끝나면 리스트 뷰 형식으로 보여줌 */
        Cursor cursor = sqh.getLog(carNumber);
        logView = (ListView)findViewById(R.id.listView);
        LogAdapter logList = new LogAdapter(TimeLogActivity.this, cursor,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        logView.setAdapter(logList);

        /* 전체 방문 횟수를 보여줌 */
        visit_count_txt = (TextView)findViewById(R.id.visit_count_text);
        int visit_count = sqh.getVisitCount(carNumber);
        visit_count_txt.setText("총 방문 횟수 : " + String.valueOf(visit_count));

        /* 요금 테이블 갱신 */
        for(int i=0; i<entered_array.size(); i++){
            long entered_at = (long)entered_array.get(i) * 1000L;
            long end_at = (long)exited_array.get(i) * 1000L;

            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            start.setTimeInMillis(entered_at);
            end.setTimeInMillis(end_at);

            int BetweenMinute, BetweenHour;
            BetweenHour = end.get(Calendar.HOUR) - start.get(Calendar.HOUR);
            BetweenMinute = end.get(Calendar.MINUTE) - start.get(Calendar.MINUTE);

            int fare;

            if(BetweenMinute <= 30){
                fare = 10000 * BetweenHour + 5000;  // 5000, 15000
            }
            else{
                fare = 10000 * (BetweenHour + 1) ;  // 10000, 20000
            }

            String pattern = "yyyy-MM-dd";
            SimpleDateFormat formatter = new SimpleDateFormat(pattern);
            String date = formatter.format( ((long)entered_array.get(i) * 1000L) );
            sqh.addMoneyLog(date, carNumber, fare);
        }
    }

    public void BackToStartMenu(View v){
        finish();
    }
}
