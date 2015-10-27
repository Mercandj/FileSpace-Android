package mercandalli.com.filespace.model.file;

import java.io.File;

/**
 * Created by Jonathan on 18/10/2015.
 */
public class MusicFileModel extends FileModel {

    private String mAlbum;
    private String mArtist;

    public MusicFileModel(File file) {
        super(file);
    }

    public String getAlbum() {
        return mAlbum;
    }

    public String getArtist() {
        return mArtist;
    }

    public void setArtist(final String artist) {
        mArtist = artist;
    }

    public void setAlbum(final String album) {
        mAlbum = album;
    }
}
