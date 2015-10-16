/**
 * This file is part of Jarvis for Android, an app for managing your server (files, talks...).
 * <p/>
 * Copyright (c) 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 * <p/>
 * LICENSE:
 * <p/>
 * Jarvis for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 * <p/>
 * Jarvis for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 */
package mercandalli.com.filespace.ui.dialogs;

import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.listeners.IPostExecuteListener;
import mercandalli.com.filespace.models.ModelGenealogyPerson;
import mercandalli.com.filespace.net.TaskPost;
import mercandalli.com.filespace.ui.activities.ApplicationActivity;
import mercandalli.com.filespace.ui.fragments.genealogy.GenealogyListFragment;
import mercandalli.com.filespace.utils.StringPair;
import mercandalli.com.filespace.utils.StringUtils;

import static mercandalli.com.filespace.utils.NetUtils.isInternetConnection;

public class DialogAddGenealogyPerson extends Dialog {

    private ApplicationActivity app;
    private EditText et_first_name_1;
    private EditText et_first_name_2;
    private EditText et_first_name_3;
    private EditText et_last_name;
    private EditText et_date_birth;
    private EditText et_date_death;
    private EditText et_description;
    private Button bt_add, bt_father, bt_mother, bt_marriage, bt_remove_marriage;
    private TextView tv_marriage;

    private CheckBox sex;

    private ModelGenealogyPerson mother, father;
    private List<ModelGenealogyPerson> marriages;

    public DialogAddGenealogyPerson(final ApplicationActivity app, final IPostExecuteListener listener) {
        this(app, listener, app.getString(R.string.genealogy_add_person), null);
    }

    public DialogAddGenealogyPerson(final ApplicationActivity app, final IPostExecuteListener listener, String title, final ModelGenealogyPerson genealogyUser) {
        super(app);
        this.app = app;

        this.setContentView(R.layout.dialog_add_genealogy_person);
        this.setTitle(title);
        this.setCancelable(true);

        this.et_first_name_1 = (EditText) this.findViewById(R.id.et_first_name_1);
        this.et_first_name_2 = (EditText) this.findViewById(R.id.et_first_name_2);
        this.et_first_name_3 = (EditText) this.findViewById(R.id.et_first_name_3);
        this.et_last_name = (EditText) this.findViewById(R.id.et_last_name);
        this.et_date_birth = (EditText) this.findViewById(R.id.et_date_birth);
        this.et_date_death = (EditText) this.findViewById(R.id.et_date_death);
        this.et_description = (EditText) this.findViewById(R.id.et_description);
        this.bt_add = (Button) this.findViewById(R.id.add);
        this.bt_marriage = (Button) this.findViewById(R.id.bt_marriage);
        this.bt_remove_marriage = (Button) this.findViewById(R.id.bt_remove_marriage);
        this.tv_marriage = (TextView) this.findViewById(R.id.tv_marriage);

        this.sex = (CheckBox) this.findViewById(R.id.sex);
        this.sex.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sex.setText(isChecked ? "Is man" : "Is woman");
            }
        });

        this.bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isFormEmpty = StringUtils.isNullOrEmpty(et_last_name.getText().toString()) &&
                        StringUtils.isNullOrEmpty(et_first_name_1.getText().toString()) &&
                        StringUtils.isNullOrEmpty(et_first_name_2.getText().toString()) &&
                        StringUtils.isNullOrEmpty(et_first_name_3.getText().toString());

                if (isInternetConnection(app) && !isFormEmpty) {
                    List<StringPair> parameters = new ArrayList<>();

                    if (!StringUtils.isNullOrEmpty(et_first_name_1.getText().toString()))
                        parameters.add(new StringPair("first_name_1", et_first_name_1.getText().toString()));
                    if (!StringUtils.isNullOrEmpty(et_first_name_2.getText().toString()))
                        parameters.add(new StringPair("first_name_2", et_first_name_2.getText().toString()));
                    if (!StringUtils.isNullOrEmpty(et_first_name_3.getText().toString()))
                        parameters.add(new StringPair("first_name_3", et_first_name_3.getText().toString()));
                    if (!StringUtils.isNullOrEmpty(et_last_name.getText().toString()))
                        parameters.add(new StringPair("last_name", et_last_name.getText().toString()));
                    if (!StringUtils.isNullOrEmpty(et_description.getText().toString()))
                        parameters.add(new StringPair("description", et_description.getText().toString()));
                    if (!StringUtils.isNullOrEmpty(et_date_birth.getText().toString()))
                        if (et_date_birth.getText().toString().length() == 10)
                            parameters.add(new StringPair("date_birth", et_date_birth.getText().toString() + " 12:00:00"));
                    if (!StringUtils.isNullOrEmpty(et_date_death.getText().toString()))
                        if (et_date_death.getText().toString().length() == 10)
                            parameters.add(new StringPair("date_death", et_date_death.getText().toString() + " 12:00:00"));
                    if (father != null)
                        parameters.add(new StringPair("id_father", "" + father.id));
                    if (mother != null)
                        parameters.add(new StringPair("id_mother", "" + mother.id));

                    parameters.add(new StringPair("is_man", "" + sex.isChecked()));

                    if (genealogyUser == null)
                        (new TaskPost(app, app.getConfig().getUrlServer() + app.getConfig().routeGenealogy, listener, parameters)).execute();
                    else
                        (new TaskPost(app, app.getConfig().getUrlServer() + app.getConfig().routeGenealogyPut + "/" + genealogyUser.id, listener, parameters)).execute();

                    DialogAddGenealogyPerson.this.dismiss();
                } else if (isFormEmpty)
                    Toast.makeText(app, "No name or first name", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(app, app.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            }
        });

        this.bt_mother = (Button) this.findViewById(R.id.bt_mother);
        this.bt_mother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(app, "Select the mother", Toast.LENGTH_SHORT).show();
                GenealogyListFragment.resetMode();
                GenealogyListFragment.MODE_SELECTION_MOTHER = true;
                app.dialog.hide();
            }
        });

        this.bt_father = (Button) this.findViewById(R.id.bt_father);
        this.bt_father.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(app, "Select the father", Toast.LENGTH_SHORT).show();
                GenealogyListFragment.resetMode();
                GenealogyListFragment.MODE_SELECTION_FATHER = true;
                app.dialog.hide();
            }
        });

        this.bt_marriage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(app, "Select a partner", Toast.LENGTH_SHORT).show();
                GenealogyListFragment.resetMode();
                GenealogyListFragment.MODE_SELECTION_PARTNER = true;
                app.dialog.hide();
            }
        });

        this.bt_remove_marriage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                marriages = null;
                refresh();
            }
        });

        if (genealogyUser != null) {
            this.bt_add.setText(R.string.modify);
            if (!StringUtils.isNullOrEmpty(genealogyUser.first_name_1))
                this.et_first_name_1.setText(genealogyUser.first_name_1);
            if (!StringUtils.isNullOrEmpty(genealogyUser.first_name_2))
                this.et_first_name_2.setText(genealogyUser.first_name_2);
            if (!StringUtils.isNullOrEmpty(genealogyUser.first_name_3))
                this.et_first_name_3.setText(genealogyUser.first_name_3);
            if (!StringUtils.isNullOrEmpty(genealogyUser.last_name))
                this.et_last_name.setText(genealogyUser.last_name);
            if (!StringUtils.isNullOrEmpty(genealogyUser.date_birth))
                this.et_date_birth.setText(StringUtils.substring(genealogyUser.date_birth, 10));
            if (!StringUtils.isNullOrEmpty(genealogyUser.date_death))
                this.et_date_death.setText(StringUtils.substring(genealogyUser.date_death, 10));
            if (!StringUtils.isNullOrEmpty(genealogyUser.description))
                this.et_description.setText(genealogyUser.description);
            this.sex.setChecked(genealogyUser.is_man);
        }

        DialogAddGenealogyPerson.this.show();
    }

    public void setMother(ModelGenealogyPerson genealogyPerson) {
        this.mother = genealogyPerson;
        refresh();
    }

    public void setFather(ModelGenealogyPerson genealogyPerson) {
        this.father = genealogyPerson;
        refresh();
    }

    public void addPartner(ModelGenealogyPerson genealogyPerson) {
        if (this.marriages == null)
            this.marriages = new ArrayList<>();
        if (genealogyPerson != null) {
            boolean addPerson = true;
            for (ModelGenealogyPerson person : this.marriages) {
                if (person.id == genealogyPerson.id)
                    addPerson = false;
            }
            if (addPerson)
                this.marriages.add(genealogyPerson);
        }
        refresh();
    }

    private void refresh() {
        if (this.mother != null)
            this.bt_mother.setText("" + this.mother.first_name_1);
        if (this.father != null)
            this.bt_father.setText("" + this.father.first_name_1);
        this.tv_marriage.setText(null);
        if (this.marriages != null) {
            if (this.marriages.size() != 0) {
                String tv_marriage_str = "Partner" + (this.marriages.size() > 1 ? "s: " : ": ");
                int marriages_size = this.marriages.size();
                for (int i = 0; i < marriages_size; i++) {
                    tv_marriage_str += this.marriages.get(i).first_name_1 + (i < marriages_size - 1 ? ", " : "");
                }
                this.tv_marriage.setText(tv_marriage_str);
            }
        }
    }

}
