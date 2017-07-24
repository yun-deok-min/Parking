package myactivityresult.book.com.parking;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class temp extends Activity{
    public void TellToParkingCenter(){
        Intent intent = new Intent();
        TextView textView = (TextView)findViewById(R.id.) ;
        intent.setAction(Intent.ACTION_DIAL);
        String tel_number = textView.getText().toString();
        intent.setData(Uri.parse("tel:" + tel_number));
        startActivity(intent);
    }

    public void CalculateMoney(){
        int monthly_fare = 0;
        ArrayList<Integer> daliy_fare;
        EditText CarNumber = (EditText)findViewById(R.id.);
        String url = "http://~~~/money/";
        HttpURLConnector conn = new HttpURLConnector(url + CarNumber.getText().toString());
        conn.start();
        try{
            Thread.sleep(70);
        }catch (InterruptedException e){ }
        String result = conn.getResult();
        JSONParser jsonParser = new JSONParser(result);
        jsonParser.parser();
        daliy_fare = jsonParser.getDaliyFare();
        for(int i=0; i<daliy_fare.size(); i++){
            monthly_fare = monthly_fare + daliy_fare.get(i);
        }
    }
}
