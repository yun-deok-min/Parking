package myactivityresult.book.com.parking;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

public class emptySpace extends Thread {
    private TextView empty;
    private HttpURLConnector conn;
    private int emptySpace;
    private String result;
    Context context;

    public emptySpace(TextView empty, Context context){
        this.empty = empty;
        this.context = context;
    }

    public void run(){
        while(true) {
            String url = "http://"; // 여유 칸 보내주는 API 주소 나중에 추가
            conn = new HttpURLConnector(url);
            conn.start();
            LoadingDialog dialog = new LoadingDialog(context);
            dialog.execute();
            try{
                conn.join();
            } catch(InterruptedException e){};
            dialog.setIsEnd();
            result = conn.getResult();
            JSONParser parser = new JSONParser(result);
            parser.parser(2);
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
