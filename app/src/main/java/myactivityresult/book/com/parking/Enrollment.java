package myactivityresult.book.com.parking;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Enrollment extends AppCompatActivity {
    EditText EdtCarNumber;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollment);
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

    public void BackToStartMenu(View v){
        finish();
    }
}
