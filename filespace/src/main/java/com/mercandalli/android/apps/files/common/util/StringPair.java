package com.mercandalli.android.apps.files.common.util;

public class StringPair {
    private String name, value;

    public StringPair(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        return name + "=" + value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StringPair)) {
            return false;
        }
        StringPair obj = (StringPair) o;
        return StringUtils.isEquals(this.name, obj.name) && StringUtils.isEquals(this.value, obj.value);
    }
}
