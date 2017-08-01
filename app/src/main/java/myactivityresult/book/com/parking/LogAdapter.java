package myactivityresult.book.com.parking;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class LogAdapter extends CursorAdapter {
    public LogAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView entered_txt = (TextView) view.findViewById(R.id.entered_txt);
        TextView exited_txt = (TextView) view.findViewById(R.id.exited_txt);
        String entered_at = cursor.getString(cursor.getColumnIndex(TimeLogTable.Start));
        String exited_at = cursor.getString(cursor.getColumnIndex(TimeLogTable.End));
        entered_txt.setText(entered_at);
        exited_txt.setText(exited_at);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.logview_adapter, parent, false);
        return view;
    }
}
