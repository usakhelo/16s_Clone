package com.sergepogosyan.shishnashki;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TileView extends View {

  public int getDirection() {
    return mDirection;
  }

  public void setDirection(int direction) {
    final int dir = direction;
    Animator hide = hideButtonsAnim();
    hide.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        mDirection = dir;
        startButtonsAnimation().start();
      }
    });
    hide.start();
  }

  private int mDirection;
  private Rect mBounds, mButtonCW, mButtonCCW;
  private int mLeftPad, mTopPad, mTileWidth, mTileHeight;
  private int mTileMarginRatio = 15;
  private int mButtonSizeRatio = 2;
  private int mTileCount = 4;
  private ArrayList<Tile> mTiles;
  private ArrayList<RotateButton> mButtons;

  private AnimatorSet moveAnimatorSet, startAnimatorSet, clickAnimator;

  class PointEvaluator implements TypeEvaluator {
    public Object evaluate(float fraction, Object startValue, Object endValue) {
      Point startPoint = (Point) startValue;
      Point endPoint = (Point) endValue;
      float x = startPoint.x + fraction * (endPoint.x - startPoint.x);
      float y = startPoint.y + fraction * (endPoint.y - startPoint.y);
      return new Point((int)x, (int)y);
    }
  }

  public TileView(Context context) {
    super(context);
    init(null, 0);
  }

  public TileView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(attrs, 0);
  }

  public TileView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init(attrs, defStyle);
  }

  private void init(AttributeSet attrs, int defStyle) {
    final TypedArray a = getContext().obtainStyledAttributes(
        attrs, R.styleable.TileView, defStyle, 0);

    a.recycle();
    initTiles();
    initButtons();
  }

  private void initButtons() {
    mButtons = new ArrayList<>();
    Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.button);
    mButtonCW = new Rect(0, 0, bitmap.getWidth() / 2, bitmap.getHeight());
    mButtonCCW = new Rect(bitmap.getWidth() / 2, 0, bitmap.getWidth(), bitmap.getHeight());
    for (int i = 0; i < (mTileCount - 1); i++) {
      for (int j = 0; j < (mTileCount - 1); j++) {
        mButtons.add(new RotateButton(j, i, this, bitmap));
      }
    }
  }

  public int[] getTiles() {
    int[] tileNums = new int[(mTileCount * mTileCount)];
    for (int i = 0; i < (mTileCount * mTileCount); i++) {
      tileNums[i] = mTiles.get(i).getNumber();
    }
    return tileNums;
  }

  public void setTiles(int[] tileNums) {
    for (int i = 0; i < (mTileCount * mTileCount); i++) {
      mTiles.get(i).setNumber(tileNums[i]);
    }
  }

  public void resetTiles() {
    Animator hide = hideAnimation();
    hide.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        for (int i = 0; i < (mTileCount * mTileCount); i++) {
          mTiles.get(i).setNumber(i+1);
        }
        mDirection = 0;
        startAnimation();
      }
    });
    hide.start();
  }
  public void shuffleTiles() {
    Animator hide = hideAnimation();
    hide.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        ArrayList<Integer> nums = new ArrayList<>();

        for (int i = 0; i < (mTileCount * mTileCount); i++) {
          nums.add(i + 1);
        }
        Collections.shuffle(nums);
        for (int i = 0; i < (mTileCount * mTileCount); i++) {
          mTiles.get(i).setNumber(nums.get(i));
        }
        mDirection = 0;
        startAnimation();
      }
    });
    hide.start();
  }

  public void initTiles() {
    mTiles = new ArrayList<>();
    ArrayList<Integer> nums = new ArrayList<>();

    for (int i = 0; i < (mTileCount * mTileCount); i++) {
      nums.add(i + 1);
    }

    for (int index : nums) {
      mTiles.add(new Tile(index, this));
    }
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int w = MeasureSpec.getSize(widthMeasureSpec);
    int h = MeasureSpec.getSize(heightMeasureSpec);
    Configuration config = getResources().getConfiguration();
    if (config.orientation == Configuration.ORIENTATION_LANDSCAPE)
      setMeasuredDimension(h, h);
    else
      setMeasuredDimension(w, w);
    Log.i("shishanshki: ", "onMeasure: " + w + ", " + h);
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);

    // Account for padding
    mLeftPad = getPaddingLeft();
    mTopPad = getPaddingTop();
    int xPad = mLeftPad + getPaddingRight();
    int yPad = mTopPad + getPaddingBottom();

    int ww = w - xPad;
    int hh = h - yPad;

    int tileMargin = ww / mTileCount / mTileMarginRatio;
    mBounds = new Rect(0, 0, ww, hh);
    mTileWidth = (mBounds.width() - (tileMargin * (mTileCount - 1))) / mTileCount;
    mTileHeight = (mBounds.height() - (tileMargin * mTileCount - 1)) / mTileCount;

    //tiles
    int tilePlaceW = ww / mTileCount;
    int tilePlaceH = hh / mTileCount;
    for (int i = 0; i < mTileCount; i++) {
      for (int j = 0; j < mTileCount; j++) {
        Tile tile = mTiles.get((i * mTileCount) + j);
        if (!this.isInEditMode())
          tile.setSize(0);
        int x = mLeftPad + (tilePlaceW * j) + (tilePlaceW / 2);
        int y = mTopPad + (tilePlaceH * i) + (tilePlaceH / 2);
        tile.setPosition(new Point(x, y));
      }
    }

    //buttons
    int buttonCount = mTileCount - 1;
    for (int i = 0; i < buttonCount; i++) {
      for (int j = 0; j < buttonCount; j++) {
        RotateButton button = mButtons.get((i * buttonCount) + j);
        if (!this.isInEditMode())
          button.setSize(0);
        int x = mLeftPad + tilePlaceW + (tilePlaceW * j);
        int y = mTopPad + tilePlaceH + (tilePlaceH * i);
        button.setPosition(new Point(x, y));
      }
    }
    startAnimation();
  }

  private Animator hideTilesAnim() {
    List<Animator> tileAnimators = new ArrayList<>();
    for (int i = 0; i < mTiles.size(); i++) {
      Tile tile = mTiles.get(i);
      ObjectAnimator zoomInX = ObjectAnimator.ofInt(tile, "size", mTileWidth, 0);
      zoomInX.setDuration(100);
      zoomInX.setInterpolator(new AccelerateInterpolator());
      tileAnimators.add(zoomInX);
    }
    AnimatorSet tileAnimatorSet = new AnimatorSet();
    tileAnimatorSet.playTogether(tileAnimators);
    return tileAnimatorSet;
  }
  private Animator hideButtonsAnim() {
    List<Animator> buttonAnimators = new ArrayList<>();
    for (int i = 0; i < mButtons.size(); i++) {
      RotateButton button = mButtons.get(i);
      ObjectAnimator buttonPopOut = ObjectAnimator.ofFloat(button, "size", (mTileWidth / mButtonSizeRatio), 0);
      buttonPopOut.setDuration(100);
      buttonPopOut.setInterpolator(new AccelerateInterpolator());
      buttonAnimators.add(buttonPopOut);
    }
    AnimatorSet animatorSet = new AnimatorSet();
    animatorSet.playTogether(buttonAnimators);
    return animatorSet;
  }

  private Animator hideAnimation() {
    AnimatorSet tileAnimatorSet = (AnimatorSet)hideTilesAnim();
    AnimatorSet buttonAnimatorSet = (AnimatorSet)hideButtonsAnim();
    startAnimatorSet = new AnimatorSet();
    startAnimatorSet.play(tileAnimatorSet).with(buttonAnimatorSet);
    return startAnimatorSet;
  }

  private Animator startTilesAnimation() {
    List<Animator> tileAnimators = new ArrayList<>();
    for (int i = 0; i < mTiles.size(); i++) {
      Tile tile = mTiles.get(i);
      ObjectAnimator zoomInX = ObjectAnimator.ofInt(tile, "size", 0, mTileWidth);
      zoomInX.setDuration(400);
      zoomInX.setInterpolator(new OvershootInterpolator());
      zoomInX.setStartDelay( i * 50);
      tileAnimators.add(zoomInX);
    }

    AnimatorSet tileAnimatorSet = new AnimatorSet();
    tileAnimatorSet.playTogether(tileAnimators);
    return tileAnimatorSet;
  }

  private Animator startButtonsAnimation() {
    List<Animator> buttonAnimators = new ArrayList<>();
    for (int i = 0; i < mButtons.size(); i++) {
      RotateButton button = mButtons.get(i);
      ObjectAnimator buttonPopOut = ObjectAnimator.ofFloat(button, "size", 0, (mTileWidth / mButtonSizeRatio));
      buttonPopOut.setDuration(400);
      buttonPopOut.setInterpolator(new OvershootInterpolator());
      buttonAnimators.add(buttonPopOut);
    }
    AnimatorSet buttonAnimatorSet = new AnimatorSet();
    buttonAnimatorSet.playTogether(buttonAnimators);
    return buttonAnimatorSet;
  }

  private void startAnimation() {
    List<Animator> tileAnimators = new ArrayList<>();
    for (int i = 0; i < mTiles.size(); i++) {
      Tile tile = mTiles.get(i);
      ObjectAnimator zoomInX = ObjectAnimator.ofInt(tile, "size", 0, mTileWidth);
      zoomInX.setDuration(400);
      zoomInX.setInterpolator(new OvershootInterpolator());
      zoomInX.setStartDelay( i * 50);
      tileAnimators.add(zoomInX);
    }

    List<Animator> buttonAnimators = new ArrayList<>();
    for (int i = 0; i < mButtons.size(); i++) {
      RotateButton button = mButtons.get(i);
      ObjectAnimator buttonPopOut = ObjectAnimator.ofFloat(button, "size", 0, (mTileWidth / mButtonSizeRatio));
      buttonPopOut.setDuration(400);
      buttonPopOut.setInterpolator(new OvershootInterpolator());
      buttonAnimators.add(buttonPopOut);
    }
    AnimatorSet tileAnimatorSet = (AnimatorSet) startTilesAnimation();
    AnimatorSet buttonAnimatorSet = (AnimatorSet) startButtonsAnimation();
    startAnimatorSet = new AnimatorSet();
    startAnimatorSet.play(tileAnimatorSet).before(buttonAnimatorSet);
    startAnimatorSet.start();

//    ValueAnimator colorAnim = ObjectAnimator.ofInt(this, "backgroundColor", 0xffFF8080, 0xff8080FF);
//    colorAnim.setDuration(3000);
//    colorAnim.setEvaluator(new ArgbEvaluator());
//    colorAnim.setRepeatCount(ValueAnimator.INFINITE);
//    colorAnim.setRepeatMode(ValueAnimator.REVERSE);
//    colorAnim.start();
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (event.getAction() != MotionEvent.ACTION_DOWN) {
      return false;
    }

    if (moveAnimatorSet != null && moveAnimatorSet.isRunning()) {
      return false;
    }

    float x = event.getX();
    float y = event.getY();
    Log.i("shishnashki", "touched at: " + x + ", " + y);

    //find pressed button
    //play button press animation
    //find four tile around the button
    //play animation of tiles moving to their new places
    RotateButton pressedButton = null;
    for (RotateButton button :
        mButtons) {
      float butX = button.getPosition().x;
      float butY = button.getPosition().y;
      double squareDistance = Math.pow(butX - x, 2) + Math.pow(butY - y, 2);
      if (squareDistance <= Math.pow(button.getSize() / 2f, 2) ) {

        pressedButton = button;

        float endRot = mDirection == 0 ? 90f : -90f;
        ObjectAnimator rotation = ObjectAnimator.ofFloat(button, "rotation", 0, endRot);
        rotation.setDuration(300);
        rotation.setInterpolator(new DecelerateInterpolator());
        clickAnimator = new AnimatorSet();
        clickAnimator.play(rotation);
        clickAnimator.start();
      }
    }

    if (pressedButton != null) {
      int col = pressedButton.getCol();
      int row = pressedButton.getRow();
      int[] tileNumSrc = {(row * 4) + col, (row * 4) + col + 1, ((row + 1) * 4) + col, ((row + 1) * 4) + col + 1};
      int[] tileNumTrg;
      tileNumTrg = mDirection == 0 ? rotateCW(tileNumSrc) : rotateCCW(tileNumSrc);

      List<Animator> moveAnimators = new ArrayList<>();
      for (int i = 0; i < 4; i++) {
        Point from = mTiles.get(tileNumSrc[i]).getPosition();
        Point to = mTiles.get(tileNumTrg[i]).getPosition();
        ObjectAnimator mover = ObjectAnimator.ofObject(mTiles.get(tileNumSrc[i]), "position", new PointEvaluator(), from, to);
        mover.setDuration(300);

        AnimatorSet move = new AnimatorSet();
        move.play(mover);
        moveAnimators.add(move);
      }

      moveAnimatorSet = new AnimatorSet();
      moveAnimatorSet.playTogether(moveAnimators);
      moveAnimatorSet.start();
    }
    return true;
  }

  private int[] rotateCW(int[] tileNumsSrc) {
    //New places (src -> target): 0->1, 1->3, 3->2, 2->0
    int[] tileNumTrg = {tileNumsSrc[1], tileNumsSrc[3], tileNumsSrc[0], tileNumsSrc[2]};
    Tile temp = mTiles.get(tileNumsSrc[0]);
    mTiles.set(tileNumsSrc[0], mTiles.get(tileNumsSrc[2]));
    mTiles.set(tileNumsSrc[2], mTiles.get(tileNumsSrc[3]));
    mTiles.set(tileNumsSrc[3], mTiles.get(tileNumsSrc[1]));
    mTiles.set(tileNumsSrc[1], temp);
    return tileNumTrg;
  }
  private int[] rotateCCW(int[] tileNumsSrc) {
    //New places (src -> target): 0->2, 2->3, 3->1, 1->0
    int[] tileNumTrg = {tileNumsSrc[2], tileNumsSrc[0], tileNumsSrc[3], tileNumsSrc[1]};
    Tile temp = mTiles.get(tileNumsSrc[0]);
    mTiles.set(tileNumsSrc[0], mTiles.get(tileNumsSrc[1]));
    mTiles.set(tileNumsSrc[1], mTiles.get(tileNumsSrc[3]));
    mTiles.set(tileNumsSrc[3], mTiles.get(tileNumsSrc[2]));
    mTiles.set(tileNumsSrc[2], temp);
    return tileNumTrg;
  }

  RectF rectDst = new RectF();
  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    for (Tile tile : mTiles) {
      float xOffset = tile.getPosition().x - tile.getSize() / 2;
      float yOffset = tile.getPosition().y - tile.getSize() / 2;
      canvas.save();
      canvas.translate(xOffset, yOffset);
      tile.setTile(mTiles.indexOf(tile) + 1 == tile.getNumber());
      tile.getDrawable().draw(canvas);
      canvas.restore();
    }

    for (RotateButton button :
        mButtons) {
      float xOffset = button.getPosition().x - button.getSize() / 2;
      float yOffset = button.getPosition().y - button.getSize() / 2;
      rectDst.top = 0;
      rectDst.left = 0;
      rectDst.right = button.getSize();
      rectDst.bottom = button.getSize();
      canvas.save();
      canvas.rotate(button.getRotation(), xOffset + button.getSize() / 2, yOffset + button.getSize() / 2);
      canvas.translate(xOffset, yOffset);
      if (mDirection == 0) {
        canvas.drawBitmap(button.getBitmap(), mButtonCW, rectDst, null);
      } else {
        canvas.drawBitmap(button.getBitmap(), mButtonCCW, rectDst, null);
      }
      canvas.restore();
    }
  }
}