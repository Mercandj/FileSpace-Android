package com.mercandalli.android.apps.files.file.audio;

import android.os.Parcel;
import android.util.Log;

import com.mercandalli.android.apps.files.file.FileModel;

import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.myid3.MyID3;

import java.io.File;

/**
 * An audio {@link FileModel}. Contains a title, album, artist.
 */
public class FileAudioModel extends FileModel {

    protected String mTitle;
    protected String mAlbum;
    protected String mArtist;

    public static class FileMusicModelBuilder extends FileModelBuilder {
        private String mTitle;
        private String mAlbum;
        private String mArtist;

        public FileMusicModelBuilder title(String title) {
            this.mTitle = title;
            return this;
        }

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
            if (!file.getName().toLowerCase().endsWith(".mp3")) {
                return this;
            }
            try {
                final IMusicMetadata metadata = (new MyID3().read(file)).getSimplified();
                title(metadata.getSongTitle());
                album(metadata.getAlbum());
                artist(metadata.getArtist());
            } catch (Exception e) {
                Log.e(getClass().getName(), "Exception", e);
            }
            return this;
        }

        @Override
        public FileAudioModel build() {
            final FileAudioModel fileAudioModel = new FileAudioModel();
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

            fileAudioModel.mTitle = mTitle;
            fileAudioModel.mAlbum = mAlbum;
            fileAudioModel.mArtist = mArtist;
            return fileAudioModel;
        }
    }

    public String getTitle() {
        return mTitle;
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

    /* Parcelable */

    public static final Creator<FileModel> CREATOR = new Creator<FileModel>() {
        @Override
        public FileModel createFromParcel(Parcel in) {
            return new FileModelBuilder().parcel(in).build();
        }

        @Override
        public FileModel[] newArray(int size) {
            return new FileModel[size];
        }
    };
}
