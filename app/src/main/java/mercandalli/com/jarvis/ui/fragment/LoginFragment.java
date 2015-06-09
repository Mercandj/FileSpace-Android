/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.listener.IPostExecuteListener;
import mercandalli.com.jarvis.model.ModelUser;
import mercandalli.com.jarvis.net.TaskGet;
import mercandalli.com.jarvis.ui.activity.ActivityMain;
import mercandalli.com.jarvis.ui.activity.Application;
import mercandalli.com.jarvis.util.HashUtils;

import static mercandalli.com.jarvis.util.NetUtils.isInternetConnection;

public class LoginFragment extends Fragment {

	private Application app;

    private boolean requestLaunch = false; // Block the second task if one launch

    EditText username, password;

	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.app = (Application) activity;
    }

	public LoginFragment() {
		super();
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inscription, container, false);
        this.username = (EditText) rootView.findViewById(R.id.username);
        this.password = (EditText) rootView.findViewById(R.id.password);

        if(this.app.getConfig().getUserUsername()!=null)
            if(!this.app.getConfig().getUserUsername().equals("")) {
                this.username.setText(this.app.getConfig().getUserUsername());
            }

        if(this.app.getConfig().getUserPassword()!=null)
            if(!this.app.getConfig().getUserPassword().equals("")) {
                this.password.setHint("•••••••••");
            }

        ((CheckBox) rootView.findViewById(R.id.autoconnection)).setChecked(app.getConfig().isAutoConncetion());
        ((CheckBox) rootView.findViewById(R.id.autoconnection)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                app.getConfig().setAutoConnection(isChecked);
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
                    clickSignIn();
                    return true;
                }
                return false;
            }
        });

        SignInButton btnSignIn = (SignInButton) rootView.findViewById(R.id.btn_sign_in);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGplus();
            }
        });

        // Initializing google plus api client
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {

                    @Override
                    public void onConnected(Bundle arg0) {
                        mSignInClicked = false;
                        Toast.makeText(getActivity(), "User is connected!", Toast.LENGTH_LONG).show();

                        // Get user's information
                        getProfileInformation();
                    }

                    @Override
                    public void onConnectionSuspended(int arg0) {
                        mGoogleApiClient.connect();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        if (!result.hasResolution()) {
                            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), getActivity(),
                                    0).show();
                            return;
                        }

                        if (!mIntentInProgress) {
                            // Store the ConnectionResult for later usage
                            mConnectionResult = result;

                            if (mSignInClicked) {
                                // The user has already clicked 'sign-in' so we attempt to
                                // resolve all
                                // errors until the user is signed in, or they cancel.
                                resolveSignInError();
                            }
                        }

                    }
                }).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();


        return rootView;
    }

    public void connectionSucceed() {
        Intent intent = new Intent(getActivity(), ActivityMain.class);
        this.startActivity(intent);
        getActivity().overridePendingTransition(R.anim.left_in, R.anim.left_out);
        getActivity().finish();
    }

    public void clickSignIn() {
        if (requestLaunch)
            return;
        requestLaunch = true;

        ModelUser user = new ModelUser();

        if (!username.getText().toString().equals("")) {
            user.username = username.getText().toString();
            app.getConfig().setUserUsername(user.username);
        } else
            user.username = app.getConfig().getUserUsername();

        if (!password.getText().toString().equals("")) {
            user.password = HashUtils.sha1(password.getText().toString());
            app.getConfig().setUserPassword(user.password);
        } else
            user.password = app.getConfig().getUserPassword();

        if (app.getConfig().getUrlServer() == null) {
            requestLaunch = false;
            return;
        }
        if (app.getConfig().getUrlServer().equals("")) {
            requestLaunch = false;
            return;
        }

        // Login : POST /user
        List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
        parameters.add(new BasicNameValuePair("login", "true"));
        if(isInternetConnection(app))
            (new TaskGet(app, app.getConfig().getUser(), app.getConfig().getUrlServer() + app.getConfig().routeUser, new IPostExecuteListener() {
                @Override
                public void execute(JSONObject json, String body) {
                    try {
                        if (json != null) {
                            if (json.has("succeed"))
                                if (json.getBoolean("succeed")) {
                                    connectionSucceed();
                                }
                            if (json.has("user")) {
                                JSONObject user = json.getJSONObject("user");
                                if (user.has("id"))
                                    app.getConfig().setUserId(user.getInt("id"));
                                if (user.has("admin"))
                                    app.getConfig().setUserAdmin(user.getBoolean("admin"));
                                if (user.has("id_file_profile_picture"))
                                    app.getConfig().setUserIdFileProfilePicture(user.getInt("id_file_profile_picture"));
                            }
                        } else
                            Toast.makeText(app, app.getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requestLaunch = false;
                }
            }, parameters)).execute();
    }



    /****************************************
     *      Login via Google+
     ****************************************/
    private static final int RC_SIGN_IN = 0;

    // Profile pic image size in pixels
    private static final int PROFILE_PIC_SIZE = 400;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;


    /**
     * A flag indicating that a PendingIntent is in progress and prevents us
     * from starting further intents.
     */
    private boolean mIntentInProgress;

    private boolean mSignInClicked;

    private ConnectionResult mConnectionResult;

    /**
     * Sign-in into google
     * */
    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    /**
     * Method to resolve any signin errors
     * */
    private void resolveSignInError() {
        if (mConnectionResult == null) {
            mIntentInProgress = false;
            mGoogleApiClient.connect();
            return;
        }
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(getActivity(), RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }


    /**
     *  * Fetching user's information name, email, profile pic
     *  *
     */
    private void getProfileInformation() {
        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            Person currentPerson = Plus.PeopleApi
                    .getCurrentPerson(mGoogleApiClient);
            String personName = currentPerson.getDisplayName();
            String personPhotoUrl = currentPerson.getImage().getUrl();
            String personGooglePlusProfile = currentPerson.getUrl();
            String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

            Log.e("LoginFragment", "Name: " + personName + ", plusProfile: "
                    + personGooglePlusProfile + ", email: " + email
                    + ", Image: " + personPhotoUrl);

            // by default the profile url gives 50x50 px image only
            // we can replace the value with whatever dimension we want by
            // replacing sz=X
            personPhotoUrl = personPhotoUrl.substring(0,
                    personPhotoUrl.length() - 2)
                    + PROFILE_PIC_SIZE;

        } else if (Plus.AccountApi.getAccountName(mGoogleApiClient) != null) {
            Toast.makeText(getActivity(), ""+Plus.AccountApi.getAccountName(mGoogleApiClient), Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getActivity(), "Person information is null", Toast.LENGTH_LONG).show();
        }
    }

    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }
}
