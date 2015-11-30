package com.sergepogosyan.shishnashki;

import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.view.View;

public class Tile {
  private float x = 0, y = 0;
  private int color;
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
  public ShapeDrawable getShape() {
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

  public int getColor() {
    return color;
  }
  public void setColor(int value) {
    color = value;
  }

  public float getWidth() {
    return mShape.getShape().getWidth();
  }
  public void setWidth(float width) {
    Shape s = mShape.getShape();
    s.resize(width, s.getHeight());
    mView.postInvalidate();
  }

  public float getHeight() {
    return mShape.getShape().getHeight();
  }
  public void setHeight(float height) {
    Shape s = mShape.getShape();
    s.resize(s.getWidth(), height);
    mView.postInvalidate();
  }

  public Tile(int num, View view) {
    mNumber = num;
    mView = view;
    float rad = 8f;
    RoundRectShape shape = new RoundRectShape(new float[] {rad, rad, rad, rad, rad, rad, rad, rad, rad}, null, null);
    mShape = new ShapeDrawable(shape);
    int red = (int)(Math.random() * 255);
    int green = (int)(Math.random() * 255);
    int blue = (int)(Math.random() * 255);
    int color = 0xff000000 | red << 16 | green << 8 | blue;
    Paint paint = mShape.getPaint();
    paint.setFlags(Paint.ANTI_ALIAS_FLAG);
//    int darkColor = 0xff000000 | red/4 << 16 | green/4 << 8 | blue/4;
//    RadialGradient gradient = new RadialGradient(37.5f, 12.5f, 50f, color, darkColor, Shader.TileMode.CLAMP);
//    paint.setShader(gradient);
    paint.setColor(color);
    paint.setStyle(Paint.Style.FILL);
  }
}
