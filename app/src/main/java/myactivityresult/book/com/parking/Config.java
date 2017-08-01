package myactivityresult.book.com.parking;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Config extends AppCompatActivity {
    EditText EdtCarNumber;
    SharedPreferences pref;
    String carNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        pref = getSharedPreferences("save01", Context.MODE_PRIVATE);
        carNumber = pref.getString("CarNumber","");
        if(!carNumber.equals("")) {
           EdtCarNumber = (EditText) findViewById(R.id.EdtCarNumber);
           EdtCarNumber.setText(carNumber);
        }
    }

    public void EnrollCar(View v){
        EdtCarNumber = (EditText)findViewById(R.id.EdtCarNumber);
        pref = getSharedPreferences("save01", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();
        editor.putString("CarNumber", EdtCarNumber.getText().toString() );
        editor.commit();
        Toast.makeText(getApplicationContext(),
                "차량 번호가 등록되었습니다.", Toast.LENGTH_LONG).show();
    }

    public void ShowTimeLog(View v){
        carNumber = pref.getString("CarNumber","");
        if(carNumber.equals("")) {
            Toast.makeText(getApplicationContext(), "등록된 차량이 없습니다",
                    Toast.LENGTH_LONG).show();
        }
        else {
            Intent intent = new Intent(getApplicationContext(), TimeLogActivity.class);
            startActivity(intent);
        }
    }

    public void BackToStartMenu(View v){
        finish();
    }
}
