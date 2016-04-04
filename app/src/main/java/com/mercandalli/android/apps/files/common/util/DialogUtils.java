package com.mercandalli.android.apps.files.common.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Spanned;
import android.widget.EditText;

import com.mercandalli.android.apps.files.common.listener.IListener;
import com.mercandalli.android.apps.files.common.listener.IStringListener;

/**
 * Created by Jonathan on 23/10/2015.
 */
public class DialogUtils {

    public static void alert(
            final Context context,
            final String title,
            final String message,
            final String positive,
            final IListener positiveListener,
            final String negative,
            final IListener negativeListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        if (positive != null) {
            builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (positiveListener != null) {
                        positiveListener.execute();
                    }
                    dialog.dismiss();
                }
            });
        }
        if (negative != null) {
            builder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (negativeListener != null) {
                        negativeListener.execute();
                    }
                    dialog.dismiss();
                }
            });
        }
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void alert(Context context, String title, Spanned message, String positive, final IListener positiveListener, String negative, final IListener negativeListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        if (positive != null) {
            builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (positiveListener != null) {
                        positiveListener.execute();
                    }
                    dialog.dismiss();
                }
            });
        }
        if (negative != null) {
            builder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (negativeListener != null) {
                        negativeListener.execute();
                    }
                    dialog.dismiss();
                }
            });
        }
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void prompt(Context context, String title, String message, String positive, final IStringListener positiveListener, String negative, final IListener negativeListener) {
        prompt(context, title, message, positive, positiveListener, negative, negativeListener, null);
    }

    public static void prompt(Context context, String title, String message, String positive, final IStringListener positiveListener, String negative, final IListener negativeListener, String preTex) {
        prompt(context, title, message, positive, positiveListener, negative, negativeListener, preTex, null);
    }

    public static void prompt(Context context, String title, String message, String positive, final IStringListener positiveListener, String negative, final IListener negativeListener, String preText, String hint) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle(title);
        if (message != null) {
            alert.setMessage(message);
        }

        // Set an EditText view to get user input
        final EditText input = new EditText(context);

        if (preText != null) {
            input.setText(preText);
        }
        if (hint != null) {
            input.setHint(hint);
        }

        alert.setPositiveButton(positive,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (positiveListener != null) {
                            positiveListener.execute(input.getText().toString());
                        }
                    }
                }
        );

        alert.setNegativeButton(negative,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (negativeListener != null) {
                            negativeListener.execute();
                        }
                    }
                }
        );

        //alert.show();
        final AlertDialog alertDialog = alert.create();
        alertDialog.setView(input, 38, 20, 38, 0);
        alertDialog.show();
    }

}
