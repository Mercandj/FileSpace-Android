package mercandalli.com.filespace.net.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Jonathan on 24/10/2015.
 */
public class GetFilesResponse {

    @SerializedName("result")
    private List<GetFileResponse> mFiles;

    @SerializedName("toast")
    private String mToast;

    public List<GetFileResponse> getFiles() {
        return mFiles;
    }

    public String getToast() {
        return mToast;
    }
}
