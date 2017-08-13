package myactivityresult.book.com.parking;

import android.os.AsyncTask;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;

/* 작업 처리가 오래 걸릴 경우 처리중 임을 알리기 위한 클래스 */
public class ProgressBarTask extends AsyncTask<Integer, Integer, Void> {
    HttpURLConnector conn;
    ProgressBar progressBar;
    WebView webView;
    String url;

    ProgressBarTask(String url, WebView webView, ProgressBar progressBar){  // 조감도 화면 생성자
        this.url = url;
        this.webView = webView;
        this.progressBar = progressBar;
    }

    ProgressBarTask(String url){
        conn = new HttpURLConnector(url);
    }

    @Override
    protected Void doInBackground(Integer... params) {
        try{
            Thread.sleep(4000);
        }catch(InterruptedException e){}
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
