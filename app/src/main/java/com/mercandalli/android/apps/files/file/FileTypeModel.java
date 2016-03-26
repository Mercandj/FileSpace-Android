package com.mercandalli.android.apps.files.file;

import android.content.Context;
import android.support.annotation.StringRes;

import com.mercandalli.android.apps.files.R;

/**
 * Created by Jonathan on 24/10/2015.
 */
public class FileTypeModel {

    @StringRes
    private int mTitleId = R.string.file_model_type_unknown;
    private String mTitle;
    private String[] mExtensions;

    public FileTypeModel(final String value) {
        mExtensions = new String[]{value};
        label:
        for (FileTypeModelENUM t : FileTypeModelENUM.values()) {
            for (String ext : t.type.getExtensions()) {
                if (value.equals(ext)) {
                    mTitleId = t.type.getTitleId();
                    break label;
                }
            }
        }
    }

    public FileTypeModel(@StringRes int title, String value) {
        mTitleId = title;
        mExtensions = new String[]{value};
    }

    public FileTypeModel(@StringRes int title, String[] extensions) {
        mTitleId = title;
        mExtensions = extensions;
    }

    @Override
    public String toString() {
        return getFirstExtension();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FileTypeModel)) {
            return false;
        }
        String[] objExtensions = ((FileTypeModel) o).getExtensions();
        if (this.mExtensions == null || objExtensions == null) {
            return false;
        }
        for (String myExt : this.mExtensions) {
            for (String objExt : objExtensions) {
                if (myExt.equals(objExt)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return mTitleId;
    }

    public String getTitle(final Context context) {
        if (mTitle != null) {
            return mTitle;
        }
        return mTitle = context.getString(mTitleId);
    }

    @StringRes
    public int getTitleId() {
        return mTitleId;
    }

    public String[] getExtensions() {
        return mExtensions;
    }

    public String getFirstExtension() {
        return (mExtensions.length > 0) ? mExtensions[0] : "";
    }
}
