package ru.zzsdeo.mruapps;

import android.app.Activity;
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

    /*private Context mContext;
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
    }*/
    private Context mContext;
    private static final int ICON_WIDTH = 100;
    private static final int ICON_HEIGHT = 100;
    private List<ResolveInfo> mObjects;
    private int mResource;

    public AppsGridViewAdapter(Context context, int resource, List<ResolveInfo> objects) {
        super(context, resource, objects);
        mContext = context;
        mObjects = objects;
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            rowView = inflater.inflate(mResource, null, true);
            holder = new ViewHolder();
            holder.textView = (TextView) rowView.findViewById(R.id.name);
            holder.imageView = (ImageView) rowView.findViewById(R.id.icon);
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        holder.textView.setText(mObjects.get(position).loadLabel(mContext.getPackageManager()));
        holder.imageView.setImageBitmap(Utils.convertToBitmap(mObjects.get(position).loadIcon(mContext.getPackageManager()), ICON_WIDTH, ICON_HEIGHT));

        return rowView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    static class ViewHolder {
        public ImageView imageView;
        public TextView textView;
    }
}