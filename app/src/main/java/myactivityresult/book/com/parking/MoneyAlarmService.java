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
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;

public class MoneyAlarmService extends Service {
    Config mConfig;
    final static int GET = 1000;
    final static int POST = 1001;

    public MoneyAlarmService() { }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mConfig = (Config) intent.getSerializableExtra("Config");

        int virtual_money = getVirtual_money();
        int fare = getFare();

        if(fare > virtual_money){
            this.MakeNotification();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void MakeNotification(){
        Resources res = getResources();
        Intent intent = new Intent(this, NotificationNoMoney.class);
        int NotificationID = 12345;
        intent.putExtra("NotificationID", NotificationID);
        intent.putExtra("Config", mConfig);
        intent.putExtra("VirtualMoney", getVirtual_money());
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
        HttpURLConnector conn = new HttpURLConnector(url + CarNumber, GET);
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
        HttpURLConnector conn = new HttpURLConnector(url + CarNumber, GET);
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
