package com.mercandalli.android.apps.files.file.audio.metadata.read;

import java.util.Hashtable;

/* package */ class MyMap extends Hashtable {
    public static final long serialVersionUID = 1L;

    public MyMap() {
    }

    private Object actualGet(Object key, Object def) {
        Object result = super.get(key);
        return result == null ? def : result;
    }

    public Object get(Object key, Object def) {
        return this.actualGet(key, def);
    }

    public Integer get(Object key, Integer def) {
        return (Integer) this.actualGet(key, def);
    }

    public String get(Object key, String def) {
        return (String) this.actualGet(key, def);
    }

    public Boolean get(Object key, Boolean def) {
        return (Boolean) this.actualGet(key, def);
    }

    public Integer get(Object key, int def) {
        return (Integer) this.actualGet(key, new Integer(def));
    }

    public final Object put(Object key, Object value) {
        if (key != null && value != null) {
            return super.put(key, value);
        } else {
            super.remove(key);
            return null;
        }
    }
}

