package com.sergepogosyan.shishnashki;

import android.app.Application;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class GameApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    // initalize Calligraphy
    CalligraphyConfig.initDefault(
        new CalligraphyConfig.Builder()
            .setDefaultFontPath("fonts/LuckiestGuy.ttf")
            .setFontAttrId(R.attr.fontPath)
            .build()
    );
  }
}