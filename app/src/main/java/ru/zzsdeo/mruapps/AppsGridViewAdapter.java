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

    Context mContext;
    LayoutInflater mInflater;
    int mResource;

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
        iv.setImageDrawable(ri.loadIcon(mContext.getPackageManager()));
        return v;
    }
}