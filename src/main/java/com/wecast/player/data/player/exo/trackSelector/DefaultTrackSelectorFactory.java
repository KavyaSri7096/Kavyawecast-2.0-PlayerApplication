package com.wecast.player.data.player.exo.trackSelector;

import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.wecast.player.data.model.WePlayerParams;

/**
 * Created by ageech@live.com
 */

public class DefaultTrackSelectorFactory {

    public static DefaultTrackSelector create() {
        return create((TrackSelection.Factory) null);
    }

    public static DefaultTrackSelector create(BandwidthMeter bandwidthMeter) {
        return new DefaultTrackSelector(bandwidthMeter);
    }

    public static DefaultTrackSelector create(TrackSelection.Factory adaptiveTrackSelectionFactory) {
        return new DefaultTrackSelector(adaptiveTrackSelectionFactory);
    }

    public static DefaultTrackSelector create(BandwidthMeter bandwidthMeter, WePlayerParams params) {
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(bandwidthMeter);
        DefaultTrackSelector.Parameters parameters = new DefaultTrackSelector.Parameters();
        if (params.getMaxBitrate() > 0) {
            parameters.withMaxVideoBitrate(params.getMaxBitrate());
        }
        if (params.getPreferredAudioLanguage() != null) {
            parameters.withPreferredAudioLanguage(params.getPreferredAudioLanguage());
        }
        if (params.getPreferredSubtitleLanguage() != null) {
            parameters.withPreferredTextLanguage(params.getPreferredSubtitleLanguage());
        }
        trackSelector.setParameters(parameters);
        return trackSelector;
    }

    public static DefaultTrackSelector create(TrackSelection.Factory adaptiveTrackSelectionFactory, WePlayerParams params) {
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(adaptiveTrackSelectionFactory);
        DefaultTrackSelector.Parameters parameters = new DefaultTrackSelector.Parameters();
        if (params.getMaxBitrate() > 0) {
            parameters.withMaxVideoBitrate(params.getMaxBitrate());
        }
        if (params.getPreferredAudioLanguage() != null) {
            parameters.withPreferredAudioLanguage(params.getPreferredAudioLanguage());
        }
        if (params.getPreferredSubtitleLanguage() != null) {
            parameters.withPreferredTextLanguage(params.getPreferredSubtitleLanguage());
        }
        trackSelector.setParameters(parameters);
        return trackSelector;
    }
}

