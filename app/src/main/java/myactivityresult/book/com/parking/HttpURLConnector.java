package myactivityresult.book.com.parking;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpURLConnector extends Thread {
    private String url_str;
    private HttpURLConnection conn;
    private String result;
    final static int GET = 1000;
    final static int POST = 1001;
    private int mode;  // 기본 모드는 GET 으로
    JSONObject jsonObject;

    public HttpURLConnector(String url_str) {
        this.url_str = url_str;
        mode = GET;
    }

    public HttpURLConnector(String url_str, int mode, JSONObject jsonObject) {
        this.url_str = url_str;
        this.mode = mode;
        this.jsonObject = jsonObject;
    }

    public void run(){
        try {
            URL url = new URL(url_str);
            conn = (HttpURLConnection) url.openConnection();

            if (conn != null) {
                if(mode == GET){
                    conn.setDoInput(true);
                    conn.setRequestMethod("GET");
                }
                else if(mode == POST){
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                }
                conn.setConnectTimeout(10000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        try {
            int res_code = conn.getResponseCode();
            Log.d("test", "연결 상태 code : " + res_code);
            Log.d("test",jsonObject.toString());

            if (res_code == HttpURLConnection.HTTP_OK) {
                if(mode == GET) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line = null;
                    while (true) {
                        line = reader.readLine();
                        if (line == null)
                            break;
                        sb.append(line + "\n");
                    }
                    reader.close();
                }
                else if(mode == POST){
                    OutputStream os = conn.getOutputStream();
                    os.write(jsonObject.toString().getBytes("euc-kr")); // UTF-8
                    os.flush();
                    os.close();
                }
            }
            else if(res_code == 422){
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String line = null;
                while (true) {
                    line = reader.readLine();
                    if (line == null)
                        break;
                    sb.append(line + "\n");
                }
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        result = sb.toString();
        conn.disconnect();
    }

    public String getResult(){
        return  result;
    }
}
