package ru.zzsdeo.mruapps;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class AppsGridViewAdapter extends ArrayAdapter<ResolveInfo> {

    private Context mContext;
    private List<ResolveInfo> mObjects;
    private int mResource;
    private Picasso mPicasso;

    public AppsGridViewAdapter(Context context, int resource, List<ResolveInfo> objects) {
        super(context, resource, objects);
        mContext = context;
        mObjects = objects;
        mResource = resource;
        mPicasso = Picasso.with(context);
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
        File file = new File(mContext.getCacheDir(), mObjects.get(position).activityInfo.applicationInfo.packageName);
        if (!file.exists()) {
            file = Utils.createIconFile(mContext, mObjects.get(position));
        }
        mPicasso.load(file).into(holder.imageView);
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