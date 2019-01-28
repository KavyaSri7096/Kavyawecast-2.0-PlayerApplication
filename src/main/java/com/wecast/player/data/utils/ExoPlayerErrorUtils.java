package com.wecast.player.data.utils;

import android.content.res.Resources;
import android.net.Uri;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.wecast.player.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ageech@live.com
 */

public final class ExoPlayerErrorUtils {

    public static String getErrorMessage(ExoPlaybackException e, Resources resources) {
        String errorString = "";
        if (e.type == ExoPlaybackException.TYPE_RENDERER) {
            Exception cause = e.getRendererException();
            if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
                // Special case for decoder initialization failures.
                MediaCodecRenderer.DecoderInitializationException decoderInitializationException = (MediaCodecRenderer.DecoderInitializationException) cause;
                if (decoderInitializationException.decoderName == null) {
                    if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                        errorString = resources.getString(R.string.error_querying_decoders);
                    } else if (decoderInitializationException.secureDecoderRequired) {
                        errorString = resources.getString(R.string.error_no_secure_decoder,
                                decoderInitializationException.mimeType);
                    } else {
                        errorString = resources.getString(R.string.error_no_decoder,
                                decoderInitializationException.mimeType);
                    }
                } else {
                    errorString = resources.getString(R.string.error_instantiating_decoder,
                            decoderInitializationException.decoderName);
                }
            }
        } else if (e.type == ExoPlaybackException.TYPE_SOURCE) {
            IOException exception = e.getSourceException();
            if (exception instanceof HttpDataSource.InvalidResponseCodeException) {
                HttpDataSource.InvalidResponseCodeException ex = (HttpDataSource.InvalidResponseCodeException) exception;
                errorString = ex.getMessage();
            } else {
                errorString = exception.getMessage();
            }
        }

        if (errorString == null && isBehindLiveWindow(e)) {
            errorString = resources.getString(R.string.error_behind_live_window);
        }
        return errorString;
    }

    public static Map<String, String> getErrorParams(ExoPlaybackException e) {
        Map<String, String> map = new HashMap<>();
        if (e.type == ExoPlaybackException.TYPE_RENDERER) {
            Exception cause = e.getRendererException();
            if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
                // Special case for decoder initialization failures.
                MediaCodecRenderer.DecoderInitializationException decoderInitializationException =
                        (MediaCodecRenderer.DecoderInitializationException) cause;
                if (decoderInitializationException.decoderName == null) {
                    if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                        map.put("Message", decoderInitializationException.getCause().getMessage());
                        map.put("Localized Message", decoderInitializationException.getCause().getLocalizedMessage());
                    } else if (decoderInitializationException.secureDecoderRequired) {
                        map.put("Message", decoderInitializationException.getMessage());
                        map.put("Localized Message", decoderInitializationException.getLocalizedMessage());
                    } else {
                        map.put("Message", decoderInitializationException.getMessage());
                        map.put("Localized Message", decoderInitializationException.getLocalizedMessage());
                    }
                } else {
                    map.put("Message", decoderInitializationException.getMessage());
                    map.put("Localized Message", decoderInitializationException.getLocalizedMessage());
                }
            }
        } else if (e.type == ExoPlaybackException.TYPE_SOURCE) {
            IOException exception = e.getSourceException();
            if (exception instanceof HttpDataSource.InvalidResponseCodeException) {
                HttpDataSource.InvalidResponseCodeException ex = (HttpDataSource.InvalidResponseCodeException) exception;
                map.put("Response code", ex.responseCode + "");
                Uri uri = ex.dataSpec != null ? ex.dataSpec.uri : null;
                map.put("Uri", uri != null ? uri.toString() : null);
            }
        }

        if (map.size() == 0 && isBehindLiveWindow(e)) {
            map.put("Message", getBehindWindowException(e));
        }

        return map;
    }

    private static String getBehindWindowException(ExoPlaybackException e) {
        Throwable cause = e.getSourceException();
        while (cause != null) {
            if (cause instanceof BehindLiveWindowException) {
                return cause.getMessage();
            }
            cause = cause.getCause();
        }
        return "";
    }

    private static boolean isBehindLiveWindow(ExoPlaybackException e) {
        if (e.type != ExoPlaybackException.TYPE_SOURCE) {
            return false;
        }
        Throwable cause = e.getSourceException();
        while (cause != null) {
            if (cause instanceof BehindLiveWindowException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }
}
