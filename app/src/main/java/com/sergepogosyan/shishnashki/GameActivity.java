package com.sergepogosyan.shishnashki;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class GameActivity extends AppCompatActivity
    implements WelcomeFragment.OnFragmentInteractionListener,
    GameFragment.OnFragmentInteractionListener {

  private TileView gameView;
  static final String STATE_SCORE = "playerScore";
  static final String STATE_TIME = "playerTime";
  static final String STATE_TILES = "tileNums";

  private Fragment welcomeScreen;
  // TODO: 12/10/2015 add welcome screen - start button, description, "like" button
  // TODO: 12/10/2015 add results screen - restart button, score
  // TODO: 12/10/2015 implement score and time
  // TODO: 12/10/2015 implement hints and solution algorithm
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    showWelcomeDialog();
//    showGameDialog2();
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    // Always call the superclass so it can save the view hierarchy state
    super.onSaveInstanceState(savedInstanceState);
  }

  private void showWelcomeDialog() {
    FragmentManager fm = getSupportFragmentManager();
    welcomeScreen = WelcomeFragment.newInstance("test1", "test2");
    ((WelcomeFragment )welcomeScreen).show(fm, "fragment_edit_name");
  }
  private void showGameDialog2() {
    FragmentManager fm = getSupportFragmentManager();
    GameFragment welcomeScreen = GameFragment.newInstance("test1", "test2");
    welcomeScreen.show(fm, "fragment_edit_name");
  }
  private void showGameDialog() {
    FragmentManager fm = getSupportFragmentManager();
    GameFragment welcomeScreen = GameFragment.newInstance("test1", "test2");

    FragmentTransaction transaction = fm.beginTransaction();

//    transaction.setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_top, R.anim.abc_slide_in_top, R.anim.abc_slide_out_bottom);

//    transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
    transaction.replace(android.R.id.content, welcomeScreen)
        .commit();
  }

  @Override
  public void onBackPressed() {
    if(welcomeScreen.isVisible())
      super.onBackPressed();
//      welcomeScreen.onBackPressed();
//    else
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

  @Override
  public void onFragmentInteraction(Uri uri) {

  }
  @Override
  public void onFragmentInteraction2(String str) {
    showGameDialog();
  }
}
