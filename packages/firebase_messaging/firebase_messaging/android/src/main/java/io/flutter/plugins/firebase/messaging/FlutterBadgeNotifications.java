package io.flutter.plugins.firebase.messaging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.util.Log;
import android.view.Display;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.embedding.engine.plugins.shim.ShimPluginRegistry;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.view.FlutterCallbackInformation;

public class FlutterBadgeNotification extends FlutterActivity{
  MethodChannel channel;
  private static io.flutter.plugin.common.PluginRegistry.PluginRegistrantCallback
    pluginRegistrantCallback;
  private static final String CHANNEL = "flutter.badge.notification/request";
  private static final String TAG = "FLTFireMsgReceiver";
  FlutterEngine flutterEngine;

  public void setBadge(Context context, int count) {
    File path = context.getFilesDir();
    File file = new File(path, "count.text");
    Log.d(TAG, "Setting badge");
    String launcherClassName = getLauncherClassName(context);
    if (launcherClassName == null) {
      return;
    }
    Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
    if(file.exists()){
      StringBuilder text = new StringBuilder();

      try {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String content;
        content = br.readLine();
        text.append(content);
        br.close();
        Log.d(TAG, "Content "+text.toString());
      }
      catch (IOException e) {
        e.printStackTrace();
      }
      int total = 1+Integer.parseInt(text.toString());
      intent.putExtra("badge_count", total);
    }else{
      intent.putExtra("badge_count", count);
    }
    intent.putExtra("badge_count_package_name", context.getPackageName());
    intent.putExtra("badge_count_class_name", launcherClassName);
    context.sendBroadcast(intent);
    addCount(context);
  }

  public void addCount(Context context){
    File path = context.getFilesDir();
    File file = new File(path, "count.text");
    if(file.exists()){
      StringBuilder text = new StringBuilder();

      try {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String content;
        content = br.readLine();
        text.append(content);
        br.close();
        Log.d(TAG, "Content "+text.toString());
      }
      catch (IOException e) {
        e.printStackTrace();
      }
      int total = 1+Integer.parseInt(text.toString());

      FileOutputStream stream = null;
      try {
        stream = new FileOutputStream(file);
        stream.write(String.valueOf(total).getBytes());
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }else{
      FileOutputStream stream = null;
      try {
        stream = new FileOutputStream(file);
        stream.write(String.valueOf(1).getBytes());
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    Log.d(TAG, "File saved");

  }

  public String getLauncherClassName(Context context) {

    PackageManager pm = context.getPackageManager();

    Intent intent = new Intent(Intent.ACTION_MAIN);
    intent.addCategory(Intent.CATEGORY_LAUNCHER);

    List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
    for (ResolveInfo resolveInfo : resolveInfos) {
      String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
      if (pkgName.equalsIgnoreCase(context.getPackageName())) {
        String className = resolveInfo.activityInfo.name;
        return className;
      }
    }
    return null;
  }

  @Override
  public String getPackageName()
  {
    Log.d(TAG, "Called method");
    try
    {
      throw new Exception();
    }
    catch (Exception e)
    {
      StackTraceElement[] elements = e.getStackTrace();

      for (StackTraceElement element: elements)
      {
        if(element.getClassName().startsWith("android.webkit."))
        {
          return "br.com.toakiapp";
        }
      }
    }
    try {
      this.createPackageContext("br.com.toakiapp", Context.CONTEXT_IGNORE_SECURITY);
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    return "br.com.toakiapp";
  }

  @Override
  public Context createPackageContext(String var, int val) throws PackageManager.NameNotFoundException {
    return createPackageContext(var, val);
  }
}
