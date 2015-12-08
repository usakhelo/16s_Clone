package com.sergepogosyan.shishnashki;

import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

public class Tile {
  private Point mPosition;
  private int mTileSet;
  private View mView;
  private BitmapDrawable mBitmap;
  private int mNumber;

  public int getNumber() {
    return mNumber;
  }

  public void setNumber(int num) {
    mNumber = num;
  }

  public Point getPosition() {
    return mPosition;
  }
  public void setPosition(Point position) {
    this.mPosition = position;
    mView.postInvalidate();
  }
  public int getSize() {
    return mBitmap.getBounds().width();
  }
  public void setSize(int size) {
    mBitmap.setBounds(0, 0, size, size);
    mView.postInvalidate();
  }

  public Drawable getDrawable() {
    return mBitmap;
  }

  public void setTile(boolean set) {
    mTileSet = set ? 1 : 0;
    Rect size = mBitmap.getBounds();
    mBitmap = (BitmapDrawable) mView.getResources().getDrawable(Images.tiles[mTileSet][mNumber-1]);
    mBitmap.setBounds(0, 0, size.width(), size.height());
  }

  public Tile(int num, View view) {
    mNumber = num;
    mView = view;
    mBitmap = (BitmapDrawable) mView.getResources().getDrawable(Images.tiles[mTileSet][mNumber-1]);
  }
}
