package myactivityresult.book.com.parking;

import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

public class emptySpace extends Thread {
    private TextView empty;
    private HttpURLConnector conn;
    private String StrEmptySpace;
    private int emptySpace;

    public emptySpace(TextView empty){
        this.empty = empty;
    }

    public void run(){
        while(true) {
            String url = "https://"; // 여유 칸 보내주는 API 주소 나중에 추가
            conn = new HttpURLConnector(url);
            StrEmptySpace = conn.connect();
            JSONParser parser = new JSONParser(StrEmptySpace);
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
