package mercandalli.com.jarvis.action;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import mercandalli.com.jarvis.activity.Application;
import mercandalli.com.jarvis.activity.ApplicationDrawer;
import mercandalli.com.jarvis.fragment.HomeFragment;
import mercandalli.com.jarvis.listener.IPostExecuteListener;
import mercandalli.com.jarvis.net.TaskGet;

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

        return output;
    }

}
