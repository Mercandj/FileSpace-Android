package mercandalli.com.filespace.model.file;

import java.io.File;

public class FileMusicModel extends FileModel {

    protected String mAlbum;
    protected String mArtist;

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

        public FileMusicModelBuilder fileModel(FileModel fileModel) {
            id(fileModel.getId());
            idUser(fileModel.getIdUser());
            idFileParent(fileModel.getIdFileParent());
            name(fileModel.getName());
            url(fileModel.getUrl());
            size(fileModel.getSize());
            isPublic(fileModel.isPublic());
            type(fileModel.getType());
            isDirectory(fileModel.isDirectory());
            dateCreation(fileModel.getDateCreation());
            isApkUpdate(fileModel.isApkUpdate());
            content(fileModel.getContent());
            file(fileModel.getFile());
            lastModified(fileModel.getLastModified());
            count(fileModel.getCount());
            return this;
        }

        public FileMusicModelBuilder file(final File file) {
            super.file(file);
            return this;
        }

        @Override
        public FileMusicModel build() {
            FileMusicModel fileMusicModel = new FileMusicModel();
            fileMusicModel.mId = id;
            fileMusicModel.mIdUser = idUser;
            fileMusicModel.mIdFileParent = idFileParent;
            fileMusicModel.mName = name;
            fileMusicModel.mUrl = url;
            fileMusicModel.mSize = size;
            fileMusicModel.mIsPublic = isPublic;
            fileMusicModel.mType = type;
            fileMusicModel.mIsDirectory = isDirectory;
            fileMusicModel.mDateCreation = dateCreation;
            fileMusicModel.mIsApkUpdate = isApkUpdate;
            fileMusicModel.mContent = content;
            fileMusicModel.mFile = file;
            fileMusicModel.mLastModified = lastModified;
            fileMusicModel.mCount = count;

            fileMusicModel.mAlbum = album;
            fileMusicModel.mArtist = artist;
            return fileMusicModel;
        }
    }

    public String getAlbum() {
        return mAlbum;
    }

    public String getArtist() {
        return mArtist;
    }

    public String getPath() {
        if (mFile != null) {
            return mFile.getPath();
        } else {
            getOnlineUrl();
        }
        return null;
    }
}
