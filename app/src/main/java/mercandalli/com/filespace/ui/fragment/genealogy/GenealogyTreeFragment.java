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
package mercandalli.com.filespace.ui.fragment.genealogy;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.json.JSONObject;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.listener.IPostExecuteListener;
import mercandalli.com.filespace.model.ModelGenealogyUser;
import mercandalli.com.filespace.ui.activity.Application;
import mercandalli.com.filespace.ui.fragment.Fragment;

/**
 * Created by Jonathan on 28/08/2015.
 */
public class GenealogyTreeFragment extends Fragment {


    private Application app;
    private View rootView;

    static ModelGenealogyUser genealogyUser = null;

    private EditText et_user, et_father, et_mother;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        app = (Application) activity;
    }

    public GenealogyTreeFragment() {
        super();
    }

    public GenealogyTreeFragment(Application app) {
        this.app = app;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_genealogy_tree, container, false);

        this.et_user = (EditText) this.rootView.findViewById(R.id.user);
        this.et_father = (EditText) this.rootView.findViewById(R.id.et_father);
        this.et_mother = (EditText) this.rootView.findViewById(R.id.et_mother);

        return rootView;
    }

    public void update() {
        this.et_user.setText("");
        this.et_father.setText("");
        this.et_mother.setText("");

        if(genealogyUser != null)
            if(genealogyUser.selected) {
                this.et_user.setText(genealogyUser.getAdapterTitle());
                this.et_user.setTextColor(Color.parseColor(genealogyUser.is_man ? "#1976D2" : "#E91E63"));
                this.et_user.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(genealogyUser != null)
                            genealogyUser.modify(new IPostExecuteListener() {
                                @Override
                                public void execute(JSONObject json, String body) {
                                    update();
                                }
                            });
                    }
                });

                if(genealogyUser.father != null) {
                    this.et_father.setText(genealogyUser.father.getAdapterTitle());
                    this.et_father.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (genealogyUser.father != null)
                                genealogyUser.father.modify(new IPostExecuteListener() {
                                    @Override
                                    public void execute(JSONObject json, String body) {
                                        update();
                                    }
                                });
                        }
                    });
                }
                if(genealogyUser.mother != null) {
                    this.et_mother.setText(genealogyUser.mother.getAdapterTitle());
                    this.et_mother.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (genealogyUser.mother != null)
                                genealogyUser.mother.modify(new IPostExecuteListener() {
                                    @Override
                                    public void execute(JSONObject json, String body) {
                                        update();
                                    }
                                });
                        }
                    });
                }
            }
    }

    @Override
    public boolean back() {
        return false;
    }

    public static void select(ModelGenealogyUser genealogyUser_) {
        genealogyUser = genealogyUser_;
    }
}
