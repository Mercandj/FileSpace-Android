package mercandalli.com.jarvis.dialog;

import android.app.TimePickerDialog;
import android.content.Context;

/**
 * Created by Jonathan on 22/03/2015.
 */
public class DialogTimePicker extends TimePickerDialog {

    public DialogTimePicker(Context context, OnTimeSetListener callBack, int hourOfDay, int minute, boolean is24HourView) {
        super(context, callBack, hourOfDay, minute, is24HourView);
    }

}
