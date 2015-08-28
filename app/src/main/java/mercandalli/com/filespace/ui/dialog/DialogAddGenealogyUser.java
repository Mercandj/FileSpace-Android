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
package mercandalli.com.filespace.ui.dialog;

import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.listener.IPostExecuteListener;
import mercandalli.com.filespace.net.TaskPost;
import mercandalli.com.filespace.ui.activity.Application;
import mercandalli.com.filespace.util.StringPair;
import mercandalli.com.filespace.util.StringUtils;

public class DialogAddGenealogyUser extends Dialog {

	private Application app;
    private EditText et_first_name_1;
    private EditText et_first_name_2;
    private EditText et_first_name_3;
    private EditText et_last_name;
    private EditText et_date_birth;
    private EditText et_date_death;

    private CheckBox sexe;

	public DialogAddGenealogyUser(final Application app, final IPostExecuteListener listener) {
		super(app);
		this.app = app;
		
		this.setContentView(R.layout.dialog_add_genealogy_user);
		this.setTitle(R.string.genealogy_add_user);
		this.setCancelable(true);

        et_first_name_1 = (EditText) this.findViewById(R.id.et_first_name_1);
        et_first_name_2 = (EditText) this.findViewById(R.id.et_first_name_2);
        et_first_name_3 = (EditText) this.findViewById(R.id.et_first_name_3);
        et_last_name = (EditText) this.findViewById(R.id.et_last_name);
        et_date_birth = (EditText) this.findViewById(R.id.et_date_birth);
        et_date_death = (EditText) this.findViewById(R.id.et_date_death);

        sexe = (CheckBox) this.findViewById(R.id.sexe);
        sexe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sexe.setText(isChecked ? "Is man" : "Is woman");
            }
        });

        ((Button) this.findViewById(R.id.add)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<StringPair> parameters = new ArrayList<>();

                if (!StringUtils.isNullOrEmpty(et_first_name_1.getText().toString()))
                    parameters.add(new StringPair("first_name_1", et_first_name_1.getText().toString()));
                if (!StringUtils.isNullOrEmpty(et_first_name_2.getText().toString()))
                    parameters.add(new StringPair("first_name_2", et_first_name_2.getText().toString()));
                if (!StringUtils.isNullOrEmpty(et_first_name_3.getText().toString()))
                    parameters.add(new StringPair("first_name_3", et_first_name_3.getText().toString()));
                if (!StringUtils.isNullOrEmpty(et_last_name.getText().toString()))
                    parameters.add(new StringPair("last_name", et_last_name.getText().toString()));
                if (!StringUtils.isNullOrEmpty(et_date_birth.getText().toString()))
                    if (et_date_birth.getText().toString().length() == 10)
                        parameters.add(new StringPair("date_birth", et_date_birth.getText().toString() + " 12:00:00"));
                if (!StringUtils.isNullOrEmpty(et_date_death.getText().toString()))
                    if (et_date_death.getText().toString().length() == 10)
                        parameters.add(new StringPair("date_death", et_date_death.getText().toString() + " 12:00:00"));

                parameters.add(new StringPair("is_man", "" + sexe.isChecked()));

                (new TaskPost(app, app.getConfig().getUrlServer() + app.getConfig().routeGenealogy, listener, parameters)).execute();

                DialogAddGenealogyUser.this.dismiss();
            }
        });
        
        DialogAddGenealogyUser.this.show();
	}

}
