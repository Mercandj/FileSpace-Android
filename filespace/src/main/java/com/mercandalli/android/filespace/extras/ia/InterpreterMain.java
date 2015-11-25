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
package com.mercandalli.android.filespace.extras.ia;

import android.content.Context;
import android.util.Log;

import com.mercandalli.android.filespace.common.util.StringUtils;

/**
 * Created by Jonathan on 19/04/2015.
 */
public class InterpreterMain extends Interpreter {

    private Interpreter interpreterActionEquals,
            interpreterActionContains,
            interpreterRoboticsEquals,
            interpreterDialogEquals;
    private boolean mIsAdmin = false;

    public InterpreterMain(Context context, boolean isAdmin) {
        super(context);
        mIsAdmin = isAdmin;
        interpreterActionEquals = new InterpreterActionEquals(context, mResource);
        interpreterActionContains = new InterpreterActionContains(context, mResource);
        //interpreterRoboticsEquals = new InterpreterRoboticsEquals(context, mResource);
        interpreterDialogEquals = new InterpreterDialogEquals(context, mResource);
    }

    @Override
    public InterpreterResult interpret(String input) {
        Log.d("InterpreterMain", "input : " + input);

        input = StringUtils.nomalizeString(input);

        InterpreterResult outputActionEquals = interpreterActionEquals.interpret(input);
        if (outputActionEquals != null)
            if (!outputActionEquals.isEmpty())
                return outputActionEquals;

        if (mIsAdmin) {
            InterpreterResult outputRoboticsEquals = interpreterRoboticsEquals.interpret(input);
            if (outputRoboticsEquals != null)
                if (!outputRoboticsEquals.isEmpty())
                    return outputRoboticsEquals;
        }

        InterpreterResult outputDialogEquals = interpreterDialogEquals.interpret(input);
        if (outputDialogEquals != null)
            if (!outputDialogEquals.isEmpty())
                return outputDialogEquals;

        InterpreterResult outputActionContains = interpreterActionContains.interpret(input);
        if (outputActionContains != null)
            if (!outputActionContains.isEmpty())
                return outputActionContains;

        return new InterpreterResult(input);
    }

}
