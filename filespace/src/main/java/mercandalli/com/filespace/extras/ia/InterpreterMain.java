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
package mercandalli.com.filespace.extras.ia;

import android.util.Log;

import mercandalli.com.filespace.ui.activities.ApplicationActivity;

import static mercandalli.com.filespace.utils.StringUtils.nomalizeString;

/**
 * Created by Jonathan on 19/04/2015.
 */
public class InterpreterMain extends Interpreter {

    private Interpreter interpreterActionEquals,
            interpreterActionContains,
            interpreterRoboticsEquals,
            interpreterDialogEquals;

    public InterpreterMain(ApplicationActivity app) {
        super(app);
        interpreterActionEquals = new InterpreterActionEquals(app, this.res);
        interpreterActionContains = new InterpreterActionContains(app, this.res);
        interpreterRoboticsEquals = new InterpreterRoboticsEquals(app, this.res);
        interpreterDialogEquals = new InterpreterDialogEquals(app, this.res);
    }

    @Override
    public InterpreterResult interpret(String input) {
        Log.d("InterpreterMain", "input : "+input);

        input = nomalizeString(input);

        InterpreterResult outputActionEquals = interpreterActionEquals.interpret(input);
        if(outputActionEquals != null)
            if(!outputActionEquals.isEmpty())
                return outputActionEquals;

        if(this.app.getConfig().getUser().isAdmin()) {
            InterpreterResult outputRoboticsEquals = interpreterRoboticsEquals.interpret(input);
            if (outputRoboticsEquals != null)
                if(!outputRoboticsEquals.isEmpty())
                    return outputRoboticsEquals;
        }

        InterpreterResult outputDialogEquals = interpreterDialogEquals.interpret(input);
        if(outputDialogEquals != null)
            if(!outputDialogEquals.isEmpty())
                return outputDialogEquals;

        InterpreterResult outputActionContains = interpreterActionContains.interpret(input);
        if(outputActionContains != null)
            if(!outputActionContains.isEmpty())
                return outputActionContains;

        return new InterpreterResult(input);
    }

}
