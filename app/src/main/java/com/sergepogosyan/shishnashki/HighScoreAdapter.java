package com.sergepogosyan.shishnashki;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

public class HighScoreAdapter extends SimpleCursorAdapter {

  private static final String TAG = "shishnashki adapter";
  private Bitmap iconBitmap;
  private int tileWidth, tileHeight;

  public HighScoreAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
    super(context, layout, c, from, to, flags);

    this.iconBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.tiles);

    tileWidth = iconBitmap.getWidth() / 16;
    tileHeight = iconBitmap.getHeight() / 2;
  }

  @Override
  public void setViewImage(ImageView v, String value) {
    super.setViewImage(v, value);
    int num = 0;
    try {
      num = Integer.parseInt(value);
    } catch (Exception e) {
      Log.e(TAG, "Could not get player id");
    }

    v.setImageBitmap(Bitmap.createBitmap(iconBitmap, (--num) * tileWidth, 0, tileWidth, tileHeight));
    v.setScaleType(ImageView.ScaleType.FIT_CENTER);
    Log.i(TAG, "setViewImage:" + v + ":" + value);
  }
}
