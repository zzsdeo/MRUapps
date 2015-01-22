package ru.zzsdeo.mruapps;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class Utils {

    public static final int ICON_WIDTH = 100;
    public static final int ICON_HEIGHT = 100;
    public static final String ICONS_DIR = "icons";

    public static Bitmap convertToBitmap(Drawable drawable, int widthPixels, int heightPixels) {
        Bitmap mutableBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mutableBitmap);
        drawable.setBounds(0, 0, widthPixels, heightPixels);
        drawable.draw(canvas);
        return mutableBitmap;
    }

    public static void createIcon (Context context, ResolveInfo ri) {
        File dir = new File(context.getFilesDir(), File.separator + ICONS_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String filename = ri.activityInfo.name;
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
    }

    public static Bitmap getIcon (Context context, String imageName) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        String path = new File(context.getFilesDir() + File.separator + ICONS_DIR, imageName).getAbsolutePath();
        return BitmapFactory.decodeFile(path, options);
    }

    public static void deleteIcon (Context context, String filename) {
        File dir = new File(context.getFilesDir(), File.separator + ICONS_DIR);
        File file = new File(dir, filename);
        file.delete();
    }
}