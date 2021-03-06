package com.hzy.platinum.media.activity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.devbrackets.android.exomedia.listener.OnErrorListener;
import com.devbrackets.android.exomedia.ui.widget.VideoControls;
import com.devbrackets.android.exomedia.ui.widget.VideoControlsCore;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.hzy.platinum.media.R;
import com.lejia.arglass.media.media.MediaInfo;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by huzongyao on 2018/6/29.
 * To play video media
 */

public class VideoActivity extends BasePlayActivity {

    @BindView(R.id.video_view)
    VideoView mVideoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);
        initVideoPlayer();
        //setCurrentMediaAndPlay();
    }

    private void initVideoPlayer() {
        mVideoView.setHandleAudioFocus(false);
        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnBufferUpdateListener(this);
        mVideoView.setOnErrorListener(new OnErrorListener() {
            @Override
            public boolean onError(Exception e) {
                Log.e("_______video play error","______error:  "+e.getMessage());
                return false;
            }
        });
    }

    @Override
    void setCurrentMediaAndPlay() {
        if (mMediaInfo != null) {
            VideoControlsCore videoControls = mVideoView.getVideoControlsCore();
            if (videoControls instanceof VideoControls) {
                ((VideoControls) videoControls).setTitle(mMediaInfo.title);
            }
            Log.e("______play url:   ","url:   "+mMediaInfo.url);
            Uri uri = Uri.parse(mMediaInfo.url);
            mVideoView.setVideoURI(uri);
        }
    }

    void setCurrentMediaAndPlay(MediaInfo mediaInfo) {
        mMediaInfo = mediaInfo;
        if (mMediaInfo != null) {
            VideoControlsCore videoControls = mVideoView.getVideoControlsCore();
            if (videoControls instanceof VideoControls) {
                ((VideoControls) videoControls).setTitle(mMediaInfo.title);
            }
            Log.e("______play url:   ","url:   "+mMediaInfo.url);
            Uri uri = Uri.parse(mMediaInfo.url);
            mVideoView.setVideoURI(uri);
        }
    }


    @Override
    protected void onMediaPause() {
        if(mVideoView.isPlaying()){
            mVideoView.pause();
        }
    }

    @Override
    public void onPrepared() {
        if (!mVideoView.isPlaying()) {
            mVideoView.start();
        }
    }

    @Override
    public void onCompletion() {
        mVideoView.seekTo(0);
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onPlayState(MediaInfo event) {
        if ("play".equals(event.event)){
            setCurrentMediaAndPlay(event);
        }else if ("pause".equals(event.event)){
            onMediaPause();
        }
    }
}
