package com.lejia.arglass.media.instance;

import android.util.Log;

import com.lejia.arglass.media.event.NativeAsyncEvent;
import com.lejia.arglass.media.media.MediaInfo;
import com.lejia.arglass.media.utils.DLNAUtils;
import com.lejia.arglass.media.utils.MediaType;
import com.plutinosoft.platinum.CallbackTypes;
import com.plutinosoft.platinum.DLNACallback;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huzongyao on 2018/6/21.
 * To receive the messages from native
 */

public enum CallbackInstance {

    INSTANCE;

    String TAG = "CallbackInstance";

    private final DLNACallback mCallback;

    CallbackInstance() {
        mCallback = new DLNACallback() {
            @Override
            public void onEvent(int type, String param1, String param2, String param3) {
                Log.d(TAG, "______type: " + type
                        + "\nparam1: " + param1
                        + "\nparam2: " + param2
                        + "\nparam3: " + param3);
                switch (type) {
                    case CallbackTypes.CALLBACK_EVENT_ON_PLAY:
                        CallbackInstance.this.startPlayMedia(type, param1, param2);
                        break;
                    case CallbackTypes.CALLBACK_EVENT_ON_PAUSE:
                        CallbackInstance.this.stopPlayMedia(type,param1,param2);
                        break;
                    case CallbackTypes.CALLBACK_EVENT_ON_SEEK:
                        break;
                }
            }
        };
    }

    private void startPlayMedia(int type, String url, String meta) {
        MediaInfo mediaInfo = DLNAUtils.getMediaInfo(url, meta);
        if (mediaInfo.mediaType == MediaType.TYPE_UNKNOWN) {
            Log.w(TAG, "Media Type Unknown!");
            return;
        }
        NativeAsyncEvent event = new NativeAsyncEvent(type, mediaInfo);
        EventBus.getDefault().post(event);
    }

    private void stopPlayMedia(int type,String url,String meta){
        MediaInfo mediaInfo = DLNAUtils.getMediaInfo(url, meta);
        if (type!=CallbackTypes.CALLBACK_EVENT_ON_PAUSE) {
            return;
        }
        mediaInfo.event = "pause";
        NativeAsyncEvent event = new NativeAsyncEvent(type, mediaInfo);
        EventBus.getDefault().post(event);
    }

    public DLNACallback getCallback() {
        return mCallback;
    }
}
