package mercandalli.com.jarvis.dialog;

import android.app.DatePickerDialog;
import android.content.Context;

/**
 * Created by Jonathan on 22/03/2015.
 */
public class DialogDatePicker extends DatePickerDialog {

    public DialogDatePicker(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
    }
}
