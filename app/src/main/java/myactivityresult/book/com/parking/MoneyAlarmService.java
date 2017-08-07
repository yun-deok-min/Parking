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
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;

public class MoneyAlarmService extends Service implements Runnable{
    public class LocalBinder extends Binder{
        public MoneyAlarmService getService(){
            return MoneyAlarmService.this;
        }
    }
    private final IBinder binder = new LocalBinder();

    private boolean isRun; // 서비스 실행 flag
    private int virtual_money;
    private int fare;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRun = true;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        Thread myThread = new Thread(this);
        myThread.start();

        super.onCreate();
    }

    public void run(){
        Log.d("test","(요금, 보유머니) 비교 스레드 실행");
        while (true) {
            while (isRun) {
                setVirtual_money(); // 서버에서 차량이 보유하고 있는 가상머니 받아옴
                setFare();  // 입차시간 ~ 현재시간 으로 요금 계산

                if (fare > virtual_money) { // 요금이 가상머니보다 많아지면 알림
                    this.MakeNotification();
                }
                try {
                    Thread.sleep(3000); // 요금이 늘어나는 시간에 따라서 주기 조정
                } catch (InterruptedException e) {
                }
            }

            try {
                Thread.sleep(5000); // 서비스 비활성화 시, 재활성화 여부 확인 주기
            } catch (InterruptedException e) {
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void setIsRun(boolean isRun){
        if(isRun){
            Log.d("test","서비스 isRun = true");
        }
        else{
            Log.d("test","서비스 isRun = false");
        }
        this.isRun = isRun;
    }

    public void MakeNotification(){
        Log.d("test","알람 띄우면서 서비스 중지");
        setIsRun(false); // 서비스 일시 중지

        Resources res = getResources();
        Intent intent = new Intent(this, NotificationNoMoney.class);
        int NotificationID = 12345;
        intent.putExtra("NotificationID", NotificationID);
        intent.putExtra("VirtualMoney", virtual_money);
        intent.putExtra("Fare", fare);
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

    public void setVirtual_money(){
        SharedPreferences pref = getSharedPreferences("save01", Context.MODE_PRIVATE);
        String CarNumber = pref.getString("CarNumber","");
        if(!CarNumber.equals("")) {
            String url = "http://13.124.74.249:3000/cars/";
            HttpURLConnector conn = new HttpURLConnector(url + CarNumber);
            conn.start();
            try {
                conn.join();
            } catch (InterruptedException e) {
            }
            ;
            String result = conn.getResult();

            JSONParser jsonParser = new JSONParser(result);
            jsonParser.parser(3);
            virtual_money = jsonParser.getVirtual_money();
            Log.d("test", "보유 가상 머니 : " + virtual_money);
        }
        else {
            Log.d("test", "차량 번호를 등록해주세요");
        }
    }

    public void setFare(){
        SharedPreferences pref = getSharedPreferences("save01", Context.MODE_PRIVATE);
        String CarNumber = pref.getString("CarNumber","");
        if(!CarNumber.equals("")) {
            String url = "http://13.124.74.249:3000/cars/";
            HttpURLConnector conn = new HttpURLConnector(url + CarNumber);
            conn.start();
            try {
                conn.join();
            } catch (InterruptedException e) {
            }
            ;
            String result = conn.getResult();

            JSONParser jsonParser = new JSONParser(result);
            jsonParser.parser(1);

            int start_at = jsonParser.getStarted_at();
            long millis = (long) start_at * 1000L;

            Calendar end = Calendar.getInstance();
            Calendar start = Calendar.getInstance();
            start.setTimeInMillis(millis);
            int StartHour = start.get(Calendar.HOUR_OF_DAY);
            int StartMinute = start.get(Calendar.MINUTE);
            int EndHour = end.get(Calendar.HOUR_OF_DAY);
            int EndMinute = end.get(Calendar.MINUTE);
            int BetweenHour = EndHour - StartHour;
            int BetweenMinute = EndMinute - StartMinute;

            if (BetweenMinute <= 30) {
                fare = 10000 * BetweenHour + 5000;  // 5000, 15000
            } else {
                fare = 10000 * (BetweenHour + 1);  // 10000, 20000
            }
            Log.d("test", "요금 : " + fare);
        }
        else {
            Log.d("test", "차량 번호를 등록해주세요");
        }
    }
}
