package myactivityresult.book.com.parking;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    GridView gridView;
    TextView TotalSpace, AvailableSpace;
    SharedPreferences pref;
    Handler mHandler;
    HttpURLConnector conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = (GridView)findViewById(R.id.gridView01);
        gridView.setAdapter(new ImageAdapter(this));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;

                switch (position){
                    case 0 :
                        intent = new Intent(getApplicationContext(), aeroview.class);
                        startActivity(intent);
                        break;
                    case 1 :
                        intent = new Intent(getApplicationContext(), CalculateFare.class);
                        startActivity(intent);
                        break;
                    case 2 :
                        intent = new Intent(getApplicationContext(), MyCarPosition.class);
                        startActivity(intent);
                        break;
                    case 3 :
                        intent = new Intent(getApplicationContext(), Enrollment.class);
                        startActivity(intent);
                        break;
                    default :
                        break;
                }
            }
        });

        pref = getSharedPreferences("save01", Context.MODE_PRIVATE);
        /* 애플리케이션에 첫 방문인지 확인 */
        boolean hasVisited = pref.getBoolean("hasVisited", false);
        if(!hasVisited){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("환영합니다");
            builder.setMessage("차량을 등록하시겠습니까?");
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setPositiveButton("예", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which){
                    Intent intent = new Intent(getApplicationContext(), Enrollment.class);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("아니요", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which){ }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("hasVisited", true);
            editor.commit();
        }

        /*
         핸들러 생성
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                }
            }
        };
         서버와 지속적으로 통신해서 칸 현황 파악하는 스레드
        SpaceCountThread = new Thread(){
            public void run(){
                while(true) {
                    try {
                        try {
                            Thread.sleep(2000);
                        }
                        catch (Exception ex) {
                        }
                        mHandler.sendMessage(Message.obtain(mHandler, 0));
                    }
                    catch(Exception exTemp) {
                        Log.e("", exTemp.toString());
                    }
                    String total="전체 칸 : ", available="여유 칸 : ";
                    TotalSpace = (TextView) findViewById(R.id.TotalSpace);
                    AvailableSpace = (TextView) findViewById(R.id.AvailableSpace);
                    TotalSpace.setText(total);
                    AvailableSpace.setText(available);
                }
            }
        };
        SpaceCountThread.start();
        */
    }

    @Override
    protected void onDestroy() {
        conn.End();  // 앱 종료되면 서버와 연결 종료
        super.onDestroy();
    }
}

class ImageAdapter extends BaseAdapter{
    private Integer[] FunctionImage = {
            R.drawable.aeroview, R.drawable.calculate_fare,
            R.drawable.car_position, R.drawable.enrollment};

    Context mContext;

    public ImageAdapter(Context context){
        mContext = context;
        // MainActivity 클래스에게 이미지뷰를 넘겨줘야 하기에 Context 를 받아옴
    }

    @Override
    public int getCount(){
        return FunctionImage.length;  // 이미지 총 개수
    }

    @Override
    public Object getItem(int position){
        return null;
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ImageView imageView;
        if(convertView == null){
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(600, 600)); // 이미지 뷰 세로, 가로
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP); // 이미지 자체 size 조정 및 이동
            imageView.setPadding(8,8,8,8);  // 상하좌우 여백
        }
        else{
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(FunctionImage[position]);

        return imageView;
    }
}
