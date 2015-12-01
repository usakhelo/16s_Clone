package com.sergepogosyan.shishnashki;

import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.Shape;
import android.view.View;

public class RotateButton {
  private float x = 0, y = 0;

  private Paint mPaint;
  private View mView;
  private ShapeDrawable mShape;

  private int mNumber;

  public int getNumber() {
    return mNumber;
  }
  public Paint getPaint() {
    return mPaint;
  }
  public void setPaint(Paint value) {
    mPaint = value;
  }
  public ShapeDrawable getDrawable() {
    return mShape;
  }
  public void setX(float value) {
    x = value;
  }
  public float getX() {
    return x;
  }
  public void setY(float value) {
    y = value;
  }
  public float getY() {
    return y;
  }

  public float getSize() {
    return mShape.getShape().getWidth();
  }
  public void setSize(float size) {
    Shape s = mShape.getShape();
    s.resize(size, size);
    mView.postInvalidate();
  }

  public RotateButton(int num, View view) {
    mNumber = num;
    mView = view;
    OvalShape shape = new OvalShape();
    mShape = new ShapeDrawable(shape);
    int red = (int)(Math.random() * 255);
    int green = (int)(Math.random() * 255);
    int blue = (int)(Math.random() * 255);
    int color = 0xff000000 | red << 16 | green << 8 | blue;
    Paint paint = mShape.getPaint();
    paint.setFlags(Paint.ANTI_ALIAS_FLAG);
    paint.setColor(color);
    paint.setStyle(Paint.Style.FILL);
  }
}
