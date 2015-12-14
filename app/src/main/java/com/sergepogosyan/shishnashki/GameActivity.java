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

import java.util.List;

public class GameActivity extends AppCompatActivity
    implements WelcomeFragment.OnWelcomeListener,
    GameFragment.OnGameListener {

  private static final String TAG = "shishnashki activity";
  static final String STATE_GAME = "gameState";

  enum GameState {welcome, started, results, finished}
  private GameState currentState;

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

    if (savedInstanceState != null) {
      currentState = GameState.valueOf(savedInstanceState.getString(STATE_GAME, "welcome"));
    } else {
      currentState = GameState.welcome;
    }
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    savedInstanceState.putString(STATE_GAME, currentState.toString());
    super.onSaveInstanceState(savedInstanceState);
  }

  @Override
  protected void onStart() {
    super.onStart();
    switchState(currentState);
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
    FragmentManager fm = getSupportFragmentManager();
    Fragment gameScreen = fm.findFragmentByTag("game");
    Log.i(TAG, "showGameDialog : " + fm.getFragments());
    Log.i(TAG, "showGameDialog before: " + gameScreen);
    if (gameScreen != null) {
      fm.beginTransaction().remove(gameScreen).commit();
      fm.executePendingTransactions();
    }
    gameScreen = GameFragment.newInstance("test1", "test2");
    FragmentTransaction transaction = fm.beginTransaction();
    transaction.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out, R.anim.abc_popup_enter, R.anim.abc_popup_exit);
    transaction.replace(android.R.id.content, gameScreen, "game").commit();
    Log.i(TAG, "showGameDialog after: " + gameScreen);
    Log.i(TAG, "showGameDialog : " + fm.getFragments());
  }

  @Override
  public void onBackPressed() {
    Log.i(TAG, "onBackPressed: true");
//    super.onBackPressed();
    switchState(GameState.welcome);
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
        FragmentManager fm = getSupportFragmentManager();
        Fragment game = fm.findFragmentByTag("game");
        Log.i(TAG, "switchState: " + fm.getFragments());
        Log.i(TAG, "switchState: " + game);
        if (game != null){
          fm.beginTransaction().remove(game).commit();
        }
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