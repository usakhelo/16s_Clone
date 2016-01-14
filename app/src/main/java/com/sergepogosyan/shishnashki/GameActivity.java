package com.sergepogosyan.shishnashki;

import android.animation.LayoutTransition;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TimeUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

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
  private Handler mHandler;
  private Timer mTimer;
  private TimerTask mTimerTask;
  private Runnable mRunnable;
  private View welcomeScreen, gameScreen, resultsScreen;
  private TextView scoreView, timeView;
  private ViewGroup container;

  private int time, score;
  // TODO: 12/10/2015 add to welcome screen - "like" button
  // TODO: 12/10/2015 add to results screen - scoreView
  // TODO: 12/10/2015 implement hints and solution algorithm
  // TODO: 1/8/2016 add hightscore panel
  // TODO: 1/8/2016 add custom animation to views

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

    mTimer = new Timer();
    mHandler = new Handler();

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
        initGame();
        switchState(GameState.started);
      }
    });
    startButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        initGame();
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
      time = savedInstanceState.getInt(STATE_TIME, 0);
      score = savedInstanceState.getInt(STATE_SCORE, 0);
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
    Log.i(TAG, "onSaveInstanceState: " + savedInstanceState);
    super.onSaveInstanceState(savedInstanceState);
  }

  @Override
  protected void onStart() {
    super.onStart();
    Log.i(TAG, "onStart: ");
    switchState(currentState);
  }


  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);
    if (hasFocus) {
      if (currentState == GameState.started) {
        initTimer();
        printTime();
        mTimer.scheduleAtFixedRate(mTimerTask, 1000L, 1000L);
      }
    }
    else
      stopTimer();
    Log.i(TAG, "onWindowFocusChanged: " + hasFocus);
  }

  @Override
  protected void onStop() {
    super.onStop();
    Log.i(TAG, "onStop: ");
    stopTimer();
  }

  @Override
  public void onBackPressed() {
    Log.i(TAG, "onBackPressed: true");
    switch (currentState) {
      case started:
        stopTimer();
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

  private void printTime() {
    int minutes = time / 60;
    int secs = time % 60;
    String timeStr = String.format("%1$d:%2$02d", minutes, secs);
    timeView.setText(timeStr);
  }

  private void initTimer() {
    if (mRunnable == null) {
      mRunnable = new Runnable() {
        @Override
        public void run() {
          time += 1;
          printTime();
        }
      };
    }

    if (mTimerTask == null) {
      mTimerTask = new TimerTask() {
        public void run() {
          mHandler.post(mRunnable);
        }
      };
    }
  }

  private void stopTimer() {
    if (mTimerTask != null) {
      mTimerTask.cancel();
      mTimerTask = null;
    }
  }

  private void initGame() {
    score = 0;
    time = 0;
    gameView.shuffleTiles();
  }

  private void switchState(GameState state) {
    Log.i(TAG, "switchState: " + state.toString());
    switch (state) {
      case welcome:
        stopTimer();
        resultsScreen.setVisibility(View.GONE);
        gameScreen.setVisibility(View.GONE);
        welcomeScreen.setVisibility(View.VISIBLE);
        break;
      case started:
        if (hasWindowFocus()) {
          initTimer();
          printTime();
          mTimer.scheduleAtFixedRate(mTimerTask, 1000L, 1000L);
        }
        scoreView.setText(String.valueOf(score));
        welcomeScreen.setVisibility(View.GONE);
        resultsScreen.setVisibility(View.GONE);
        gameScreen.setVisibility(View.VISIBLE);
        break;
      case results:
        stopTimer();
        resultsScreen.setVisibility(View.VISIBLE);
        break;
      case finished:
        finish();
        break;
    }
    currentState = state;
  }
}