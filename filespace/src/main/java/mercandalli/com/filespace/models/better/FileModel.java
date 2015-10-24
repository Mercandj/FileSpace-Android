package mercandalli.com.filespace.models.better;

/**
 * Created by Jonathan on 24/10/2015.
 */
public class FileModel {

    private int mId;
    private int mIdUser;
    private String mName;
    private String mUrl;
    private long mSize;
    private boolean mIsPublic;
    private FileTypeModel mType;
    private boolean mIsDirectory;

    public static class FileModelBuilder {
        private int id;
        private int idUser;
        private String name;
        private String url;
        private long size;
        private boolean isPublic;
        private FileTypeModel type;
        private boolean isDirectory;

        public FileModelBuilder id(int id) {
            this.id = id;
            return this;
        }

        public FileModelBuilder idUser(int idUser) {
            this.idUser = idUser;
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

        public FileModel build() {
            FileModel fileModel = new FileModel();
            fileModel.setId(id);
            fileModel.setIdUser(idUser);
            fileModel.setName(name);
            fileModel.setUrl(url);
            fileModel.setSize(size);
            fileModel.setPublic(isPublic);
            fileModel.setType(type);
            fileModel.setIsDirectory(isDirectory);
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
}
