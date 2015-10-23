/**
 * Personal Project : Control server
 * <p/>
 * MERCANDALLI Jonathan
 */

package mercandalli.com.filespace.ui.fragments;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.listeners.IPostExecuteListener;
import mercandalli.com.filespace.models.ModelUser;
import mercandalli.com.filespace.net.TaskPost;
import mercandalli.com.filespace.ui.activities.MainActivity;
import mercandalli.com.filespace.utils.GpsUtils;
import mercandalli.com.filespace.utils.HashUtils;
import mercandalli.com.filespace.utils.NetUtils;
import mercandalli.com.filespace.utils.StringPair;
import mercandalli.com.filespace.utils.StringUtils;

public class LoginFragment extends BackFragment {

    private boolean requestLaunched = false; // Block the second task if one launch
    private EditText username, password;

    public static LoginFragment newInstance() {
        Bundle args = new Bundle();
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inscription, container, false);
        this.username = (EditText) rootView.findViewById(R.id.username);
        this.password = (EditText) rootView.findViewById(R.id.password);

        if (this.mApplicationCallback.getConfig().getUserUsername() != null)
            if (!this.mApplicationCallback.getConfig().getUserUsername().equals("")) {
                this.username.setText(this.mApplicationCallback.getConfig().getUserUsername());
            }

        if (this.mApplicationCallback.getConfig().getUserPassword() != null)
            if (!this.mApplicationCallback.getConfig().getUserPassword().equals("")) {
                this.password.setHint(Html.fromHtml("&#8226;&#8226;&#8226;&#8226;&#8226;&#8226;&#8226;"));
            }

        ((CheckBox) rootView.findViewById(R.id.autoconnection)).setChecked(mApplicationCallback.getConfig().isAutoConncetion());
        ((CheckBox) rootView.findViewById(R.id.autoconnection)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mApplicationCallback.getConfig().setAutoConnection(isChecked);
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
        ModelUser user = new ModelUser();

        if (!StringUtils.isNullOrEmpty(username.getText().toString()))
            user.username = username.getText().toString();

        if (!StringUtils.isNullOrEmpty(password.getText().toString()))
            user.password = HashUtils.sha1(password.getText().toString());

        login(user);
    }

    public void login(ModelUser user) {
        Log.d("LoginFragment", "login requestLaunched=" + requestLaunched);
        if (requestLaunched)
            return;
        requestLaunched = true;

        if (!StringUtils.isNullOrEmpty(user.username))
            mApplicationCallback.getConfig().setUserUsername(user.username);
        else
            user.username = mApplicationCallback.getConfig().getUserUsername();

        if (!StringUtils.isNullOrEmpty(user.password))
            mApplicationCallback.getConfig().setUserPassword(user.password);
        else
            user.password = mApplicationCallback.getConfig().getUserPassword();

        if (StringUtils.isNullOrEmpty(mApplicationCallback.getConfig().getUrlServer())) {
            requestLaunched = false;
            return;
        }

        // Login : POST /user
        List<StringPair> parameters = new ArrayList<>();
        double latitude = GpsUtils.getLatitude(getActivity()),
                longitude = GpsUtils.getLongitude(getActivity());
        parameters.add(new StringPair("login", "true"));
        if (latitude != 0 && longitude != 0) {
            parameters.add(new StringPair("latitude", "" + latitude));
            parameters.add(new StringPair("longitude", "" + longitude));
            parameters.add(new StringPair("altitude", "" + GpsUtils.getAltitude(getActivity())));
        }
        Log.d("LoginFragment", "login " + mApplicationCallback.getConfig().getUserPassword() + mApplicationCallback.getConfig().getUserUsername() + " isInternetConnection=" + NetUtils.isInternetConnection(mActivity));
        if (NetUtils.isInternetConnection(mActivity))
            (new TaskPost(mActivity, mApplicationCallback, mApplicationCallback.getConfig().getUrlServer() + mApplicationCallback.getConfig().routeUser, new IPostExecuteListener() {
                @Override
                public void onPostExecute(JSONObject json, String body) {
                    requestLaunched = false;
                    try {
                        if (json != null) {
                            if (json.has("succeed"))
                                if (json.getBoolean("succeed")) {
                                    connectionSucceed();
                                }
                            if (json.has("user")) {
                                JSONObject user = json.getJSONObject("user");
                                if (user.has("id"))
                                    mApplicationCallback.getConfig().setUserId(user.getInt("id"));
                                if (user.has("admin")) {
                                    Object admin_obj = user.get("admin");
                                    if (admin_obj instanceof Integer)
                                        mApplicationCallback.getConfig().setUserAdmin(user.getInt("admin") == 1);
                                    else if (admin_obj instanceof Boolean)
                                        mApplicationCallback.getConfig().setUserAdmin(user.getBoolean("admin"));
                                }
                                if (user.has("id_file_profile_picture"))
                                    mApplicationCallback.getConfig().setUserIdFileProfilePicture(user.getInt("id_file_profile_picture"));
                            }
                        } else
                            Toast.makeText(mActivity, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, parameters)).execute();
        else
            requestLaunched = false;
    }

    @Override
    public boolean back() {
        return false;
    }

    @Override
    public void onFocus() {

    }
}
