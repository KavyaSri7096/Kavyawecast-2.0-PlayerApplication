package com.wecast.player;

import android.app.Activity;
import android.view.View;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.wecast.player.data.player.AbstractPlayer;
import com.wecast.player.data.player.BasePlayer;
import com.wecast.player.data.player.exo.WeExoPlayer;

/**
 * Created by ageech@live.com
 */

public class WePlayerFactory {

    public static AbstractPlayer get(WePlayerType type, Activity activity, View view) {
        switch (type) {
            case EXO_PLAYER:
                return new WeExoPlayer(activity, (SimpleExoPlayerView) view);
            case NEX_PLAYER:
                // TODO add support for Nex Player
                return null;
            case MEDIA_PLAYER:
                // TODO add support for Media Player
                return null;
        }

        return new BasePlayer(activity, view);
    }
}
