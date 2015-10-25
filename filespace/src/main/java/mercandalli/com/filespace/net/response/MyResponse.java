package mercandalli.com.filespace.net.response;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import mercandalli.com.filespace.util.StringUtils;

/**
 * Created by Jonathan on 25/10/2015.
 */
public abstract class MyResponse<T> {
    @SerializedName("result")
    protected List<T> mResult;

    @SerializedName("toast")
    protected String mToast;

    protected List<T> getResult(final Context context) {
        if (!StringUtils.isNullOrEmpty(getToast())) {
            Toast.makeText(context, getToast(), Toast.LENGTH_SHORT).show();
        }
        return mResult;
    }

    public String getToast() {
        return mToast;
    }
}
