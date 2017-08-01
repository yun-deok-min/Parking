package myactivityresult.book.com.parking;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class TimeLogActivity extends AppCompatActivity {
    ListView logView;
    TextView visit_count_txt;
    SharedPreferences pref;
    final static int GET = 1000;
    final static int POST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_log);

         /* DB를 보여주기전에 서버와 통신해서 최신화 */
        pref = getSharedPreferences("save01", Context.MODE_PRIVATE);
        String carNumber = pref.getString("CarNumber",null);

        HttpURLConnector conn = new HttpURLConnector("http://13.124.74.249:3000/entering_logs?car_numbering=" + carNumber, GET);
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
    }
}
