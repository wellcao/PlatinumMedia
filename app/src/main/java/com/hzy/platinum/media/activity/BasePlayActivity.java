package com.hzy.platinum.media.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.devbrackets.android.exomedia.listener.OnBufferUpdateListener;
import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.hzy.platinum.media.R;
import com.lejia.arglass.media.event.NativeAsyncEvent;
import com.lejia.arglass.media.media.MediaInfo;
import com.lejia.arglass.media.media.MediaUtils;
import com.lejia.arglass.media.service.DLNAService;
import com.lejia.arglass.media.utils.UUIDUtils;
import com.plutinosoft.platinum.CallbackTypes;
import com.plutinosoft.platinum.ServerParams;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by huzongyao on 2018/7/4.
 */

@SuppressLint("Registered")
public abstract class BasePlayActivity extends AppCompatActivity
        implements OnPreparedListener, OnCompletionListener,
        OnBufferUpdateListener {

    protected com.lejia.arglass.media.media.MediaInfo mMediaInfo;
    private SharedPreferences mPreference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        parseIntent(getIntent());
        mPreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        EventBus.getDefault().register(this);
        startServerService();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        stopServerService();
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        parseIntent(intent);
        setCurrentMediaAndPlay(null);
    }

    private void parseIntent(Intent intent) {
        mMediaInfo = intent.getParcelableExtra(MediaUtils.EXTRA_MEDIA_INFO);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

 /*   @SuppressWarnings("UnusedDeclaration")
    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onServerStateChange(NativeAsyncEvent event) {
        switch (event.type) {
            case CallbackTypes.CALLBACK_EVENT_ON_PAUSE:
                onMediaPause();
                break;
            case CallbackTypes.CALLBACK_EVENT_ON_PLAY:
                break;
            case CallbackTypes.CALLBACK_EVENT_ON_SET_VOLUME:
                Log.e("TAG", "" + event.param1);
                break;
            default:
                break;
        }
    }
*/
    protected void onMediaPause() {
    }

    @Override
    public void onBufferingUpdate(int percent) {
    }

    @Override
    public void onCompletion() {
    }

    @Override
    public void onPrepared() {
    }

    /**
     * Set current media source and start to play
     */
    abstract void setCurrentMediaAndPlay(MediaInfo event);

    abstract void stopMedia();

    abstract void play();

    protected void setVolume(float percent){
    }

    protected void seekTo(long current){

    }

    /**
     * Start the server
     */
    private void startServerService() {
        Intent intent = new Intent(this, com.lejia.arglass.media.service.DLNAService.class);
        intent.putExtra(DLNAService.EXTRA_SERVER_PARAMS, loadServerParams());
        startService(intent);
    }

    /**
     * Stop the server service
     */
    private void stopServerService() {
        Intent intent = new Intent(this, com.lejia.arglass.media.service.DLNAService.class);
        stopService(intent);
    }

    /**
     * load Params from shared preferences
     * if preferences are not exists, save defaults
     *
     * @return params
     */
    private ServerParams loadServerParams() {
        String key = getString(R.string.pref_server_name_key);
        String name = mPreference.getString(key, getString(R.string.app_name));
        if (!mPreference.contains(key)) {
            mPreference.edit().putString(key, name).apply();
        }
        key = getString(R.string.pref_if_show_ip_key);
        boolean showIp = mPreference.getBoolean(key, true);
        if (!mPreference.contains(key)) {
            mPreference.edit().putBoolean(key, showIp).apply();
        }
        key = getString(R.string.pref_uuid_key);
        String uuid = mPreference.getString(key, UUIDUtils.getRandomUUID());
        if (!mPreference.contains(key)) {
            mPreference.edit().putString(key, uuid).apply();
        }
        return new ServerParams(name, showIp, uuid);
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onPlayState(NativeAsyncEvent event) {
        MediaInfo mediaInfo = event.mediaInfo;
        if ("setAVTransportURI".equals(mediaInfo.event)){
            setCurrentMediaAndPlay(mediaInfo);
        }else if ("pause".equals(mediaInfo.event)){
            onMediaPause();
        }else if ("stop".equals(mediaInfo.event)){
            stopMedia();
        }else if ("setVolume".equals(mediaInfo.event)){
            setVolume((mediaInfo.volume+0.0f)/100);
        }else if ("seek".equals(mediaInfo.event)){
            seekTo(mediaInfo.current);
        }else if ("play".equals(mediaInfo.event)){
            play();
        }
    }
}
