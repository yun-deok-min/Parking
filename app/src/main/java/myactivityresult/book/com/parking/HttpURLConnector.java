package myactivityresult.book.com.parking;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.net.HttpURLConnection.HTTP_OK;

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
            if(mode == GET) {
                int res_code = conn.getResponseCode();
                // Log.d("test", "연결 상태 code : " + res_code);
                if (res_code == HTTP_OK) {
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
                else if(res_code == 422) {
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
            }
            else if (mode == POST) {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
                writer.write(jsonObject.toString());
                writer.close();

                int res_code = conn.getResponseCode();

                if(res_code == HttpURLConnection.HTTP_OK){
                    Log.d("test", "송신 내용 : " + jsonObject.toString());
                }
                else{
                    Log.d("test", "연결 상태 code : " + res_code);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        result = sb.toString();
        conn.disconnect();
    }

    public String getResult(){
        // Log.d("test", "서버에게 받은 json 내용 : " + result);
        return  result;
    }
}
