package com.sergepogosyan.shishnashki;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

public class Tile {
  private Point mPosition;
  private float mSize;
  private View mView;
  private Bitmap mBitmap;
  private Rect mRectSrc;
  private int mNumber;

  public boolean isInPlace() {
    return mIsInPlace;
  }

  public void setInPlace(boolean mIsInPlace) {
    this.mIsInPlace = mIsInPlace;
  }

  public boolean mIsInPlace;

  public Rect getRectSrc() {
    return mRectSrc;
  }

  public void setRectSrc(Rect rectSrc) {
    this.mRectSrc = rectSrc;
  }

  public int getNumber() {
    return mNumber;
  }

  public void setNumber(int num) {
    mNumber = num;
    if (mBitmap != null) {
      int tileWidth = mBitmap.getWidth() / 16;
      int tileHeight = mBitmap.getHeight() / 2;
      setRectSrc(new Rect((num - 1)*tileWidth, 0, num * tileWidth, tileHeight));
    } else
      Log.e("shishnashki", "bitmap is not set to tile#: " + num);
  }

  public Point getPosition() {
    return mPosition;
  }
  public void setPosition(Point position) {
    this.mPosition = position;
    mView.postInvalidate();
  }
  public int getSize() {
    return (int)mSize;
  }
  public void setSize(int size) {
    mSize = size;
    mView.postInvalidate(); // TODO: 12/10/2015 minimize invalidate calls amount
  }

  public Bitmap getBitmap() {
    return mBitmap;
  }

  public Tile(int num, Bitmap bitmap, View view) {
    mView = view;
    mBitmap = bitmap;
    setNumber(num);
  }
}
