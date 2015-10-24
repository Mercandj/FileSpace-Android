package mercandalli.com.filespace.models.better;

import java.io.File;
import java.util.Date;

import mercandalli.com.filespace.utils.FileUtils;

/**
 * Created by Jonathan on 24/10/2015.
 */
public class FileModel {

    // Online & Local attrs
    private int mId;
    private int mIdUser;
    private int mIdFileParent;
    private String mName;
    private String mUrl;
    private long mSize;
    private boolean mIsPublic;
    private FileTypeModel mType;
    private boolean mIsDirectory;
    private Date mDateCreation;

    // Local attrs
    private File mFile;
    private long mLastModified;
    private long mCount;

    public static class FileModelBuilder {

        // Online & Local attrs
        private int id;
        private int idUser;
        private int idFileParent;
        private String name;
        private String url;
        private long size;
        private boolean isPublic;
        private FileTypeModel type;
        private boolean isDirectory;
        private Date dateCreation;

        // Local attrs
        private File file;
        private long lastModified;
        private long count;

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

        public FileModelBuilder file(File file) {
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

        public FileModel build() {
            FileModel fileModel = new FileModel();
            fileModel.setId(id);
            fileModel.setIdUser(idUser);
            fileModel.setIdFileParent(idFileParent);
            fileModel.setName(name);
            fileModel.setUrl(url);
            fileModel.setSize(size);
            fileModel.setPublic(isPublic);
            fileModel.setType(type);
            fileModel.setIsDirectory(isDirectory);
            fileModel.setDateCreation(dateCreation);
            fileModel.setFile(file);
            fileModel.setLastModified(lastModified);
            fileModel.setCount(count);
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

    /*
     * GETTER and SETTER
     */

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public int getIdUser() {
        return mIdUser;
    }

    public void setIdUser(int mIdUser) {
        this.mIdUser = mIdUser;
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

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public long getSize() {
        return mSize;
    }

    public void setSize(long mSize) {
        this.mSize = mSize;
    }

    public boolean isPublic() {
        return mIsPublic;
    }

    public void setPublic(boolean mIsPublic) {
        this.mIsPublic = mIsPublic;
    }

    public FileTypeModel getType() {
        return mType;
    }

    public void setType(FileTypeModel mType) {
        this.mType = mType;
    }

    public boolean isDirectory() {
        return mIsDirectory;
    }

    public void setIsDirectory(boolean mIsDirectory) {
        this.mIsDirectory = mIsDirectory;
    }

    public Date getDateCreation() {
        return mDateCreation;
    }

    public void setDateCreation(Date mDateCreation) {
        this.mDateCreation = mDateCreation;
    }

    public File getFile() {
        return mFile;
    }

    public void setFile(File mFile) {
        this.mFile = mFile;
    }

    public long getLastModified() {
        return mLastModified;
    }

    public void setLastModified(long mLastModified) {
        this.mLastModified = mLastModified;
    }

    public long getCount() {
        return mCount;
    }

    public void setCount(long mCount) {
        this.mCount = mCount;
    }
}
