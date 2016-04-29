package com.mercandalli.android.apps.files.file.audio;

import android.os.Parcel;
import android.support.annotation.Nullable;

import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.file.audio.metadata.FileAudioMetaDataExtractor;

import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.myid3.MyID3;

import java.io.File;
import java.io.IOException;

/**
 * An audio {@link FileModel}. Contains a title, album, artist.
 */
public class FileAudioModel extends FileModel {

    protected String mTitle;
    protected String mAlbum;
    protected String mArtist;

    public static class FileAudioModelBuilder extends FileModelBuilder {
        private String mTitle;
        private String mAlbum;
        private String mArtist;

        public FileAudioModelBuilder title(String title) {
            this.mTitle = title;
            return this;
        }

        public FileAudioModelBuilder album(String album) {
            this.mAlbum = album;
            return this;
        }

        public FileAudioModelBuilder artist(String artist) {
            this.mArtist = artist;
            return this;
        }

        public FileAudioModelBuilder fileModel(FileModel fileModel) {
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

        public FileAudioModelBuilder file(final File file) {
            return file(file, new MyID3());
        }

        public FileAudioModelBuilder file(final File file, final MyID3 myID3) {
            super.file(file);
            if (mUrl != null && mUrl.toLowerCase().endsWith(".mp3")) {

                if (true) {
                    final FileAudioMetaDataExtractor.MetaData extract = FileAudioMetaDataExtractor.getInstance().extract(file);
                    if (extract == null) {
                        return this;
                    }
                    title(extract.title);
                    album(extract.album);
                    artist(extract.artist);
                    return this;
                }

                try {
                    final IMusicMetadata metadata = (myID3.read(file)).getSimplified();
                    title(metadata.getSongTitle());
                    album(metadata.getAlbum());
                    artist(metadata.getArtist());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return this;
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

    @Nullable
    public String getTitle() {
        return mTitle;
    }

    @Nullable
    public String getAlbum() {
        return mAlbum;
    }

    @Nullable
    public String getArtist() {
        return mArtist;
    }

    @Nullable
    public String getPath() {
        if (isOnline()) {
            return getOnlineUrl();
        }
        final File file = getFile();
        if (file == null) {
            return null;
        }
        return file.getPath();
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
