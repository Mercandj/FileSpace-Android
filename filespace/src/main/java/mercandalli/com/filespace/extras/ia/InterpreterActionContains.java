/**
 * This file is part of FileSpace for Android, an app for managing your server (files, talks...).
 * <p/>
 * Copyright (c) 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 * <p/>
 * LICENSE:
 * <p/>
 * FileSpace for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 * <p/>
 * FileSpace for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 FileSpace for Android contributors (http://mercandalli.com)
 */
package mercandalli.com.filespace.extras.ia;

import android.content.Context;

import mercandalli.com.filespace.extras.ia.action.ENUM_Action;
import mercandalli.com.filespace.common.listener.IModelFormListener;
import mercandalli.com.filespace.common.model.ModelForm;
import mercandalli.com.filespace.common.util.AlarmUtils;

/**
 * Created by Jonathan on 19/04/2015.
 */
public class InterpreterActionContains extends Interpreter {

    public InterpreterActionContains(Context context, Resource resource) {
        super(context, resource);
    }

    @Override
    public InterpreterResult interpret(String input) {
        String output = null;

        if (input.contains("quit") || input.contains("kit l'app"))
            output = ENUM_Action.QUIT.action.action(mContext, "Bye.");

        else if (input.contains("alarme") || input.contains("reveil")) {
            AlarmUtils.setAlarmFromString(mContext, input);
            output = "Je lance l'application permettant de définir les alarmes.";
        } else if (input.contains("note")) {
            InterpreterResult interpreterResult = new InterpreterResult();
            interpreterResult.content = mResource.getSentence("action_note_remplir");
            interpreterResult.modelForm = new ModelForm();
            interpreterResult.modelForm.input1Text = "Your note";

            String startsWith = mResource.startsWithSentenece("action_note_start_with", input);
            if (startsWith != null) {
                interpreterResult.modelForm.input1EditText = input.replaceFirst(startsWith, "");
                interpreterResult.content = mResource.getSentence("action_note_valider");
            }

            interpreterResult.modelForm.sendListener = new IModelFormListener() {
                @Override
                public void execute(ModelForm modelFile) {
                    /*
                    if (NetUtils.isInternetConnection(mContext)) {
                        String url = app.getConfig().getUrlServer() + app.getConfig().routeUserConversation + "/" + app.getConfig().getUserId();
                        List<StringPair> parameters = new ArrayList<>();
                        parameters.add(new StringPair("message", "" + modelFile.input1EditText));

                        new TaskPost(app, url, new IPostExecuteListener() {
                            @Override
                            public void onPostExecute(JSONObject json, String body) {

                            }
                        }, parameters).execute();
                    }
                    */
                }
            };
            return interpreterResult;
        }

        return new InterpreterResult(output);
    }

}
