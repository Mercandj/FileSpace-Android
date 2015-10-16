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
package mercandalli.com.filespace.models;

public enum ModelFileTypeENUM {
    APK(new ModelFileType("Android application", "apk")),
    TEXT(new ModelFileType("Text", new String[]{"txt", "csv", "rtf", "text", "json"})),
    HTML(new ModelFileType("Html", new String[]{"html", "htm", "php", "xml"})),
    PICTURE(new ModelFileType("Picture", new String[]{"jpeg", "jpg", "png", "gif", "raw", "psd", "bmp", "tiff", "tif"})),
    AUDIO(new ModelFileType("Audio", new String[]{"mp3", "wav", "m4a", "aiff", "wma", "caf", "flac", "m4p", "amr"})),
    VIDEO(new ModelFileType("Video", new String[]{"m4v", "3gp", "wmv", "mp4", "mpeg", "mpg", "rm", "mov", "avi", "mkv", "flv", "ogg"})),
    ARCHIVE(new ModelFileType("Archive", new String[]{"zip", "gzip", "rar", "tar", "tar.gz", "gz", "7z"})),
    FILESPACE(new ModelFileType("FileSpace", new String[]{"filespace", "jarvis"})),
    WORD(new ModelFileType("Word", new String[]{"doc", "docx"})),
    OPEN_DOCUMENT(new ModelFileType("OpenDocument", new String[]{"odp"})),
    POWERPOINT(new ModelFileType("Power Point", new String[]{"ppt", "pptx"})),
    EXCEL(new ModelFileType("Excel", new String[]{"xlsx"})),
    VCF(new ModelFileType("Business card", new String[]{"vcf"})),
    PDF(new ModelFileType("Pdf", "pdf")),
    SOURCE(new ModelFileType("Source code", new String[]{"java", "c", "cs", "cpp", "sql", "php", "html", "js", "css"})),
    THREE_D(new ModelFileType("3d model", new String[]{"3ds", "obj", "max"})),
    ISO(new ModelFileType("Optical disc", new String[]{"iso"})),
    KEYSTORE(new ModelFileType("KeyStore", new String[]{"keystore", "jdk"})),
    DIRECTORY(new ModelFileType("Directory", new String[]{"dir", ""}));

    public final ModelFileType type;

    ModelFileTypeENUM(ModelFileType type) {
        this.type = type;
    }
}
