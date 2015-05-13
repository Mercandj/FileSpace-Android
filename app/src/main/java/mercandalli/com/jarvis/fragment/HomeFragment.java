package mercandalli.com.jarvis.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.action.Interpreter;
import mercandalli.com.jarvis.action.InterpreterMain;
import mercandalli.com.jarvis.activity.Application;
import mercandalli.com.jarvis.activity.ApplicationDrawer;
import mercandalli.com.jarvis.adapter.AdapterModelHome;
import mercandalli.com.jarvis.config.Const;
import mercandalli.com.jarvis.listener.IModelHomeListener;
import mercandalli.com.jarvis.model.ModelHome;
import mercandalli.com.jarvis.model.ModelServerMessage;

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

        list.add(new ModelHome(list.size(), "Welcome", new IModelHomeListener() {
            @Override
            public void execute(ModelHome modelHome) {
                removeItemList(modelHome);
            }
        }, Html.fromHtml("<p align=\"justify\">This app give you the Cloud control from your Android device and your PC thanks to the <font color=\"#26AEEE\">web application</font>. You can share files and talk with your friends.</p>"), Const.TAB_VIEW_TYPE_HOME_INFORMATION));

        list.add(new ModelHome(list.size(), "Tabs", Const.TAB_VIEW_TYPE_SECTION));
        list.add(new ModelHome(list.size(),
                "Files",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(app instanceof ApplicationDrawer) {
                            ((ApplicationDrawer)app).selectItem(3);
                        }
                    }
                },
                "Talks",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((ApplicationDrawer)app).selectItem(4);
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
                    speakWords(interpreter.interpret(input));
                }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    // speak the user text
    public void speakWords(String speech) {
        if(speech==null) return;
        else if(speech.equals("")||speech.equals(" ")) return;
        addItemList(speech, "Jarvis");

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

    public void addItemList(String txt, String subtxt) {
        if(txt!=null && subtxt!=null) {
            recyclerView.scrollToPosition(0);
            mAdapter.addItem(
                    new ModelHome(list.size(), subtxt, new IModelHomeListener() {
                        @Override
                        public void execute(ModelHome modelHome) {
                            removeItemList(modelHome);
                        }
                    },
                    txt,
                    Const.TAB_VIEW_TYPE_HOME_INFORMATION_SHORT),
                0
            );
        }
    }

    public void removeItemList(ModelHome modelHome) {
        for(int i=0; i<list.size(); i++) {
            if(list.get(i).equals(modelHome))
                mAdapter.removeItem(i);
        }
    }
}
