package myactivityresult.book.com.parking;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    GridView gridView;
    TextView TotalSpace, AvailableSpace;
    SharedPreferences pref;
    emptySpace empty_space;
    ScreenRate screen_rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int ScreenWidth = size.x;
        int ScreenHeight = size.y;
        screen_rate = new ScreenRate(ScreenWidth, ScreenHeight);
        screen_rate.setSize(100, 100);

        gridView = (GridView)findViewById(R.id.gridView01);
        gridView.setAdapter(new ImageAdapter(this));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;

                switch (position){
                    case 0 :  // 조감도 아이콘
                        intent = new Intent(getApplicationContext(), aeroview.class);
                        startActivity(intent);
                        break;
                    case 1 :  // 요금 계산 아이콘
                        intent = new Intent(getApplicationContext(), CalculateFare.class);
                        startActivity(intent);
                        break;
                    case 2 :  // 내 차량 위치 검색 아이콘
                        intent = new Intent(getApplicationContext(), MyCarPosition.class);
                        startActivity(intent);
                        break;
                    case 3 :  // 차량 번호 등록 아이콘
                        intent = new Intent(getApplicationContext(), Config.class);
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
                    Intent intent = new Intent(getApplicationContext(), Config.class);
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

        TotalSpace = (TextView)findViewById(R.id.TotalSpace); // 고정값?
        AvailableSpace = (TextView)findViewById(R.id.AvailableSpace);

        Intent intent = new Intent(this, MoneyAlarmService.class);
        Log.d("test","메인 엑티비티에서 startService");
        startService(intent);
    }

    @Override
    protected void onResume() {
        empty_space = new emptySpace(AvailableSpace, MainActivity.this);
        empty_space.setRun(true);
        empty_space.start();
        super.onResume();
    }

    @Override
    protected void onPause() {
        empty_space.setRun(false);
        super.onPause();
    }

    class ImageAdapter extends BaseAdapter{
        private Integer[] FunctionImage = {
                R.drawable.aeroview, R.drawable.calculate_fare,
                R.drawable.car_position, R.drawable.enrollment};

        Context mContext;

        public ImageAdapter(Context context){
            mContext = context;
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
                imageView.setLayoutParams(new GridView.LayoutParams(
                        screen_rate.getX(50), screen_rate.getY(30))); // 아이콘 이미지 가로, 세로
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP); // 이미지 자체 size 조정 및 이동
                imageView.setPadding(10,10,10,10);  // 상하좌우 여백
            }
            else{
                imageView = (ImageView) convertView;
            }

            imageView.setImageResource(FunctionImage[position]);

            return imageView;
        }
    }
}




