/**
 * This file is part of FileSpace for Android, an app for managing your server (files, talks...).
 * <p>
 * Copyright (c) 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 * <p>
 * LICENSE:
 * <p>
 * FileSpace for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 * <p>
 * FileSpace for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 */
package com.mercandalli.android.apps.files.file;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.mercandalli.android.library.base.precondition.Preconditions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

    private static final String TAG = "FileUtils";

    private static String sSdCardPath = null;

    public static boolean isAudioPath(@NonNull final String pathBrut) {
        final String path = pathBrut.toLowerCase();
        for (final String end : FileTypeModelENUM.AUDIO.type.getExtensions()) {
            if (path.endsWith(end)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isImagePath(@NonNull final String pathBrut) {
        final String path = pathBrut.toLowerCase();
        for (final String end : FileTypeModelENUM.IMAGE.type.getExtensions()) {
            if (path.endsWith(end)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isVideoPath(@NonNull final String pathBrut) {
        final String path = pathBrut.toLowerCase();
        for (final String end : FileTypeModelENUM.VIDEO.type.getExtensions()) {
            if (path.endsWith(end)) {
                return true;
            }
        }
        return false;
    }

    public static String humanReadableByteCount(long bytes) {
        return humanReadableByteCount(bytes, true);
    }

    @SuppressLint("DefaultLocale")
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1_000 : 1_024;
        if (bytes < unit) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static void writeStringFile(final Context context, String file, String txt) {
        try {
            final FileOutputStream output = context.openFileOutput(file, Context.MODE_PRIVATE);
            output.write((txt).getBytes());
            output.close();
        } catch (IOException e) {
            Log.e(TAG, "writeStringFile: Exception", e);
        }
    }

    /**
     * Get the {@link File} content to {@link String}.
     *
     * @param context  The current {@link Context}.
     * @param filePath The {@link File} path.
     * @return The {@link File} content.
     */
    @Nullable
    public static String readStringFile(final Context context, @Nullable final String filePath) {
        Preconditions.checkNotNull(context);
        if (filePath == null) {
            return "";
        }
        String res = null;
        try {
            final FileInputStream input = context.openFileInput(filePath);
            int value;
            @SuppressWarnings("StringBufferMayBeStringBuilder")
            final StringBuffer lu = new StringBuffer();
            while ((value = input.read()) != -1) {
                lu.append((char) value);
            }
            input.close();
            res = lu.toString();
        } catch (IOException e) {
            Log.e(TAG, "readStringFile: Exception", e);
        }
        return res;
    }

    /**
     * Deletes a directory recursively.
     *
     * @param directory directory to delete
     */
    public static boolean deleteDirectory(final File directory) {
        if (directory.isDirectory()) {
            final String[] children = directory.list();
            if (children == null) {
                return directory.delete();
            }
            for (final String str : children) {
                boolean success = deleteDirectory(new File(directory, str));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return directory.delete();
    }

    public static boolean createFile(String path, String name) {
        final int len = path.length();
        if (len < 1 || name.length() < 1) {
            return false;
        }
        if (path.charAt(len - 1) != '/') {
            path += "/";
        }
        if (!name.contains(".")) {
            if (new File(path + name).mkdir()) {
                return true;
            }
        } else {
            try {
                if (new File(path + name).createNewFile()) {
                    return true;
                }
            } catch (IOException e) {
                Log.e(TAG, "Exception", e);
                return false;
            }
        }
        return false;
    }

    @Nullable
    public static String getSdCardPath() {
        if (sSdCardPath != null) {
            if (TextUtils.isEmpty(sSdCardPath)) {
                return null;
            }
            return sSdCardPath;
        }
        final String fileExtPath = (new File(Environment.getExternalStorageDirectory().getAbsolutePath())).getAbsolutePath();
        File sdFile;
        sSdCardPath = "";
        if ((sdFile = new File("/storage/extSdCard")).exists() && !fileExtPath.equalsIgnoreCase(sdFile.getAbsolutePath())) {
            sSdCardPath = "/storage/extSdCard";
        } else if ((sdFile = new File("/storage/sdcard1/")).exists() && !fileExtPath.equalsIgnoreCase(sdFile.getAbsolutePath())) {
            sSdCardPath = "/storage/sdcard/";
        } else if ((sdFile = new File("/storage/usbcard1")).exists() && !fileExtPath.equalsIgnoreCase(sdFile.getAbsolutePath())) {
            sSdCardPath = "/storage/usbcard1";
        }
        return sSdCardPath;
    }

    /**
     * @return True if the external storage is writable. False otherwise.
     */
    public static boolean isSdCardAvailable() {
        return getSdCardPath() != null;
    }

    /**
     * Get the size of the file (file or directory).
     *
     * @param file The file.
     * @return The size in bytes.
     */
    public static long getLocalFolderSize(final File file) {
        if (file == null || !file.exists()) {
            return 0;
        }
        if (file.isDirectory()) {
            final File[] children = file.listFiles();
            if (children == null) {
                return 0;
            }
            long size = 0;
            for (final File f : children) {
                size += getLocalFolderSize(f);
            }
            return size;
        }
        return file.length();
    }
}
