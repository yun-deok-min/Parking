package myactivityresult.book.com.parking;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpURLConnector implements Serializable {
    private String url_str;
    private HttpURLConnection conn;

    public HttpURLConnector(String url_str) {
        this.url_str = url_str;
    }

    public String result(int select) {
        StringBuilder sb = new StringBuilder();
        try {
            int res_code = conn.getResponseCode();
            if (res_code == HttpURLConnection.HTTP_OK) {
                /*StatusLine statusLine = response.getStatusLine();
                statusLine.getStatusCode() == 200이면 서버와 정상적으로 연결된 것*/
                PrintWriter writer = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(conn.getOutputStream())), true);

                switch (select) {
                    case 1:
                        writer.print("GET 'cars/:numbering'");
                        writer.close();
                        break;
                    case 2:
                        writer.print("GET 'entering_logs?car_numbering=:car_numbering'");
                        writer.close();
                        break;
                    case 3:
                        writer.print("GET 'places'");
                        writer.close();
                        break;
                }

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

    public void connect() {
        try {
            URL url = new URL(url_str);
            conn = (HttpURLConnection) url.openConnection();

            if (conn != null) {
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("POST");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void End(){
        conn.disconnect();
    }
}
