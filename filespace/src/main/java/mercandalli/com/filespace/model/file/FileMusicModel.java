package mercandalli.com.filespace.model.file;

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
            FileMusicModel fileMusicModel = new FileMusicModel();
            fileMusicModel.setId(id);
            fileMusicModel.setIdUser(idUser);
            fileMusicModel.setIdFileParent(idFileParent);
            fileMusicModel.setName(name);
            fileMusicModel.setUrl(url);
            fileMusicModel.setSize(size);
            fileMusicModel.setPublic(isPublic);
            fileMusicModel.setType(type);
            fileMusicModel.setIsDirectory(isDirectory);
            fileMusicModel.setDateCreation(dateCreation);
            fileMusicModel.setIsApkUpdate(isApkUpdate);
            fileMusicModel.setContent(content);
            fileMusicModel.setFile(file);
            fileMusicModel.setLastModified(lastModified);
            fileMusicModel.setCount(count);

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
