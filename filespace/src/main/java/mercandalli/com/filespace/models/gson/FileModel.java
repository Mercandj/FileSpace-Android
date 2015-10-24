package mercandalli.com.filespace.models.gson;

/**
 * Created by Jonathan on 24/10/2015.
 */
public class FileModel {

    private int mId;

    private String mName;

    private String mUrl;

    public static class FileModelBuilder {
        private int id;
        private String name;
        private String url;

        public FileModelBuilder id(int id) {
            this.id = id;
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

        public FileModel build() {
            FileModel fileModel = new FileModel();
            fileModel.setId(id);
            return new FileModel();
        }

    }

    @Override
    public String toString() {
        String toString = super.toString();
        toString += "FileModel[" + mId + "] ";
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
}
