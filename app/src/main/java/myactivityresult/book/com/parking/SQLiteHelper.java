package myactivityresult.book.com.parking;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/* DB 관련 처리를 하는 클래스 */
public class SQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "my_database.db";
    private static final int DATABASE_VERSION = 1;

    SQLiteHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE " + CarTable.TABLE_NAME + " (" +
                CarTable.CarNumber + " TEXT PRIMARY KEY, " +
                CarTable.State + " TEXT, " +
                CarTable.VisitCount + " INTEGER" + ");" );

        db.execSQL("CREATE TABLE " + MoneyLogTable.TABLE_NAME + " (" +
                MoneyLogTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoneyLogTable.Date + " TEXT, " +   // yy:MM:dd
                MoneyLogTable.CarNumber + " TEXT, " +
                MoneyLogTable.Fare + " INTEGER" + ");" );

        db.execSQL("CREATE TABLE " + TimeLogTable.TABLE_NAME + " (" +
                TimeLogTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TimeLogTable.CarNumber + " TEXT, " +
                TimeLogTable.Start + " TEXT, " +      // yy:MM:dd
                TimeLogTable.End + " TEXT" + ");" );  // yy:MM:dd

        /*db.execSQL("ALTER TABLE " + CarTable.TABLE_NAME + " ADD CONSTRAINT "
                + CarTable.TABLE_NAME + "_CK CHECK (" + CarTable.State
                + " IN('I','O'));");  // in, out

        db.execSQL("ALTER TABLE " + MoneyLogTable.TABLE_NAME + " ADD CONSTRAINT "
                + MoneyLogTable.TABLE_NAME + "_FK FOREIGN KEY (" + MoneyLogTable.CarNumber
                + ") REFERENCES " + CarTable.TABLE_NAME + "(" + CarTable.CarNumber + ");" );*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        Log.w("LOG_TAG", oldVersion + "버전에서" + newVersion + "버전으로"
                + "데이터베이스 업그레이드(이전 데이터는 모두 삭제)");

        /* 업그레이드 되면 이전 테이블 제거 */
        db.execSQL("DROP TABLE IF EXISTS " + CarTable.TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + MoneyLogTable.TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + TimeLogTable.TABLE_NAME + ";");

        onCreate(db);  // 테이블의 새 인스턴스 생성
    }

    public void addCar(String CarNumber, String State, int VisitCount){
        ContentValues contentvalues = new ContentValues();

        // ID는 자동으로 증가하기 때문에 입력할 필요 없음
        contentvalues.put(CarTable.CarNumber, CarNumber);
        contentvalues.put(CarTable.State, State);
        contentvalues.put(CarTable.VisitCount, VisitCount);

        SQLiteDatabase sqlDB = getWritableDatabase();  // 읽고 쓸수 있는 데이터베이스를 가져옴
        sqlDB.insert(CarTable.TABLE_NAME, CarTable.CarNumber, contentvalues);
    }

    public void addMoneyLog(String Date, String CarNumber, int fare){
        ContentValues contentvalues = new ContentValues();

        contentvalues.put(MoneyLogTable.Date, Date);
        contentvalues.put(MoneyLogTable.CarNumber, CarNumber);
        contentvalues.put(MoneyLogTable.Fare, fare);

        SQLiteDatabase sqlDB = getWritableDatabase();
        sqlDB.insert(MoneyLogTable.TABLE_NAME, MoneyLogTable.CarNumber, contentvalues);
    }

    public Cursor getAllDatabase(String TableName){
        SQLiteDatabase sqlDB = getWritableDatabase();

        String[] columns = new String[] { "*" };

        Cursor cursor = sqlDB.query(TableName, columns, null, null , null, null, null);
        return cursor;
    }

    public Cursor getMonthlyFare(String CarNumber, int year, int month){ // yyyy-MM-dd
        SQLiteDatabase sqlDB = getWritableDatabase();

        String[] columns = new String[] { MoneyLogTable.Fare };
        String date;
        if(month < 10) {
            date = String.valueOf(year) + "-0" + String.valueOf(month);
        }
        else{
            date = String.valueOf(year) + "-" + String.valueOf(month);
        }
        String[] selectionArgs = new String[] { date, CarNumber };

        Cursor cursor = sqlDB.query(MoneyLogTable.TABLE_NAME, columns,
                "substr(" + MoneyLogTable.Date + ",1,7) = ? AND " + MoneyLogTable.CarNumber + "= ?"
                , selectionArgs, null, null, null);

        // where substring(date, 1, 7) = 2017-07 and car_number = 88허1234
        return cursor;
    }

    public boolean removeCar(String CarNumber){
        SQLiteDatabase sqlDB = getWritableDatabase();
        String[] whereArgs = new String[] { CarNumber };

        sqlDB.delete(CarTable.TABLE_NAME, CarTable.CarNumber + "= ?", whereArgs);

        int result = sqlDB.delete(CarTable.TABLE_NAME, CarTable.CarNumber + "= ?", whereArgs);
        return (result > 0);
    }

    public void setLog(ArrayList<Integer> entered_array, ArrayList<Integer> ended_array, String car_number){
        ContentValues contentvalues = new ContentValues();
        SQLiteDatabase db = getWritableDatabase();
        for(int i = 0 ; i < entered_array.size(); i++){
            String pattern = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat formatter = new SimpleDateFormat(pattern);
            String entered_date = formatter.format( ((long)entered_array.get(i) * 1000L) );
            String ended_date = formatter.format( ((long)ended_array.get(i) * 1000L) );

            contentvalues.put(TimeLogTable.CarNumber, car_number);
            contentvalues.put(TimeLogTable.Start, entered_date);
            contentvalues.put(TimeLogTable.End, ended_date);

            db.insert(TimeLogTable.TABLE_NAME, CarTable.CarNumber, contentvalues);
        }
    }

    public Cursor getLog(String car_number){
        SQLiteDatabase db = getReadableDatabase();
        String[] selectionArgs = new String[] { car_number };
        return db.rawQuery("select * from " + TimeLogTable.TABLE_NAME + " where " + TimeLogTable.CarNumber
                + " = ? order by " +  TimeLogTable.Start + " desc;", selectionArgs);
        // 커서 어댑터를 사용하기 위해서 id 컬럼을 반드시 포함해서 반환
        // return db.rawQuery("select " + TimeLogTable.Start + ", " + TimeLogTable.End + " from
        //        + TimeLogTable.TABLE_NAME + " where " + TimeLogTable.CarNumber + " = ?;", selectionArgs);
    }

    public int getVisitCount(String CarNumber){
        Cursor cursor = getLog(CarNumber);
        int visit_count=0;
        while (cursor.moveToNext()){
            visit_count++;
        }
        return visit_count;
    }
}
