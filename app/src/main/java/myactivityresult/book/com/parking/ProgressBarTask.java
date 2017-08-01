package myactivityresult.book.com.parking;

import android.os.AsyncTask;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;

public class ProgressBarTask extends AsyncTask<Integer, Integer, Void> {
    HttpURLConnector conn;
    ProgressBar progressBar;
    WebView webView;
    String url;
    String result;
    final static int GET = 1000;
    final static int POST = 1001;

    ProgressBarTask(String url, WebView webView, ProgressBar progressBar){  // 조감도 화면 생성자
        this.url = url;
        this.webView = webView;
        this.progressBar = progressBar;
    }

    ProgressBarTask(String url){
        conn = new HttpURLConnector(url, GET);
    }

    @Override
    protected Void doInBackground(Integer... params) {
        try{
            Thread.sleep(4000);
        }catch(InterruptedException e){}

        if(conn.equals(null)) {
            conn.start();
            try {
                conn.join();
            } catch (InterruptedException e) { }
            result = conn.getResult();
            JSONParser parser = new JSONParser(result);
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressBar.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
        webView.loadUrl(url);
    }
}
