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

    public void EnteringLog(String CarNumber){
        // MainActivity 의 onCreate 에서 마지막 접속 시간 이후의 로그를 서버와 통신해서 가져옴
        // if(start_at > lastVisitTime) 이면 JSON 파싱
        /* MainActivity 의 onDestroy 에서 마지막 접속 시간 저장
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 hh시 mm분 ss초");
        String lastVisitTime = sdf.format(today);

        pref = getSharedPreferences("my_db", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("lastVisitTime", lastVisitTime);  // 종료 시 시간 저장
        editor.commit();
        */
        String url = "http://13.124.74.249:3000/entering_logs?car_numbering=:";
        HttpURLConnector conn = new HttpURLConnector(url + CarNumber);
        conn.start();
        try{
            conn.join();
        } catch(InterruptedException e){};
        String result = conn.getResult();
        JSONParser parser = new JSONParser(result);
        parser.parser(2);
        int start_at = parser.getStarted_at();
        int end_at = parser.getEnd_at();
    }
}
