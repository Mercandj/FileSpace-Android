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

    public InterpreterRoboticsEquals(Application app, List<QA> qas) {
        super(app, qas);
    }

    @Override
    public String interpret(String input) {
        String output = null;

        if(input.equals("raspberry") ||
                input.equals("etat du raspberry") ||
                input.equals("la valeur de la pine 18") ||
                input.equals("quelle est la valeur de la pine 18") ||
                input.equals("quelle est la valeur de pi 18") ||
                input.equals("pi 18") ||
                input.equals("quelle est la valeur de pin 18") ||
                input.equals("pin 18") ||
                input.equals("quelle est la valeur de teen18") ||
                input.equals("teen18") || input.equals("teen 18"))
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
                                                if (value.has("value")) {
                                                    if(app instanceof ApplicationDrawer) {
                                                        ApplicationDrawer tmpApp = (ApplicationDrawer) app;
                                                        if(tmpApp.fragment != null)
                                                            if(tmpApp.fragment instanceof HomeFragment)
                                                            {
                                                                ((HomeFragment)tmpApp.fragment).speakWords((value.getInt("value") == 1)?"La Pin 18 du raspberry est activée.":"La Pin 18 du raspberry est éteinte.");
                                                            }
                                                    }
                                                }
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

        if(input.equals("allume pin 18") ||
                input.equals("allumer la led") ||
                input.equals("allume la lumiere") ||
                input.equals("allume la led") ||
                input.equals("allumer pin 18") ||
                input.equals("active led") ||
                input.equals("active la led") ||
                input.equals("activer led") ||
                input.equals("activer la led") ||
                input.equals("open led") ||
                input.equals("activer la pin 18") ||
                input.equals("active pin 18"))
            if(this.app.isInternetConnection()) {
                List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
                parameters.add(new BasicNameValuePair("value", "1"));
                new TaskPost(
                        this.app,
                        this.app.getConfig().getUrlServer() + this.app.getConfig().routeRobotics + "/18",
                        new IPostExecuteListener() {
                            @Override
                            public void execute(JSONObject json, String body) {
                                if(app instanceof ApplicationDrawer) {
                                    ApplicationDrawer tmpApp = (ApplicationDrawer) app;
                                    if(tmpApp.fragment != null)
                                        if(tmpApp.fragment instanceof HomeFragment)
                                        {
                                            ((HomeFragment)tmpApp.fragment).speakWords("Je viens d'allumer la LED. Je reste à votre disposition.");
                                        }
                                }
                            }
                        },
                        parameters
                ).execute();
                return "";
            }

        if(input.equals("eteindre pin 18") ||
                input.equals("eteindre la led") ||
                input.equals("eteindre la lumiere") ||
                input.equals("eteindre pin 18") ||
                input.equals("desactive led") ||
                input.equals("desactive la led") ||
                input.equals("desactiver led") ||
                input.equals("desactiver la led") ||
                input.equals("off led") ||
                input.equals("led off") ||
                input.equals("desactiver la pin 18") ||
                input.equals("desactive pin 18"))
            if(this.app.isInternetConnection()) {
                List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
                parameters.add(new BasicNameValuePair("value", "0"));
                new TaskPost(
                        this.app,
                        this.app.getConfig().getUrlServer() + this.app.getConfig().routeRobotics + "/18",
                        new IPostExecuteListener() {
                            @Override
                            public void execute(JSONObject json, String body) {
                                if(app instanceof ApplicationDrawer) {
                                    ApplicationDrawer tmpApp = (ApplicationDrawer) app;
                                    if(tmpApp.fragment != null)
                                        if(tmpApp.fragment instanceof HomeFragment)
                                        {
                                            ((HomeFragment)tmpApp.fragment).speakWords("Je viens d'éteindre la LED. Je reste à votre disposition.");
                                        }
                                }
                            }
                        },
                        parameters
                ).execute();
                return "";
            }

        return output;
    }

}
