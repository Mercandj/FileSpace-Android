package com.mercandalli.android.apps.files.file;

import android.os.Parcel;
import android.os.Parcelable;

import com.mercandalli.android.apps.files.file.filespace.FileSpaceModel;
import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.apps.files.main.Constants;

import java.io.File;
import java.util.Date;

/**
 * The main file buildModel.
 */
public class FileModel implements Parcelable {

    // Online & Local attrs
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

    // Local attrs
    protected File mFile;
    protected long mLastModified;
    // Nb of files inside this folder
    protected long mCount;
    private long mCountAudio;

    public static class FileModelBuilder {

        // Online & Local attrs
        protected int mId;
        protected int mIdUser;
        protected int idFileParent;
        protected String name;
        protected String url;
        protected long size;
        protected boolean isPublic;
        protected FileTypeModel type;
        protected boolean isDirectory;
        protected Date dateCreation;
        protected boolean isApkUpdate;
        protected boolean isOnline;
        protected FileSpaceModel content;

        // Local attrs
        protected File file;
        protected long lastModified;
        protected long count;
        protected long countAudio;

        public FileModelBuilder id(int id) {
            this.mId = id;
            return this;
        }

        public FileModelBuilder idUser(int idUser) {
            this.mIdUser = idUser;
            return this;
        }

        public FileModelBuilder idFileParent(int idFileParent) {
            this.idFileParent = idFileParent;
            return this;
        }

        public FileModelBuilder name(String name) {
            this.name = name;
            return this;
        }

        public FileModelBuilder url(String url) {
            this.url = url;
            return this;
        }

        public FileModelBuilder size(long size) {
            this.size = size;
            return this;
        }

        public FileModelBuilder isPublic(boolean isPublic) {
            this.isPublic = isPublic;
            return this;
        }

        public FileModelBuilder type(FileTypeModel type) {
            this.type = type;
            return this;
        }

        public FileModelBuilder isDirectory(boolean isDirectory) {
            this.isDirectory = isDirectory;
            return this;
        }

        public FileModelBuilder dateCreation(Date dateCreation) {
            this.dateCreation = dateCreation;
            return this;
        }

        public FileModelBuilder isApkUpdate(boolean isApkUpdate) {
            this.isApkUpdate = isApkUpdate;
            return this;
        }

        public FileModelBuilder content(final FileSpaceModel content) {
            this.content = content;
            return this;
        }

        public FileModelBuilder file(final File file) {
            isOnline = false;
            if (file != null && file.exists()) {
                this.isDirectory = file.isDirectory();
                this.size = file.length();
                this.url = file.getAbsolutePath();
                this.mId = url.hashCode();
                final String tmpName = file.getName();
                this.name = (tmpName.lastIndexOf('.') == -1) ? tmpName : tmpName.substring(0, tmpName.lastIndexOf('.'));
                this.type = new FileTypeModel(FileUtils.getExtensionFromPath(this.url));
                this.lastModified = file.lastModified();
                this.dateCreation = new Date(this.lastModified);
                if (this.isDirectory) {
                    final File[] tmpListFiles = file.listFiles();
                    if (tmpListFiles != null) {
                        this.count = tmpListFiles.length;
                        this.countAudio = 0;
                        for (File f : tmpListFiles) {
                            if ((new FileTypeModel(FileUtils.getExtensionFromPath(f.getPath()))).equals(FileTypeModelENUM.AUDIO.type)) {
                                this.countAudio++;
                            }
                        }
                    }
                }
                this.file = file;
            }
            return this;
        }

        public FileModelBuilder lastModified(long lastModified) {
            this.lastModified = lastModified;
            return this;
        }

        public FileModelBuilder count(long count) {
            this.count = count;
            return this;
        }

        public FileModelBuilder countAudio(long countAudio) {
            this.countAudio = countAudio;
            return this;
        }

        public FileModelBuilder isOnline(boolean isOnline) {
            this.isOnline = isOnline;
            return this;
        }

        public FileModelBuilder parcel(Parcel in) {
            mId = in.readInt();
            url = in.readString();
            name = in.readString();
            size = in.readLong();
            boolean[] b = new boolean[1];
            in.readBooleanArray(b);
            isDirectory = b[0];
            type = new FileTypeModel(in.readString());
            return this;
        }

        public FileModel build() {
            FileModel fileModel = new FileModel();
            fileModel.mId = mId;
            fileModel.mIdUser = mIdUser;
            fileModel.mIdFileParent = idFileParent;
            fileModel.mName = name;
            fileModel.mUrl = url;
            fileModel.mSize = size;
            fileModel.mIsPublic = isPublic;
            fileModel.mType = type;
            fileModel.mIsDirectory = isDirectory;
            fileModel.mDateCreation = dateCreation;
            fileModel.mIsApkUpdate = isApkUpdate;
            fileModel.mContent = content;
            fileModel.mFile = file;
            fileModel.mLastModified = lastModified;
            fileModel.mCount = count;
            fileModel.mCountAudio = countAudio;
            fileModel.mIsOnline = isOnline;
            return fileModel;
        }
    }

    @Override
    public String toString() {
        String toString = "FileModel[" + mId + "] ";
        toString += mName;
        return toString;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof FileModel)) {
            return false;
        }
        FileModel fileModel = (FileModel) obj;
        return fileModel.mId == ((FileModel) obj).getId();
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

    public String getOnlineUrl() {
        return Constants.URL_DOMAIN_API + "/" + Config.routeFile + "/" + getId();
    }

    public boolean isAudio() {
        return !(mIsDirectory || mType == null) && mType.equals(FileTypeModelENUM.AUDIO.type);
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
        return mSize;
    }

    public boolean isPublic() {
        return mIsPublic;
    }

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
    public long getCount() {
        return mCount;
    }


    public long getCountAudio() {
        return mCountAudio;
    }

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
