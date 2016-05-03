package com.mercandalli.android.apps.files.note;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.fragment.BackFragment;
import com.mercandalli.android.apps.files.common.listener.IListener;
import com.mercandalli.android.apps.files.common.util.DialogUtils;
import com.mercandalli.android.apps.files.file.filespace.FileSpaceModel;
import com.mercandalli.android.apps.files.main.Config;
import com.mercandalli.android.library.baselibrary.java.StringUtils;
import com.mercandalli.android.library.baselibrary.rating.Rating;

import java.io.UnsupportedEncodingException;

/**
 * Created by Jonathan on 21/07/2015.
 */
public class NoteFragment extends BackFragment {

    private EditText mInputEdiText;

    private FileSpaceModel mFileSpaceModel;

    public static NoteFragment newInstance() {
        return new NoteFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_workspace_note, container, false);

        mFileSpaceModel = new FileSpaceModel.FileSpaceModelBuilder().type("article").build();
        mInputEdiText = (EditText) rootView.findViewById(R.id.fragment_workspace_note_input);

        final String txt = Config.getUserNoteWorkspace1();
        if (!StringUtils.isNullOrEmpty(txt)) {
            try {
                final String txtTmp = new String(txt.getBytes("ISO-8859-1"), "UTF-8");
                this.mInputEdiText.setText(txtTmp);
                mFileSpaceModel.getArticle().article_content_1 = txtTmp;
            } catch (UnsupportedEncodingException e) {
                Log.e(getClass().getName(), "Failed UnsupportedEncodingException", e);
            }
        }

        mInputEdiText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    Config.setUserNoteWorkspace1(getContext(), s.toString());
                    mFileSpaceModel.getArticle().article_content_1 = s.toString();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return rootView;
    }

    @Override
    public boolean back() {
        return false;
    }

    /**
     * Delete the current note. Delete the {@link #mInputEdiText} content.
     */
    public void delete() {
        DialogUtils.alert(getContext(), "Delete note", "Delete the current note?", getString(android.R.string.yes), new IListener() {
            @Override
            public void execute() {
                mInputEdiText.setText("");
                Config.setUserNoteWorkspace1(getContext(), "");
            }
        }, getString(android.R.string.no), null);
    }

    /**
     * Share the current note. Share the {@link #mInputEdiText}.
     */
    public void share() {
        final Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, mInputEdiText.getText().toString());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
}

