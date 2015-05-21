package mercandalli.com.jarvis.ui.fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.config.Const;
import mercandalli.com.jarvis.ia.Interpreter;
import mercandalli.com.jarvis.ia.InterpreterMain;
import mercandalli.com.jarvis.ia.InterpreterResult;
import mercandalli.com.jarvis.listener.IModelHomeListener;
import mercandalli.com.jarvis.model.ModelHome;
import mercandalli.com.jarvis.model.ModelServerMessage;
import mercandalli.com.jarvis.ui.activity.Application;
import mercandalli.com.jarvis.ui.activity.ApplicationDrawer;
import mercandalli.com.jarvis.ui.adapter.AdapterModelHome;

/**
 * Created by Jonathan on 03/01/2015.
 */
public class HomeFragment extends Fragment implements TextToSpeech.OnInitListener {

    private Application app;
    private View rootView;

    private RecyclerView recyclerView;
    private AdapterModelHome mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    List<ModelHome> list;
    private ProgressBar circulerProgressBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    public TextToSpeech myTTS;

    private EditText input;

    public HomeFragment(Application app) {
        this.app = app;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_home, container, false);
        circulerProgressBar = (ProgressBar) rootView.findViewById(R.id.circulerProgressBar);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        (rootView.findViewById(R.id.circle)).setVisibility(View.GONE);

        this.input = (EditText) rootView.findViewById(R.id.input);
        this.input.setVisibility(View.GONE);
        this.input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Interpreter interpreter = new InterpreterMain(app);
                    addItemList("Jarvis", interpreter.interpret(input.getText().toString()));
                    input.setText("");
                    return true;
                }
                return false;
            }
        });

        (rootView.findViewById(R.id.circle)).setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                if (input.getVisibility() == View.GONE) {
                    input.setVisibility(View.VISIBLE);
                    InputMethodManager inputMethodManager = (InputMethodManager) app.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    InputMethodManager mgr = (InputMethodManager) app.getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(input.getWindowToken(), 0);
                    input.setVisibility(View.GONE);
                }
                return true;
            }
        });

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
        list = new ArrayList<>();

        List<ModelServerMessage> serverMessageList = app.getConfig().getListServerMessage_1();
        for(int i = serverMessageList.size()-1; i>=0; i--) {
            list.add(new ModelHome(list.size(), "Notification", new IModelHomeListener() {
                @Override
                public void execute(ModelHome modelHome) {
                    removeItemList(modelHome);
                    if(modelHome.serverMessage != null)
                        app.getConfig().removeServerMessage(modelHome.serverMessage);
                }
            }, serverMessageList.get(i), Const.TAB_VIEW_TYPE_HOME_INFORMATION));
        }

        if(this.app.getConfig().isHomeWelcomeMessage())
            list.add(new ModelHome(list.size(), "Welcome", new IModelHomeListener() {
                @Override
                public void execute(ModelHome modelHome) {
                    removeItemList(modelHome);
                    app.getConfig().setHomeWelcomeMessage(false);
                }
            }, Html.fromHtml("<a>This app give you the Cloud control from your Android device and your PC thanks to the <font color=\"#26AEEE\">web application</font>. You can share files and talk with your friends.</a>"), Const.TAB_VIEW_TYPE_HOME_INFORMATION));

        list.add(new ModelHome(list.size(), "Tabs", Const.TAB_VIEW_TYPE_SECTION));
        list.add(new ModelHome(list.size(),
                "Files",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (app instanceof ApplicationDrawer) {
                            ((ApplicationDrawer) app).selectItem(3);
                        }
                    }
                },
                "Talks",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((ApplicationDrawer) app).selectItem(4);
                    }
                },
                Const.TAB_VIEW_TYPE_TWO_BUTTONS));
        updateAdapter();
    }

    public void updateAdapter() {
        if (this.recyclerView != null && this.list != null && this.isAdded()) {
            this.circulerProgressBar.setVisibility(View.GONE);

            this.mAdapter = new AdapterModelHome(app, list);
            this.recyclerView.setAdapter(mAdapter);
            this.recyclerView.setItemAnimator(/*new SlideInFromLeftItemAnimator(mRecyclerView)*/new DefaultItemAnimator());

            if ((rootView.findViewById(R.id.circle)).getVisibility() == View.GONE) {
                (rootView.findViewById(R.id.circle)).setVisibility(View.VISIBLE);
                Animation animOpen = AnimationUtils.loadAnimation(this.app, R.anim.circle_button_bottom_open);
                (rootView.findViewById(R.id.circle)).startAnimation(animOpen);
            }

            (rootView.findViewById(R.id.circle)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        // Specify the calling package to identify your application
                        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getClass()
                                .getPackage().getName());

                        // Display an hint to the user about what he should say.
                        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Jarvis");

                        // Given an hint to the recognizer about what the user is going to say
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);

                        int noOfMatches = 1;
                        // Specify how many results you want to receive. The results will be
                        // sorted where the first result is the one with higher confidence.

                        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, noOfMatches);

                        HomeFragment.this.startActivityForResult(intent, 1001);
                    }
                    catch(ActivityNotFoundException e)
                    {
                        Toast.makeText(getActivity(), "Google voice recognition not found.", Toast.LENGTH_LONG).show();
                    }
                    if(myTTS==null)
                        myTTS = new TextToSpeech(HomeFragment.this.getActivity(), HomeFragment.this);
                }

            });

            this.mAdapter.setOnItemClickListener(new AdapterModelHome.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                }
            });

            this.swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public boolean back() {
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1001 && data!=null) {

            if(myTTS==null)
                myTTS = new TextToSpeech(this.getActivity(), this);

            ArrayList<String> textMatchList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            if (textMatchList != null)
                if (!textMatchList.isEmpty()) {
                    Interpreter interpreter = new InterpreterMain(this.app);
                    String input = textMatchList.get(0);
                    addItemList("Jarvis", interpreter.interpret(input));
                }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    // speak the user text
    public void speakWords(String speech) {
        if(speech==null) return;
        else if(speech.equals("")||speech.equals(" ")) return;

        if(myTTS==null)
            myTTS = new TextToSpeech(this.getActivity(), this);

        HashMap<String,String> ttsParams = new HashMap<>();
        ttsParams.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, this.getActivity().getPackageName());
        myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, ttsParams);
    }

    @Override
    public void onInit(int initStatus) {
        // check for successful instantiation
        if (initStatus == TextToSpeech.SUCCESS) {
            if (myTTS.isLanguageAvailable(Locale.US) == TextToSpeech.LANG_AVAILABLE)
                myTTS.setLanguage(Locale.US);
        } else if (initStatus == TextToSpeech.ERROR) {
            Toast.makeText(getActivity(), "Text-To-Speech error...", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(myTTS != null)
            myTTS.shutdown();
    }

    public void addItemList(String title, InterpreterResult interpreterResult) {
        if(title!=null && interpreterResult!=null) {
            if(interpreterResult.content != null)
                speakWords(interpreterResult.content);

            recyclerView.scrollToPosition(0);
            if(interpreterResult.modelForm!=null)
                mAdapter.addItem(
                        new ModelHome(list.size(), title, new IModelHomeListener() {
                            @Override
                            public void execute(ModelHome modelHome) {
                                removeItemList(modelHome);
                            }
                        },
                                interpreterResult.modelForm,
                                Const.TAB_VIEW_TYPE_HOME_INFORMATION_FORM),
                        0
                );
            else if(interpreterResult.content != null)
                if(!interpreterResult.content.equals(""))
                    mAdapter.addItem(
                            new ModelHome(list.size(), title, new IModelHomeListener() {
                                @Override
                                public void execute(ModelHome modelHome) {
                                    removeItemList(modelHome);
                                }
                            },
                                    interpreterResult.content,
                                    Const.TAB_VIEW_TYPE_HOME_INFORMATION),
                            0
                    );
        }
    }

    public void removeItemList(ModelHome modelHome) {
        mAdapter.removeItem(modelHome);
    }
}
