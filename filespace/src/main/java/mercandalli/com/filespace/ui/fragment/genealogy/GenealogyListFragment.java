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
package mercandalli.com.filespace.ui.fragment.genealogy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.listener.IListener;
import mercandalli.com.filespace.listener.IModelGenealogyUserListener;
import mercandalli.com.filespace.listener.IPostExecuteListener;
import mercandalli.com.filespace.model.ModelGenealogyPerson;
import mercandalli.com.filespace.net.TaskGet;
import mercandalli.com.filespace.ui.activitiy.ApplicationCallback;
import mercandalli.com.filespace.ui.adapter.AdapterModelGenealogyUser;
import mercandalli.com.filespace.ui.dialog.DialogAddGenealogyPerson;
import mercandalli.com.filespace.ui.fragment.FabFragment;
import mercandalli.com.filespace.ui.view.divider.DividerItemDecoration;
import mercandalli.com.filespace.util.DialogUtils;
import mercandalli.com.filespace.util.NetUtils;
import mercandalli.com.filespace.util.StringPair;
import mercandalli.com.filespace.util.StringUtils;

/**
 * Created by Jonathan on 28/08/2015.
 */
public class GenealogyListFragment extends FabFragment {

    private View rootView;

    private Dialog mDialog;

    private List<ModelGenealogyPerson> list;
    private RecyclerView recyclerView;
    private AdapterModelGenealogyUser mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar circularProgressBar;
    private TextView message;

    private IModelGenealogyUserListener onSelect;

    private View coordinatorLayoutView;

    public static boolean MODE_SELECTION_FATHER = false;
    public static boolean MODE_SELECTION_MOTHER = false;
    public static boolean MODE_SELECTION_PARTNER = false;

    private Activity mActivity;
    private ApplicationCallback mApplicationCallback;

    public static void resetMode() {
        MODE_SELECTION_FATHER = false;
        MODE_SELECTION_MOTHER = false;
        MODE_SELECTION_PARTNER = false;
    }

    public static GenealogyListFragment newInstance() {
        return new GenealogyListFragment();
    }

    public void setOnSelect(IModelGenealogyUserListener onSelect) {
        this.onSelect = onSelect;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_genealogy_list, container, false);
        this.coordinatorLayoutView = (View) rootView.findViewById(R.id.snackBarPosition);
        this.circularProgressBar = (ProgressBar) rootView.findViewById(R.id.circularProgressBar);
        this.message = (TextView) rootView.findViewById(R.id.message);

        this.recyclerView = (RecyclerView) rootView.findViewById(R.id.listView);
        this.recyclerView.setHasFixedSize(true);
        this.mLayoutManager = new LinearLayoutManager(getActivity());
        this.recyclerView.setLayoutManager(mLayoutManager);
        this.recyclerView.setItemAnimator(/*new SlideInFromLeftItemAnimator(mRecyclerView)*/new DefaultItemAnimator());
        this.recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });

        refreshList();

        return rootView;
    }

    public void refreshList() {
        refreshList(null);
    }

    public void refreshList(String search) {
        List<StringPair> parameters = new ArrayList<>();
        if (!StringUtils.isNullOrEmpty(search))
            parameters.add(new StringPair("search", search));
        if (NetUtils.isInternetConnection(mActivity) && mApplicationCallback.isLogged())
            new TaskGet(
                    mActivity,
                    mApplicationCallback,
                    mApplicationCallback.getConfig().getUrlServer() + mApplicationCallback.getConfig().routeGenealogy,
                    new IPostExecuteListener() {
                        @Override
                        public void onPostExecute(JSONObject json, String body) {
                            list = new ArrayList<>();
                            try {
                                if (json != null) {
                                    if (json.has("result")) {
                                        JSONArray array = json.getJSONArray("result");
                                        for (int i = 0; i < array.length(); i++) {
                                            ModelGenealogyPerson modelUser = new ModelGenealogyPerson(mActivity, mApplicationCallback, array.getJSONObject(i));
                                            list.add(modelUser);
                                        }
                                    }
                                } else
                                    Toast.makeText(mActivity, getString(R.string.action_failed), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            updateAdapter();
                        }
                    },
                    parameters
            ).execute();
        else {
            this.circularProgressBar.setVisibility(View.GONE);
            this.message.setText(mApplicationCallback.isLogged() ? getString(R.string.no_internet_connection) : getString(R.string.no_logged));
            this.message.setVisibility(View.VISIBLE);
            this.swipeRefreshLayout.setRefreshing(false);

            if (!NetUtils.isInternetConnection(mActivity)) {
                this.setListVisibility(false);
                this.refreshFab.execute();
            }
        }
    }

    public void updateAdapter() {
        if (this.recyclerView != null && this.list != null && this.isAdded()) {
            this.circularProgressBar.setVisibility(View.GONE);

            this.refreshFab.execute();
            this.recyclerView.setVisibility(View.VISIBLE);

            if (this.list.size() == 0) {
                this.message.setText(getString(R.string.no_person));
                this.message.setVisibility(View.VISIBLE);
            } else
                this.message.setVisibility(View.GONE);

            this.mAdapter = new AdapterModelGenealogyUser(mActivity, list, new IModelGenealogyUserListener() {
                @Override
                public void execute(final ModelGenealogyPerson modelGenealogyUser) {

                    final AlertDialog.Builder menuAlert = new AlertDialog.Builder(mActivity);
                    String[] menuList = {getString(R.string.modify), getString(R.string.delete), getString(R.string.properties)};
                    menuAlert.setTitle("Action");
                    menuAlert.setItems(menuList,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int item) {
                                    switch (item) {
                                        case 0:
                                            modelGenealogyUser.modify(new IPostExecuteListener() {
                                                @Override
                                                public void onPostExecute(JSONObject json, String body) {
                                                    refreshList();
                                                }
                                            });
                                            break;
                                        case 1:
                                            DialogUtils.alert(mActivity, "Delete", "Delete " + (modelGenealogyUser.first_name_1) + " ?", "Yes", new IListener() {
                                                @Override
                                                public void execute() {
                                                    modelGenealogyUser.delete(new IPostExecuteListener() {
                                                        @Override
                                                        public void onPostExecute(JSONObject json, String body) {
                                                            refreshList();
                                                        }
                                                    });
                                                }
                                            }, "No", null);
                                            break;
                                        case 2:
                                            DialogUtils.alert(mActivity,
                                                    getString(R.string.data) + " : " + modelGenealogyUser.first_name_1,
                                                    modelGenealogyUser.toSpanned(),
                                                    "OK",
                                                    null,
                                                    null,
                                                    null);
                                            break;

                                    }
                                }
                            });
                    AlertDialog menuDrop = menuAlert.create();
                    menuDrop.show();

                }
            }, false);
            this.recyclerView.setAdapter(mAdapter);

            this.mAdapter.setOnItemClickListener(new AdapterModelGenealogyUser.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (MODE_SELECTION_FATHER) {
                        if (mDialog != null) {
                            if (mDialog instanceof DialogAddGenealogyPerson) {
                                ((DialogAddGenealogyPerson) mDialog).setFather(list.get(position));
                                mDialog.show();
                            }
                        }

                    } else if (MODE_SELECTION_MOTHER) {
                        if (mDialog != null) {
                            if (mDialog instanceof DialogAddGenealogyPerson) {
                                ((DialogAddGenealogyPerson) mDialog).setMother(list.get(position));
                                mDialog.show();
                            }
                        }
                    } else if (MODE_SELECTION_PARTNER) {
                        if (mDialog != null) {
                            if (mDialog instanceof DialogAddGenealogyPerson) {
                                ((DialogAddGenealogyPerson) mDialog).addPartner(list.get(position));
                                mDialog.show();
                            }
                        }
                    } else {
                        DialogUtils.alert(mActivity,
                                getString(R.string.data) + " : " + list.get(position).first_name_1,
                                list.get(position).toSpanned(),
                                "OK",
                                null,
                                null,
                                null);
                    }
                    resetMode();
                }
            });

            this.mAdapter.setOnItemLongClickListener(new AdapterModelGenealogyUser.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(View view, int position) {
                    boolean tmp = !list.get(position).selected;
                    deselect();
                    list.get(position).selected = tmp;
                    mAdapter.notifyItemChanged(position);

                    onSelect.execute(list.get(position));

                    if (tmp)
                        Toast.makeText(mActivity, "Selected for tree", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });

            this.swipeRefreshLayout.setRefreshing(false);
        }
    }

    public void deselect() {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).selected) {
                list.get(i).selected = false;
                mAdapter.notifyItemChanged(i);
            }
        }
    }

    @Override
    public boolean back() {
        return false;
    }

    @Override
    public void onFocus() {
        //refreshList();
    }

    private void setListVisibility(boolean visible) {
        if (this.recyclerView != null)
            this.recyclerView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    public void add() {
        mDialog = new DialogAddGenealogyPerson(mActivity, mApplicationCallback, new IPostExecuteListener() {
            @Override
            public void onPostExecute(JSONObject json, String body) {
                refreshList();
            }
        });
    }

    @Override
    public void onFabClick(int fab_id, FloatingActionButton fab) {
        add();
    }

    @Override
    public boolean isFabVisible(int fab_id) {
        return true;
    }

    @Override
    public int getFabImageResource(int fab_id) {
        return android.R.drawable.ic_input_add;
    }
}
