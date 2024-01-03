package com.lemma.lemmasignagesdk;

import android.net.Uri;

import com.lemma.lemmasignagesdk.api.LMConfigI;
import com.lemma.lemmasignagesdk.vast.VastBuilder.Vast;

public class LMConfig implements LMConfigI {

    public Uri uri;
    public Vast vast;
    public boolean playLastSavedLoop;
    public String duration;
    public Uri imageUri;
    public String imagedDuration;
    public Boolean deleteCacheContinuously = false;
    private Boolean executeImpressionInWebContainer = false;

    // Executes impression tracker in web context, default value is false

    public LMConfigI getInnerIml() {
        return null;
    }

    public Boolean getExecuteImpressionInWebContainer() {
        return executeImpressionInWebContainer;
    }

    public void setExecuteImpressionInWebContainer(Boolean executeImpressionInWebContainer) {
        this.executeImpressionInWebContainer = executeImpressionInWebContainer;
    }

    public Boolean getDeleteCacheContinuously() {
        return deleteCacheContinuously;
    }

    public void setDeleteCacheContinuously(Boolean deleteCacheContinuously) {
        this.deleteCacheContinuously = deleteCacheContinuously;
    }

    public void setPlayLastSavedLoop(boolean playLastSavedLoop) {
        this.playLastSavedLoop = playLastSavedLoop;
    }

    public Uri getVideoUri() {
        return uri;
    }

    public void setPlaceHolderVideo(Uri uri, long duration) {
        this.uri = uri;
        this.duration = "00:00:" + duration;
    }

    public void setPlaceHolderImage(Uri uri, long duration) {
        if (this.uri == null) {
            this.imageUri = uri;
            this.imagedDuration = "00:00:" + duration;
        }
    }

    public void setPlaceHolderVast(Vast vast) {
        this.vast = vast;
    }

    public String getDuration() {
        return duration;
    }

    public static boolean useThirdPartyTime() {
        return false;
    }

}
