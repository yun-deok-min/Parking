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

public class emptySpace extends Thread {
    private TextView empty;
    private HttpURLConnector conn;
    private int emptySpace;
    private String result;
    Context context;
    final static int GET = 1000;
    final static int POST = 1001;
    Handler handler;
    private boolean isRun = false;

    public emptySpace(TextView empty, Context context){
        this.empty = empty;
        this.context = context;
        handler = new Handler();
    }

    public void run(){
        while(isRun) {
            String url = "http://13.124.74.249:3000/empty_places_count"; // 여유 칸 보내주는 API 주소 나중에 추가
            conn = new HttpURLConnector(url, GET);
            conn.start();
            try{
                conn.join();
            } catch(InterruptedException e){};
            result = conn.getResult();
            JSONParser parser = new JSONParser(result);
            parser.parser(2);
            emptySpace = parser.getEmpty_space();
            if(emptySpace == 0){
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
        int NotificationID = 1234;
        intent.putExtra("NotificationID", NotificationID);
        PendingIntent contentIntent = PendingIntent.getActivity
                (context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

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
        nm.notify(NotificationID, builder.build());
    }
}
