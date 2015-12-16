package com.sergepogosyan.shishnashki;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.List;

public class GameActivity extends AppCompatActivity
    implements WelcomeFragment.OnWelcomeListener,
    GameFragment.OnGameListener {

  private static final String TAG = "shishnashki activity";
  static final String STATE_GAME = "gameState";
  static final String STATE_TILES = "tileNums";

  enum GameState {welcome, started, results, finished}
  private GameState currentState;
  private int[] gameTiles;
  private TileView gameView;

  private Fragment welcomeScreen, resultsScreen;
  // TODO: 12/10/2015 add welcome screen - start button, description, "like" button
  // TODO: 12/10/2015 add results screen - restart button, score
  // TODO: 12/10/2015 implement score and time
  // TODO: 12/10/2015 implement hints and solution algorithm
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    currentState = GameState.welcome;

    Log.i(TAG, "onCreate: " + savedInstanceState);
    setContentView(R.layout.game_layout);

    gameView = (TileView) findViewById(R.id.game_view);
    Button buttonReset = (Button) findViewById(R.id.button_reset);
    Button buttonReverse = (Button) findViewById(R.id.button_reverse);
    Button buttonShuffle = (Button) findViewById(R.id.button_shuffle);
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
    if (savedInstanceState != null) {
      currentState = GameState.valueOf(savedInstanceState.getString(STATE_GAME, "welcome"));
      gameTiles = savedInstanceState.getIntArray(STATE_TILES);
      gameView.setTiles(gameTiles);
    }
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    savedInstanceState.putIntArray(STATE_TILES, gameView.getTiles());
    savedInstanceState.putString(STATE_GAME, currentState.toString());
    super.onSaveInstanceState(savedInstanceState);
  }

  @Override
  protected void onStart() {
    super.onStart();
//    switchState(currentState);
  }

  private void showWelcomeDialog() {
    FragmentManager fm = getSupportFragmentManager();
    welcomeScreen = WelcomeFragment.newInstance("test1", "test2");
    ((WelcomeFragment )welcomeScreen).show(fm, "welcome");
  }

  private void showResultsDialog() {
    FragmentManager fm = getSupportFragmentManager();
    resultsScreen = WelcomeFragment.newInstance("test1", "test2");
    ((WelcomeFragment )resultsScreen).show(fm, "results");
  }

  private void showGameDialog() {
    Log.i(TAG, "showGameDialog after: ");
  }

  @Override
  public void onBackPressed() {
    Log.i(TAG, "onBackPressed: true");
    super.onBackPressed();
//    switchState(GameState.welcome);
  }

  @Override
  public void onGameWon(String str) {
    switchState(GameState.results);
  }

  @Override
  public void onQuitGame(String str) {
    switchState(GameState.welcome);
  }

  @Override
  public void onStartGame(String str) {
    switchState(GameState.started);
  }

  @Override
  public void onExitGame() {
    switchState(GameState.finished);
  }

  private void switchState(GameState state) {
    switch (state) {
      case welcome:
        //remove game and results fragments
        showWelcomeDialog();
        break;
      case started:
        //remove welcome fragment
        //if from welcome init for new game
        //if not init from saved state
        if (welcomeScreen != null && !welcomeScreen.isHidden())
          ((WelcomeFragment)welcomeScreen).dismiss();
        showGameDialog();
        break;
      case results:
        //show results fragment
        break;
      case finished:
        //if prevstate started then save tileview
        //if prevstate welcome then exit
        //if results go to welcome
        finish();
        break;
    }
    currentState = state;
  }
}