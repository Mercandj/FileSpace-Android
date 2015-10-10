/**
 * This file is part of Jarvis for Android, an app for managing your server (files, talks...).
 *
 * Copyright (c) 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 *
 * LICENSE:
 *
 * Jarvis for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 *
 * Jarvis for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 */
package mercandalli.com.filespace.ia;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.filespace.ui.activities.Application;
import mercandalli.com.filespace.ia.action.ENUM_Action;
import mercandalli.com.filespace.listeners.IModelFormListener;
import mercandalli.com.filespace.listeners.IPostExecuteListener;
import mercandalli.com.filespace.models.ModelForm;
import mercandalli.com.filespace.net.TaskPost;
import mercandalli.com.filespace.utils.StringPair;

import static mercandalli.com.filespace.utils.AlarmUtils.setAlarmFromString;
import static mercandalli.com.filespace.utils.NetUtils.isInternetConnection;

/**
 * Created by Jonathan on 19/04/2015.
 */
public class InterpreterActionContains extends Interpreter {

    public InterpreterActionContains(Application app, Resource res) {
        super(app, res);
    }

    @Override
    public InterpreterResult interpret(String input) {
        String output = null;

        if(input.contains("quit") || input.contains("kit l'app"))
            output = ENUM_Action.QUIT.action.action(this.app, "Bye.");

        else if(input.contains("alarme") || input.contains("reveil")) {
            setAlarmFromString(app, input);
            output = "Je lance l'application permettant de d�finir les alarmes.";
        }

        else if(input.contains("note")) {
            InterpreterResult interpreterResult = new InterpreterResult();
            interpreterResult.content = this.res.getSentence("action_note_remplir");
            interpreterResult.modelForm = new ModelForm();
            interpreterResult.modelForm.input1Text = "Your note";

            String startsWith = this.res.startsWithSentenece("action_note_start_with", input);
            if(startsWith != null) {
                interpreterResult.modelForm.input1EditText = input.replaceFirst(startsWith, "");
                interpreterResult.content = this.res.getSentence("action_note_valider");
            }

            interpreterResult.modelForm.sendListener = new IModelFormListener() {
                @Override
                public void execute(ModelForm modelFile) {
                    if(isInternetConnection(app)) {
                        String url = app.getConfig().getUrlServer() + app.getConfig().routeUserConversation + "/" + app.getConfig().getUserId();
                        List< StringPair > parameters = new ArrayList<>();
                        parameters.add(new StringPair("message", "" + modelFile.input1EditText));

                        new TaskPost(app, url, new IPostExecuteListener() {
                            @Override
                            public void execute(JSONObject json, String body) {

                            }
                        }, parameters).execute();
                    }
                }
            };
            return interpreterResult;
        }

        return new InterpreterResult(output);
    }

}
