package myactivityresult.book.com.parking;

import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

public class emptySpace extends Thread {
    private TextView empty;
    private HttpURLConnector conn;
    private int emptySpace;
    private String result;

    public emptySpace(TextView empty){
        this.empty = empty;
    }

    public void run(){
        while(true) {
            String url = "http://"; // 여유 칸 보내주는 API 주소 나중에 추가
            conn = new HttpURLConnector(url);
            conn.start();
            try{
                Thread.sleep(70);
            }catch (InterruptedException e){ }
            result = conn.getResult();
            JSONParser parser = new JSONParser(result);
            emptySpace = parser.getEmpty_space();

            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    empty.setText("여유칸 : " + String.valueOf(emptySpace));
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
