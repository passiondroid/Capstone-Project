package plus.health.app.application;

import android.app.Application;

import com.facebook.stetho.Stetho;

public class HealthPlusApp extends Application {
  public void onCreate() {
    super.onCreate();
    Stetho.initializeWithDefaults(this);
  }
}