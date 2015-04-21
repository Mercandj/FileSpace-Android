package mercandalli.com.jarvis.action;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import mercandalli.com.jarvis.activity.Application;

/**
 * Created by Jonathan on 19/04/2015.
 */
public abstract class Interpreter {

    protected Application app;
    protected List<QA> qas;

    public Interpreter(Application app) {
        this.app = app;
        this.qas = new ArrayList<>();

        Writer writer = new StringWriter();
        try {
            InputStream is = app.getResources().getAssets().open("jarvis_qa_fr.json");
            char[] buffer = new char[2048];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String text = writer.toString();

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

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Interpreter(Application app, List<QA> qas) {
        this.app = app;
        this.qas = qas;
    }

    public abstract String interpret(String input);

}
