package mercandalli.com.jarvis.ia;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.jarvis.ia.language.Sentence;
import mercandalli.com.jarvis.ui.activity.Application;

import static mercandalli.com.jarvis.util.FileUtils.readStringAssets;

/**
 * Created by Jonathan on 24/04/2015.
 */
public class Resource {

    private Application app;
    protected List<QA> qas;
    protected List<Sentence> sentences;

    public Resource(Application app, String file_name) {
        this.app = app;
        this.qas = new ArrayList<>();
        this.sentences = new ArrayList<>();

        String text = readStringAssets(app, file_name);

        try {
            JSONObject json = new JSONObject(text);
            if(json.has("qa")) {
                JSONArray qa_array = json.getJSONArray("qa");
                int qa_array_length = qa_array.length();
                for(int i=0; i<qa_array_length; i++) {
                    JSONObject qa = qa_array.getJSONObject(i);
                    QA real_qa = new QA(app, qa);
                    if(real_qa.isValid())
                        qas.add(real_qa);
                }
            }

            if(json.has("sentences")) {
                JSONArray sentences_array = json.getJSONArray("sentences");
                int sentences_array_length = sentences_array.length();
                for(int i=0; i<sentences_array_length; i++) {
                    JSONObject sentence = sentences_array.getJSONObject(i);
                    Sentence real_sentence = new Sentence(app, sentence);
                    if(real_sentence.isValid())
                        sentences.add(real_sentence);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<QA> getQas() {
        return qas;
    }

    public boolean equalsSentenece(String title, String sentence) {
        if(title == null || sentence == null)
            return false;
        for(Sentence sent : this.sentences)
            if(sent.getTitle().equals(title))
                for(String str : sent.getSentence())
                    if(str.equals(sentence))
                        return true;
        return false;
    }
}
