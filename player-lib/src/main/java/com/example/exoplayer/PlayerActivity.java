/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
* limitations under the License.
 */
package com.example.exoplayer;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;


/**
 * A fullscreen activity to play audio or video streams.
 */
public class PlayerActivity extends AppCompatActivity {

    private static final int MEDIA_URI = R.string.media_url_mp3;

    private PlayerView mPlayerView;
    private ExoPlayer mPlayer;

    private boolean mPlayWhenReady = true;
    private int mCurrentPosition;
    private long mPlaybackPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        mPlayerView = findViewById(R.id.spv_video_view);
    }

    // Starting with API level 24 Android supports multiple windows. As our app can be visible
    // but not active in split window mode, we need to initialize the player in onStart.
    // Before API level 24 we wait as long as possible until we grab resources, so we wait until
    // onResume before initializing the player.
    @Override
    protected void onStart() {
        super.onStart();

        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        hideSystemUI();
        if ((Util.SDK_INT <= 23 || mPlayer == null)) {
            initializePlayer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    // Before API Level 24 there is no guarantee of onStop being called. So we have to release the
    // player as early as possible in onPause. Starting with API Level 24 (which brought multi and
    // split window mode) onStop is guaranteed to be called and in the paused mode our activity is
    // eventually still visible. Hence we need to wait releasing until onStop.
    @Override
    protected void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    // This is called in onResume is just an implementation detail to have a pure full screen experience
    private void hideSystemUI() {
        mPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void initializePlayer() {

        if (mPlayer == null) {
            mPlayer = ExoPlayerFactory.newSimpleInstance(
                    // Roughly a RenderersFactory creates renderers for timestamp synchronized
                    // rendering of video, audio and text (subtitles).
                    new DefaultRenderersFactory(this),
                    // The TrackSelector is responsible for selecting from the available audio,
                    // video and text tracks and the LoadControl manages buffering of the player.
                    new DefaultTrackSelector(), new DefaultLoadControl()
            );

            mPlayerView.setPlayer(mPlayer);
            mPlayer.setPlayWhenReady(mPlayWhenReady);
            mPlayer.seekTo(mCurrentPosition, mPlaybackPosition);
        }

        // Creating media source
        Uri uri = Uri.parse(getString(MEDIA_URI));
        MediaSource mediaSource = buildMediaSource(uri);
        mPlayer.prepare(mediaSource, false, false);
    }

    private void releasePlayer() {
        if(mPlayer != null) {
            mPlaybackPosition = mPlayer.getCurrentPosition();
            mCurrentPosition = mPlayer.getCurrentWindowIndex();
            mPlayWhenReady = mPlayer.getPlayWhenReady();
            mPlayer.release();
            mPlayer = null;
        }

    }

    // The method constructs and returns a ExtractorMediaSource for the given uri.
    // It uses a new DefaultHttpDataSourcFactory which only needs a user agent string.
    // By default the factory will use a DefaultExtractorFactory for the media source.
    // This supports almost all non-adaptive audio and video formats supported on Android.
    // It will recognize our mp3 file and play it nicely.
    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("exoplayer-codelab")
        ).createMediaSource(uri);
    }

}
