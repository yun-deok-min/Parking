package myactivityresult.book.com.parking;

import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

public class emptySpace extends Thread {
    private TextView empty;
    private HttpURLConnector conn;
    private int emptySpace;

    public emptySpace(TextView empty, HttpURLConnector conn){
        this.empty = empty;
        this.conn = conn;
    }

    public void run(){
        while(true) {
            String emptyStr = conn.result(1); // select 1번이 남은 공간을 표시할 경우
            JSONParser parser = new JSONParser(emptyStr);
            emptySpace = parser.getEmpty_space();

            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    empty.setText(emptySpace + "");
                }
            });
            try{
                Thread.sleep(5000); // 빈 공간 갱신 주기
            } catch(InterruptedException e){
                Log.d("Exception","InterruptedException");
            }
        }
    }
}
