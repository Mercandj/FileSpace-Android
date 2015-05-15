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
package mercandalli.com.jarvis.ia;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.jarvis.ui.activity.Application;
import mercandalli.com.jarvis.ia.action.ENUM_Action;
import mercandalli.com.jarvis.listener.IModelFormListener;
import mercandalli.com.jarvis.listener.IPostExecuteListener;
import mercandalli.com.jarvis.model.ModelForm;
import mercandalli.com.jarvis.net.TaskPost;

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

        else if(input.contains("note")) {
            InterpreterResult interpreterResult = new InterpreterResult();
            interpreterResult.content = "Je vous laisse valider votre note.";
            interpreterResult.modelForm = new ModelForm();
            interpreterResult.modelForm.input1Text = "Your note";
            if(input.startsWith("note "))
                interpreterResult.modelForm.input1EditText = input.replaceFirst("note ", "");
            if(input.startsWith("note le message "))
                interpreterResult.modelForm.input1EditText = input.replaceFirst("note le message ", "");
            if(input.startsWith("note le message suivant "))
                interpreterResult.modelForm.input1EditText = input.replaceFirst("note le message suivant ", "");
            interpreterResult.modelForm.sendListener = new IModelFormListener() {
                @Override
                public void execute(ModelForm modelFile) {
                    if(app.isInternetConnection()) {
                        String url = app.getConfig().getUrlServer() + app.getConfig().routeUserConversation + "/" + app.getConfig().getUserId();
                        List< BasicNameValuePair > parameters = new ArrayList<>();
                        parameters.add(new BasicNameValuePair("message", "" + modelFile.input1EditText));

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
