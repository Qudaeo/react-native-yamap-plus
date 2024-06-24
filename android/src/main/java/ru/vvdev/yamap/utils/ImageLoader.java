package ru.vvdev.yamap.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;

public class ImageLoader {
    private static int getResId(String resName, Class<?> c) {
        try {
            var idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    private static Bitmap getBitmap(final Context context, final String url) throws IOException {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            var aURL = new URL(url);
            var conn = aURL.openConnection();
            conn.connect();

            var is = conn.getInputStream();
            var bis = new BufferedInputStream(is);
            var bitmap = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();

            return bitmap;
        }

        var id = context.getResources().getIdentifier(url, "drawable", context.getPackageName());

        return BitmapFactory.decodeResource(context.getResources(), id); //getResId(url, R.drawable.class));
    }

    public static void DownloadImageBitmap(final Context context, final String url, final Callback<Bitmap> cb) {
        new Thread() {
            @Override
            public void run() {
                try {
                    final var bitmap = getBitmap(context, url);
                    if (bitmap != null) {
                        new Handler(Looper.getMainLooper()).post(() -> cb.invoke(bitmap));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.start();
    }
}