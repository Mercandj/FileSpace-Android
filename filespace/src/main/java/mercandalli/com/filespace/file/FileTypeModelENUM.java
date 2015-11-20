/**
 * This file is part of Jarvis for Android, an app for managing your server (files, talks...).
 * <p/>
 * Copyright (c) 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 * <p/>
 * LICENSE:
 * <p/>
 * Jarvis for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 * <p/>
 * Jarvis for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 */
package mercandalli.com.filespace.file;


public enum FileTypeModelENUM {
    APK(new FileTypeModel("Android application", "apk")),
    TEXT(new FileTypeModel("Text", new String[]{"txt", "csv", "rtf", "text", "json"})),
    HTML(new FileTypeModel("Html", new String[]{"html", "htm", "php", "xml"})),
    PICTURE(new FileTypeModel("Picture", new String[]{"jpeg", "jpg", "png", "gif", "raw", "psd", "bmp", "tiff", "tif"})),
    AUDIO(new FileTypeModel("Audio", new String[]{"mp3", "wav", "m4a", "aiff", "wma", "caf", "flac", "m4p", "amr"})),
    VIDEO(new FileTypeModel("Video", new String[]{"m4v", "3gp", "wmv", "mp4", "mpeg", "mpg", "rm", "mov", "avi", "mkv", "flv", "ogg"})),
    ARCHIVE(new FileTypeModel("Archive", new String[]{"zip", "gzip", "rar", "tar", "tar.gz", "gz", "7z"})),
    FILESPACE(new FileTypeModel("FileSpace", new String[]{"filespace", "jarvis"})),
    WORD(new FileTypeModel("Word", new String[]{"doc", "docx"})),
    OPEN_DOCUMENT(new FileTypeModel("OpenDocument", new String[]{"odp"})),
    POWERPOINT(new FileTypeModel("Power Point", new String[]{"ppt", "pptx"})),
    EXCEL(new FileTypeModel("Excel", new String[]{"xlsx"})),
    VCF(new FileTypeModel("Business card", new String[]{"vcf"})),
    PDF(new FileTypeModel("Pdf", "pdf")),
    SOURCE(new FileTypeModel("Source code", new String[]{"java", "c", "cs", "cpp", "sql", "php", "html", "js", "css"})),
    THREE_D(new FileTypeModel("3d model", new String[]{"3ds", "obj", "max"})),
    ISO(new FileTypeModel("Optical disc", new String[]{"iso"})),
    KEYSTORE(new FileTypeModel("KeyStore", new String[]{"keystore", "jdk"})),
    DIRECTORY(new FileTypeModel("Directory", new String[]{"dir", ""}));

    public final FileTypeModel type;

    FileTypeModelENUM(FileTypeModel type) {
        this.type = type;
    }
}
