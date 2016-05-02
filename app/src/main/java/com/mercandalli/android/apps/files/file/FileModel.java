package com.mercandalli.android.apps.files.file;

import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mercandalli.android.apps.files.file.filespace.FileSpaceModel;
import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.apps.files.main.Constants;

import java.io.File;
import java.util.Date;

import static com.mercandalli.android.library.baselibrary.java.FileUtils.getExtensionFromPath;

/**
 * The main file buildModel.
 */
public class FileModel implements Parcelable {

    //region Online & Local attrs
    protected int mId;
    protected int mIdUser;
    protected int mIdFileParent;
    protected String mName;
    protected String mUrl;
    protected long mSize;
    protected boolean mIsPublic;
    protected FileTypeModel mType;
    protected boolean mIsDirectory;
    protected Date mDateCreation;
    protected boolean mIsApkUpdate;
    protected FileSpaceModel mContent;
    protected boolean mIsOnline;

    //region Local attrs
    protected File mFile;
    protected long mLastModified;
    // Nb of files inside this folder
    protected int mCount;
    private int mCountAudio;
    private int mCountImage;

    /**
     * A Builder to instantiate immutable object.
     */
    public static class FileModelBuilder {

        //region Online & Local attrs
        protected int mId;
        protected int mIdUser;
        protected int mIdFileParent;
        protected String mName;
        protected String mUrl;
        protected long mSize;
        protected boolean mIsPublic;
        protected FileTypeModel mType;
        protected boolean mIsDirectory;
        protected Date mDateCreation;
        protected boolean mIsApkUpdate;
        protected boolean mIsOnline;
        protected FileSpaceModel mContent;

        //region Local attrs
        protected File mFile;
        protected long mLastModified;
        protected int mCount;
        protected int mCountAudio;
        protected int mCountImage;

        public FileModelBuilder id(int id) {
            this.mId = id;
            return this;
        }

        public FileModelBuilder idUser(int idUser) {
            this.mIdUser = idUser;
            return this;
        }

        public FileModelBuilder idFileParent(int idFileParent) {
            this.mIdFileParent = idFileParent;
            return this;
        }

        public FileModelBuilder name(String name) {
            this.mName = name;
            return this;
        }

        public FileModelBuilder url(String url) {
            this.mUrl = url;
            return this;
        }

        public FileModelBuilder size(long size) {
            this.mSize = size;
            return this;
        }

        public FileModelBuilder isPublic(boolean isPublic) {
            this.mIsPublic = isPublic;
            return this;
        }

        public FileModelBuilder type(FileTypeModel type) {
            this.mType = type;
            return this;
        }

        public FileModelBuilder isDirectory(boolean isDirectory) {
            this.mIsDirectory = isDirectory;
            return this;
        }

        public FileModelBuilder dateCreation(Date dateCreation) {
            this.mDateCreation = dateCreation;
            return this;
        }

        public FileModelBuilder isApkUpdate(boolean isApkUpdate) {
            this.mIsApkUpdate = isApkUpdate;
            return this;
        }

        public FileModelBuilder content(final FileSpaceModel content) {
            this.mContent = content;
            return this;
        }

        public FileModelBuilder file(final File file) {
            if (file == null) {
                throw new IllegalStateException("file is null in FileModelBuilder#file(File).");
            }
            if (!file.exists()) {
                throw new IllegalStateException("file not exits in FileModelBuilder#file(File).");
            }
            mIsOnline = false;
            mIsDirectory = file.isDirectory();
            mUrl = file.getAbsolutePath();
            if (!mIsDirectory) {
                mSize = file.length();
            }
            mId = mUrl.hashCode();
            final String tmpName = file.getName();
            mName = (tmpName.lastIndexOf('.') == -1) ? tmpName : tmpName.substring(0, tmpName.lastIndexOf('.'));
            mType = new FileTypeModel(getExtensionFromPath(mUrl));
            mLastModified = file.lastModified();
            mDateCreation = new Date(mLastModified);
            if (mIsDirectory) {
                final File[] tmpListFiles = file.listFiles();
                if (tmpListFiles != null) {
                    mCount = tmpListFiles.length;
                    mCountAudio = 0;
                    for (File f : tmpListFiles) {
                        if ((new FileTypeModel(getExtensionFromPath(f.getPath())))
                                .equals(FileTypeModelENUM.AUDIO.type)) {
                            mCountAudio++;
                        }
                    }
                }
            }
            mFile = file;
            return this;
        }

        public FileModelBuilder lastModified(long lastModified) {
            this.mLastModified = lastModified;
            return this;
        }

        public FileModelBuilder count(int count) {
            this.mCount = count;
            return this;
        }

        public FileModelBuilder countAudio(int countAudio) {
            this.mCountAudio = countAudio;
            return this;
        }

        public FileModelBuilder countImage(int countImage) {
            this.mCountImage = countImage;
            return this;
        }

        public FileModelBuilder isOnline(boolean isOnline) {
            this.mIsOnline = isOnline;
            return this;
        }

        public FileModelBuilder parcel(Parcel in) {
            mId = in.readInt();
            mUrl = in.readString();
            mName = in.readString();
            mSize = in.readLong();
            boolean[] b = new boolean[1];
            in.readBooleanArray(b);
            mIsDirectory = b[0];
            mType = new FileTypeModel(in.readString());
            return this;
        }

        public FileModel build() {
            final FileModel fileModel = new FileModel();
            fileModel.mId = mId;
            fileModel.mIdUser = mIdUser;
            fileModel.mIdFileParent = mIdFileParent;
            fileModel.mName = mName;
            fileModel.mUrl = mUrl;
            fileModel.mSize = mSize;
            fileModel.mIsPublic = mIsPublic;
            fileModel.mType = mType;
            fileModel.mIsDirectory = mIsDirectory;
            fileModel.mDateCreation = mDateCreation;
            fileModel.mIsApkUpdate = mIsApkUpdate;
            fileModel.mContent = mContent;
            fileModel.mFile = mFile;
            fileModel.mLastModified = mLastModified;
            fileModel.mCount = mCount;
            fileModel.mCountAudio = mCountAudio;
            fileModel.mCountImage = mCountImage;
            fileModel.mIsOnline = mIsOnline;
            return fileModel;
        }
    }

    @Override
    public String toString() {
        return "FileModel[" + mId + "] " + mName;
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj instanceof FileModel && mId == ((FileModel) obj).getId();
    }

    @Override
    public int hashCode() {
        return mId;
    }

    public boolean isOnline() {
        return mIsOnline;
    }

    public String getFullName() {
        return mName + (mIsDirectory ? "" : ("." + mType));
    }

    public String getCopyName() {
        return mName + " - Copy" + (mIsDirectory ? "" : ("." + mType));
    }

    @NonNull
    public String getOnlineUrl() {
        return Constants.URL_DOMAIN + Config.ROUTE_FILE + "/" + getId();
    }

    public boolean isAudio() {
        return !(mIsDirectory || mType == null) && FileTypeModelENUM.AUDIO.type.equals(mType);
    }

    /*
     * GETTER and SETTER
     */

    public int getId() {
        return mId;
    }

    public int getIdUser() {
        return mIdUser;
    }

    public int getIdFileParent() {
        return mIdFileParent;
    }

    public void setIdFileParent(int mIdFileParent) {
        this.mIdFileParent = mIdFileParent;
    }

    public String getName() {
        return mName;
    }

    public String getUrl() {
        return mUrl;
    }

    public long getSize() {
        if (mSize == 0 && mIsDirectory && mUrl != null &&
                mUrl.startsWith(Environment.getExternalStorageDirectory().getAbsolutePath())) {
            mSize = FileUtils.getLocalFolderSize(mFile);
        }
        return mSize;
    }

    public boolean isPublic() {
        return mIsPublic;
    }

    @Nullable
    public FileTypeModel getType() {
        return mType;
    }

    public boolean isDirectory() {
        return mIsDirectory;
    }

    public Date getDateCreation() {
        return mDateCreation;
    }

    public boolean isApkUpdate() {
        return mIsApkUpdate;
    }

    public FileSpaceModel getContent() {
        return mContent;
    }

    @Nullable
    public File getFile() {
        if (mFile != null) {
            return mFile;
        } else if (!isOnline() && mUrl != null) {
            mFile = new File(mUrl);
            return mFile;
        }
        return null;
    }

    public long getLastModified() {
        return mLastModified;
    }

    /**
     * @return If is a directory return then umber of children.
     */
    public int getCount() {
        return mCount;
    }

    public int getCountAudio() {
        return mCountAudio;
    }

    public int getCountImage() {
        return mCountImage;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mUrl);
        dest.writeString(mName);
        dest.writeLong(mSize);
        dest.writeBooleanArray(new boolean[]{this.isDirectory()});
        dest.writeString(mType.getFirstExtension());
    }
}
