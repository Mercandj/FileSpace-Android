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
package mercandalli.com.filespace.common.fragment.genealogy;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.common.listener.IPostExecuteListener;
import mercandalli.com.filespace.common.model.ModelGenealogyPerson;
import mercandalli.com.filespace.common.net.TaskGet;
import mercandalli.com.filespace.common.fragment.FabFragment;
import mercandalli.com.filespace.common.view.GenealogyBigTreeView;
import mercandalli.com.filespace.common.util.NetUtils;

/**
 * Created by Jonathan on 28/08/2015.
 */
public class GenealogyBigTreeFragment extends FabFragment {

    private View rootView;

    private static ModelGenealogyPerson genealogyPerson = null;
    private GenealogyBigTreeView bigTreeView;

    private static ModelGenealogyPerson genealogyUser = null;

    public static GenealogyBigTreeFragment newInstance() {
        return new GenealogyBigTreeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_genealogy_big_tree, container, false);

        this.bigTreeView = (GenealogyBigTreeView) rootView.findViewById(R.id.arbre_view);
        this.rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bigTreeView.onTouch(event);
                return true;
            }
        });

        ((Button) this.rootView.findViewById(R.id.reset)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bigTreeView.resetOffsetTouch();
            }
        });

        return rootView;
    }


    @Override
    public boolean back() {
        return false;
    }

    @Override
    public void onFocus() {
    }

    public void select(final ModelGenealogyPerson genealogyUser) {
        this.genealogyPerson = genealogyUser;

        if (NetUtils.isInternetConnection(mActivity)) {
            new TaskGet(
                    mActivity,
                    mApplicationCallback.getConfig().getUrlServer() + mApplicationCallback.getConfig().routeGenealogy + "/" + genealogyPerson.id,
                    new IPostExecuteListener() {
                        @Override
                        public void onPostExecute(JSONObject json, String body) {
                            try {
                                if (json != null) {
                                    if (json.has("result")) {
                                        genealogyPerson = new ModelGenealogyPerson(mActivity, mApplicationCallback, json.getJSONObject("result"));
                                        bigTreeView.select(genealogyPerson);
                                    }
                                } else
                                    Toast.makeText(mActivity, mActivity.getString(R.string.action_failed), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    null
            ).execute();
        }
        this.bigTreeView.select(genealogyUser);
    }

    @Override
    public void onFabClick(int fab_id, FloatingActionButton fab) {

    }

    @Override
    public boolean isFabVisible(int fab_id) {
        return false;
    }

    @Override
    public int getFabImageResource(int fab_id) {
        return -1;
    }
}
