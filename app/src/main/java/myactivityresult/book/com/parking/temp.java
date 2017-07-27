package myactivityresult.book.com.parking;

import android.app.Activity;

public class temp extends Activity{
    /*public void TellToParkingCenter(){
        Intent intent = new Intent();
        TextView textView = (TextView)findViewById(R.id.) ;
        intent.setAction(Intent.ACTION_DIAL);
        String tel_number = textView.getText().toString();
        intent.setData(Uri.parse("tel:" + tel_number));
        startActivity(intent);
    }

    public void CalculateMoney(){
        int monthly_fare = 0;
        int temp_fare;
        Cursor daily_fare;
        EditText CarNumber = (EditText)findViewById(R.id.);
        EditText Year = (EditText)findViewById(R.id.);
        EditText Month = (EditText)findViewById(R.id.);

        SQLiteHelper sqh = new SQLiteHelper(temp.this);

        String month = Month.getText().toString();
        String year = Year.getText().toString();
        daily_fare = sqh.getMonthlyFare(CarNumber.getText().toString(), Integer.parseInt(month));
        daily_fare = sqh.getMonthlyFare(CarNumber.getText().toString(),
                Integer.parseInt(month), Integer.parseInt(year));

        while(daily_fare.moveToNext()){
            temp_fare = daily_fare.getInt(daily_fare.getColumnIndex(MoneyLogTable.Fare));
            monthly_fare = monthly_fare + temp_fare;
        }
    }*/
}
