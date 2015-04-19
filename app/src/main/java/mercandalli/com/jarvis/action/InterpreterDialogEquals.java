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

import mercandalli.com.jarvis.activity.Application;

/**
 * Created by Jonathan on 19/04/2015.
 * Just funny responses
 */
public class InterpreterDialogEquals extends Interpreter {

    public InterpreterDialogEquals(Application app) {
        super(app);
    }

    @Override
    public String interpret(String input) {
        String output = null;

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

                    if(qa.has("q")) {
                        JSONArray q_array = qa.getJSONArray("q");

                        for(int j=0; j<q_array.length(); j++) {
                            String q = q_array.getString(j);

                            if(q.equals(input)) {
                                if(qa.has("a")) {
                                    JSONArray a_array = qa.getJSONArray("a");
                                    return a_array.getString(app.getLibrary().random(0, a_array.length()-1));
                                }
                            }
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return output;
    }

}
