package mercandalli.com.filespace.manager.file;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.filespace.listener.IPostExecuteListener;
import mercandalli.com.filespace.listener.ResultCallback;
import mercandalli.com.filespace.net.TaskGet;
import mercandalli.com.filespace.util.StringPair;

/**
 * Created by Jonathan on 05/11/2015.
 */
public class CoverUtils {

    public static void getCoverUrl(final Context context, final String album, final ResultCallback<String> resultUrl) {
        final String albumSearch = album.replaceAll("\\s", "+");

        List<StringPair> parameters = new ArrayList<>();
        parameters.add(new StringPair("term", albumSearch));

        new TaskGet(context, "https://itunes.apple.com/search", new IPostExecuteListener() {
            @Override
            public void onPostExecute(JSONObject json, String body) {
                if (json != null) {
                    try {
                        if (json.has("results")) {
                            JSONArray res = json.getJSONArray("results");
                            if (res.length() >= 1) {
                                JSONObject track = res.getJSONObject(0);
                                if(track.has("artworkUrl100")) {
                                    resultUrl.success(track.getString("artworkUrl100"));
                                    return;
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                resultUrl.failure();
            }
        }, parameters, false).execute();
    }
}
