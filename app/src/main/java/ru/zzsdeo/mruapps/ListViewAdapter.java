package ru.zzsdeo.mruapps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.SpannableStringBuilder;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class ListViewAdapter extends CursorAdapter {

    private LayoutInflater mInflater;

    public ListViewAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return mInflater.inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView tv = (TextView) view.findViewById(R.id.item);
        final CheckBox cb = (CheckBox) view.findViewById(R.id.checkBoxItem);

        tv.setText(cursor.getString(cursor.getColumnIndex(StatisticTable.COLUMN_APP_NAME)));
        if (cursor.getInt(cursor.getColumnIndex(StatisticTable.COLUMN_IGNORE)) == 0) {
            cb.setChecked(true);
        } else {
            cb.setChecked(false);
        }

        cb.setTag(cursor.getInt(cursor.getColumnIndex(StatisticTable.COLUMN_ID)));
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Object obj = cb.getTag();
                if (obj != null && obj instanceof Integer) {
                    ContentValues values = new ContentValues();
                    if (cb.isChecked()) {
                        values.put(StatisticTable.COLUMN_IGNORE, 0);
                    } else {
                        values.put(StatisticTable.COLUMN_IGNORE, 1);
                    }
                    context.getContentResolver().update(DBContentProvider.CONTENT_URI, values, StatisticTable.COLUMN_ID + " = " + obj, null);
                    values.clear();
                }
            }
        });
    }
}
