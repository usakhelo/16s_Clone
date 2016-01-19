package com.sergepogosyan.shishnashki;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
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
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TileView extends View {

  private static final String TAG = "shishnashki Tileview";

  private int mDirection;
  private int mDirectionTexture;
  private Rect mButtonRectCW, mButtonRectCCW;
  private int mTileWidth;
  private int mTileMarginRatio = 10;
  private int mButtonSizeRatio = 2;
  private int mTileCount = 4;
  private ArrayList<Tile> mTiles;
  private ArrayList<RotateButton> mButtons;

  private AnimatorSet moveAnimatorSet, buttonAnimatorSet;
  private OnTurnListener mOnTurnListener;

  public interface OnTurnListener {
    void onTurn();
  }

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
      mTiles.get(i).setInPlace(mTiles.indexOf(mTiles.get(i)) + 1 == tileNums[i]);
    }
  }

  public void resetTiles() {
    if (moveAnimatorSet != null && moveAnimatorSet.isRunning()) {
      Log.i(TAG, "resetTiles: " + moveAnimatorSet);
      return;
    }

    Animator hide = hideAnimation();
    hide.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        setTiles(new int[] {1,2,3,4,5,6,7,8,9,10,15,11,13,14,16,12});
        mDirection = mDirectionTexture = 0;
        startAnimation();
      }
    });
    hide.start();
  }

  public void shuffleTiles() {
    if (moveAnimatorSet != null && moveAnimatorSet.isRunning()) {
      Log.i(TAG, "shuffleTiles1: " + moveAnimatorSet);
      return;
    }

    Animator hide = hideAnimation();
//    hide.removeAllListeners();
    hide.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        ArrayList<Integer> nums = new ArrayList<>();
        for (int i = 0; i < (mTileCount * mTileCount); i++) {
          nums.add(i + 1);
        }
        Collections.shuffle(nums);
        int[] newNums = new int[nums.size()];
        for (int i = 0; i < nums.size(); i++)
          newNums[i] = nums.get(i);

        setTiles(newNums);
        mDirection = mDirectionTexture = 0;
        startAnimation();
//        Log.i(TAG, "shuffleTiles3: " + moveAnimatorSet);
      }
    });
    hide.start();
//    Log.i(TAG, "shuffleTiles2: " + moveAnimatorSet);
  }

  public void initTiles() {
    mTiles = new ArrayList<>();
    Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.tiles);
    for (int i = 0; i < (mTileCount * mTileCount); i++) {
      Tile newTile = new Tile(i + 1, bitmap, this);
      newTile.setInPlace(true);
      mTiles.add(newTile);
    }
  }

  private void initButtons() {
    mButtons = new ArrayList<>();
    Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.button);
    mButtonRectCW = new Rect(0, 0, bitmap.getWidth() / 2, bitmap.getHeight());
    mButtonRectCCW = new Rect(bitmap.getWidth() / 2, 0, bitmap.getWidth(), bitmap.getHeight());
    for (int i = 0; i < (mTileCount - 1); i++) {
      for (int j = 0; j < (mTileCount - 1); j++) {
        mButtons.add(new RotateButton(j, i, this, bitmap));
      }
    }
  }

  public int getDirection() {
    return mDirection;
  }

  public void setDirectionAnim(int direction) {
    if (buttonAnimatorSet != null && buttonAnimatorSet.isRunning())
      return;
    Animator hide = hideButtonsAnim();
//    hide.removeAllListeners();
    hide.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        mDirectionTexture = 1 - mDirectionTexture;
        startButtonsAnimation().start();
      }
    });
    mDirectionTexture = mDirection;
    mDirection = direction;
    hide.start();
  }

  public void setDirection(int direction) {
    mDirection = direction;
    mDirectionTexture = mDirection;
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
//    Log.i(TAG, "onMeasure: " + w + ", " + h);
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);

    int leftPad = getPaddingLeft();
    int topPad = getPaddingTop();
    int xPad = leftPad + getPaddingRight();
    int yPad = topPad + getPaddingBottom();

    int ww = w - xPad;
    int hh = h - yPad;

    int tileMargin = ww / mTileCount / mTileMarginRatio;
    mTileWidth = (ww - (tileMargin * (mTileCount - 1))) / mTileCount;

    //tiles
    int tilePlaceW = ww / mTileCount;
    int tilePlaceH = hh / mTileCount;
    for (int i = 0; i < mTileCount; i++) {
      for (int j = 0; j < mTileCount; j++) {
        Tile tile = mTiles.get((i * mTileCount) + j);
        tile.setSize(mTileWidth);
        int x = leftPad + (tilePlaceW * j) + (tilePlaceW / 2);
        int y = topPad + (tilePlaceH * i) + (tilePlaceH / 2);
        tile.setPosition(new Point(x, y));
      }
    }

    //buttons
    int buttonCount = mTileCount - 1;
    for (int i = 0; i < buttonCount; i++) {
      for (int j = 0; j < buttonCount; j++) {
        RotateButton button = mButtons.get((i * buttonCount) + j);
        button.setSize((mTileWidth / mButtonSizeRatio));
        int x = leftPad + tilePlaceW + (tilePlaceW * j);
        int y = topPad + tilePlaceH + (tilePlaceH * i);
        button.setPosition(new Point(x, y));
      }
    }
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
    moveAnimatorSet = new AnimatorSet();
    moveAnimatorSet.play(tileAnimatorSet).with(buttonAnimatorSet);
    return moveAnimatorSet;
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
    buttonAnimatorSet = new AnimatorSet();
    buttonAnimatorSet.playTogether(buttonAnimators);
    return buttonAnimatorSet;
  }

  private void startAnimation() {
    AnimatorSet tileAnimatorSet = (AnimatorSet) startTilesAnimation();
    AnimatorSet buttonAnimatorSet = (AnimatorSet) startButtonsAnimation();
    moveAnimatorSet = new AnimatorSet();
    moveAnimatorSet.play(tileAnimatorSet).before(buttonAnimatorSet);
    moveAnimatorSet.start();
  }

  public RotateButton findButton(float x, float y) {
    for (RotateButton button : mButtons) {
      float butX = button.getPosition().x;
      float butY = button.getPosition().y;
      double squareDistance = Math.pow(butX - x, 2) + Math.pow(butY - y, 2);
      if (squareDistance <= Math.pow(button.getSize() / 2f, 2) ) {
        return button;
      }
    }
    return null;
  }

  public void pressButton(RotateButton button) {
    if (button == null)
      return;
    if (moveAnimatorSet != null && moveAnimatorSet.isRunning()) {
      return;
    }

    int col = button.getCol();
    int row = button.getRow();
    List<Animator> moveAnimators = new ArrayList<>();

    float endRot = mDirection == 0 ? 90f : -90f;
    ObjectAnimator rotation = ObjectAnimator.ofFloat(button, "rotation", 0, endRot);
    rotation.setDuration(300);
    rotation.setInterpolator(new DecelerateInterpolator());
    moveAnimators.add(rotation);

    int[] tileNumSrc = {
        (row * mTileCount) + col,
        (row * mTileCount) + col + 1,
        ((row + 1) * mTileCount) + col,
        ((row + 1) * mTileCount) + col + 1};
    int[] tileNumTrg;
    tileNumTrg = mDirection == 0 ? rotateCW(tileNumSrc) : rotateCCW(tileNumSrc);

    for (int i = 0; i < 4; i++) {
      Point from = mTiles.get(tileNumSrc[i]).getPosition();
      Point to = mTiles.get(tileNumTrg[i]).getPosition();
      ObjectAnimator mover = ObjectAnimator.ofObject(mTiles.get(tileNumSrc[i]), "position", new PointEvaluator(), from, to);
      mover.setDuration(300);
      mover.addListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          super.onAnimationEnd(animation);
          Tile tile = (Tile)((ObjectAnimator)animation).getTarget();
          if (tile == null) return;
          tile.setInPlace(mTiles.indexOf(tile) + 1 == tile.getNumber());
        }
      });
      moveAnimators.add(mover);
    }

    moveAnimatorSet = new AnimatorSet();
    moveAnimatorSet.playTogether(moveAnimators);
    moveAnimatorSet.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        postInvalidate();
        mOnTurnListener.onTurn();
        Log.i(TAG, "onAnimationEnd:");
      }
    });
    moveAnimatorSet.start();
  }

  public void setOnTurnListener(OnTurnListener listener) {
    this.mOnTurnListener = listener;
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
      rectDst.top = 0;
      rectDst.left = 0;
      rectDst.right = tile.getSize();
      rectDst.bottom = tile.getSize();
      canvas.save();
      canvas.translate(xOffset, yOffset);
      Rect rectSrc = tile.getRectSrc();
      if (tile.isInPlace())
        tile.getRectSrc().offsetTo(rectSrc.left, rectSrc.height());
      else
        tile.getRectSrc().offsetTo(rectSrc.left, 0);
      canvas.drawBitmap(tile.getBitmap(), rectSrc, rectDst, null);
      canvas.restore();
    }

    for (RotateButton button : mButtons) {
      float xOffset = button.getPosition().x - button.getSize() / 2;
      float yOffset = button.getPosition().y - button.getSize() / 2;
      rectDst.top = 0;
      rectDst.left = 0;
      rectDst.right = button.getSize();
      rectDst.bottom = button.getSize();
      canvas.save();
      canvas.rotate(button.getRotation(), button.getPosition().x, button.getPosition().y);
      canvas.translate(xOffset, yOffset);
      if (mDirectionTexture == 0) {
        canvas.drawBitmap(button.getBitmap(), mButtonRectCW, rectDst, null);
      } else {
        canvas.drawBitmap(button.getBitmap(), mButtonRectCCW, rectDst, null);
      }
      canvas.restore();
    }
  }
}