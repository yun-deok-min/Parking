package myactivityresult.book.com.parking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpURLConnector extends Thread {
    private String url_str;
    private HttpURLConnection conn;
    private String result;
    private int mode;
    final static int GET = 1000;
    final static int POST = 1001;

    public HttpURLConnector(String url_str, int mode) {
        this.url_str = url_str;
        this.mode = mode;
    }

    public void run(){
        try {
            URL url = new URL(url_str);
            conn = (HttpURLConnection) url.openConnection();

            if (conn != null) {
                if(mode == GET){
                    conn.setDoInput(true);
                }
                else if(mode == POST){
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                }
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("GET");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        try {
            int res_code = conn.getResponseCode();
            // Log.d("test", "연결 상태 code : " + res_code);
            if (res_code == HttpURLConnection.HTTP_OK) {
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
