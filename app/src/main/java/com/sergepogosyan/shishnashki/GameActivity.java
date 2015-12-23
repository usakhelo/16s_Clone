package com.sergepogosyan.shishnashki;

import android.animation.LayoutTransition;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class GameActivity extends AppCompatActivity {

  private static final String TAG = "shishnashki activity";
  static final String STATE_GAME = "gameState";
  static final String STATE_TILES = "tileNums";
  static final String STATE_DIRECTION = "direction";

  enum GameState {welcome, started, results, finished}
  private GameState currentState;
  private int[] gameTiles;
  private TileView gameView;

  private View welcomeScreen, gameScreen, resultsScreen;
  private ViewGroup container;
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

    container = (ViewGroup) findViewById(R.id.container);
    LayoutTransition transitioner = new LayoutTransition();
    container.setLayoutTransition(transitioner);

    welcomeScreen = findViewById(R.id.welcome_screen);
    gameScreen = findViewById(R.id.game_screen);
    resultsScreen = findViewById(R.id.result_screen);
    gameView = (TileView) findViewById(R.id.game_view);

    Button buttonReset = (Button) findViewById(R.id.button_reset);
    Button buttonReverse = (Button) findViewById(R.id.button_reverse);
    Button buttonShuffle = (Button) findViewById(R.id.button_shuffle);
    Button startButton = (Button) findViewById(R.id.start_button);

    gameView.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View view, MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN) {
          return false;
        }
        float x = event.getX();
        float y = event.getY();

        RotateButton pressedButton = gameView.findButton(x, y);

        if (pressedButton != null) {
          gameView.pressButton(pressedButton);
        }
        return true;
      }
    });
    startButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        switchState(GameState.started);
      }
    });
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
        gameView.setDirectionAnim(1 - gameView.getDirection());
      }
    });

    if (savedInstanceState != null) {
      currentState = GameState.valueOf(savedInstanceState.getString(STATE_GAME, "welcome"));
      gameTiles = savedInstanceState.getIntArray(STATE_TILES);
      int direction = savedInstanceState.getInt(STATE_DIRECTION);
      gameView.setDirection(direction);
      gameView.setTiles(gameTiles);
    }
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    savedInstanceState.putInt(STATE_DIRECTION, gameView.getDirection());
    savedInstanceState.putIntArray(STATE_TILES, gameView.getTiles());
    currentState = currentState == GameState.finished ? GameState.welcome : currentState;
    savedInstanceState.putString(STATE_GAME, currentState.toString());
    super.onSaveInstanceState(savedInstanceState);
  }

  @Override
  protected void onStart() {
    super.onStart();
    switchState(currentState);
  }

  @Override
  public void onBackPressed() {
    Log.i(TAG, "onBackPressed: true");
    switch (currentState) {
      case started:
        switchState(GameState.welcome);
        break;
      case welcome:
        switchState(GameState.finished);
        break;
      case results:
        switchState(GameState.welcome);
        break;
    }
  }

  private void switchState(GameState state) {
    switch (state) {
      case welcome:
        resultsScreen.setVisibility(View.GONE);
        gameScreen.setVisibility(View.GONE);
        welcomeScreen.setVisibility(View.VISIBLE);
        break;
      case started:
        if (currentState == GameState.welcome) {
          gameView.shuffleTiles();
        }
        welcomeScreen.setVisibility(View.GONE);
        resultsScreen.setVisibility(View.GONE);
        gameScreen.setVisibility(View.VISIBLE);
        break;
      case results:
        resultsScreen.setVisibility(View.VISIBLE);
        break;
      case finished:
        finish();
        break;
    }
    currentState = state;
  }
}