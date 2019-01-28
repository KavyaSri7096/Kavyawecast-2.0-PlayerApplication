package com.wecast.player.data.model;

import com.wecast.core.data.db.entities.VodSubtitle;

import java.util.List;

/**
 * Created by ageech@live.com
 */

public class WePlayerParams {

    private String url;
    private String backupUrl;
    private String drmUrl;
    private int maxBitrate;
    private int buffer;
    private List<VodSubtitle> subtitles;
    private String preferredAudioLanguage;
    private String preferredSubtitleLanguage;

    public WePlayerParams(Builder builder) {
        this.url = builder.url;
        this.backupUrl = builder.backupUrl;
        this.drmUrl = builder.drmUrl;
        this.maxBitrate = builder.maxBitrate;
        this.buffer = builder.buffer;
        this.subtitles = builder.subtitles;
        this.preferredAudioLanguage = builder.preferredAudioLanguage;
        this.preferredSubtitleLanguage = builder.preferredSubtitleLanguage;
    }

    public static class Builder {

        private String url;
        private String backupUrl;
        private String drmUrl;
        private int maxBitrate;
        private int buffer;
        private List<VodSubtitle> subtitles;
        private String preferredAudioLanguage;
        private String preferredSubtitleLanguage;

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setBackupUrl(String backupUrl) {
            this.backupUrl = backupUrl;
            return this;
        }

        public Builder setDrmUrl(String drmUrl) {
            this.drmUrl = drmUrl;
            return this;
        }

        public Builder setMaxBitrate(int maxBitrate) {
            this.maxBitrate = maxBitrate;
            return this;
        }

        public Builder setBuffer(int buffer) {
            this.buffer = buffer;
            return this;
        }

        public Builder setSubtitles(List<VodSubtitle> subtitles) {
            this.subtitles = subtitles;
            return this;
        }

        public Builder setPreferredAudioLanguage(String preferredAudioLanguage) {
            this.preferredAudioLanguage = preferredAudioLanguage;
            return this;
        }

        public Builder setPreferredSubtitleLanguage(String preferredSubtitleLanguage) {
            this.preferredSubtitleLanguage = preferredSubtitleLanguage;
            return this;
        }

        public WePlayerParams build() {
            return new WePlayerParams(this);
        }
    }


    public String getUrl() {
        return url;
    }

    public String getBackupUrl() {
        return backupUrl;
    }

    public String getDrmUrl() {
        return drmUrl;
    }

    public int getMaxBitrate() {
        return maxBitrate;
    }


    public int getBuffer() {
        return buffer;
    }

    public List<VodSubtitle> getSubtitles() {
        return subtitles;
    }

    /**
     * @return preferredAudioLanguage ISO 639 alpha-2 or alpha-3 language code, or registered
     * language subtags up to 8 alpha letters (for future enhancements). When a language has both
     * an alpha-2 code and an alpha-3 code, the alpha-2 code must be used. You can find a full
     * list of valid language codes in the IANA Language Subtag Registry  (search for "Type:
     * language"). The language field is case insensitive, but Locale always canonicalizes  to
     * lower case.
     * Well-formed language values have the form [a-zA-Z]{2,8}. Note that this is not the the
     * full BCP47 language production, since it excludes extlang. They are not needed since
     * modern  three-letter language codes replace them.
     * Example: "en" (English), "ja" (Japanese), "kok" (Konkani)
     */
    public String getPreferredAudioLanguage() {
        return preferredAudioLanguage;
    }

    /**
     * @return preferredSubtitleLanguage ISO 639 alpha-2 or alpha-3 language code, or registered
     * language subtags up to 8 alpha letters (for future enhancements). When a language has both
     * an alpha-2 code and an alpha-3 code, the alpha-2 code must be used. You can find a full
     * list  of valid language codes in the IANA Language Subtag Registry  (search for "Type:
     * language"). The language field is case insensitive, but Locale always canonicalizes to
     * lower case.
     * Well-formed language values have the form [a-zA-Z]{2,8}. Note that this is not the the
     * full BCP47 language production, since it excludes extlang. They are not needed since
     * modern three-letter language codes replace them.
     * Example: "en" (English), "ja" (Japanese), "kok" (Konkani)
     */
    public String getPreferredSubtitleLanguage() {
        return preferredSubtitleLanguage;
    }
}
