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
package com.mercandalli.android.apps.files.home;

import com.mercandalli.android.apps.files.common.model.Model;

import org.json.JSONObject;

public class ModelForm extends Model {

    public String input1EditText, input2EditText, input3EditText, input1Text, input2Text, input3Text;
    public IModelFormListener sendListener;

    public ModelForm() {
        super();
    }

    public void send() {
        if (this.sendListener != null)
            this.sendListener.execute(this);
    }

    @Override
    public JSONObject toJSONObject() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof ModelForm))
            return false;
        ModelForm obj = (ModelForm) o;

        if ((this.input1EditText != null && obj.input1EditText == null) || (this.input1EditText == null && obj.input1EditText != null))
            return false;
        if ((this.input2EditText != null && obj.input2EditText == null) || (this.input2EditText == null && obj.input2EditText != null))
            return false;
        if ((this.input3EditText != null && obj.input3EditText == null) || (this.input3EditText == null && obj.input3EditText != null))
            return false;
        if ((this.input1Text != null && obj.input1Text == null) || (this.input1Text == null && obj.input1Text != null))
            return false;
        if ((this.input2Text != null && obj.input2Text == null) || (this.input2Text == null && obj.input2Text != null))
            return false;
        if ((this.input3Text != null && obj.input3Text == null) || (this.input3Text == null && obj.input3Text != null))
            return false;

        if (this.input1EditText != null &&
                this.input2EditText != null &&
                this.input3EditText != null &&
                this.input1Text != null &&
                this.input2Text != null &&
                this.input3Text != null)
            return this.input1EditText.equals(obj.input1EditText) &&
                    this.input2EditText.equals(obj.input2EditText) &&
                    this.input3EditText.equals(obj.input3EditText) &&
                    this.input1Text.equals(obj.input1Text) &&
                    this.input2Text.equals(obj.input2Text) &&
                    this.input3Text.equals(obj.input3Text);

        if (this.input1EditText != null &&
                this.input2EditText != null &&
                this.input1Text != null &&
                this.input2Text != null)
            return this.input1EditText.equals(obj.input1EditText) &&
                    this.input2EditText.equals(obj.input2EditText) &&
                    this.input1Text.equals(obj.input1Text) &&
                    this.input2Text.equals(obj.input2Text);

        if (this.input1EditText != null &&
                this.input1Text != null)
            return this.input1EditText.equals(obj.input1EditText) &&
                    this.input1Text.equals(obj.input1Text);

        if (this.input1Text != null)
            return this.input1Text.equals(obj.input1Text);

        return false;
    }
}
