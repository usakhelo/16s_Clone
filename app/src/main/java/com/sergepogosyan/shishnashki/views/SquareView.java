package com.sergepogosyan.shishnashki.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
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

import com.sergepogosyan.shishnashki.R;
import com.sergepogosyan.shishnashki.RotateButton;
import com.sergepogosyan.shishnashki.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SquareView extends View {

  private static final String TAG = "shishnashki SquareView";

  public SquareView(Context context) {
    super(context);
    init(null, 0);
  }

  public SquareView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(attrs, 0);
  }

  public SquareView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init(attrs, defStyle);
  }

  private void init(AttributeSet attrs, int defStyle) {
    final TypedArray a = getContext().obtainStyledAttributes(
        attrs, R.styleable.TileView, defStyle, 0);
    a.recycle();
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
}