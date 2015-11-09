package mercandalli.com.filespace.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;

import mercandalli.com.filespace.R;

public class ConfirmationDialog {
    public static Dialog newInstance(
            String titleResourceId,
            String content,
            @StringRes int positiveId,
            @StringRes int negativeId,
            @StringRes int neutralId,
            Context context,
            final DialogCallback callback) {

        AlertDialog.Builder builder = getBaseDialog(titleResourceId, content, positiveId, context, callback);

        builder.setNegativeButton(negativeId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.onNegativeClick();
            }
        });
        builder.setNeutralButton(neutralId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.onNeutralClick();
            }
        });

        builder.setTitle(titleResourceId);

        return builder.create();
    }

    public static Dialog newInstance(
            String titleResourceId,
            String content,
            @StringRes int positiveId,
            @StringRes int negativeId,
            Context context,
            final DialogCallback callback) {

        AlertDialog.Builder builder = getBaseDialog(titleResourceId, content, positiveId, context, callback);

        builder.setNegativeButton(negativeId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.onNegativeClick();
            }
        });

        return builder.create();
    }

    public static Dialog newInstance(
            String titleResourceId,
            String content,
            @StringRes int positiveId,
            Context context,
            final DialogCallback callback) {

        AlertDialog.Builder builder = getBaseDialog(titleResourceId, content, positiveId, context, callback);

        return builder.create();
    }

    private static AlertDialog.Builder getBaseDialog(
            String titleResourceId,
            String content,
            @StringRes int positiveId,
            Context context,
            final DialogCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogCustom)
                .setPositiveButton(positiveId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.onPositiveClick();
                    }
                })
                .setMessage(content);

        builder.setTitle(titleResourceId);

        return builder;
    }
}
