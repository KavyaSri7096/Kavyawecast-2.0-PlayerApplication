package com.wecast.player.data.model;

/**
 * Created by ageech@live.com
 */

public class WePlayerTrack {

    private boolean adaptive;
    private String name;
    private int groupIndex;
    private int trackIndex;
    private int trackType;
    private String trackCode;
    private int maxBitrate;
    private boolean off;

    public WePlayerTrack() {
    }

    public WePlayerTrack(boolean adaptive, String name, int groupIndex, int trackIndex, int trackType, String trackCode, int maxBitrate) {
        this.adaptive = adaptive;
        this.name = name;
        this.groupIndex = groupIndex;
        this.trackIndex = trackIndex;
        this.trackType = trackType;
        this.trackCode = trackCode;
        this.maxBitrate = maxBitrate;
    }

    public WePlayerTrack(String name, int maxBitrate, int trackType) {
        this.adaptive = false;
        this.name = name;
        this.groupIndex = -1;
        this.trackIndex = -1;
        this.trackType = trackType;
        this.trackCode = null;
        this.maxBitrate = maxBitrate;
    }

    public boolean isAdaptive() {
        return adaptive;
    }

    public void setAdaptive(boolean adaptive) {
        this.adaptive = adaptive;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGroupIndex() {
        return groupIndex;
    }

    public void setGroupIndex(int groupIndex) {
        this.groupIndex = groupIndex;
    }

    public int getTrackIndex() {
        return trackIndex;
    }

    public void setTrackIndex(int trackIndex) {
        this.trackIndex = trackIndex;
    }

    public int getTrackType() {
        return trackType;
    }

    public void setTrackType(int trackType) {
        this.trackType = trackType;
    }

    public String getTrackCode() {
        return trackCode;
    }

    public void setTrackCode(String trackCode) {
        this.trackCode = trackCode;
    }

    public int getMaxBitrate() {
        return maxBitrate;
    }

    public void setMaxBitrate(int maxBitrate) {
        this.maxBitrate = maxBitrate;
    }

    public void setOff(boolean trackOff) {
        this.off = trackOff;
    }

    public boolean isOff() {
        return off;
    }
}
