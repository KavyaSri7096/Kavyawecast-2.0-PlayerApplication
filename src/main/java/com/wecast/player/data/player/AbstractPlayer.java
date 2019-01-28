package com.wecast.player.data.player;

import android.app.Activity;

import java.lang.ref.WeakReference;

/**
 * Created by ageech@live.com
 */

public abstract class AbstractPlayer<Player, View> {

    protected WeakReference<Activity> activity;
    protected String url;
    protected View playerView;
    protected Player player;
    protected PlaybackStateListener playbackStateListener;

    public AbstractPlayer(Activity activity, View playerView) {
        this.activity = new WeakReference<>(activity);
        this.playerView = playerView;
    }

    protected abstract void initialize();

    public abstract void dispose();

    public abstract void play(String url);

    public abstract void onPause();

    public abstract void onResume();

    public abstract void onDestroy();

    public abstract void onStop();

    public Player getPlayer() {
        return player;
    }

    public void setPlaybackStateListener(PlaybackStateListener playbackStateListener) {
        this.playbackStateListener = playbackStateListener;
    }

    public interface PlaybackStateListener {

        void onPlaybackState(int playbackState);
    }
}