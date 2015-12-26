package com.sergepogosyan.shishnashki;

import android.animation.LayoutTransition;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TimeUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;

public class GameActivity extends AppCompatActivity {

  private static final String TAG = "shishnashki activity";
  static final String STATE_GAME = "gameState";
  static final String STATE_TILES = "tileNums";
  static final String STATE_DIRECTION = "direction";
  static final String STATE_SCORE = "score";
  static final String STATE_TIME = "time";

  enum GameState {welcome, started, results, finished}
  private GameState currentState;
  private int[] gameTiles;
  private TileView gameView;

  private View welcomeScreen, gameScreen, resultsScreen;
  private TextView scoreView, timeView;
  private ViewGroup container;

  private int time, score;
  // TODO: 12/10/2015 add welcome screen - start button, description, "like" button
  // TODO: 12/10/2015 add results screen - restart button, scoreView
  // TODO: 12/10/2015 implement scoreView and timeView
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
    scoreView = (TextView) findViewById(R.id.score);
    timeView = (TextView) findViewById(R.id.time);

    Button buttonReset = (Button) findViewById(R.id.button_reset);
    Button buttonReverse = (Button) findViewById(R.id.button_reverse);
    Button startButton = (Button) findViewById(R.id.start_button);
    Button restartButton = (Button) findViewById(R.id.restart_button);

    gameView.setOnTurnListener(new TileView.OnTurnListener() {
      @Override
      public void onTurn() {
        score += 1;
        scoreView.setText(String.valueOf(score));
        int[] tiles = gameView.getTiles();
        int prev = 0;
        for (int i : tiles) {
          if (i - prev != 1)
            break;
          prev = i;
        }
        if (prev == 16) { // FIXME: 12/23/2015 tileview size should be accessible and settable from the activity
          switchState(GameState.results);
        }
      }
    });
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
    restartButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        switchState(GameState.started);
      }
    });
    startButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        switchState(GameState.started);
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
    savedInstanceState.putInt(STATE_SCORE, score);
    savedInstanceState.putInt(STATE_TIME, time);
    savedInstanceState.putInt(STATE_DIRECTION, gameView.getDirection());
    savedInstanceState.putIntArray(STATE_TILES, gameView.getTiles());
    currentState = currentState == GameState.results ? GameState.welcome : currentState;
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
        if (currentState == GameState.welcome || currentState == GameState.results) {
          score = 0;
          time = 100;
          Date date = new Date();
          date.setTime(time * 1000);
          String timeStr = String.format("%1$tM:%1$tS", date);
          scoreView.setText(String.valueOf(score));
          timeView.setText(timeStr);
          gameView.shuffleTiles();
        }
        welcomeScreen.setVisibility(View.GONE);
        resultsScreen.setVisibility(View.GONE);
        gameScreen.setVisibility(View.VISIBLE);
        break;
      case results:
        gameScreen.setEnabled(false);
        gameScreen.setClickable(false);
        resultsScreen.setVisibility(View.VISIBLE);
        break;
      case finished:
        finish();
        break;
    }
    currentState = state;
  }
}