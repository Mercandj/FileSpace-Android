package mercandalli.com.jarvis.action;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.jarvis.activity.Application;
import mercandalli.com.jarvis.activity.ApplicationDrawer;
import mercandalli.com.jarvis.fragment.HomeFragment;
import mercandalli.com.jarvis.listener.IPostExecuteListener;
import mercandalli.com.jarvis.net.TaskGet;
import mercandalli.com.jarvis.net.TaskPost;

/**
 * Created by Jonathan on 19/04/2015.
 */
public class InterpreterRoboticsEquals extends Interpreter {

    public InterpreterRoboticsEquals(Application app, Resource res) {
        super(app, res);
    }

    @Override
    public String interpret(String input) {
        String output = null;

        if(this.res.equalsSentenece("raspberry êtat", input))
            if(this.app.isInternetConnection()) {
                new TaskGet(
                        this.app,
                        this.app.getConfig().getUser(),
                        this.app.getConfig().getUrlServer() + app.getConfig().routeRobotics + "/18",
                        new IPostExecuteListener() {
                            @Override
                            public void execute(JSONObject json, String body) {
                                try {
                                    if (json.has("result")) {
                                        JSONArray result = json.getJSONArray("result");
                                        if (result != null)
                                            if (result.getJSONObject(0).has("value")) {
                                                JSONObject value = new JSONObject(result.getJSONObject(0).getString("value"));
                                                if (value.has("value"))
                                                    speak((value.getInt("value") == 1)?"La Pin 18 du raspberry est activée.":"La Pin 18 du raspberry est éteinte.");
                                            }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        null
                ).execute();
                return "";
            }

        if(this.res.equalsSentenece("raspberry led on", input))
            if(this.app.isInternetConnection()) {
                List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
                parameters.add(new BasicNameValuePair("value", "1"));
                new TaskPost(
                        this.app,
                        this.app.getConfig().getUrlServer() + this.app.getConfig().routeRobotics + "/18",
                        new IPostExecuteListener() {
                            @Override
                            public void execute(JSONObject json, String body) {
                                speak("Je viens d'allumer la LED. Je reste à votre disposition.");
                            }
                        },
                        parameters
                ).execute();
                return "";
            }

        if(this.res.equalsSentenece("raspberry led off", input))
            if(this.app.isInternetConnection()) {
                List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
                parameters.add(new BasicNameValuePair("value", "0"));
                new TaskPost(
                        this.app,
                        this.app.getConfig().getUrlServer() + this.app.getConfig().routeRobotics + "/18",
                        new IPostExecuteListener() {
                            @Override
                            public void execute(JSONObject json, String body) {
                                speak("Je viens d'éteindre la LED. Je reste à votre disposition.");
                            }
                        },
                        parameters
                ).execute();
                return "";
            }

        return output;
    }

    /**
     * Use the HomeFragment to speak
     * @param input
     */
    public void speak(String input) {
        if(app instanceof ApplicationDrawer) {
            ApplicationDrawer tmpApp = (ApplicationDrawer) app;
            if(tmpApp.fragment != null)
                if(tmpApp.fragment instanceof HomeFragment)
                {
                    ((HomeFragment)tmpApp.fragment).speakWords(input);
                }
        }
    }
}
