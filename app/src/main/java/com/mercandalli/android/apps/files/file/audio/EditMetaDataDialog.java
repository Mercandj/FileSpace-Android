package com.mercandalli.android.apps.files.file.audio;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.precondition.Preconditions;

public class EditMetaDataDialog extends DialogFragment {

    private static final String ARG_ARTIST = "EditMetaDataDialog.Args.ARG_ARTIST";
    private static final String SAVED_ARTIST = "EditMetaDataDialog.Saved.SAVED_ARTIST";
    private static final String ARG_FILE = "EditMetaDataDialog.Args.ARG_FILE";
    private static final String SAVED_FILE = "EditMetaDataDialog.Saved.SAVED_FILE";

    public static EditMetaDataDialog newInstance(String fileUrl, String artist) {
        Preconditions.checkNotNull(artist);
        final Bundle args = new Bundle();
        args.putString(ARG_ARTIST, artist);
        args.putString(ARG_FILE, fileUrl);
        final EditMetaDataDialog instance = new EditMetaDataDialog();
        instance.setArguments(args);
        return instance;
    }

    private String mFilePath;
    private String mArtist;

    private EditText mArtistEditText;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = getContext();

        @SuppressLint("InflateParams")
        final View rootView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_meta_data, null);

        final Bundle args = getArguments();
        if (args.containsKey(ARG_ARTIST)) {
            mArtist = args.getString(ARG_ARTIST);
        } else if (savedInstanceState != null) {
            mArtist = savedInstanceState.getString(SAVED_ARTIST);
        }
        if (args.containsKey(ARG_FILE)) {
            mFilePath = args.getString(ARG_FILE);
        } else if (savedInstanceState != null) {
            mFilePath = savedInstanceState.getString(SAVED_FILE);
        }

        mArtistEditText = (EditText) rootView.findViewById(R.id.dialog_edit_meta_data_artist);
        mArtistEditText.setText(mArtist);

        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setView(rootView).create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setCancelable(true);

        return alertDialog;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(SAVED_ARTIST, mArtist);
        super.onSaveInstanceState(savedInstanceState);
    }
}
