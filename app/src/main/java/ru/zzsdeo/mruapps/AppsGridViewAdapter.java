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

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class AppsGridViewAdapter extends ArrayAdapter<ResolveInfo> {

    private Context mContext;
    private List<ResolveInfo> mObjects;
    private int mResource;
    private BitmapLruCache mBitmapLruCache;
    private Set<String> inProgressSet = Collections.synchronizedSet(new HashSet<String>());

    public AppsGridViewAdapter(Context context, int resource, List<ResolveInfo> objects) {
        super(context, resource, objects);
        mContext = context;
        mObjects = objects;
        mResource = resource;
        mBitmapLruCache = new BitmapLruCache();
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
        String pkg = mObjects.get(position).activityInfo.applicationInfo.packageName;
        new ImageFetcher(pkg, holder.imageView, mObjects.get(position)).execute();

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

    class ImageFetcher extends AsyncTask<Void, Void, Bitmap> {

        private String imageName;
        private ImageView imageView;
        private ResolveInfo ri;

        ImageFetcher(String imageName, ImageView imageView, ResolveInfo ri) {
            this.imageName = imageName;
            this.imageView = imageView;
            this.ri = ri;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            if (inProgressSet.contains(imageName)) {
                return null;
            }

            Bitmap icon = mBitmapLruCache.getBitmapFromMemCache(imageName);
            if (icon == null) {
                inProgressSet.add(imageName);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                String path = new File(mContext.getFilesDir() + File.separator + Utils.ICON_CACHE_SUBDIR, imageName).getAbsolutePath();
                icon = BitmapFactory.decodeFile(path, options);
                if (icon == null) {
                    icon = Utils.createCachedIcon(mContext, ri);
                }
            }
            return icon;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap == null) return;
            inProgressSet.remove(imageName);
            mBitmapLruCache.addBitmapToMemoryCache(imageName, bitmap);
            imageView.setImageBitmap(bitmap);
        }
    }
}