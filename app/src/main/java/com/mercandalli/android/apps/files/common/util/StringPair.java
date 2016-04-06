package com.mercandalli.android.apps.files.common.util;

import com.mercandalli.android.library.mainlibrary.java.StringUtils;

public class StringPair {
    private String mName, mValue;

    public StringPair(String name, String value) {
        mName = name;
        mValue = value;
    }

    public String getName() {
        return mName;
    }

    public String getValue() {
        return mValue;
    }

    public String toString() {
        return mName + "=" + mValue;
    }

    @Override
    public int hashCode() {
        return (mName.hashCode() + " " + mValue.hashCode()).hashCode();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StringPair)) {
            return false;
        }
        final StringPair obj = (StringPair) o;
        return StringUtils.isEquals(mName, obj.mName) && StringUtils.isEquals(mValue, obj.mValue);
    }
}
