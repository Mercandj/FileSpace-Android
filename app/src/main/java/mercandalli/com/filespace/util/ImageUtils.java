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
package mercandalli.com.filespace.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.WeakHashMap;

import mercandalli.com.filespace.config.Const;

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
        if(file.exists()) {

            int desiredWidth = Const.WIDTH_MAX_ONLINE_PICTURE_BITMAP;

            // Get the source image's dimensions
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file.getPath(), options);

            int srcWidth = options.outWidth;

            // Only scale if the source is big enough. This code is just trying to fit a image into a certain width.
            if(desiredWidth > srcWidth)
                desiredWidth = srcWidth;

            // Calculate the correct inSampleSize/scale value. This helps reduce memory use. It should be a power of 2
            // from: http://stackoverflow.com/questions/477572/android-strange-out-of-memory-issue/823966#823966
            int inSampleSize = 1;
            while(srcWidth / 2 > desiredWidth){
                srcWidth /= 2;
                inSampleSize *= 2;
            }

            float desiredScale = (float) desiredWidth / srcWidth;

            // Decode with inSampleSize
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inSampleSize = inSampleSize;
            options.inScaled = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap sampledSrcBitmap = BitmapFactory.decodeFile(file.getPath(), options);

            // Resize
            Matrix matrix = new Matrix();
            matrix.postScale(desiredScale, desiredScale);
            return Bitmap.createBitmap(sampledSrcBitmap, 0, 0, sampledSrcBitmap.getWidth(), sampledSrcBitmap.getHeight(), matrix, true);

            //return BitmapFactory.decodeFile(file.getPath(), options);
        }
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
