package cm.aptoide.pt.crashreports;

import android.content.Context;
import cm.aptoide.pt.logger.Logger;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import io.fabric.sdk.android.Fabric;
import lombok.Setter;

/**
 * Created by neuro on 09-12-2016.
 */

public class AptoideCrashLogger implements CrashLogger {

  static final String LANGUAGE = "Language";
  private final static String TAG = AptoideCrashLogger.class.getSimpleName();   //TAG for the logger
  private static final AptoideCrashLogger instance = new AptoideCrashLogger();
  //var with the language the app is set to
  @Setter private String language;

  protected AptoideCrashLogger() {
  }

  public static AptoideCrashLogger getInstance() {
    return instance;
  }

  /**
   * setup crash reports
   *
   * @param context context from the class that's calling this method
   */
  public AptoideCrashLogger setup(Context context, boolean isDisabled) {
    Fabric.with(context,
        new Crashlytics.Builder().core(new CrashlyticsCore.Builder().disabled(isDisabled).build())
            .build());
    Logger.d(TAG, "Setup of AptoideCrashLogger");
    return this;
  }

  /**
   * logs exception in crashes
   *
   * @param throwable exception you want to send
   */
  @Override public void logException(Throwable throwable) {
    if (!Fabric.isInitialized()) {
      Logger.w(TAG, "Fabric not initialized.");
      return;
    }

    Crashlytics.setString(LANGUAGE, language);
    Crashlytics.logException(throwable);
    Logger.d(TAG, "logException: " + throwable.toString());
  }

  /**
   * logs string in crashes
   *
   * @param key unique key to send on crash
   * @param value value you want associated with the key
   */
  @Override public void logString(String key, String value) {
    if (!Fabric.isInitialized()) {
      Logger.w(TAG, "Fabric not initialized.");
      return;
    }

    Crashlytics.setString(LANGUAGE, language);
    Crashlytics.setString(key, value);
    Logger.d(TAG, "logString : key: " + key + " , value: " + value);
  }
}
