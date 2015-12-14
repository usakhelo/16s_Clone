package com.sergepogosyan.shishnashki;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class GameFragment extends DialogFragment {
  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";

  private String mParam1;
  private String mParam2;

  private static final String TAG = "shishnashki game";

  static final String STATE_SCORE = "playerScore";
  static final String STATE_TIME = "playerTime";
  static final String STATE_TILES = "tileNums";

  private TileView gameView;
  private OnGameListener mListener;

  /**
   * @param param1 Parameter 1.
   * @param param2 Parameter 2.
   */
  public static GameFragment newInstance(String param1, String param2) {
    GameFragment fragment = new GameFragment();
    Bundle args = new Bundle();
    args.putString(ARG_PARAM1, param1);
    args.putString(ARG_PARAM2, param2);
    fragment.setArguments(args);
    return fragment;
  }
  public GameFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      mParam1 = getArguments().getString(ARG_PARAM1);
      mParam2 = getArguments().getString(ARG_PARAM2);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View content = inflater.inflate(R.layout.game_layout, container, false);

    gameView = (TileView) content.findViewById(R.id.game_view);
    Button buttonReset = (Button) content.findViewById(R.id.button_reset);
    Button buttonReverse = (Button) content.findViewById(R.id.button_reverse);
    Button buttonShuffle = (Button) content.findViewById(R.id.button_shuffle);
    buttonShuffle.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        gameView.shuffleTiles();
      }
    });
    buttonReset.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        gameView.resetTiles();
      }
    });
    buttonReverse.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        gameView.setDirection(1 - gameView.getDirection());
      }
    });

    Log.i(TAG, "onCreateView: " + savedInstanceState);

    if (savedInstanceState != null) {
      int[] numTiles = savedInstanceState.getIntArray(STATE_TILES);
      gameView.setTiles(numTiles);
    }
    else {
//      gameView.initTiles(null);
    }
    return content;
  }

  @Override
  public void onAttach(Activity context) {
    super.onAttach(context);
    if (context instanceof OnGameListener) {
      mListener = (OnGameListener) context;
    } else {
      throw new RuntimeException(context.toString()
          + " must implement OnGameListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    int[] tileNums = gameView.getTiles();
    savedInstanceState.putIntArray(STATE_TILES, tileNums);
    Log.i(TAG, "onSaveInstanceState: " + savedInstanceState);
    super.onSaveInstanceState(savedInstanceState);
  }

  public interface OnGameListener {
    void onGameWon(String str);
    void onQuitGame(String str);
  }
}
