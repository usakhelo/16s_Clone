package com.sergepogosyan.shishnashki;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Tile View - view that contains game tiles
 */
public class TileView extends View {
  private int mTextColor = Color.RED;
  private float mTextSize = 0;
  private RectF mBounds;
  private float mLeftPad, mTopPad, mTileWidth, mTileHeight, mTileMargin;
  private ArrayList<Tile> mTiles;

  private TextPaint mTextPaint;
  private float mTextWidth;
  private float mTextHeight;

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

    invalidateTextPaintAndMeasurements();
    initTiles();
  }

  private void invalidateTextPaintAndMeasurements() {
    mTextPaint.setTextSize(mTextSize);
    mTextPaint.setColor(mTextColor);
    mTextWidth = mTextPaint.measureText("2");

    Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
    mTextHeight = fontMetrics.bottom;
  }

  private void initTiles() {
    mTiles = new ArrayList<>();
    ArrayList<Integer> nums = new ArrayList(Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16));
//    Collections.shuffle(nums);
    for (int index : nums) {
      mTiles.add(new Tile(index));
    }
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);

    // Account for padding
    mLeftPad = getPaddingLeft();
    mTopPad = getPaddingTop();
    float xpad = (float) (getPaddingLeft() + getPaddingRight());
    float ypad = (float) (getPaddingTop() + getPaddingBottom());

    float ww = (float) w - xpad;
    float hh = (float) h - ypad;

    mTileMargin = ww / 4f / 5f;
    mBounds = new RectF(0, 0, ww, hh);
    mTileWidth = (mBounds.width() - (mTileMargin * 3)) /4f;
    mTileHeight = (mBounds.height() - (mTileMargin * 3))/4f;

    for (Tile tile : mTiles) {
      tile.setWidth(mTileWidth);
      tile.setHeight(mTileHeight);
    }

//    onDataChanged();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        Tile tile = mTiles.get((i * 4) + j);
        canvas.save();
        float xOffset = j == 0 ? mLeftPad: mLeftPad + ((mTileWidth + mTileMargin) * j);
        float yOffset = i == 0 ? mTopPad: mTopPad + ((mTileHeight + mTileMargin) * i);
        canvas.translate(xOffset, yOffset);
        tile.getShape().draw(canvas);

        canvas.drawText(String.valueOf(tile.getNumber()),
            mTileWidth / 2,
            (mTileHeight + mTextHeight) / 1.7f,
            mTextPaint);
        canvas.restore();
      }
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
//    invalidateTextPaintAndMeasurements();
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
//    invalidateTextPaintAndMeasurements();
  }
}
