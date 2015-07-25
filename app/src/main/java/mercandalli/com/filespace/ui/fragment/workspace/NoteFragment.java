package mercandalli.com.filespace.ui.fragment.workspace;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.model.ModelFileSpace;
import mercandalli.com.filespace.ui.activity.Application;
import mercandalli.com.filespace.ui.fragment.Fragment;
import mercandalli.com.filespace.util.FontUtils;
import mercandalli.com.filespace.util.StringUtils;

/**
 * Created by Jonathan on 21/07/2015.
 */
public class NoteFragment extends Fragment {

    Application app;
    private View rootView;
    private TextView input;

    private ModelFileSpace note;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        app = (Application) activity;
    }

    public NoteFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_workspace_note, container, false);

        note = new ModelFileSpace(app, "note");

        this.input = (TextView) this.rootView.findViewById(R.id.input);
        FontUtils.applyFont(app, this.input, "fonts/Roboto-Light.ttf");
        if(!StringUtils.isNullOrEmpty(app.getConfig().getUserNoteWorkspace1())) {
            this.input.setText(app.getConfig().getUserNoteWorkspace1());
            this.note.note.note_content = app.getConfig().getUserNoteWorkspace1();
        }
        this.input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    app.getConfig().setUserNoteWorkspace1(s.toString());
                    note.note.note_content = s.toString();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return this.rootView;
    }

    @Override
    public boolean back() {
        return false;
    }

    public void delete() {
        input.setText("");
        app.getConfig().setUserNoteWorkspace1("");
    }
}

