package com.daniilvdovin.iswork;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Date;

public class Tools {
    public static class PicassoClear{
        private static final String PICASSO_CACHE = "picasso-cache";

        public static void clearCache(Context context) {
            final File cache = new File(
                    context.getApplicationContext().getCacheDir(),
                    PICASSO_CACHE);
            if (cache.exists()) {
                deleteFolder(cache);
            }
        }
        private static void deleteFolder(File fileOrDirectory) {
            if (fileOrDirectory.isDirectory()) {
                for (File child : fileOrDirectory.listFiles())
                    deleteFolder(child);
            }
            fileOrDirectory.delete();
        }

    }
}
