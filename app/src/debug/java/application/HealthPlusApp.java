package application;


import com.facebook.stetho.Stetho;

public class HealthPlusApp extends plus.health.app.application.HealthPlusApp {
  public void onCreate() {
    super.onCreate();
    Stetho.initializeWithDefaults(this);
  }
}