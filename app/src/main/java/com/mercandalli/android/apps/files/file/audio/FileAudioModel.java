package com.mercandalli.android.apps.files.file.audio;

import android.os.Parcel;
import android.support.annotation.Nullable;

import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.library.baselibrary.audio.metadata.read.AudioMetaDataRead;
import com.mercandalli.android.library.baselibrary.audio.metadata.read.MusicMetadata;
import com.mercandalli.android.library.baselibrary.audio.metadata.read.MusicMetadataSet;

import java.io.File;

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
            super.file(file);
            //jaudiotage
            /*
            if (mUrl == null || !mUrl.toLowerCase().endsWith(".mp3")) {
                return this;
            }
            final IMusicMetadata metadata;
            try {
                metadata = (new MyID3().read(file)).getSimplified();

                title(metadata.getSongTitle());
                album(metadata.getAlbum());
                artist(metadata.getArtist());
                return this;
            } catch (IOException e) {
                e.printStackTrace();
            }
            */
            final MusicMetadataSet extract = AudioMetaDataRead.extract(file);
            if (extract == null) {
                return this;
            }
            final MusicMetadata metadata = extract.getSimplified();
            title(metadata.getSongTitle());
            album(metadata.getAlbum());
            artist(metadata.getArtist());
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
