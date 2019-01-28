package com.wecast.player.data.player;

import android.app.Activity;
import android.view.View;

/**
 * Created by ageech@live.com
 */

public class BasePlayer extends AbstractPlayer<Object, View> {

    public BasePlayer(Activity activity, View playerView) {
        super(activity, playerView);
    }

    @Override
    protected void initialize() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void play(String url) {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onStop() {

    }
}

