/**
 * Personal Project : Control server
 * <p/>
 * MERCANDALLI Jonathan
 */

package com.mercandalli.android.apps.files.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.fragment.BackFragment;
import com.mercandalli.android.apps.files.common.listener.IPostExecuteListener;
import com.mercandalli.android.apps.files.common.net.TaskPost;
import com.mercandalli.android.apps.files.common.util.StringPair;
import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.apps.files.main.Constants;
import com.mercandalli.android.apps.files.main.MainActivity;
import com.mercandalli.android.apps.files.main.network.NetUtils;
import com.mercandalli.android.library.baselibrary.java.HashUtils;
import com.mercandalli.android.library.baselibrary.java.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoginFragment extends BackFragment {

    private static final String ADMIN = "admin";

    private boolean requestLaunched = false; // Block the second task if one launch
    private EditText username, password;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_log_in, container, false);
        this.username = (EditText) rootView.findViewById(R.id.fragment_log_in_username);
        this.password = (EditText) rootView.findViewById(R.id.fragment_log_in_password);

        if (Config.getUserUsername() != null &&
                !Config.getUserUsername().equals("")) {
            this.username.setText(Config.getUserUsername());
        }

        if (Config.getUserPassword() != null &&
                !Config.getUserPassword().equals("")) {
            this.password.setHint(Html.fromHtml("&#8226;&#8226;&#8226;&#8226;&#8226;&#8226;&#8226;"));
        }

        ((CheckBox) rootView.findViewById(R.id.fragment_registration_auto_connection)).setChecked(mApplicationCallback.getConfig().isAutoConnection());
        ((CheckBox) rootView.findViewById(R.id.fragment_registration_auto_connection)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mApplicationCallback.getConfig().setAutoConnection(getContext(), isChecked);
            }
        });

        this.username.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    LoginFragment.this.password.requestFocus();
                    return true;
                }
                return false;
            }
        });

        this.password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    login();
                    return true;
                }
                return false;
            }
        });

        return rootView;
    }

    public void connectionSucceed() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        this.startActivity(intent);
        getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);
        getActivity().finish();
    }

    public void login() {
        UserModel user = new UserModel();

        if (!StringUtils.isNullOrEmpty(username.getText().toString())) {
            user.username = username.getText().toString();
        }

        if (!StringUtils.isNullOrEmpty(password.getText().toString())) {
            user.password = HashUtils.sha1(password.getText().toString());
        }

        login(user);
    }

    public void login(UserModel user) {
        Log.d("LoginFragment", "login requestLaunched=" + requestLaunched);
        if (requestLaunched) {
            return;
        }
        requestLaunched = true;

        if (!StringUtils.isNullOrEmpty(user.username)) {
            mApplicationCallback.getConfig().setUserUsername(getContext(), user.username);
        } else {
            user.username = Config.getUserUsername();
        }

        if (!StringUtils.isNullOrEmpty(user.password)) {
            mApplicationCallback.getConfig().setUserPassword(getContext(), user.password);
        } else {
            user.password = Config.getUserPassword();
        }

        // Login : POST /user
        List<StringPair> parameters = new ArrayList<>();
        //double latitude = GpsUtils.getLatitude(getActivity()),
        //        longitude = GpsUtils.getLongitude(getActivity());
        parameters.add(new StringPair("login", "true"));
        /*if (latitude != 0 && longitude != 0) {
            parameters.add(new StringPair("latitude", "" + latitude));
            parameters.add(new StringPair("longitude", "" + longitude));
            parameters.add(new StringPair("altitude", "" + GpsUtils.getAltitude(getActivity())));
        }*/
        Log.d("LoginFragment", "login " + Config.getUserPassword() +
                Config.getUserUsername() + " isInternetConnection=" +
                NetUtils.isInternetConnection(getContext()));
        if (NetUtils.isInternetConnection(getContext())) {
            (new TaskPost(getActivity(), mApplicationCallback, Constants.URL_DOMAIN + Config.ROUTE_USER, new IPostExecuteListener() {
                @Override
                public void onPostExecute(JSONObject json, String body) {
                    requestLaunched = false;
                    try {
                        if (json != null) {
                            if (json.has("succeed") && json.getBoolean("succeed")) {
                                connectionSucceed();
                            }
                            if (json.has("user")) {
                                JSONObject user = json.getJSONObject("user");
                                if (user.has("id")) {
                                    mApplicationCallback.getConfig().setUserId(getContext(), user.getInt("id"));
                                }
                                if (user.has(ADMIN)) {
                                    Object admin_obj = user.get(ADMIN);
                                    if (admin_obj instanceof Integer) {
                                        mApplicationCallback.getConfig().setUserAdmin(getContext(), user.getInt(ADMIN) == 1);
                                    } else if (admin_obj instanceof Boolean) {
                                        mApplicationCallback.getConfig().setUserAdmin(getContext(), user.getBoolean(ADMIN));
                                    }
                                }
                                if (user.has("id_file_profile_picture")) {
                                    mApplicationCallback.getConfig().setUserIdFileProfilePicture(getActivity(), user.getInt("id_file_profile_picture"));
                                }
                            }
                        } else {
                            Toast.makeText(getContext(), R.string.server_error, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e(getClass().getName(), "Failed to convert Json", e);
                    }
                }
            }, parameters)).execute();
        } else {
            requestLaunched = false;
        }
    }

    @Override
    public boolean back() {
        return false;
    }

    @Override
    public void onFocus() {

    }
}
