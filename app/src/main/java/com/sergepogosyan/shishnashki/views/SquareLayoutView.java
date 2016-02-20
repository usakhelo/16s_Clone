package com.sergepogosyan.shishnashki.views;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.sergepogosyan.shishnashki.R;

public class SquareLayoutView extends FrameLayout {

  public SquareLayoutView(Context context) {
    super(context);
  }

  public SquareLayoutView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public SquareLayoutView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    int w = MeasureSpec.getSize(widthMeasureSpec);
    int h = MeasureSpec.getSize(heightMeasureSpec);

    Configuration config = getResources().getConfiguration();
    if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
      setMeasuredDimension(h, h);
    }
    else {
      setMeasuredDimension(w, w);
    }
  }

}
