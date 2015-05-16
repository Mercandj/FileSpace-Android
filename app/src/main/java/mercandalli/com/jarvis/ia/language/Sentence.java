package mercandalli.com.jarvis.ia.language;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.jarvis.ui.activity.Application;

import static mercandalli.com.jarvis.util.LogUtils.LOGE;
import static mercandalli.com.jarvis.util.LogUtils.makeLogTag;

/**
 * Created by Jonathan on 24/04/2015.
 */
public class Sentence {
    private static final String TAG = makeLogTag(Sentence.class);

    private Application app;
    private List<String> sentence;
    private int id;
    private String title;

    public Sentence(Application app, JSONObject json) {
        this.app = app;
        this.sentence = new ArrayList<>();
        this.id = -1;

        try {
            if(json.has("id")) {
                this.id = json.getInt("id");
            }
            if(json.has("title")) {
                this.title = json.getString("title");
            }
            if(json.has("sentence")) {
                JSONArray sentence_array = json.getJSONArray("sentence");
                for(int j=sentence_array.length()-1; j>=0; j--)
                    sentence.add(sentence_array.getString(j));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            LOGE(TAG, "Constructor: Parsing JSON");
        }
    }

    public boolean isValid() {
        return this.id != -1;
    }

    public List<String> getSentence() {
        return sentence;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
