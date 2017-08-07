package myactivityresult.book.com.parking;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;

public class MoneyAlarmService extends Service implements Runnable{
    public class LocalBinder extends Binder{
        public MoneyAlarmService getService(){
            return MoneyAlarmService.this;
        }
    }
    private final IBinder binder = new LocalBinder();

    private boolean isRun = true;
    private int virtual_money;
    private int fare;

    @Override
    public void onCreate() {
        Log.d("test","onCreate 실행");
        Thread myThread = new Thread(this);
        myThread.start();

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("test","onStartCommand 실행");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("test","onDestroy 실행");
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("test","onUnbind 실행");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d("test","onRebind 실행");
        super.onRebind(intent);
    }

    public void run(){
        Log.d("test","run 실행");
        virtual_money = 10000;
        fare = 0;
        while (isRun){
            // virtual_money = getVirtual_money();
            // fare = getFare();
            fare = fare + 2000;

            if(fare > virtual_money){
                this.MakeNotification();
            }

            try{
                Thread.sleep(1000); // 요금이 늘어나는 시간에 따라서 주기 조정
            }catch(InterruptedException e){ }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("test","onBind 실행");
        return binder;
    }

    public void setIsRun(boolean isRun){
       this.isRun = isRun;
    }

    public void MakeNotification(){
        Resources res = getResources();
        Intent intent = new Intent(this, NotificationNoMoney.class);
        int NotificationID = 12345;
        intent.putExtra("NotificationID", NotificationID);
        intent.putExtra("VirtualMoney", virtual_money);
        PendingIntent contentIntent = PendingIntent.getActivity
                (this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setContentTitle("Parking Manager").setContentText("가상머니 보유 현황")
                .setTicker("잔액이 부족합니다").setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                .setContentIntent(contentIntent).setAutoCancel(true)
                .setWhen(System.currentTimeMillis()).setDefaults(Notification.DEFAULT_ALL);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            builder.setCategory(Notification.CATEGORY_MESSAGE)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setVisibility(Notification.VISIBILITY_PUBLIC);
        }

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NotificationID, builder.build());
    }

    public int getVirtual_money(){
        SharedPreferences pref = getSharedPreferences("save01", Context.MODE_PRIVATE);
        String CarNumber = pref.getString("CarNumber","");
        String url="http://13.124.74.249:3000/virtual_money/";  // API 요청
        HttpURLConnector conn = new HttpURLConnector(url + CarNumber);
        conn.start();
        try{
            conn.join();
        } catch(InterruptedException e){};
        String result = conn.getResult();

        JSONParser jsonParser = new JSONParser(result);
        //jsonParser.parser(1);
        int virtual_money = jsonParser.getVirtual_money();
        return virtual_money;
    }

    public int getFare(){
        SharedPreferences pref = getSharedPreferences("save01", Context.MODE_PRIVATE);
        String CarNumber = pref.getString("CarNumber","");
        String url="http://13.124.74.249:3000/cars/";
        HttpURLConnector conn = new HttpURLConnector(url + CarNumber);
        conn.start();
        try{
            conn.join();
        } catch(InterruptedException e){};
        String result = conn.getResult();

        JSONParser jsonParser = new JSONParser(result);
        jsonParser.parser(1);

        int start_at = jsonParser.getStarted_at();
        long millis = (long)start_at * 1000L;

        Calendar end = Calendar.getInstance();
        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(millis);
        int StartHour = start.get(Calendar.HOUR_OF_DAY);
        int StartMinute = start.get(Calendar.MINUTE);
        int EndHour = end.get(Calendar.HOUR_OF_DAY);
        int EndMinute = end.get(Calendar.MINUTE);
        int BetweenHour = EndHour - StartHour;
        int BetweenMinute = EndMinute - StartMinute;

        int fare;
        if(BetweenMinute <= 30){
            fare = 10000 * BetweenHour + 5000;  // 5000, 15000
        }
        else{
            fare = 10000 * (BetweenHour + 1) ;  // 10000, 20000
        }
        return fare;
    }
}
