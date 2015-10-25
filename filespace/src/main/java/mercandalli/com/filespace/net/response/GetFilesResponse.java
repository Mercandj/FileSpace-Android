package mercandalli.com.filespace.net.response;

import android.content.Context;

import java.util.List;

/**
 * Created by Jonathan on 24/10/2015.
 */
public class GetFilesResponse extends MyResponse<GetFileResponse> {

    @Override
    public List<GetFileResponse> getResult(final Context context) {
        return super.getResult(context);
    }

    public String getToast() {
        return mToast;
    }
}
