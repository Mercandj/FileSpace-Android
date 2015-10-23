package mercandalli.com.filespace.ui.fragments.workspace;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.listeners.IListener;
import mercandalli.com.filespace.models.ModelFileSpace;
import mercandalli.com.filespace.ui.activities.ApplicationCallback;
import mercandalli.com.filespace.ui.fragments.BackFragment;
import mercandalli.com.filespace.utils.DialogUtils;
import mercandalli.com.filespace.utils.FontUtils;
import mercandalli.com.filespace.utils.StringUtils;

/**
 * Created by Jonathan on 21/07/2015.
 */
public class NoteFragment extends BackFragment {

    private View rootView;
    private TextView input;

    private ModelFileSpace article;

    private Activity mActivity;
    private ApplicationCallback mApplicationCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        if (context instanceof ApplicationCallback) {
            mApplicationCallback = (ApplicationCallback) context;
        } else {
            throw new IllegalArgumentException("Must be attached to a HomeActivity. Found: " + context);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mApplicationCallback = null;
        app = null;
    }

    public NoteFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_workspace_note, container, false);

        this.article = new ModelFileSpace(mActivity, mApplicationCallback, "article");

        this.input = (TextView) this.rootView.findViewById(R.id.input);
        FontUtils.applyFont(app, this.input, "fonts/Roboto-Light.ttf");

        String txt = app.getConfig().getUserNoteWorkspace1();
        if (!StringUtils.isNullOrEmpty(txt))
            try {
                String txt_tmp = new String(txt.getBytes("ISO-8859-1"), "UTF-8");
                this.input.setText(txt_tmp);
                this.article.article.article_content_1 = txt_tmp;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        this.input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    app.getConfig().setUserNoteWorkspace1(s.toString());
                    article.article.article_content_1 = s.toString();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return this.rootView;
    }

    @Override
    public boolean back() {
        return false;
    }

    @Override
    public void onFocus() {

    }

    public void delete() {
        DialogUtils.alert(mActivity, "Delete note", "Delete the current note?", getString(R.string.yes), new IListener() {
            @Override
            public void execute() {
                input.setText("");
                app.getConfig().setUserNoteWorkspace1("");
            }
        }, getString(R.string.no), null);
    }
}

