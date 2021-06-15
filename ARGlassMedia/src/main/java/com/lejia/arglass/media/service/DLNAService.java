package com.lejia.arglass.media.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;

import com.lejia.arglass.R;
import com.lejia.arglass.media.event.NativeAsyncEvent;
import com.lejia.arglass.media.instance.NotificationHelper;
import com.lejia.arglass.media.instance.ServerInstance;
import com.lejia.arglass.media.media.MediaUtils;
import com.plutinosoft.platinum.CallbackTypes;
import com.plutinosoft.platinum.ServerParams;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by huzongyao on 2018/6/7.
 * The service that manage the server instance
 */

public class DLNAService extends Service {

    public static final String EXTRA_SERVER_PARAMS = "EXTRA_SERVER_PARAMS";

    private static final String TAG = "DLNAService";
    private WifiManager.MulticastLock mMulticastLock;
    private Notification mNotification;
    private final String CHANNAL_ID = "platinum_media";
    private final String CHANNAL_NAME = "platinum_media";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        acquireMulticastLock();
        buildNotification();
        EventBus.getDefault().register(this);
    }

    private void buildNotification() {
        Class dlnaClass = null;
        try {
            dlnaClass = Class.forName("com.hzy.platinum.media.activity.MainActivity");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        Intent intent = new Intent(this, dlnaClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNAL_ID, CHANNAL_NAME, NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
        }
        mNotification = NotificationHelper.INSTANCE
                .getNotification(intent, getString(R.string.server_notification_title),
                        getString(R.string.server_notification_text));
        startForeground(1, mNotification);
    }

    private void acquireMulticastLock() {
        WifiManager wifiManager = (WifiManager)
                getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            mMulticastLock = wifiManager.createMulticastLock(TAG);
            mMulticastLock.acquire();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            ServerParams params = intent.getParcelableExtra(EXTRA_SERVER_PARAMS);
            if (params != null) {
                ServerInstance.INSTANCE.start(params);
                //NotificationHelper.INSTANCE.notify(mNotification);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressWarnings("UnusedDeclaration")
    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onServerStateChange(NativeAsyncEvent event) {
        switch (event.type) {
            case CallbackTypes.CALLBACK_EVENT_ON_PLAY:
                MediaUtils.startPlayMedia(this, event.mediaInfo);
                break;
            case CallbackTypes.CALLBACK_EVENT_ON_PAUSE:
                MediaUtils.pauseMedia(event.mediaInfo);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        if (mMulticastLock != null) {
            mMulticastLock.release();
            mMulticastLock = null;
        }
        EventBus.getDefault().unregister(this);
        ServerInstance.INSTANCE.stop();
        NotificationHelper.INSTANCE.cancel();
        super.onDestroy();
    }
}
