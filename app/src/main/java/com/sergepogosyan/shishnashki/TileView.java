package com.sergepogosyan.shishnashki;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Tile View - view that contains game tiles
 */
public class TileView extends View {
  private int mTextColor = Color.RED;
  private float mTextSize = 0;
  private RectF mBounds;
  private float mLeftPad, mTopPad, mTileWidth, mTileHeight;
  private float mTileMarginRatio = 5f;
  private float mButtonSizeRatio = 2f;
  private int mTileCount = 4;
  private ArrayList<Tile> mTiles;
  private ArrayList<RotateButton> mButtons;

  private AnimatorSet moveAnimatorSet, startAnimatorSet, clickAnimator;

  private TextPaint mTextPaint;
  private float mTextWidth;
  private float mTextHeight;

  class PointEvaluator implements TypeEvaluator {
    public Object evaluate(float fraction, Object startValue, Object endValue) {
      PointF startPoint = (PointF) startValue;
      PointF endPoint = (PointF) endValue;
      return new PointF(startPoint.x + fraction * (endPoint.x - startPoint.x),
          startPoint.y + fraction * (endPoint.y - startPoint.y));
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

    mTextColor = a.getColor(
        R.styleable.TileView_textColor,
        mTextColor);
    // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
    // values that should fall on pixel boundaries.
    mTextSize = a.getDimension(
        R.styleable.TileView_textSize,
        mTextSize);

    a.recycle();

    mTextPaint = new TextPaint();
    mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
    mTextPaint.setTextAlign(Paint.Align.CENTER);

    initTextMeasurements();
    initTiles();
    initButtons();
  }

  private void initTextMeasurements() {
    mTextPaint.setTextSize(mTextSize);
    mTextPaint.setColor(mTextColor);
    mTextWidth = mTextPaint.measureText("2");

    Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
    mTextHeight = fontMetrics.bottom;
  }

  private void initButtons() {
    mButtons = new ArrayList<>();
    for (int i = 0; i < (mTileCount - 1); i++) {
      for (int j = 0; j < (mTileCount - 1); j++) {
        mButtons.add(new RotateButton(j, i, this));
      }
    }
  }

  private void initTiles() {
    mTiles = new ArrayList<>();
    ArrayList<Integer> nums = new ArrayList<>();
    for (int i = 0; i < (mTileCount * mTileCount); i++) {
      nums.add(i + 1);
    }
    Collections.shuffle(nums);
    for (int index : nums) {
      mTiles.add(new Tile(index, this));
    }
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);

    // Account for padding
    mLeftPad = getPaddingLeft();
    mTopPad = getPaddingTop();
    float xpad = mLeftPad + getPaddingRight();
    float ypad = mTopPad + getPaddingBottom();

    float ww = (float) w - xpad;
    float hh = (float) h - ypad;

    float tileMargin = ww / mTileCount / mTileMarginRatio;
    mBounds = new RectF(0, 0, ww, hh);
    mTileWidth = (mBounds.width() - (tileMargin * (mTileCount - 1))) / mTileCount;
    mTileHeight = (mBounds.height() - (tileMargin * mTileCount - 1))/ mTileCount;

    //tiles
    float tilePlaceW = ww / mTileCount;
    float tilePlaceH = hh / mTileCount;
    for (int i = 0; i < mTileCount; i++) {
      for (int j = 0; j < mTileCount; j++) {
        Tile tile = mTiles.get((i * mTileCount) + j);
        tile.setWidth(0);
        tile.setHeight(0);
        float x = mLeftPad + (tilePlaceW * j) + (tilePlaceW / 2);
        float y = mTopPad + (tilePlaceH * i) + (tilePlaceH / 2);
        tile.setPosition(new PointF(x, y));
      }
    }

    //buttons
    int buttonCount = mTileCount - 1;
    for (int i = 0; i < buttonCount; i++) {
      for (int j = 0; j < buttonCount; j++) {
        RotateButton button = mButtons.get((i * buttonCount) + j);
        button.setSize(0);
        float x = mLeftPad + tilePlaceW + (tilePlaceW * j);
        float y = mTopPad + tilePlaceH + (tilePlaceH * i);
        button.setX(x);
        button.setY(y);
      }
    }

    startAnimation();
  }

  private void startAnimation() {
    List<Animator> tileAnimators = new ArrayList<>();
    for (int i = 0; i < mTiles.size(); i++) {
      Tile tile = mTiles.get(i);
      ObjectAnimator zoomInX = ObjectAnimator.ofFloat(tile, "width", 0, mTileWidth);
      ObjectAnimator zoomInY = ObjectAnimator.ofFloat(tile, "height", 0, mTileHeight);
      zoomInX.setDuration(400);
      zoomInY.setDuration(400);
      zoomInX.setInterpolator(new OvershootInterpolator());
      zoomInY.setInterpolator(new OvershootInterpolator());
      AnimatorSet zoomIn = new AnimatorSet();
      zoomIn.playTogether(zoomInX, zoomInY);
      zoomIn.setStartDelay( i * 50);
      tileAnimators.add(zoomIn);
    }

    List<Animator> buttonAnimators = new ArrayList<>();
    for (int i = 0; i < mButtons.size(); i++) {
      RotateButton button = mButtons.get(i);
      ObjectAnimator zoomInX = ObjectAnimator.ofFloat(button, "size", 0, (mTileWidth / mButtonSizeRatio));
      zoomInX.setDuration(400);
      zoomInX.setInterpolator(new OvershootInterpolator());
      buttonAnimators.add(zoomInX);
    }
    AnimatorSet tileAnimatorSet = new AnimatorSet();
    AnimatorSet buttonAnimatorSet = new AnimatorSet();
    buttonAnimatorSet.playTogether(buttonAnimators);
    tileAnimatorSet.playTogether(tileAnimators);
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
      float butX = button.getX();
      float butY = button.getY();
      double squareDistance = Math.pow(butX - x, 2) + Math.pow(butY - y, 2);
      if (squareDistance <= Math.pow(button.getSize() / 2f, 2) ) {

        pressedButton = button;
        Log.i("shishnashki", "button clicked: " + button.getCol());

        float butSize = (mTileWidth / mButtonSizeRatio);//button.getSize();
        ObjectAnimator zoomIn = ObjectAnimator.ofFloat(button, "size", butSize, butSize * .7f);
        ObjectAnimator zoomOut = ObjectAnimator.ofFloat(button, "size", butSize * .7f, butSize);
        zoomIn.setDuration(100);
        zoomOut.setDuration(200);
        zoomIn.setInterpolator(new DecelerateInterpolator());
        zoomOut.setInterpolator(new DecelerateInterpolator());
        clickAnimator = new AnimatorSet();
        clickAnimator.playSequentially(zoomIn, zoomOut);//play(zoomIn).before(zoomOut);
        clickAnimator.start();
//        break;
      }
    }

    if (pressedButton != null) {
      int col = pressedButton.getCol();
      int row = pressedButton.getRow();
      int[] tileNumsSrc = {(row * 4) + col, (row * 4) + col + 1, ((row + 1) * 4) + col, ((row + 1) * 4) + col + 1};
      int[] tileNumsTrg = {tileNumsSrc[1], tileNumsSrc[3], tileNumsSrc[0], tileNumsSrc[2]};
      Log.i("shishnashki", "button clicked2: " + (Arrays.asList(tileNumsTrg)).toString());

      //arrange tiles animation in tileNums. New places: 0->1, 1->3, 2->0, 3->2
      List<Animator> moveAnimators = new ArrayList<>();
      for (int i = 0; i < 4; i++) {
        PointF from = mTiles.get(tileNumsSrc[i]).getPosition();
        PointF to = mTiles.get(tileNumsTrg[i]).getPosition();
        ObjectAnimator mover = ObjectAnimator.ofObject(mTiles.get(tileNumsSrc[i]), "position", new PointEvaluator(), from, to);
        mover.setDuration(300);

        AnimatorSet move = new AnimatorSet();
        move.play(mover);
        moveAnimators.add(move);
      }

      Log.i("shishnashki", "move: " + mTiles.toString());

      //swap tiles in main list
      //arrange tiles animation in tileNums. New places (src -> target): 0->1, 1->3, 2->0, 3->2

      Tile temp = mTiles.get(tileNumsTrg[2]);
      mTiles.set(tileNumsTrg[2], mTiles.get(tileNumsSrc[2]));
      mTiles.set(tileNumsTrg[3], mTiles.get(tileNumsSrc[3]));
      mTiles.set(tileNumsTrg[1], mTiles.get(tileNumsSrc[1]));
      mTiles.set(tileNumsTrg[0], temp);


      moveAnimatorSet = new AnimatorSet();
      moveAnimatorSet.playTogether(moveAnimators);
      moveAnimatorSet.start();
    }
    return true;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    for (Tile tile : mTiles) {
      float xOffset = tile.getPosition().x - tile.getWidth() / 2;
      float yOffset = tile.getPosition().y - tile.getHeight() / 2;
      canvas.save();
      canvas.translate(xOffset, yOffset);
      if (mTiles.indexOf(tile) + 1 == tile.getNumber()){
        Paint paint = tile.getPaint();
        int darkColor = 0xff72d9d7;
        paint.setColor(darkColor);
      } else {
        Paint paint = tile.getPaint();
        int darkColor = 0xffffa35f;
        paint.setColor(darkColor);
      }
      tile.getDrawable().draw(canvas);

      canvas.drawText(String.valueOf(tile.getNumber()),
          mTileWidth / 2,
          (mTileHeight + mTextHeight) / 1.7f,
          mTextPaint);

      canvas.restore();
    }

    for (RotateButton button :
        mButtons) {
      float xOffset = button.getX() - button.getSize() / 2;
      float yOffset = button.getY() - button.getSize() / 2;
      canvas.save();
      canvas.translate(xOffset, yOffset);
      button.getDrawable().draw(canvas);
      canvas.restore();
    }
  }

  /**
   * Gets the color attribute value.
   *
   * @return The color attribute value.
   */
  public int getTextColor() {
    return mTextColor;
  }

  /**
   * Sets the view's example color attribute value. In the example view, this color
   * is the font color.
   *
   * @param exampleColor The example color attribute value to use.
   */
  public void setTextColor(int exampleColor) {
    mTextColor = exampleColor;
//    initTextMeasurements();
  }

  /**
   * Gets the example dimension attribute value.
   *
   * @return The example dimension attribute value.
   */
  public float getTextSize() {
    return mTextSize;
  }

  /**
   * Sets the view's example dimension attribute value. In the example view, this dimension
   * is the font size.
   *
   * @param exampleDimension The example dimension attribute value to use.
   */
  public void setTextSize(float exampleDimension) {
    mTextSize = exampleDimension;
//    initTextMeasurements();
  }
}