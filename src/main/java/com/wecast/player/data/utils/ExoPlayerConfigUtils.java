package com.wecast.player.data.utils;

import android.content.Context;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;

import java.util.UUID;

/**
 * Created by ageech@live.com
 */

public final class ExoPlayerConfigUtils {

    public static final String WIDEVINE = "widevine";
    public static final String PLAYREADY = "playready";
    public static final String CLEARKEY = "cenc";

    public static DataSource.Factory buildDataSourceFactory(Context context, DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(context, bandwidthMeter, buildHttpDataSourceFactory(context, bandwidthMeter));
    }

    public static HttpDataSource.Factory buildHttpDataSourceFactory(Context context, DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSourceFactory(getUserAgent(context), bandwidthMeter);
    }

    private static String getUserAgent(Context context) {
        return Util.getUserAgent(context, "ExoPlayerDemo");
    }

    public static boolean useExtensionRenderers() {
        return false;
    }

    public static UUID getDrmUuid(String typeString) throws ParserException {
        switch (Util.toLowerInvariant(typeString)) {
            case WIDEVINE:
                return C.WIDEVINE_UUID;
            case PLAYREADY:
                return C.PLAYREADY_UUID;
            case CLEARKEY:
                return C.CLEARKEY_UUID;
            default:
                try {
                    return UUID.fromString(typeString);
                } catch (RuntimeException e) {
                    throw new ParserException("Unsupported drm type: " + typeString);
                }
        }
    }
}
