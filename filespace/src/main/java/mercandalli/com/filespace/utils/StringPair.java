package mercandalli.com.filespace.utils;

/**
 * Created by Jonathan on 16/06/15.
 */
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
        if (this == o) return true;
        if (!(o instanceof StringPair))
            return false;
        StringPair obj = (StringPair) o;
        if (this.name == null && obj.name != null)
            return false;
        return this.name.equals(obj.name) && this.value.equals(obj.value);
    }
}
