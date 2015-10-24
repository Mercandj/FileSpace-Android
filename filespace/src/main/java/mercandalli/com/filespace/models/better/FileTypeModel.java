package mercandalli.com.filespace.models.better;

import mercandalli.com.filespace.models.ModelFileType;
import mercandalli.com.filespace.models.ModelFileTypeENUM;

/**
 * Created by Jonathan on 24/10/2015.
 */
public class FileTypeModel {

    private String mTitle;
    private String[] mExtensions;

    public FileTypeModel(String value) {
        mExtensions = new String[]{value};
        for (ModelFileTypeENUM t : ModelFileTypeENUM.values()) {
            for (String ext : t.type.getExtensions()) {
                if (value.equals(ext)) {
                    mTitle = t.type.getTitle();
                }
            }
        }
    }

    public FileTypeModel(String title, String value) {
        mTitle = title;
        mExtensions = new String[]{value};
    }

    public FileTypeModel(String title, String[] extensions) {
        mTitle = title;
        mExtensions = extensions;
    }

    @Override
    public String toString() {
        return getFirstExtension();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ModelFileType))
            return false;
        String[] objExtensions = ((ModelFileType) o).getExtensions();
        if (this.mExtensions == null || objExtensions == null)
            return false;
        for (String myExt : this.mExtensions)
            for (String objExt : objExtensions)
                if (myExt.equals(objExt))
                    return true;
        return false;
    }

    @Override
    public int hashCode() {
        return mTitle.hashCode();
    }

    public String getTitle() {
        return this.mTitle != null ? this.mTitle : "";
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String[] getExtensions() {
        return mExtensions;
    }

    public void setExtensions(String[] mExtensions) {
        this.mExtensions = mExtensions;
    }

    public String getFirstExtension() {
        return (mExtensions.length > 0) ? mExtensions[0] : "";
    }
}
