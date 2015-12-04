package com.mercandalli.android.apps.files.file.audio;

import java.io.File;

import com.mercandalli.android.apps.files.file.FileModel;

public class FileAudioModel extends FileModel {

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
        public FileAudioModel build() {
            FileAudioModel fileAudioModel = new FileAudioModel();
            fileAudioModel.mId = id;
            fileAudioModel.mIdUser = idUser;
            fileAudioModel.mIdFileParent = idFileParent;
            fileAudioModel.mName = name;
            fileAudioModel.mUrl = url;
            fileAudioModel.mSize = size;
            fileAudioModel.mIsPublic = isPublic;
            fileAudioModel.mType = type;
            fileAudioModel.mIsDirectory = isDirectory;
            fileAudioModel.mDateCreation = dateCreation;
            fileAudioModel.mIsApkUpdate = isApkUpdate;
            fileAudioModel.mContent = content;
            fileAudioModel.mFile = file;
            fileAudioModel.mLastModified = lastModified;
            fileAudioModel.mCount = count;

            fileAudioModel.mAlbum = album;
            fileAudioModel.mArtist = artist;
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
