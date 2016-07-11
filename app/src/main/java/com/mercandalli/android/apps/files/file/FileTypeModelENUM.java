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


import android.support.annotation.StringDef;

import com.mercandalli.android.apps.files.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public enum FileTypeModelENUM {

    APK(new FileTypeModel(R.string.file_model_type_apk, "apk")),
    TEXT(new FileTypeModel(R.string.file_model_type_text, "txt", "csv", "rtf", "text", "json")),
    IMAGE(new FileTypeModel(R.string.file_model_type_picture, "jpeg", "jpg", "png", "gif", "raw", "psd", "bmp", "tiff", "tif")),
    AUDIO(new FileTypeModel(R.string.file_model_type_audio, "mp3", "wav", "m4a", "aiff", "wma", "caf", "flac", "m4p", "amr", "ogg")),
    VIDEO(new FileTypeModel(R.string.file_model_type_video, "m4v", "3gp", "wmv", "mp4", "mpeg", "mpg", "rm", "mov", "avi", "mkv", "flv", "ogg", "webm")),
    ARCHIVE(new FileTypeModel(R.string.file_model_type_archive, "zip", "gzip", "rar", "tar", "tar.gz", "gz", "7z")),
    FILESPACE(new FileTypeModel(R.string.file_model_type_file_space, "filespace", "jarvis")),
    WORD(new FileTypeModel(R.string.file_model_type_word, "doc", "docx")),
    OPEN_DOCUMENT(new FileTypeModel(R.string.file_model_type_open_document, "odp")),
    POWERPOINT(new FileTypeModel(R.string.file_model_type_power_point, "ppt", "pptx")),
    EXCEL(new FileTypeModel(R.string.file_model_type_excel, "xlsx")),
    VCF(new FileTypeModel(R.string.file_model_type_vcf, "vcf")),
    PDF(new FileTypeModel(R.string.file_model_type_pdf, "pdf")),
    N64(new FileTypeModel(R.string.file_model_type_rom_n64, "n64", "z64")),
    GB(new FileTypeModel(R.string.file_model_type_rom_gb, "gb")),
    GBC(new FileTypeModel(R.string.file_model_type_rom_gbc, "gbc")),
    GBA(new FileTypeModel(R.string.file_model_type_rom_gba, "gba")),

    // Dev
    SOURCE(new FileTypeModel(R.string.file_model_type_source, "c", "cs", "cpp", "sql", "php", "html", "js", "css", "ec")),
    SOURCE_JAVA(new FileTypeModel(R.string.file_model_type_source_java, "java", "class")),
    SOURCE_HTML(new FileTypeModel(R.string.file_model_type_html, "html", "htm", "php", "xml")),
    NO_MEDIA(new FileTypeModel(R.string.file_model_type_nomedia, "nomedia")),
    THREE_D(new FileTypeModel(R.string.file_model_type_3d, "3ds", "obj", "max")),
    ISO(new FileTypeModel(R.string.file_model_type_iso, "iso")),
    TMP(new FileTypeModel(R.string.file_model_type_tmp, "tmp")),
    INDEX(new FileTypeModel(R.string.file_model_type_index, "idx")),
    KEYSTORE(new FileTypeModel(R.string.file_model_type_keystore, "keystore", "jdk")),
    TRACE(new FileTypeModel(R.string.file_model_type_trace, "trace")),
    DATABASE(new FileTypeModel(R.string.file_model_type_database, "db")),

    DIRECTORY(new FileTypeModel(R.string.file_model_type_directory, "dir", ""));

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            NOT_OPEN,
            OPEN_AS_AUDIO,
            OPEN_AS_IMAGE,
            OPEN_AS_TEXT,
            OPEN_AS_HTML,
            OPEN_AS_VIDEO,
            OPEN_AS_APK,
            OPEN_AS_PDF,
            OPEN_AS_WORD,
            OPEN_AS_FILESPACE})
    public @interface OpenAs {
    }

    public static final String NOT_OPEN = "";
    public static final String OPEN_AS_AUDIO = "audio/*";
    public static final String OPEN_AS_IMAGE = "image/*";
    public static final String OPEN_AS_TEXT = "text/*";
    public static final String OPEN_AS_HTML = "text/html";
    public static final String OPEN_AS_VIDEO = "video/*";
    public static final String OPEN_AS_APK = "application/vnd.android.package-archive";
    public static final String OPEN_AS_PDF = "application/pdf";
    public static final String OPEN_AS_WORD = "application/msword";
    public static final String OPEN_AS_FILESPACE = "text/filespace";

    public final FileTypeModel type;

    FileTypeModelENUM(FileTypeModel type) {
        this.type = type;
    }

    /**
     * Get the mime type.
     *
     * @param type The {@link FileTypeModel}.
     * @return The mime.
     */
    @OpenAs
    public static String openAs(final FileTypeModel type) {
        if (SOURCE.type.equals(type) ||
                SOURCE_JAVA.type.equals(type) ||
                DATABASE.type.equals(type) ||
                TEXT.type.equals(type)) {
            return OPEN_AS_TEXT;
        }
        if (SOURCE_HTML.type.equals(type)) {
            return OPEN_AS_HTML;
        }
        if (PDF.type.equals(type)) {
            return OPEN_AS_PDF;
        }
        if (VIDEO.type.equals(type)) {
            return OPEN_AS_VIDEO;
        }
        if (AUDIO.type.equals(type)) {
            return OPEN_AS_AUDIO;
        }
        if (APK.type.equals(type)) {
            return OPEN_AS_APK;
        }
        if (IMAGE.type.equals(type)) {
            return OPEN_AS_IMAGE;
        }
        if (FILESPACE.type.equals(type)) {
            return OPEN_AS_FILESPACE;
        }
        return NOT_OPEN;
    }
}
