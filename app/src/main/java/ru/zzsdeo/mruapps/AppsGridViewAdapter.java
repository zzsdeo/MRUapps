package ru.zzsdeo.mruapps;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class AppsGridViewAdapter extends ArrayAdapter<ResolveInfo> {

    private Context mContext;
    private LayoutInflater mInflater;
    private int mResource;
    private static final int ICON_WIDTH = 100;
    private static final int ICON_HEIGHT = 100;

    public AppsGridViewAdapter(Context context, int resource, List<ResolveInfo> objects) {
        super(context, resource, objects);
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = mInflater.inflate(mResource, parent, false);
        }
        TextView tv = (TextView) v.findViewById(R.id.name);
        ImageView iv = (ImageView) v.findViewById(R.id.icon);
        ResolveInfo ri = getItem(position);
        tv.setText(ri.loadLabel(mContext.getPackageManager()));
        iv.setImageBitmap(Utils.convertToBitmap(ri.loadIcon(mContext.getPackageManager()), ICON_WIDTH, ICON_HEIGHT));
        return v;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}