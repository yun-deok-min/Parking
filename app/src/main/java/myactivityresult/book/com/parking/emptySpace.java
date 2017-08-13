package myactivityresult.book.com.parking;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.TextView;

/* 서버와 통신해서 남은 주차 공간 갯수를 실시간으로 보여주는 스레드 */
public class emptySpace extends Thread {
    private TextView empty;
    private HttpURLConnector conn;
    private int emptySpace;
    private String result;
    Context context;
    Handler handler;
    private boolean isRun = false;

    public emptySpace(TextView empty, Context context){
        this.empty = empty;  // 텍스트 뷰를 하나 받아와서 남은 칸을 표시
        this.context = context;
        handler = new Handler();
    }

    public void run(){
        Log.d("test","빈공간 파악 스레드 실행");
        while(isRun) {
            String url = "http://13.124.74.249:3000/empty_places_count";
            conn = new HttpURLConnector(url);
            conn.start();
            try{
                conn.join();
            } catch(InterruptedException e){};
            result = conn.getResult();
            JSONParser parser = new JSONParser(result);
            parser.parser(2);
            emptySpace = parser.getEmpty_space();
            if(emptySpace == 0){  // 남은 주차 공간이 0이면 알림을 띄움
                MakeNotification();
            }

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

    public void setRun(boolean isRun){
        this.isRun = isRun;
    }

    public void MakeNotification(){
        Resources res = context.getResources();
        Intent intent = new Intent(context, NotificationNoSpace.class);
        int NotificationID = 1234;            // 알림을 구분할 ID
        intent.putExtra("NotificationID", NotificationID);

        // PendingIntent 는 Intent 객체를 감싸는 객체로서, PendingIntent 가 포함하고 있는 인텐트를
        // 외부의 다른 애플리케이션에서 실행할 권한을 주기 위해서 사용
        // 다른 화면(애플리케이션)에서 알림을 눌렀을 때 NotificationNoSpace 액티비티로 이동 가능
        PendingIntent contentIntent = PendingIntent.getActivity
                (context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        /* 알림 형태 정의 */
        builder.setContentTitle("Parking Manager").setContentText("주차장 현황")
                .setTicker("주차장이 만차입니다").setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                .setContentIntent(contentIntent).setAutoCancel(true)
                .setWhen(System.currentTimeMillis()).setDefaults(Notification.DEFAULT_ALL);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            builder.setCategory(Notification.CATEGORY_MESSAGE)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setVisibility(Notification.VISIBILITY_PUBLIC);
        }

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NotificationID, builder.build());  // 알림 생성
    }
}
