package com.wecast.player.data.player.exo.trackSelector;

import androidx.annotation.IntDef;

import com.google.android.exoplayer2.RendererCapabilities;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.FixedTrackSelection;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.wecast.player.data.utils.WePlayerUtils;
import com.wecast.player.data.model.WePlayerTrack;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ageech@live.com
 */

public class ExoPlayerTrackSelector {

    private static final TrackSelection.Factory FIXED_FACTORY = new FixedTrackSelection.Factory();

    private MappingTrackSelector selector;
    private TrackSelection.Factory adaptiveTrackSelectionFactory;
    private HashMap<Integer, TrackGroupArray> tracksGroups = new HashMap<>();

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TRACK_TYPE_VIDEO, TRACK_TYPE_AUDIO, TRACK_TYPE_TEXT})
    @interface TrackType {
    }

    public static final int TRACK_TYPE_VIDEO = 0;
    public static final int TRACK_TYPE_AUDIO = 1;
    public static final int TRACK_TYPE_TEXT = 2;

    private ArrayList<WePlayerTrack> audioTracks;
    private ArrayList<WePlayerTrack> videoTracks;
    private ArrayList<WePlayerTrack> subtitleTracks;

    public ExoPlayerTrackSelector(MappingTrackSelector selector, TrackSelection.Factory adaptiveTrackSelectionFactory) {
        this.selector = selector;
        this.adaptiveTrackSelectionFactory = adaptiveTrackSelectionFactory;
    }

    private ArrayList<WePlayerTrack> loadVideoTracks() {
        ArrayList<WePlayerTrack> tracks = loadTracks(TRACK_TYPE_VIDEO);
        boolean haveSD = false, haveHD = false, haveUHD = false;
        for (WePlayerTrack track : tracks) {
            int maxBitrate = track.getMaxBitrate() > 0 ? track.getMaxBitrate() / 1000 : 0;
            if (maxBitrate > 0 && maxBitrate <= WePlayerUtils.MAX_BITRATE_SD) {
                haveSD = true;
            } else if (maxBitrate > WePlayerUtils.MAX_BITRATE_SD && maxBitrate <= WePlayerUtils.MAX_BITRATE_HD) {
                haveHD = true;
            } else if (maxBitrate > WePlayerUtils.MAX_BITRATE_HD) {
                haveUHD = true;
            }
        }
        ArrayList<WePlayerTrack> filteredTracks = new ArrayList<>();
        if (haveSD) {
            filteredTracks.add(new WePlayerTrack(WePlayerUtils.QUALITY_SD, WePlayerUtils.MAX_BITRATE_SD, TRACK_TYPE_VIDEO));
        }
        if (haveHD) {
            filteredTracks.add(new WePlayerTrack(WePlayerUtils.QUALITY_HD, WePlayerUtils.MAX_BITRATE_HD, TRACK_TYPE_VIDEO));
        }
        if (haveUHD) {
            filteredTracks.add(new WePlayerTrack(WePlayerUtils.QUALITY_ULTRA_HD, WePlayerUtils.MAX_BITRATE_UHD, TRACK_TYPE_VIDEO));
        }
        return filteredTracks;
    }

    private ArrayList<WePlayerTrack> loadTracks(int trackType) {
        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = this.selector.getCurrentMappedTrackInfo();
        ArrayList<WePlayerTrack> tracks = new ArrayList<>();

        if (mappedTrackInfo != null) {
            tracksGroups.put(trackType, mappedTrackInfo.getTrackGroups(trackType));
            for (int groupIndex = 0; groupIndex < tracksGroups.get(trackType).length; groupIndex++) {
                boolean adaptiveTrack = adaptiveTrackSelectionFactory != null
                        && mappedTrackInfo.getAdaptiveSupport(trackType, groupIndex, false)
                        != RendererCapabilities.ADAPTIVE_NOT_SUPPORTED
                        && tracksGroups.get(trackType).get(groupIndex).length > 1;
                TrackGroup group = tracksGroups.get(trackType).get(groupIndex);
                for (int trackIndex = 0; trackIndex < group.length; trackIndex++) {
                    if (mappedTrackInfo.getTrackFormatSupport(trackType, groupIndex, trackIndex)
                            == RendererCapabilities.FORMAT_HANDLED)
                        tracks.add(new WePlayerTrack(
                                adaptiveTrack,
                                WePlayerUtils.buildTrackName(group.getFormat(trackIndex)), groupIndex, trackIndex, trackType,
                                WePlayerUtils.buildTrackCode(group.getFormat(trackIndex)), group.getFormat(trackIndex).bitrate)
                        );
                }
            }
        }
        return tracks;
    }

    public ArrayList<WePlayerTrack> getVideoTracks() {
        if (videoTracks == null) videoTracks = loadVideoTracks();
        return videoTracks;
    }

    public void updateTrack(WePlayerTrack track) {
        MappingTrackSelector.SelectionOverride override = new MappingTrackSelector.SelectionOverride(FIXED_FACTORY, track.getGroupIndex(), track.getTrackIndex());
        selector.setSelectionOverride(track.getTrackType(), tracksGroups.get(track.getTrackType()), override);
    }

    public void changeTrack(WePlayerTrack track) {
        if (tracksGroups.get(track.getTrackType()) != null && tracksGroups.get(track.getTrackType()).isEmpty())
            return;
        updateTrack(track);
    }

    private ArrayList<WePlayerTrack> loadAudioTracks() {
        return loadTracks(TRACK_TYPE_AUDIO);
    }

    public ArrayList<WePlayerTrack> getAudioTracks() {
        if (audioTracks == null) {
            audioTracks = loadAudioTracks();
        }
        return audioTracks;
    }

    private ArrayList<WePlayerTrack> loadSubtitleTracks() {
        return loadTracks(TRACK_TYPE_TEXT);
    }

    public ArrayList<WePlayerTrack> getSubtitleTracks() {
        if (subtitleTracks == null) {
            subtitleTracks = loadSubtitleTracks();
            subtitleTracks.add(0, getOffTrack(TRACK_TYPE_TEXT));
        }
        return subtitleTracks;
    }

    private WePlayerTrack getOffTrack(@TrackType int trackType) {
        WePlayerTrack off = new WePlayerTrack();
        off.setName("Off");
        off.setTrackCode("Off");
        off.setOff(true);
        off.setTrackType(trackType);
        return off;
    }
}
