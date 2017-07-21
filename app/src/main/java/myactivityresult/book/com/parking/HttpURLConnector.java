package myactivityresult.book.com.parking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpURLConnector {
    private String url_str;
    private HttpURLConnection conn;

    public HttpURLConnector(String url_str) {
        this.url_str = url_str;
    }

    public String connect() {
        try {
            URL url = new URL(url_str);
            conn = (HttpURLConnection) url.openConnection();

            if (conn != null) {
                conn.setDoInput(true);
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("GET");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        try {
            int res_code = conn.getResponseCode();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
