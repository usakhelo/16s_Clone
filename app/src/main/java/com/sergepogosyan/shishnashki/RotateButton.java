package com.sergepogosyan.shishnashki;

import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

public class RotateButton {
  private Point mPosition;

  private View mView;
  private BitmapDrawable mBitmap;

  private int mCol, mRow;

  public int getCol() {
    return mCol;
  }
  public int getRow() {
    return mRow;
  }

  public Drawable getDrawable() {
    return mBitmap;
  }

  public Point getPosition() {
    return mPosition;
  }
  public void setPosition(Point position) {
    this.mPosition = position;
  }

  public float getSize() {
    return mBitmap.getBounds().width();
  }

  public void setSize(float size) {
    mBitmap.setBounds(0, 0, (int)size, (int)size);
    mView.postInvalidate();
  }

  public RotateButton(int col, int row, View view) {
    mCol = col;
    mRow = row;
    mView = view;
    mBitmap = (BitmapDrawable) mView.getResources().getDrawable(Images.buttons[0]);
  }
}