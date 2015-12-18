package com.mercandalli.android.apps.files.search;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.mercandalli.android.apps.files.common.Preconditions;

/**
 * Manage the soft input (keyboard).
 */
public class SoftInputUtils {

    public static void hideSoftInput(EditText editText) {
        Preconditions.checkNotNull(editText);
        final Context context = editText.getContext();
        final InputMethodManager inputMethodManager = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static void showSoftInput(EditText editText) {
        Preconditions.checkNotNull(editText);
        final Context context = editText.getContext();
        final InputMethodManager inputMethodManager = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

}
