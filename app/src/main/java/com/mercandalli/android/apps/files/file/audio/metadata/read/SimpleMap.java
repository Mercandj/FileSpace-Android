package com.mercandalli.android.apps.files.file.audio.metadata.read;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/* package */ class SimpleMap implements Map {
    @NonNull
    private final Map mMap = new Hashtable();

    public SimpleMap() {
    }

    public SimpleMap(Map other) {
        this.mMap.putAll(other);
    }

    public int size() {
        return this.mMap.size();
    }

    public boolean isEmpty() {
        return this.mMap.isEmpty();
    }

    private Object simplifyKey(Object key) {
        return key == null ? null : (!(key instanceof String) ? key : ((String) key).toLowerCase());
    }

    public boolean containsKey(Object key) {
        return key != null && this.mMap.containsKey(this.simplifyKey(key));
    }

    public boolean containsValue(Object value) {
        return value != null && this.mMap.containsValue(value);
    }

    public Object get(Object key) {
        return key == null ? null : this.mMap.get(this.simplifyKey(key));
    }

    public Object put(Object key, Object value) {
        if (key == null) {
            return null;
        } else if (value == null) {
            this.mMap.remove(key);
            return null;
        } else {
            return this.mMap.put(this.simplifyKey(key), value);
        }
    }

    public Object remove(Object key) {
        return key == null ? null : this.mMap.remove(this.simplifyKey(key));
    }

    public void putAll(Map t) {
        Vector entries = new Vector((Collection) t.entrySet());

        for (int i = 0; i < entries.size(); ++i) {
            Entry entry = (Entry) entries.get(i);
            this.put(entry.getKey(), entry.getValue());
        }

    }

    public void clear() {
        this.mMap.clear();
    }

    public Set keySet() {
        return this.mMap.keySet();
    }

    public Collection values() {
        return this.mMap.values();
    }

    public Set entrySet() {
        return this.mMap.entrySet();
    }
}

