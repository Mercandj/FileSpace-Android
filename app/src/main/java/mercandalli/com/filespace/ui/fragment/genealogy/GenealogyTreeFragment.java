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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.listener.IPostExecuteListener;
import mercandalli.com.filespace.model.ModelGenealogyUser;
import mercandalli.com.filespace.net.TaskGet;
import mercandalli.com.filespace.ui.activity.Application;
import mercandalli.com.filespace.ui.fragment.Fragment;
import mercandalli.com.filespace.util.StringPair;

import static mercandalli.com.filespace.util.NetUtils.isInternetConnection;

/**
 * Created by Jonathan on 28/08/2015.
 */
public class GenealogyTreeFragment extends Fragment {


    private Application app;
    private View rootView;

    private static ModelGenealogyUser genealogyUser = null;
    private boolean requestReady = true;

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

    public void getChildren(int id_user) {
        List<StringPair> parameters = null;
        if(isInternetConnection(app) && app.isLogged()) {
            if (requestReady) {
                requestReady = false;
                new TaskGet(
                        app,
                        this.app.getConfig().getUser(),
                        this.app.getConfig().getUrlServer() + this.app.getConfig().routeGenealogyChildren + "/" + id_user,
                        new IPostExecuteListener() {
                            @Override
                            public void execute(JSONObject json, String body) {
                                requestReady = true;
                                List<ModelGenealogyUser> listChildren = new ArrayList<>();
                                try {
                                    if (json != null) {
                                        if (json.has("result")) {
                                            JSONArray array = json.getJSONArray("result");
                                            for (int i = 0; i < array.length(); i++) {
                                                listChildren.add(new ModelGenealogyUser(app, array.getJSONObject(i)));
                                            }
                                        }
                                    } else
                                        Toast.makeText(app, app.getString(R.string.action_failed), Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if(listChildren.size() != 0) {
                                    GenealogyTreeFragment.genealogyUser = listChildren.get(0);
                                    GenealogyTreeFragment.genealogyUser.selected = true;
                                }
                                else {
                                    Toast.makeText(app, "No children", Toast.LENGTH_SHORT).show();
                                }
                                update();
                            }
                        },
                        parameters
                ).execute();
            }
            else
                Toast.makeText(app, getString(R.string.waiting_for_response), Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(app, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }
    }

    public void changeUser(int id_user) {

        List<StringPair> parameters = null;
        if(isInternetConnection(app) && app.isLogged()) {
            if (requestReady) {
                requestReady = false;
                new TaskGet(
                        app,
                        this.app.getConfig().getUser(),
                        this.app.getConfig().getUrlServer() + this.app.getConfig().routeGenealogy + "/" + id_user,
                        new IPostExecuteListener() {
                            @Override
                            public void execute(JSONObject json, String body) {
                                requestReady = true;
                                try {
                                    if (json != null) {
                                        if (json.has("result")) {
                                            GenealogyTreeFragment.this.genealogyUser = new ModelGenealogyUser(app, json.getJSONObject("result"));
                                            GenealogyTreeFragment.this.genealogyUser.selected = true;
                                        }
                                    } else
                                        Toast.makeText(app, app.getString(R.string.action_failed), Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                update();
                            }
                        },
                        parameters
                ).execute();
            }
            else
                Toast.makeText(app, getString(R.string.waiting_for_response), Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(app, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }
    }

    public void update() {
        this.et_user.setText("");
        this.et_father.setText("");
        this.et_mother.setText("");

        if(genealogyUser != null)
            if(genealogyUser.selected) {
                this.et_user.setText(genealogyUser.getAdapterTitle());
                this.et_user.setTextColor(Color.parseColor(genealogyUser.is_man ? "#1976D2" : "#E91E63"));
                this.et_user.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (genealogyUser != null) {
                            genealogyUser.modify(new IPostExecuteListener() {
                                @Override
                                public void execute(JSONObject json, String body) {
                                    update();
                                }
                            });
                        }
                        return false;
                    }
                });
                this.et_user.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (genealogyUser != null) {
                            getChildren(genealogyUser.id);
                        }
                    }
                });

                if (genealogyUser.father != null) {
                    this.et_father.setText(genealogyUser.father.getAdapterTitle());
                    this.et_father.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            if (genealogyUser.father != null) {
                                genealogyUser.father.modify(new IPostExecuteListener() {
                                    @Override
                                    public void execute(JSONObject json, String body) {
                                        update();
                                    }
                                });
                            }
                            return false;
                        }
                    });
                    this.et_father.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (genealogyUser.father != null) {
                                changeUser(genealogyUser.id_father);
                            }
                        }
                    });
                }
                if (genealogyUser.mother != null) {
                    this.et_mother.setText(genealogyUser.mother.getAdapterTitle());
                    this.et_mother.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            if (genealogyUser.mother != null) {
                                genealogyUser.mother.modify(new IPostExecuteListener() {
                                    @Override
                                    public void execute(JSONObject json, String body) {
                                        update();
                                    }
                                });
                            }
                            return false;
                        }
                    });
                    this.et_mother.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (genealogyUser.mother != null) {
                                changeUser(genealogyUser.id_mother);
                            }
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
