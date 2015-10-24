package mercandalli.com.filespace.net.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jonathan on 23/10/2015.
 */
public class GetFileResponse {
    @SerializedName("id")
    private int mId;

    @SerializedName("name")
    private String mName;

    @SerializedName("url")
    private String mUrl;

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getUrl() {
        return mUrl;
    }
}
