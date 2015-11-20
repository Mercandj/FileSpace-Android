package mercandalli.com.filespace.common.model;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jonathan on 30/09/2015.
 */
public class ModelNasaImage extends Model {

    public String date;
    public String url;
    public String media_type;
    public String explanation;
    public String[] concepts;
    public Bitmap bitmap;

    public ModelNasaImage(JSONObject json, String date) {
        this.date = date;
        if (json == null)
            return;
        try {
            if (json.has("url"))
                this.url = json.getString("url");
            if (json.has("media_type"))
                this.media_type = json.getString("media_type");
            if (json.has("explanation"))
                this.explanation = json.getString("explanation");
            if (json.has("concepts")) {
                JSONArray explanation_jr = json.getJSONArray("concepts");
                this.concepts = new String[explanation_jr.length()];
                for (int i = 0; i < explanation_jr.length(); i++) {
                    this.concepts[i] = explanation_jr.getString(i);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public JSONObject toJSONObject() {
        return null;
    }
}
