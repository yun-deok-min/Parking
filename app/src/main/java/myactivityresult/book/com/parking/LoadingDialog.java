package myactivityresult.book.com.parking;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class LoadingDialog extends AsyncTask<Void, Void, Void> {
    ProgressDialog waitDialog;
    Context context;
    Boolean isEnd;

    LoadingDialog(Context context) {
        this.context = context;
        isEnd = false;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        waitDialog = new ProgressDialog(context);
        waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        waitDialog.setMessage("잠시만 기다려 주세요");
        waitDialog.show();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        waitDialog.dismiss();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        while (!isEnd){ }
        return null;
    }

    public void setIsEnd(){ isEnd = true; }
}

