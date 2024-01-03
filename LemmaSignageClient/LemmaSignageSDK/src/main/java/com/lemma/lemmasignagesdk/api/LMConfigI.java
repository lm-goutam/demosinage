package com.lemma.lemmasignagesdk.api;

import android.net.Uri;

public interface LMConfigI {

    LMConfigI getInnerIml();

    String getDuration();

    void setPlayLastSavedLoop(boolean playLastSavedLoop);

    void setDeleteCacheContinuously(Boolean deleteCacheContinuously);

    void setExecuteImpressionInWebContainer(Boolean executeImpressionInWebContainer);

    Uri getVideoUri();

    void setPlaceHolderVideo(Uri uri, long duration);

    void setPlaceHolderImage(Uri uri, long duration);
    //public void setPlaceHolderVast(Vast vast);
}
