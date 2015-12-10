/**
 * This file is part of FileSpace for Android, an app for managing your server (files, talks...).
 * <p/>
 * Copyright (c) 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 * <p/>
 * LICENSE:
 * <p/>
 * FileSpace for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 * <p/>
 * FileSpace for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 */
package com.mercandalli.android.apps.files.file;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by Jonathan on 15/05/2015.
 */
public class FileUtils {

    public static String humanReadableByteCount(long bytes) {
        return humanReadableByteCount(bytes, true);
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static void writeStringFile(Context context, String file, String txt) {
        try {
            FileOutputStream output = context.openFileOutput(file, Context.MODE_PRIVATE);
            output.write((txt).getBytes());
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readStringFile(Context context, String file) {
        String res = "";
        try {
            FileInputStream input = context.openFileInput(file);
            int value;
            StringBuffer lu = new StringBuffer();
            while ((value = input.read()) != -1) {
                lu.append((char) value);
            }
            input.close();
            res = lu.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static String readStringAssets(Context context, String file) {
        Writer writer = new StringWriter();
        try {
            InputStream is = context.getResources().getAssets().open(file);
            char[] buffer = new char[2048];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    /**
     * Deletes a directory recursively.
     *
     * @param directory directory to delete
     */
    public static boolean deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            String[] children = directory.list();
            for (String str : children) {
                boolean success = deleteDirectory(new File(directory, str));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return directory.delete();
    }

    public static String getExtensionFromPath(String path) {
        if (!path.contains(".")) {
            return "";
        }
        return path.substring(path.lastIndexOf(".") + 1);
    }

    public static String getNameFromPath(String path) {
        if (!path.contains("/")) {
            return "";
        }
        if (path.endsWith("/")) {
            return getNameFromPath(path.substring(0, path.length() - 1));
        }
        return path.substring(path.lastIndexOf("/") + 1);
    }

    public static String getParentPathFromPath(String path) {
        if (!path.contains("/")) {
            return "";
        }
        if (path.endsWith("/")) {
            return getParentPathFromPath(path.substring(0, path.length() - 1));
        }
        return path.substring(0, path.lastIndexOf("/"));
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
