package mercandalli.com.jarvis.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;

import mercandalli.com.jarvis.R;

/**
 * Created by Jonathan on 14/12/2014.
 */
public class ActivityEditTxt extends Activity {

    private String initateString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.view_edit_txt);
        super.onCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setIcon(R.drawable.transparent);

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            this.finish();
            return;
        }
        else {
            initateString = extras.getString("TXT");
            ((EditText) this.findViewById(R.id.txt)).setText(""+initateString);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
