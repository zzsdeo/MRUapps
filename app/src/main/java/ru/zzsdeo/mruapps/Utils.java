package ru.zzsdeo.mruapps;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Utils {

    public static final int ICON_WIDTH = 100;
    public static final int ICON_HEIGHT = 100;

    public static Bitmap convertToBitmap(Drawable drawable, int widthPixels, int heightPixels) {
        Bitmap mutableBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mutableBitmap);
        drawable.setBounds(0, 0, widthPixels, heightPixels);
        drawable.draw(canvas);
        return mutableBitmap;
    }

    public static File createIconFile (Context context, ResolveInfo ri) {
        String filename = ri.activityInfo.applicationInfo.packageName;
        Bitmap bitmap = convertToBitmap(ri.loadIcon(context.getPackageManager()), ICON_WIDTH, ICON_HEIGHT);
        File file = new File(context.getCacheDir(), filename);

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
        return file;
    }
}