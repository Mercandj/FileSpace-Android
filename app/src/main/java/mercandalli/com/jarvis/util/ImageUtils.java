/**
 * This file is part of Jarvis for Android, an app for managing your server (files, talks...).
 *
 * Copyright (c) 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 *
 * LICENSE:
 *
 * Jarvis for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 *
 * Jarvis for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 */
package mercandalli.com.jarvis.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by Jonathan on 29/05/2015.
 */
public class ImageUtils {

    private static Map<Integer, Bitmap> images = new WeakHashMap<Integer, Bitmap>();

    public synchronized static void save_image(Context context, int fileId, Bitmap bm) {
        images.put(fileId, bm);

        File file = new File(context.getFilesDir()+"/file_"+fileId);
        if(file.exists()) return;

        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(context.getFilesDir()+"/file_"+fileId);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
        }
    }

    public synchronized static Bitmap load_image(Context context, int fileId) {
        if(images.containsKey(fileId))
            return images.get(fileId);

        File file = new File(context.getFilesDir()+"/file_"+fileId);
        if(file.exists())
            return BitmapFactory.decodeFile(file.getPath());
        Log.e("TaskGetDownloadImage", "load_image(String url) return null");
        return null;
    }

    public synchronized static boolean is_image(Context context, int fileId) {
        if(images.containsKey(fileId))
            return true;
        File file = new File(context.getFilesDir()+"/file_"+fileId);
        return file.exists();
    }
}
