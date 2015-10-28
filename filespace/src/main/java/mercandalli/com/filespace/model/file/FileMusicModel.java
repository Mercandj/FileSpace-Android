package mercandalli.com.filespace.model.file;

/**
 * Created by Jonathan on 18/10/2015.
 */
public class FileMusicModel extends FileModel {

    private String mAlbum;
    private String mArtist;

    public static class FileMusicModelBuilder extends FileModelBuilder {

        private String album;
        private String artist;

        public FileMusicModelBuilder album(String album) {
            this.album = album;
            return this;
        }

        public FileMusicModelBuilder artist(String artist) {
            this.artist = artist;
            return this;
        }

        public FileMusicModel build() {
            FileMusicModel fileMusicModel = (FileMusicModel) super.build();
            fileMusicModel.setAlbum(album);
            fileMusicModel.setArtist(artist);
            return fileMusicModel;
        }
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
