package com.mercandalli.android.apps.files.file;

/**
 * Created by Jonathan on 24/10/2015.
 */
public class FileTypeModel {

    private String mTitle;
    private String[] mExtensions;

    public FileTypeModel(String value) {
        mExtensions = new String[]{value};
        for (FileTypeModelENUM t : FileTypeModelENUM.values()) {
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
        if (!(o instanceof FileTypeModel))
            return false;
        String[] objExtensions = ((FileTypeModel) o).getExtensions();
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

    public String[] getExtensions() {
        return mExtensions;
    }

    public String getFirstExtension() {
        return (mExtensions.length > 0) ? mExtensions[0] : "";
    }
}
