package com.mercandalli.android.apps.files.file.audio;

import com.mercandalli.android.apps.files.file.FileModel;

import java.io.File;

public class FileAudioModel extends FileModel {

    protected String mAlbum;
    protected String mArtist;

    public static class FileMusicModelBuilder extends FileModelBuilder {
        private String mAlbum;
        private String mArtist;

        public FileMusicModelBuilder album(String album) {
            this.mAlbum = album;
            return this;
        }

        public FileMusicModelBuilder artist(String artist) {
            this.mArtist = artist;
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
        public FileAudioModel build() {
            FileAudioModel fileAudioModel = new FileAudioModel();
            fileAudioModel.mId = mId;
            fileAudioModel.mIdUser = mIdUser;
            fileAudioModel.mIdFileParent = mIdFileParent;
            fileAudioModel.mName = mName;
            fileAudioModel.mUrl = mUrl;
            fileAudioModel.mSize = mSize;
            fileAudioModel.mIsPublic = mIsPublic;
            fileAudioModel.mType = mType;
            fileAudioModel.mIsDirectory = mIsDirectory;
            fileAudioModel.mDateCreation = mDateCreation;
            fileAudioModel.mIsApkUpdate = mIsApkUpdate;
            fileAudioModel.mContent = mContent;
            fileAudioModel.mFile = mFile;
            fileAudioModel.mLastModified = mLastModified;
            fileAudioModel.mCount = mCount;

            fileAudioModel.mAlbum = mAlbum;
            fileAudioModel.mArtist = mArtist;
            return fileAudioModel;
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
