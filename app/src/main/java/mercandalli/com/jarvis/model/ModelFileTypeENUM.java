/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis.model;

public enum ModelFileTypeENUM {
	APK(new ModelFileType("Android application", "apk")),
	TEXT(new ModelFileType("Text", new String[] {"txt", "doc", "csv", "rtf", "text", "json"})),
	HTML(new ModelFileType("Html", new String[] {"html", "htm", "php", "xml"})),
	PICTURE(new ModelFileType("Picture", new String[] {"jpeg",	"jpg", "png", "gif", "raw",	"psd", "bmp", "tiff", "tif"})),
	AUDIO(new ModelFileType("Audio", new String[] {"mp3", "wav", "m4a", "aiff", "wma", "caf", "flac", "m4p", "amr"})),
	VIDEO(new ModelFileType("Video", new String[] {"m4v", "3gp", "wmv", "mp4", "mpeg", "mpg", "rm", "mov", "avi", "mkv", "flv", "ogg", "wav"})),
	ZIP(new ModelFileType("Zip", "zip")),
	GZIP(new ModelFileType("Gzip", new String[] {"gzip", "rar", "tar", "tar.gz", "gz"})),
    JARVIS(new ModelFileType("Jarvis", new String[] {"jarvis"})),
    WORD(new ModelFileType("Word", new String[] {"docx"})),
    EXCEL(new ModelFileType("Excel", new String[] {"xlsx"})),
    PDF(new ModelFileType("Pdf","pdf"));
	
	public ModelFileType type;
	
	private ModelFileTypeENUM(ModelFileType type) {
		this.type = type;
	}
}
