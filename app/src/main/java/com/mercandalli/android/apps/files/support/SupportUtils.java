package com.mercandalli.android.apps.files.support;

import android.content.Context;
import android.provider.Settings.Secure;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.mercandalli.android.apps.files.precondition.Preconditions;

/**
 * Support utils.
 */
public class SupportUtils {

    /* package */
    static String getIdentifier(final Context context) {
        Preconditions.checkNotNull(context);
        return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
    }

    /**
     * Manage the soft input (keyboard).
     */
    public static void hideSoftInput(final EditText editText) {
        Preconditions.checkNotNull(editText);
        final Context context = editText.getContext();
        final InputMethodManager inputMethodManager = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * Manage the soft input (keyboard).
     */
    public static void showSoftInput(final EditText editText) {
        Preconditions.checkNotNull(editText);
        final Context context = editText.getContext();
        final InputMethodManager inputMethodManager = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }
}
