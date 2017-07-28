package myactivityresult.book.com.parking;

import android.os.AsyncTask;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;

public class ProgressBarTask extends AsyncTask<Integer, Integer, Void> {
    ProgressBar progressBar;
    WebView webView;
    String url;

    ProgressBarTask(String url, WebView webView, ProgressBar progressBar){
        this.url = url;
        this.webView = webView;
        this.progressBar = progressBar;
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
        webView.setVisibility(View.GONE);
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
