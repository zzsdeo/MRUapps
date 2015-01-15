package ru.zzsdeo.mruapps;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class Utils {

    public static final String ICON_CACHE_SUBDIR = "icon_cache";
    private static final int ICON_WIDTH = 100;
    private static final int ICON_HEIGHT = 100;

    public static Bitmap convertToBitmap(Drawable drawable, int widthPixels, int heightPixels) {
        Bitmap mutableBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mutableBitmap);
        drawable.setBounds(0, 0, widthPixels, heightPixels);
        drawable.draw(canvas);
        return mutableBitmap;
    }

    /*public static boolean createCache (Context context) {
        boolean done = false;
        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> activities = context.getPackageManager().queryIntentActivities(startupIntent, 0);
        File dir = new File(context.getFilesDir(), File.separator + ICON_CACHE_SUBDIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        for (ResolveInfo ri : activities) {
            String filename = ri.activityInfo.applicationInfo.packageName;
            Bitmap bitmap = convertToBitmap(ri.loadIcon(context.getPackageManager()), 100, 100);
            File file = new File(dir, filename);

            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (Exception e) {
                done = false;
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                        done = true;
                    }
                } catch (IOException e) {
                    done = false;
                    e.printStackTrace();
                }
            }
        }
        Log.d("my", String.valueOf(done));
        return done;
    }*/

    public static Bitmap createCachedIcon (Context context, ResolveInfo ri) {
        File dir = new File(context.getFilesDir(), File.separator + ICON_CACHE_SUBDIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String filename = ri.activityInfo.applicationInfo.packageName;
        Bitmap bitmap = convertToBitmap(ri.loadIcon(context.getPackageManager()), ICON_WIDTH, ICON_HEIGHT);
        File file = new File(dir, filename);

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }
}