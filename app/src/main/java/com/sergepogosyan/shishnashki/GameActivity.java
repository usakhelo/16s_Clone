package com.sergepogosyan.shishnashki;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.sergepogosyan.shishnashki.solver.Solutions;
import com.sergepogosyan.shishnashki.db.DbOpenHelper;
import com.sergepogosyan.shishnashki.db.Player;
import com.sergepogosyan.shishnashki.views.TileView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class GameActivity extends AppCompatActivity {

  private static final String TAG = "shishnashki activity";
  static final String STATE_GAME = "gameState";
  static final String STATE_TILES = "tileNums";
  static final String STATE_DIRECTION = "direction";
  static final String STATE_SCORE = "score";
  static final String STATE_TIME = "time";
  static final String STATE_PLAYERID = "lastPlayer";
  static final String STATE_HINT = "hint"; //if hint button was shown

  enum GameState {welcome, started, results, highScore, finished}
  private GameState currentState;
  private TileView gameView;
  private Handler mHandler;
  private Timer mTimer;
  private TimerTask mTimerTask;
  private DbOpenHelper mDbHelper;
  private Runnable mRunnable;
  private ListView highScoreList;
  private View welcomeScreen, gameScreen, highScoreScreen, resultsScreen;
  private TextView scoreView, timeView;
  private ViewGroup container;
  private Button hintButton;
  private boolean hintWasUsed;
  private int playerId;
  private int mTime, mScore;

  // TODO: 12/10/2015 add to results screen - scoreView
  // TODO: 1/21/2016  complete highscore table

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    currentState = GameState.welcome;

    Log.i(TAG, "onCreate: " + savedInstanceState);
    setContentView(R.layout.game_layout);

    container = (ViewGroup) findViewById(R.id.container);
    LayoutTransition transitioner = new LayoutTransition();
    transitioner.setAnimator(LayoutTransition.APPEARING, getScreenAnim(transitioner));
    container.setLayoutTransition(transitioner);

    gameScreen = findViewById(R.id.game_screen);
    transitioner = new LayoutTransition();
    transitioner.setAnimator(LayoutTransition.APPEARING, getButtonAnim(transitioner));
    ((ViewGroup)gameScreen).setLayoutTransition(transitioner);

    welcomeScreen = findViewById(R.id.welcome_screen);
    resultsScreen = findViewById(R.id.result_screen);
    highScoreScreen = findViewById(R.id.highscore_screen);
    highScoreList = (ListView) findViewById(R.id.highscore_list);
    gameView = (TileView) findViewById(R.id.game_view);
    scoreView = (TextView) findViewById(R.id.turns);
    timeView = (TextView) findViewById(R.id.time);

    mTimer = new Timer();
    mHandler = new Handler();

    Button buttonReset = (Button) findViewById(R.id.button_reset);
    Button buttonReverse = (Button) findViewById(R.id.button_reverse);
    Button startButton = (Button) findViewById(R.id.start_button);
    Button restartButton = (Button) findViewById(R.id.restart_button);
    Button restartButton2 = (Button) findViewById(R.id.restart_button2);
    hintButton = (Button) findViewById(R.id.button_hint);

    hintButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        hintWasUsed = true;
        int[] tiles = gameView.getTiles();
        List<TileView.GameCommand> cmdList = new ArrayList<>();
        cmdList.add(new TileView.GameCommand() {
          @Override
          public void doCommand() {
            gameView.setEnabled(false);
            gameView.runNextCommand();
          }
        });

        boolean firstHalf = isFirstHalfComplete(tiles);

        int caseNum = firstHalf ? Solutions.getCaseH(Arrays.copyOfRange(tiles, 8, 16)) : Solutions.getCaseL(Arrays.copyOfRange(tiles, 0, 8));
        int[] turns = Solutions.getSolutions(caseNum);
        int prevDirection = gameView.getDirection();
        for (int turn : turns) {
          final int buttonNumber;
          if (turn < 4) {
            if (prevDirection != 1) {
              cmdList.add(directionCommand(1));
              prevDirection = 1;
            }
            buttonNumber = turn - 1;
          } else {
            if (prevDirection != 0) {
              cmdList.add(directionCommand(0));
              prevDirection = 0;
            }
            buttonNumber = turn - 4;
          }
          if (firstHalf)
            cmdList.add(buttonCommandH(buttonNumber));
          else
            cmdList.add(buttonCommandL(buttonNumber));
        }
        cmdList.add(new TileView.GameCommand() {
          @Override
          public void doCommand() {
            gameView.setEnabled(true);
            gameView.runNextCommand();
          }
        });
        gameView.addCommands(cmdList);
      }
    });

    gameView.setOnTurnListener(new TileView.OnTurnListener() {
      @Override
      public void onTurn() {
        mScore += 1;
        scoreView.setText(String.valueOf(mScore));
        if (hintButton.getVisibility() == View.VISIBLE)
          hintButton.setVisibility(View.GONE);

        int[] tiles = gameView.getTiles();
        int caseNum = 0;
        if (isWinningPosition(tiles)) {
          if (!hintWasUsed) {
            putPlayerDB();
            switchState(GameState.highScore);
          } else {
            switchState(GameState.results);
          }
        } else if ((!hintWasUsed) && gameView.isEnabled()) {
          if (isFirstHalfComplete(tiles)) {
            caseNum = Solutions.getCaseH(Arrays.copyOfRange(tiles, 8, 16));
          } else if (isSecondHalfComplete(tiles)) {
            caseNum = Solutions.getCaseL(Arrays.copyOfRange(tiles, 0, 8));
          }
          if (caseNum != 0) {
            double rand = Math.random() * 100;
            if (rand > 50 && rand < 60)
              hintButton.setVisibility(View.VISIBLE);
          }
        }
        Log.i(TAG, "setOnTurnListener: " + Thread.currentThread().getName());
      }
    });

    gameView.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View view, MotionEvent event) {
        if (hintWasUsed)
          return false;

        if (event.getAction() != MotionEvent.ACTION_DOWN) {
          return false;
        }
        float x = event.getX();
        float y = event.getY();

        final int pressedButton = gameView.findButton(x, y);

      if (pressedButton != -1) {
          gameView.addCommand(buttonCommandL(pressedButton));
        }
        return true;
      }
    });
    restartButton2.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        newGame();
        switchState(GameState.started);
        gameView.showTiles();
      }
    });
    restartButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        newGame();
        switchState(GameState.started);
        gameView.showTiles();
      }
    });
    startButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        gameView.hideTiles();
        newGame();
        switchState(GameState.started);
      }
    });
    buttonReset.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (!hintWasUsed) {
          gameView.setTiles(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 15, 11, 13, 14, 16, 12});
          gameView.setDirection(0);
          gameView.resetTiles();
        }
      }
    });
    buttonReverse.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (!hintWasUsed) {
          gameView.addCommand(directionCommand(1 - gameView.getDirection()));
        }
      }
    });

    if (savedInstanceState != null) {
      mTime = savedInstanceState.getInt(STATE_TIME, 0);
      mScore = savedInstanceState.getInt(STATE_SCORE, 0);
      boolean hintVisible = savedInstanceState.getBoolean(STATE_HINT, false);
      hintButton.setVisibility(hintVisible ? View.VISIBLE : View.GONE);
      currentState = GameState.valueOf(savedInstanceState.getString(STATE_GAME, "welcome"));
      if (currentState == GameState.highScore)
        playerId = savedInstanceState.getInt(STATE_PLAYERID);
      int[] gameTiles = savedInstanceState.getIntArray(STATE_TILES);
      int direction = savedInstanceState.getInt(STATE_DIRECTION);
      gameView.setDirection(direction);
      gameView.setTiles(gameTiles);
    }
  }

  private TileView.GameCommand buttonCommandL(final int number) {
    return (new TileView.GameCommand() {
      @Override
      public void doCommand() {
        gameView.pressButton(number);
        Log.i(TAG, "buttonCommandL: " + Thread.currentThread().getName());
      }
    });
  }
  private TileView.GameCommand buttonCommandH(final int number) {
    return (new TileView.GameCommand() {
      @Override
      public void doCommand() {
        gameView.pressButton(number + 6);
        Log.i(TAG, "buttonCommandH: " + Thread.currentThread().getName());
      }
    });
  }

  private TileView.GameCommand directionCommand(final int direction) {
    return (new TileView.GameCommand() {
      @Override
      public void doCommand() {
        gameView.setDirectionAnim(direction);
        Log.i(TAG, "directionCommand: " + Thread.currentThread().getName());
      }
    });
  }

  private boolean isWinningPosition(int[] tiles) {
    int prev = 0;
    for (int i = 0; i < 16; i++) {
      if (tiles[i] - prev != 1)
        break;
      prev = tiles[i];
    }
    return prev == 16;
  }

  private boolean isFirstHalfComplete(int[] tiles) {
    int prev = 0;
    for (int i = 0; i < 8; i++) {
      if (tiles[i] - prev != 1)
        break;
      prev = tiles[i];
    }
    return prev == 8;
  }

  private boolean isSecondHalfComplete(int[] tiles) {
    int prev = 8;
    for (int i = 8; i < 16; i++) {
      if (tiles[i] - prev != 1)
        break;
      prev = tiles[i];
    }
    return prev == 16;
  }

  private Animator getButtonAnim(LayoutTransition transition) {
    PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofFloat("scaleX", 0f, 1f);
    PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofFloat("scaleY", 0f, 1f);
    PropertyValuesHolder pvhAlpha = PropertyValuesHolder.ofFloat("alpha", 0f, .75f, 1f);
//    PropertyValuesHolder pvhTranslY = PropertyValuesHolder.ofFloat("translationY", -800f, 0f);
    Animator animator = ObjectAnimator.ofPropertyValuesHolder(transition, pvhAlpha, pvhScaleX, pvhScaleY).
      setDuration(300);
    animator.setInterpolator(new OvershootInterpolator());
    animator.addListener(new AnimatorListenerAdapter() {
      public void onAnimationEnd(Animator anim) {
        View view = (View) ((ObjectAnimator) anim).getTarget();
        if (view!=null) {
          view.setVisibility(View.VISIBLE);
          view.setAlpha(1f);
          view.invalidate();
        }
      }
    });
    return animator;
  }

  private Animator getScreenAnim(LayoutTransition transition) {
    PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofFloat("scaleX", .5f, 1f);
    PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofFloat("scaleY", .5f, 1f);
    PropertyValuesHolder pvhAlpha = PropertyValuesHolder.ofFloat("alpha", 0f, 1f);
    Animator animator = ObjectAnimator.ofPropertyValuesHolder(transition, pvhAlpha, pvhScaleX, pvhScaleY);
    return animator;
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    savedInstanceState.putInt(STATE_SCORE, mScore);
    savedInstanceState.putInt(STATE_TIME, mTime);
    savedInstanceState.putBoolean(STATE_HINT, hintButton.getVisibility() == View.VISIBLE);
    savedInstanceState.putInt(STATE_DIRECTION, gameView.getDirection());
    savedInstanceState.putInt(STATE_PLAYERID, playerId);
    savedInstanceState.putIntArray(STATE_TILES, gameView.getTiles());
    currentState = currentState == GameState.results ? GameState.welcome : currentState;
    savedInstanceState.putString(STATE_GAME, currentState.toString());
    Log.i(TAG, "onSaveInstanceState: " + savedInstanceState);
    super.onSaveInstanceState(savedInstanceState);
  }

  @Override
  protected void onStart() {
    super.onStart();
    Log.i(TAG, "onStart: " + currentState.toString());
    switchState(currentState);
  }


  @Override
  public void  onWindowFocusChanged(boolean hasFocus) {
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
      case highScore:
        switchState(GameState.welcome);
        break;
    }
  }

  private void printTime() {
    int minutes = mTime / 60;
    int secs = mTime % 60;
    String timeStr = String.format("%1$d:%2$02d", minutes, secs);
    timeView.setText(timeStr);
  }

  private void initTimer() {
    if (mRunnable == null) {
      mRunnable = new Runnable() {
        @Override
        public void run() {
          mTime += 1;
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

  private void newGame() {
    mScore = 0;
    mTime = 0;
    hintButton.setVisibility(View.GONE);
    ArrayList<Integer> nums = new ArrayList<>();
    for (int i = 0; i < (16); i++) {
      nums.add(i + 1);
    }
    Collections.shuffle(nums);
    int[] newNums = new int[nums.size()];
    for (int i = 0; i < nums.size(); i++)
      newNums[i] = nums.get(i);

    gameView.setTiles(newNums);
    gameView.setDirection(0);
  }

  private void putPlayerDB() {
    if (mDbHelper == null)
      mDbHelper = new DbOpenHelper(this);
    SQLiteDatabase db = mDbHelper.getWritableDatabase();
    Player player = Player.newPlayer("test1", mScore, mTime);
    playerId = (int)cupboard().withDatabase(db).put(player);
    Log.i(TAG, "putPlayerDB:playerId " + playerId);
  }

  private void fillHighscores() {
    if (mDbHelper == null)
      mDbHelper = new DbOpenHelper(this);
    SQLiteDatabase db = mDbHelper.getReadableDatabase();
    Cursor playerCursor = cupboard().withDatabase(db).query(Player.class).orderBy("time").limit(10).getCursor();

    String[] fromColumns = {"_id", "name", "score", "time"};
    int[] toViews = {R.id.player_icon, R.id.player_name, R.id.player_score, R.id.player_time};

    SimpleCursorAdapter adapter = new HighScoreAdapter(this,
        R.layout.list_item, playerCursor, fromColumns, toViews, 0);

    adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
      @Override
      public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        if (columnIndex == 0) {
          int id = cursor.getInt(columnIndex);
          View parentView = (View)view.getParent();
          if (id == playerId) {
            parentView.setBackgroundResource(R.color.blueTile);
          } else {
            parentView.setBackgroundResource(R.color.screenBackground);
          }
        }
        return false;
      }
    });
    highScoreList.setAdapter(adapter);
  }

  private void switchState(GameState state) {
    Log.i(TAG, "switchState: " + state.toString());
    switch (state) {
      case welcome:
        stopTimer();
        highScoreScreen.setVisibility(View.GONE);
        resultsScreen.setVisibility(View.GONE);
        gameScreen.setVisibility(View.GONE);
        welcomeScreen.setVisibility(View.VISIBLE);
        break;
      case started:
        if (hasWindowFocus()) {
          initTimer();
          mTimer.scheduleAtFixedRate(mTimerTask, 1000L, 1000L);
        }
        printTime();
        scoreView.setText(String.valueOf(mScore));
        welcomeScreen.setVisibility(View.GONE);
        highScoreScreen.setVisibility(View.GONE);
        resultsScreen.setVisibility(View.GONE);
        hintWasUsed = false;
        if (gameScreen.getVisibility() != View.VISIBLE) {
          container.getLayoutTransition().getAnimator(LayoutTransition.APPEARING).addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
              super.onAnimationEnd(animation);
              Log.i(TAG, "APPEARING onAnimationEnd:");
              gameView.showTiles();
              container.getLayoutTransition().getAnimator(LayoutTransition.APPEARING).removeAllListeners();
            }
          });
          gameScreen.setVisibility(View.VISIBLE);
        }
        break;
      case results:
        stopTimer();
        resultsScreen.setVisibility(View.VISIBLE);
        break;
      case highScore:
        stopTimer();
        fillHighscores();
        highScoreScreen.setVisibility(View.VISIBLE);
        break;
      case finished:
        finish();
        break;
    }
    currentState = state;
  }
}