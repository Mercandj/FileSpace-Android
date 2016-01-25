package com.mercandalli.android.apps.files.extras.admin.game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.fragment.BackFragment;

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

        rootView.findViewById(R.id.gps).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GameWay gameWay = gameView.thread.mGameGrille.findWay();
                for (int i = gameWay.array.size() - 1; i >= 0; i--) {
                    gameView.thread.mGameGrille.setCaseValue(gameWay.array.get(i).x, gameWay.array.get(i).y, 8);
                }
            }
        });

        rootView.findViewById(R.id.reset_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameView.thread.mGameGrille.resetMap();
            }
        });

        rootView.findViewById(R.id.reset_way).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameView.thread.mGameGrille.resetWay();
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
