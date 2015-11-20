package mercandalli.com.filespace.file;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.util.Date;

import mercandalli.com.filespace.main.Config;
import mercandalli.com.filespace.main.Constants;
import mercandalli.com.filespace.file.filespace.FileSpaceModel;
import mercandalli.com.filespace.common.util.FileUtils;

/**
 * Created by Jonathan on 24/10/2015.
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

    // Local attrs
    protected File mFile;
    protected long mLastModified;
    protected long mCount;

    public static class FileModelBuilder {

        // Online & Local attrs
        protected int id;
        protected int idUser;
        protected int idFileParent;
        protected String name;
        protected String url;
        protected long size;
        protected boolean isPublic;
        protected FileTypeModel type;
        protected boolean isDirectory;
        protected Date dateCreation;
        protected boolean isApkUpdate;
        protected FileSpaceModel content;

        // Local attrs
        protected File file;
        protected long lastModified;
        protected long count;

        public FileModelBuilder id(int id) {
            this.id = id;
            return this;
        }

        public FileModelBuilder idUser(int idUser) {
            this.idUser = idUser;
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
            if (file != null && file.exists()) {
                id = file.hashCode();
                isDirectory = file.isDirectory();
                size = file.length();
                url = file.getAbsolutePath();
                name = (file.getName().lastIndexOf(".") == -1) ? file.getName() : file.getName().substring(0, file.getName().lastIndexOf("."));
                type = new FileTypeModel(FileUtils.getExtensionFromPath(url));
                dateCreation = new Date(file.lastModified());
                lastModified = file.lastModified();
                if (isDirectory && file.listFiles() != null) {
                    count = file.listFiles().length;
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

        public FileModelBuilder parcel(Parcel in) {
            id = in.readInt();
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
            fileModel.mId = id;
            fileModel.mIdUser = idUser;
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
        return (mFile == null);
    }

    public String getFullName() {
        return mName + (mIsDirectory ? "" : ("." + mType));
    }

    public String getCopyName() {
        return mName + " - Copy" + (mIsDirectory ? "" : ("." + mType));
    }

    public String getOnlineUrl() {
        return Constants.URL_API + "/" + Config.routeFile + "/" + getId();
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
        return mFile;
    }

    public long getLastModified() {
        return mLastModified;
    }

    public long getCount() {
        return mCount;
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
