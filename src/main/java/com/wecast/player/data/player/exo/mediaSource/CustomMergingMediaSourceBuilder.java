package com.wecast.player.data.player.exo.mediaSource;

import android.net.Uri;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.util.MimeTypes;
import com.wecast.core.data.db.entities.VodSubtitle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ageech@live.com
 */

public class CustomMergingMediaSourceBuilder {

    public static MergingMediaSource buildMediaSource(List<VodSubtitle> subtitles, MediaSource mediaSource, DataSource.Factory mediaDataSourceFactory) {
        return new MergingMediaSource(createSubtitleSources(mediaSource, subtitles, mediaDataSourceFactory));
    }

    private static MediaSource[] createSubtitleSources(MediaSource videoSource, List<VodSubtitle> subtitles, DataSource.Factory mediaDataSourceFactory) {
        ArrayList<MediaSource> mediaSources = new ArrayList<>();
        mediaSources.add(videoSource);
        for (VodSubtitle subtitle : subtitles) {
            mediaSources.add(createSubtitleSource(mediaDataSourceFactory, Uri.parse(subtitle.getUrl()), createSubtitleFormat(subtitle)));
        }
        return mediaSources.toArray(new MediaSource[mediaSources.size()]);
    }

    private static Format createSubtitleFormat(VodSubtitle subtitle) {
        return Format.createTextSampleFormat(
                null,                                           // An identifier for the track. May be null.
                MimeTypes.APPLICATION_SUBRIP,                       // The mime type. Must be set correctly.MimeTypes.APPLICATION_SUBRIP
                C.SELECTION_FLAG_DEFAULT,                           // Selection flags for the track.
                subtitle != null ? subtitle.getLocale() : null);    // The subtitle language. May be null.
    }

    private static MediaSource createSubtitleSource(DataSource.Factory mediaDataSourceFactory, Uri uri, Format format) {
        return new SingleSampleMediaSource.Factory(mediaDataSourceFactory).createMediaSource(uri, format, C.TIME_UNSET);
    }
}