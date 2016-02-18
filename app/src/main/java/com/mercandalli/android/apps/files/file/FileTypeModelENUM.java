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


import com.mercandalli.android.apps.files.R;

public enum FileTypeModelENUM {
    APK(new FileTypeModel(R.string.file_model_type_apk, "apk")),
    TEXT(new FileTypeModel(R.string.file_model_type_text, new String[]{"txt", "csv", "rtf", "text", "json"})),
    HTML(new FileTypeModel(R.string.file_model_type_html, new String[]{"html", "htm", "php", "xml"})),
    PICTURE(new FileTypeModel(R.string.file_model_type_picture, new String[]{"jpeg", "jpg", "png", "gif", "raw", "psd", "bmp", "tiff", "tif"})),
    AUDIO(new FileTypeModel(R.string.file_model_type_audio, new String[]{"mp3", "wav", "m4a", "aiff", "wma", "caf", "flac", "m4p", "amr", "ogg"})),
    VIDEO(new FileTypeModel(R.string.file_model_type_video, new String[]{"m4v", "3gp", "wmv", "mp4", "mpeg", "mpg", "rm", "mov", "avi", "mkv", "flv", "ogg"})),
    ARCHIVE(new FileTypeModel(R.string.file_model_type_archive, new String[]{"zip", "gzip", "rar", "tar", "tar.gz", "gz", "7z"})),
    FILESPACE(new FileTypeModel(R.string.file_model_type_file_space, new String[]{"filespace", "jarvis"})),
    WORD(new FileTypeModel(R.string.file_model_type_word, new String[]{"doc", "docx"})),
    OPEN_DOCUMENT(new FileTypeModel(R.string.file_model_type_open_document, new String[]{"odp"})),
    POWERPOINT(new FileTypeModel(R.string.file_model_type_power_point, new String[]{"ppt", "pptx"})),
    EXCEL(new FileTypeModel(R.string.file_model_type_excel, new String[]{"xlsx"})),
    VCF(new FileTypeModel(R.string.file_model_type_vcf, new String[]{"vcf"})),
    PDF(new FileTypeModel(R.string.file_model_type_pdf, "pdf")),
    SOURCE(new FileTypeModel(R.string.file_model_type_source, new String[]{"java", "c", "cs", "cpp", "sql", "php", "html", "js", "css"})),
    THREE_D(new FileTypeModel(R.string.file_model_type_3d, new String[]{"3ds", "obj", "max"})),
    ISO(new FileTypeModel(R.string.file_model_type_iso, new String[]{"iso"})),
    TMP(new FileTypeModel(R.string.file_model_type_tmp, new String[]{"tmp"})),
    INDEX(new FileTypeModel(R.string.file_model_type_index, new String[]{"idx"})),
    KEYSTORE(new FileTypeModel(R.string.file_model_type_keystore, new String[]{"keystore", "jdk"})),
    TRACE(new FileTypeModel(R.string.file_model_type_trace, new String[]{"trace"})),
    DIRECTORY(new FileTypeModel(R.string.file_model_type_directory, new String[]{"dir", ""}));

    public final FileTypeModel type;

    FileTypeModelENUM(FileTypeModel type) {
        this.type = type;
    }
}
