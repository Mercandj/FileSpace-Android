package mercandalli.com.filespace.ui.fragment.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import mercandalli.com.filespace.R;
import mercandalli.com.filespace.ui.fragment.BackFragment;
import mercandalli.com.filespace.ui.view.game.GameView;
import mercandalli.com.filespace.ui.view.game.Way;

/**
 * Created by Jonathan on 02/09/2015.
 */
public class GameFragment extends BackFragment {

    private View rootView;
    private GameView gameView;

    public static GameFragment newInstance() {
        return new GameFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_admin_game, container, false);

        gameView = (GameView) rootView.findViewById(R.id.game_view);

        ((Button) rootView.findViewById(R.id.gps)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Way way = gameView.thread.grille.findWay();
                for (int i = way.array.size() - 1; i >= 0; i--)
                    gameView.thread.grille.setValeurCase(way.array.get(i).x, way.array.get(i).y, 8);
            }
        });

        ((Button) rootView.findViewById(R.id.reset_map)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameView.thread.grille.resetMap();
            }
        });

        ((Button) rootView.findViewById(R.id.reset_way)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameView.thread.grille.resetWay();
            }
        });

        return rootView;
    }

    public void delete() {
        ((EditText) rootView.findViewById(R.id.console)).setText("");
    }

    @Override
    public boolean back() {
        return false;
    }

    @Override
    public void onFocus() {

    }
}
