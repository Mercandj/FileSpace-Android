/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis.model;

public enum ModelFileTypeENUM {
	APK(new ModelFileType("apk")),
	TEXT(new ModelFileType(new String[] {"txt", "doc", "csv", "rtf", "text", "json"})),
	HTML(new ModelFileType(new String[] {"html", "htm", "php", "xml"})),
	PICTURE(new ModelFileType(new String[] {"jpeg",	"jpg", "png", "gif", "raw",	"psd", "bmp", "tiff", "tif"})),
	AUDIO(new ModelFileType(new String[] {"mp3", "wav", "m4a", "aiff", "wma", "caf", "flac", "m4p", "amr"})),
	VIDEO(new ModelFileType(new String[] {"m4v", "3gp", "wmv", "mp4", "mpeg", "mpg", "rm", "mov", "avi", "mkv", "flv", "ogg", "wav"})),
	ZIP(new ModelFileType("zip")),
	GZIP(new ModelFileType(new String[] {"gzip", "rar", "tar", "tar.gz", "gz"})),
    JARVIS(new ModelFileType(new String[] {"jarvis"})),
	PDF(new ModelFileType("pdf"));
	
	public ModelFileType type;
	
	private ModelFileTypeENUM(ModelFileType type) {
		this.type = type;
	}
}
