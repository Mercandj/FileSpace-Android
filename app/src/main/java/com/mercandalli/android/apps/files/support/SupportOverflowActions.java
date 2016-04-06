package com.mercandalli.android.apps.files.support;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.listener.IListener;
import com.mercandalli.android.apps.files.common.util.DialogUtils;
import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.main.FileApp;
import com.mercandalli.android.library.mainlibrary.precondition.Preconditions;

/**
 * A simple class that open a Dialog with the actions related to the {@link FileModel}.
 */
public class SupportOverflowActions implements PopupMenu.OnMenuItemClickListener {

    private final Activity mActivity;

    private final String mDeleteString;

    private final SupportManager mSupportManager;
    private SupportComment mSupportComment;

    public SupportOverflowActions(final Context context) {
        Preconditions.checkNotNull(context);

        mActivity = (Activity) context;
        mSupportManager = FileApp.get().getFileAppComponent().provideSupportManager();
        mDeleteString = context.getString(R.string.delete);
    }

    @Override
    public boolean onMenuItemClick(final MenuItem item) {
        final int itemId = item.getItemId();
        switch (itemId) {
            case R.id.popup_overflow_support_delete:
                delete(mSupportComment);
                return true;
        }
        return false;
    }

    /**
     * Show the {@link AlertDialog}.
     */
    public void show(
            final SupportComment supportComment,
            final View overflow) {

        Preconditions.checkNotNull(supportComment);
        mSupportComment = supportComment;

        final PopupMenu popupMenu = new PopupMenu(mActivity, overflow);
        popupMenu.getMenuInflater().inflate(R.menu.popup_overflow_support, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }

    //region Actions
    private void delete(final SupportComment supportComment) {
        DialogUtils.alert(
                mActivity,
                mDeleteString,
                "Delete " + supportComment.getComment() + " ?",
                mActivity.getResources().getString(android.R.string.yes),
                new IListener() {
                    @Override
                    public void execute() {
                        mSupportManager.deleteSupportComment(supportComment);
                    }
                },
                mActivity.getResources().getString(android.R.string.no),
                null);
    }
    //endregion Actions
}
