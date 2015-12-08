package com.sergepogosyan.shishnashki;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

  private TileView gameView;
  static final String STATE_SCORE = "playerScore";
  static final String STATE_TIME = "playerTime";
  static final String STATE_TILES = "tileNums";


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.game_layout);
    gameView = (TileView) findViewById(R.id.game_view);
    Button buttonReset = (Button) findViewById(R.id.button_reset);
    Button buttonReverse = (Button) findViewById(R.id.button_reverse);
    Button buttonUndo = (Button) findViewById(R.id.button_undo);
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
    // Check whether we're recreating a previously destroyed instance
    if (savedInstanceState != null) {
      int[] numTiles = savedInstanceState.getIntArray(STATE_TILES);
      gameView.setTiles(numTiles);
    }
    else {
//      gameView.initTiles(null);
    }
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    // Save the user's current game state
//    savedInstanceState.putInt(STATE_SCORE, mCurrentScore);
//    savedInstanceState.putInt(STATE_LEVEL, mCurrentLevel);
    int[] tileNums = gameView.getTiles();
    savedInstanceState.putIntArray(STATE_TILES, tileNums);
    // Always call the superclass so it can save the view hierarchy state
    super.onSaveInstanceState(savedInstanceState);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_game, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
