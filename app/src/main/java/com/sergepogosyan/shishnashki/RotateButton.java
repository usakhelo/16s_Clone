package com.sergepogosyan.shishnashki;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

public class RotateButton {
  private Point mPosition;

  private float mRotation;
  private float mSize;

  private View mView;
  private Bitmap mBitmap;

  private int mCol, mRow;

  public int getCol() {
    return mCol;
  }
  public int getRow() {
    return mRow;
  }

  public Bitmap getBitmap() {
    return mBitmap;
  }

  public Point getPosition() {
    return mPosition;
  }
  public void setPosition(Point position) {
    this.mPosition = position;
  }

  public float getSize() {
    return mSize;
  }

  public void setSize(float size) {
    mSize = size;
    mView.postInvalidate();
  }

  public float getRotation() {
    return mRotation;
  }

  public void setRotation(float rotation) {
    this.mRotation = rotation;
  }

  public RotateButton(int col, int row, View view, Bitmap bitmap) {
    mCol = col;
    mRow = row;
    mView = view;
    mBitmap = bitmap;
  }
}