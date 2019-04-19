package com.wecast.player.data.player.exo;

import android.app.Activity;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.drm.FrameworkMediaDrm;
import com.google.android.exoplayer2.drm.HttpMediaDrmCallback;
import com.google.android.exoplayer2.drm.UnsupportedDrmException;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.ui.SubtitleView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.Util;
import com.wecast.core.data.db.entities.AspectRatio;
import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.core.logger.Logger;
import com.wecast.player.R;
import com.wecast.player.data.player.AbstractPlayer;
import com.wecast.player.data.player.exo.mediaSource.CustomMediaSourceBuilder;
import com.wecast.player.data.player.exo.mediaSource.CustomMergingMediaSourceBuilder;
import com.wecast.player.data.model.WePlayerParams;
import com.wecast.player.data.player.exo.trackSelector.DefaultTrackSelectorFactory;
import com.wecast.player.data.player.exo.trackSelector.ExoPlayerTrackSelector;
import com.wecast.player.data.utils.ExoPlayerConfigUtils;
import com.wecast.player.data.utils.WePlayerUtils;

import java.util.UUID;

import javax.inject.Inject;

/**
 * Created by ageech@live.com
 */

public class WeExoPlayer extends AbstractPlayer<SimpleExoPlayer, SimpleExoPlayerView> implements SimpleExoPlayer.VideoListener {

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

    @Inject
    PreferenceManager preferenceManager;

    private DataSource.Factory mediaDataSourceFactory;
    private Handler mainHandler;
    private DefaultTrackSelector defaultTrackSelector;
    private int resumeWindow;
    private long resumePosition;
    private boolean needRetrySource;
    private boolean shouldAutoPlay;
    private ExoPlayerTrackSelector trackSelector;
    private WePlayerParams params;
    private WeCastExoPlayerErrorListener errorListener;
    private boolean isPlayingBackup = false;
    // Aspect ratio
    private AspectRatioFrameLayout aspectRatioLayout;
    private AspectRatio aspectRatio;
    private boolean isAspectSet;
    // DRM
    private String[] drmKeyRequestProperties = null;
    private DrmSessionManager<FrameworkMediaCrypto> drmSessionManager = null;
    // Event tracker
    private Player.DefaultEventListener eventListener = new Player.DefaultEventListener() {

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            if (!isPlayingBackup && params.getBackupUrl() != null) {
                playBackup();
                Logger.e("WeExoPlayer", "Player error, lets try with backup url");
            } else {
                errorListener.onError(error);
            }
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            playbackStateListener.onPlaybackState(playbackState);

            if (playbackState == Player.STATE_READY && !isAspectSet) {
                changeAspectRatio(aspectRatio);
                isAspectSet = true;
            }
        }
    };

    public WeExoPlayer(Activity activity, SimpleExoPlayerView playerView) {
        super(activity, playerView);

        shouldAutoPlay = true;
        clearResumePosition();
        mediaDataSourceFactory = ExoPlayerConfigUtils.buildDataSourceFactory(activity, BANDWIDTH_METER);
        mainHandler = new Handler();
        aspectRatioLayout = playerView.findViewById(com.google.android.exoplayer2.ui.R.id.exo_content_frame);
    }

    @Override
    public void initialize() {
        if (url == null || url.length() < 1) {
            return;
        }

        // Set default 16:9 aspect ratio (fullscreen) to avoid shutter visibility
        // when we change channel if aspect ratio is original
        // (aspect ratio is set in onVideoSizeChanged after player is loaded)
        //TODO Check why preference manager is not injected
        //aspectRatio = preferenceManager.getAspectRatio();
        //setDefaultAspectRatio();

        boolean needNewPlayer = player == null;
        if (needNewPlayer) {
            if (params != null && params.getDrmUrl() != null) {
                buildDrmSession();
            }

            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
            defaultTrackSelector = createDefaultTrackSelector(videoTrackSelectionFactory);
            trackSelector = new ExoPlayerTrackSelector(defaultTrackSelector, videoTrackSelectionFactory);
            player = createPlayer();
            player.addListener(eventListener);
            playerView.setPlayer(player);
            player.setPlayWhenReady(shouldAutoPlay);
            player.addVideoListener(this);
        }

        if (needNewPlayer || needRetrySource) {
            MediaSource mediaSource = createMediaSource();
            // Check for resume position
            boolean haveResumePosition = resumeWindow != C.INDEX_UNSET;
            if (haveResumePosition) {
                player.seekTo(resumeWindow, resumePosition);
            }
            updateSubtitleVisibility(false);
            player.prepare(mediaSource, !haveResumePosition, false);
            needRetrySource = false;
        }
    }

    private SimpleExoPlayer createPlayer() {
        int buffer = params != null ? params.getBuffer() * 1000 : 0;
        DefaultLoadControl defaultLoadControl;
        if (buffer == 0) {
            defaultLoadControl = new DefaultLoadControl();
        } else {
            DefaultAllocator defaultAllocator = new DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE);
            defaultLoadControl = new DefaultLoadControl(defaultAllocator, buffer, buffer, buffer, buffer, C.LENGTH_UNSET, true);
        }
        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(activity.get(), drmSessionManager);
        return ExoPlayerFactory.newSimpleInstance(renderersFactory, defaultTrackSelector, defaultLoadControl);
    }

    private void buildDrmSession() {
        UUID drmSchemeUuid = null;
        try {
            drmSchemeUuid = ExoPlayerConfigUtils.getDrmUuid(ExoPlayerConfigUtils.WIDEVINE);
        } catch (ParserException e) {
            e.printStackTrace();
        }
        if (drmSchemeUuid != null) {
            String[] keyRequestPropertiesArray = drmKeyRequestProperties;
            int errorStringId = R.string.error_drm_unknown;
            try {
                drmSessionManager = buildDrmSessionManagerV18(drmSchemeUuid, params.getDrmUrl(), keyRequestPropertiesArray);
            } catch (UnsupportedDrmException e) {
                errorStringId = e.reason == UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME ? R.string.error_drm_unsupported_scheme : R.string.error_drm_unknown;
            }
            if (drmSessionManager == null) {
                Toast.makeText(playerView.getContext(), playerView.getContext().getResources().getString(errorStringId), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private DrmSessionManager<FrameworkMediaCrypto> buildDrmSessionManagerV18(UUID uuid, String licenseUrl, String[] keyRequestPropertiesArray) throws UnsupportedDrmException {
        HttpMediaDrmCallback drmCallback = new HttpMediaDrmCallback(licenseUrl, ExoPlayerConfigUtils.buildHttpDataSourceFactory(activity.get(), BANDWIDTH_METER));
        if (keyRequestPropertiesArray != null) {
            for (int i = 0; i < keyRequestPropertiesArray.length - 1; i += 2) {
                drmCallback.setKeyRequestProperty(keyRequestPropertiesArray[i], keyRequestPropertiesArray[i + 1]);
            }
        }
        return new DefaultDrmSessionManager<>(uuid, FrameworkMediaDrm.newInstance(uuid), drmCallback, null, mainHandler, null);
    }

    private MediaSource createMediaSource() {
        Uri sourceUri = Uri.parse(params.getUrl());
        MediaSource mediaSource = CustomMediaSourceBuilder.buildMediaSource(sourceUri, "", mainHandler, mediaDataSourceFactory);
        if (params != null && params.getSubtitles() != null && !params.getSubtitles().isEmpty()) {
            mediaSource = CustomMergingMediaSourceBuilder.buildMediaSource(params.getSubtitles(), mediaSource, mediaDataSourceFactory);
        }
        return mediaSource;
    }

    private void setDefaultAspectRatio() {
        if (aspectRatio == null) return;

        if (aspectRatio.getName().equals(WePlayerUtils.ASPECT_RATIO_ORIGINAL)) {
            aspectRatioLayout.setAspectRatio(WePlayerUtils.RATIO_MULTIPLIER_16_9);
        } else {
            aspectRatioLayout.setAspectRatio(aspectRatio.getWidthHeightRatio());
        }
    }

    private void changeAspectRatio(AspectRatio aspectRatio) {
        this.aspectRatio = aspectRatio;

        if (aspectRatio != null) {
            if (aspectRatio.getName().equals(WePlayerUtils.ASPECT_RATIO_ORIGINAL)) {
                aspectRatioLayout.setAspectRatio(getDefaultAspectRatio());
            } else {
                aspectRatioLayout.setAspectRatio(aspectRatio.getWidthHeightRatio());
            }
        }
    }

    private float getDefaultAspectRatio() {
        Format format = player.getVideoFormat();
        return format == null || format.height == 0 ? WePlayerUtils.RATIO_MULTIPLIER_16_9 : (format.width * 1f) / format.height;
    }

    private DefaultTrackSelector createDefaultTrackSelector(TrackSelection.Factory videoTrackSelectionFactory) {
        if (params != null) {
            return DefaultTrackSelectorFactory.create(videoTrackSelectionFactory, params);
        } else {
            return DefaultTrackSelectorFactory.create(videoTrackSelectionFactory);
        }
    }

    public void setUseController(boolean useController) {
        playerView.setUseController(useController);
    }

    public void setAspectRatioResizeMode(int resideMode) {
        playerView.setResizeMode(resideMode);
    }

    public boolean isSubtitleViewVisible() {
        SubtitleView subtitleView = playerView.findViewById(R.id.exo_subtitles);
        return subtitleView.getVisibility() == View.VISIBLE;
    }

    public void updateSubtitleVisibility(boolean shouldShowSubtitle) {
        SubtitleView subtitleView = playerView.findViewById(R.id.exo_subtitles);
        subtitleView.setVisibility(shouldShowSubtitle ? View.VISIBLE : View.GONE);
    }

    @Override
    public void play(String url) {
        this.params = new WePlayerParams.Builder()
                .setUrl(url)
                .build();
        reinitialize(url);
        isAspectSet = false;
        isPlayingBackup = false;
    }

    public void play(WePlayerParams params) {
        this.params = params;
        reinitialize(params.getUrl());
        isAspectSet = false;
        isPlayingBackup = false;
    }

    private void playBackup() {
        reinitialize(params.getBackupUrl());
        isAspectSet = false;
        isPlayingBackup = true;
    }

    private void reinitialize(String url) {
        this.url = url;
        dispose();
        initialize();
    }

    @Override
    public void onResume() {
        if (Util.SDK_INT <= 23 || player == null) {
            initialize();
        }
    }

    @Override
    public void onPause() {
        if (Util.SDK_INT <= 23) {
            dispose();
        }
    }

    @Override
    public void onStop() {
        if (Util.SDK_INT > 23) {
            dispose();
        }
    }


    @Override
    public void onDestroy() {

    }

    @Override
    public void dispose() {
        if (player != null) {
            shouldAutoPlay = player.getPlayWhenReady();
            updateResumePosition();
            player.release();
            player = null;
            defaultTrackSelector = null;
            trackSelector = null;
        }
    }

    private void clearResumePosition() {
        resumeWindow = C.INDEX_UNSET;
        resumePosition = C.TIME_UNSET;
    }

    private void updateResumePosition() {
        resumeWindow = player.getCurrentWindowIndex();
        resumePosition = player.isCurrentWindowSeekable() ? Math.max(0, player.getCurrentPosition()) : C.TIME_UNSET;
    }

    public void seekToPosition(int position) {
        if (player != null) {
            player.seekTo(position * 1000);
        }
    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unAppliedRotationDegrees, float pixelWidthHeightRatio) {
        if (aspectRatio != null) {
            if (aspectRatio.getName() != null && aspectRatio.getName().equals(WePlayerUtils.ASPECT_RATIO_ORIGINAL)) {
                aspectRatioLayout.setAspectRatio(height == 0 ? WePlayerUtils.RATIO_MULTIPLIER_16_9 : (width * pixelWidthHeightRatio) / height);
            } else {
                aspectRatioLayout.setAspectRatio(aspectRatio.getWidthHeightRatio());
            }
        }
    }

    @Override
    public void onRenderedFirstFrame() {

    }

    public ExoPlayerTrackSelector getTrackSelector() {
        return trackSelector;
    }

    public void setErrorListener(WeCastExoPlayerErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    public interface WeCastExoPlayerErrorListener {

        void onError(ExoPlaybackException exception);
    }
}
