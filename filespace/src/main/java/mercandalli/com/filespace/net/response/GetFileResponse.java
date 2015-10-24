package mercandalli.com.filespace.net.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jonathan on 23/10/2015.
 */
public class GetFileResponse {
    @SerializedName("id")
    private int mId;

    @SerializedName("id_user")
    private int mIdUser;

    @SerializedName("name")
    private String mName;

    @SerializedName("url")
    private String mUrl;

    @SerializedName("size")
    private long mSize;

    @SerializedName("public")
    private long mPublic;

    public int getId() {
        return mId;
    }

    public int getIdUser() {
        return mIdUser;
    }

    public String getName() {
        return mName;
    }

    public String getUrl() {
        return mUrl;
    }

    public long getSize() {
        return mSize;
    }

    public long getPublic() {
        return mPublic;
    }
}
