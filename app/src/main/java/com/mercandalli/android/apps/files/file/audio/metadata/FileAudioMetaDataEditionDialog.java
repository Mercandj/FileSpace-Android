package com.mercandalli.android.apps.files.file.audio.metadata;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.file.audio.FileAudioManager;
import com.mercandalli.android.apps.files.file.audio.FileAudioModel;
import com.mercandalli.android.apps.files.main.FileApp;

import java.io.File;

import static com.mercandalli.android.library.baselibrary.java.StringUtils.isEquals;

public class FileAudioMetaDataEditionDialog extends DialogFragment {

    private static final String ARG_FILE = "FileAudioMetaDataEditionDialog.Args.ARG_FILE";
    private static final String ARG_TITLE = "FileAudioMetaDataEditionDialog.Args.ARG_TITLE";
    private static final String ARG_ARTIST = "FileAudioMetaDataEditionDialog.Args.ARG_ARTIST";
    private static final String ARG_ALBUM = "FileAudioMetaDataEditionDialog.Args.ARG_ALBUM";

    private static final String SAVED_FILE = "FileAudioMetaDataEditionDialog.Saved.SAVED_FILE";
    private static final String SAVED_TITLE = "FileAudioMetaDataEditionDialog.Saved.SAVED_TITLE";
    private static final String SAVED_ARTIST = "FileAudioMetaDataEditionDialog.Saved.SAVED_ARTIST";
    private static final String SAVED_ALBUM = "FileAudioMetaDataEditionDialog.Saved.SAVED_ALBUM";

    public static FileAudioMetaDataEditionDialog newInstance(FileAudioModel fileAudioModel) {
        final Bundle args = new Bundle();
        args.putString(ARG_FILE, fileAudioModel.getUrl());
        args.putString(ARG_TITLE, fileAudioModel.getTitle());
        args.putString(ARG_ARTIST, fileAudioModel.getArtist());
        args.putString(ARG_ALBUM, fileAudioModel.getAlbum());
        final FileAudioMetaDataEditionDialog instance = new FileAudioMetaDataEditionDialog();
        instance.setArguments(args);
        return instance;
    }

    private String mFilePath;
    private String mTitle;
    private String mArtist;
    private String mAlbum;

    private EditText mTitleEditText;
    private EditText mArtistEditText;
    private EditText mAlbumEditText;

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final Context context = getContext();
        final FileAudioManager fileAudioManager = FileApp.get().getFileAppComponent()
                .provideFileAudioManager();

        @SuppressLint("InflateParams")
        final View rootView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_meta_data, null);

        final Bundle args = getArguments();
        if (args.containsKey(ARG_FILE)) {
            mFilePath = args.getString(ARG_FILE);
        } else if (savedInstanceState != null) {
            mFilePath = savedInstanceState.getString(SAVED_FILE);
        }
        if (args.containsKey(ARG_TITLE)) {
            mTitle = args.getString(ARG_TITLE);
        } else if (savedInstanceState != null) {
            mTitle = savedInstanceState.getString(SAVED_TITLE);
        }
        if (args.containsKey(ARG_ARTIST)) {
            mArtist = args.getString(ARG_ARTIST);
        } else if (savedInstanceState != null) {
            mArtist = savedInstanceState.getString(SAVED_ARTIST);
        }
        if (args.containsKey(ARG_ALBUM)) {
            mAlbum = args.getString(ARG_ALBUM);
        } else if (savedInstanceState != null) {
            mAlbum = savedInstanceState.getString(SAVED_ALBUM);
        }

        mTitleEditText = (EditText) rootView.findViewById(R.id.dialog_edit_meta_data_title);
        mTitleEditText.setText(mTitle);
        mArtistEditText = (EditText) rootView.findViewById(R.id.dialog_edit_meta_data_artist);
        mArtistEditText.setText(mArtist);
        mAlbumEditText = (EditText) rootView.findViewById(R.id.dialog_edit_meta_data_album);
        mAlbumEditText.setText(mAlbum);

        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String newTitle = mTitleEditText.getText().toString();
                        final String newArtist = mArtistEditText.getText().toString();
                        final String newAlbum = mAlbumEditText.getText().toString();
                        if (nothingChanged(newTitle,
                                newArtist,
                                newAlbum)) {
                            Toast.makeText(context, "Nothing changed", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        final boolean succeeded = fileAudioManager.setFileAudioMetaData(
                                new File(mFilePath),
                                newTitle,
                                newArtist,
                                newAlbum);
                        Toast.makeText(context, succeeded ? "Succeed" : "Failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .setView(rootView).create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setCancelable(true);

        return alertDialog;
    }

    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {
        savedInstanceState.putString(SAVED_FILE, mFilePath);
        savedInstanceState.putString(SAVED_TITLE, mTitle);
        savedInstanceState.putString(SAVED_ARTIST, mArtist);
        savedInstanceState.putString(SAVED_ALBUM, mAlbum);
        super.onSaveInstanceState(savedInstanceState);
    }

    private boolean nothingChanged(
            @Nullable final String newTitle,
            @Nullable final String newArtist,
            @Nullable final String newAlbum) {

        return isEquals(mTitle, newTitle) &&
                isEquals(mArtist, newArtist) &&
                isEquals(mAlbum, newAlbum);
    }
}
