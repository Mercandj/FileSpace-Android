package mercandalli.com.filespace.ui.fragments.workspace;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.extras.ia.crypt.Crypt;
import mercandalli.com.filespace.ui.activities.ApplicationDrawerActivity;
import mercandalli.com.filespace.ui.fragments.BackFragment;

/**
 * Created by Jonathan on 21/07/2015.
 */
public class CryptFragment extends BackFragment {

    private View rootView;
    private TextView input, output, tv_cryptKey;
    private ImageButton circle, circle2;
    private SeekBar seekBar;

    private int cryptKey = 50;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        app = (ApplicationDrawerActivity) activity;
    }

    public CryptFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_workspace_crypt, container, false);

        this.circle = (ImageButton) this.rootView.findViewById(R.id.circle);
        this.circle2 = (ImageButton) this.rootView.findViewById(R.id.circle2);

        this.input = (TextView) this.rootView.findViewById(R.id.input);
        this.output = (TextView) this.rootView.findViewById(R.id.output);

        this.tv_cryptKey = (TextView) this.rootView.findViewById(R.id.tv_cryptKey);

        this.input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                output.setText(Crypt.crypte(input.getText().toString(), cryptKey));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        this.circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                output.setText(Crypt.crypte(input.getText().toString(), cryptKey));
            }
        });

        this.circle2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input.setText(Crypt.decrypte(output.getText().toString(), cryptKey));
            }
        });

        this.circle.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getActivity(), "Copy", Toast.LENGTH_SHORT).show();
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(getActivity().CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", (output.getText().toString()));
                clipboard.setPrimaryClip(clip);
                return false;
            }
        });

        this.circle2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getActivity(), "Copy", Toast.LENGTH_SHORT).show();
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(getActivity().CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", (input.getText().toString()));
                clipboard.setPrimaryClip(clip);
                return false;
            }
        });

        this.seekBar = (SeekBar) this.rootView.findViewById(R.id.seekBar);
        this.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setCryptKey(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return this.rootView;
    }

    @Override
    public boolean back() {
        return false;
    }

    @Override
    public void onFocus() {

    }

    public void delete() {
        input.setText("");
        output.setText("");
    }

    private void setCryptKey(int value) {
        this.cryptKey = value;
        this.tv_cryptKey.setText("Crypt key = "+value);
        output.setText(Crypt.crypte(input.getText().toString(), cryptKey));
    }
}

