package myactivityresult.book.com.parking;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class NotificationNoSpace extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_no_space);

        Intent intent = getIntent();

        int NotificationID = intent.getIntExtra("NotificationID", 0);

        NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(NotificationID);
    }

    public void Back(View v){
        finish();
    }
}
