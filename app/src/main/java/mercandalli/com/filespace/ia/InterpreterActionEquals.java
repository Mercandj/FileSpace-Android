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

import mercandalli.com.filespace.ui.activity.Application;
import mercandalli.com.filespace.ia.action.ENUM_Action;

/**
 * Created by Jonathan on 19/04/2015.
 */
public class InterpreterActionEquals extends Interpreter {

    public InterpreterActionEquals(Application app, Resource res) {
        super(app, res);
    }

    @Override
    public InterpreterResult interpret(String input) {
        String output = null;

        if(input.equals("recherche") || input.equals("recherche google") || input.equals("google") || input.equals("ouvre google"))
            output = ENUM_Action.WEB_SEARCH.action.action(this.app, "http://www.google.com/");

        return new InterpreterResult(output);
    }

}
