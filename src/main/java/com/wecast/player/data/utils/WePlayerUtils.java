package com.wecast.player.data.utils;

import android.text.TextUtils;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.util.MimeTypes;

import java.util.Locale;

/**
 * Created by ageech@live.com
 */

public final class WePlayerUtils {

    public static final String QUALITY_SD = "SD";
    public static final String QUALITY_HD = "HD";
    public static final String QUALITY_ULTRA_HD = "UHD";
    public static final String QUALITY_UNDEFINED = "UNDEFINED";

    public static final int MAX_BITRATE_SD = 1000;
    public static final int MAX_BITRATE_HD = 15000;
    public static final int MAX_BITRATE_UHD = 500000;

    public static final String ASPECT_RATIO_ORIGINAL = "Original";
    public static final String ASPECT_RATIO_16_9 = "16:9";
    public static final String ASPECT_RATIO_4_3 = "4:3";

    public static final int RATIO_ORIGINAL = 0;
    public static final int RATIO_16_9 = 1;
    public static final int RATIO_4_3 = 2;

    public static final float RATIO_MULTIPLIER_ORIGINAL = 0;
    public static final float RATIO_MULTIPLIER_16_9 = 16f / 9f;
    public static final float RATIO_MULTIPLIER_4_3 = 4f / 3f;

    /**
     * Builds a track name for display.
     *
     * @param format {@link Format} of the track.
     * @return a generated name specific to the track.
     */
    public static String buildTrackName(Format format) {
        String trackName;
        if (MimeTypes.isVideo(format.sampleMimeType)) {
            int bitrate = format.bitrate > 0 ? format.bitrate / 1000 : 0;
            if (bitrate >= 0 && bitrate <= MAX_BITRATE_SD) {
                trackName = QUALITY_SD;
            } else if (bitrate > MAX_BITRATE_SD && bitrate <= MAX_BITRATE_HD) {
                trackName = QUALITY_HD;
            } else if (bitrate > MAX_BITRATE_HD) {
                trackName = QUALITY_ULTRA_HD;
            } else {
                trackName = QUALITY_UNDEFINED;
            }
        } else if (MimeTypes.isAudio(format.sampleMimeType)) {
            String language = buildLanguageString(format);
            if (language.equals("")) {
                trackName = joinWithSeparator(joinWithSeparator(buildLanguageString(format), buildAudioPropertyString(format)), buildSampleMimeTypeString(format));
            } else {
                trackName = buildLanguageString(format);
            }
        } else {
            String language = buildLanguageString(format);
            if (language.equals("")) {
                trackName = joinWithSeparator(joinWithSeparator(buildLanguageString(format), buildAudioPropertyString(format)), buildSampleMimeTypeString(format));
            } else {
                trackName = buildLanguageString(format);
            }
        }
        return trackName.length() == 0 ? "unknown" : trackName;
    }

    public static String buildTrackCode(Format format) {
        String trackName = "";
        if (MimeTypes.isAudio(format.sampleMimeType) || MimeTypes.isText(format.sampleMimeType))
            trackName = buildLanguageString(format);
        return trackName.length() == 0 ? "unknown" : trackName;
    }

    private static String buildResolutionString(Format format) {
        return format.width == Format.NO_VALUE || format.height == Format.NO_VALUE
                ? "" : format.width + "x" + format.height;
    }

    private static String buildAudioPropertyString(Format format) {
        return format.channelCount == Format.NO_VALUE || format.sampleRate == Format.NO_VALUE
                ? "" : format.channelCount + "ch, " + format.sampleRate + "Hz";
    }

    private static String buildLanguageString(Format format) {
        if (format != null && format.language != null) {
            Locale locale = new Locale(format.language);
            String name = locale.getDisplayName(locale);
            return TextUtils.isEmpty(format.language) || "und".equals(format.language) ? "" : capitalize(name);
        } else {
            return "";
        }
    }

    private static String buildBitrateString(Format format) {
        return format.bitrate == Format.NO_VALUE ? ""
                : String.format(Locale.US, "%.2fMbit", format.bitrate / 1000000f);
    }

    private static String joinWithSeparator(String first, String second) {
        return first.length() == 0 ? second : (second.length() == 0 ? first : first + ", " + second);
    }

    private static String buildTrackIdString(Format format) {
        return format.id == null ? "" : ("id:" + format.id);
    }

    private static String buildSampleMimeTypeString(Format format) {
        return format.sampleMimeType == null ? "" : format.sampleMimeType;
    }

    private static String capitalize(String str) {
        if (str != null && str.length() > 0) {
            return Character.toUpperCase(str.charAt(0)) + str.substring(1) + " ";
        } else {
            return str;
        }
    }
}

