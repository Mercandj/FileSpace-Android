package mercandalli.com.filespace.models;

import java.io.File;

import mercandalli.com.filespace.ui.activities.ApplicationActivity;
import mercandalli.com.filespace.utils.StringUtils;

/**
 * Created by Jonathan on 18/10/2015.
 */
public class MusicModelFile extends ModelFile {

    private String mAlbum;
    private String mArtist;

    public MusicModelFile(ApplicationActivity app, File file) {
        super(app, file);
    }

    public MusicModelFile(ApplicationActivity app, ModelFile file) {
        super(app, file.getFile());
    }

    @Override
    public String getAdapterSubtitle() {
        if (!StringUtils.isNullOrEmpty(mAlbum) && !StringUtils.isNullOrEmpty(mArtist)) {
            return mArtist + " - " + mAlbum;
        }
        if (!StringUtils.isNullOrEmpty(mArtist)) {
            return mArtist;
        }
        if (!StringUtils.isNullOrEmpty(mAlbum)) {
            return mAlbum;
        }
        return super.getAdapterSubtitle();
    }

    public void setArtist(final String artist) {
        mArtist = artist;
    }

    public void setAlbum(final String album) {
        mAlbum = album;
    }
}
