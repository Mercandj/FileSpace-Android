package mercandalli.com.filespace.model;

import android.app.Activity;

import java.io.File;

import mercandalli.com.filespace.ui.activitiy.ApplicationCallback;
import mercandalli.com.filespace.util.StringUtils;

/**
 * Created by Jonathan on 18/10/2015.
 */
public class MusicModelFile extends ModelFile {

    private String mAlbum;
    private String mArtist;

    public MusicModelFile(Activity activity, ApplicationCallback app, File file) {
        super(activity, app, file);
    }

    public MusicModelFile(Activity activity, ApplicationCallback app, ModelFile file) {
        super(activity, app, file.getFile());
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
