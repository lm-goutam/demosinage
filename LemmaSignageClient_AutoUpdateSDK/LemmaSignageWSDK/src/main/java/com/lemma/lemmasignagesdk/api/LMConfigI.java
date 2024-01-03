package com.lemma.lemmasignagesdk.api;

import android.net.Uri;

public interface LMConfigI {

    public  LMConfigI getInnerIml();
    public  String getDuration();
    public void setPlayLastSavedLoop(boolean playLastSavedLoop);
    public void setDeleteCacheContinuously(Boolean deleteCacheContinuously);
    public void setExecuteImpressionInWebContainer(Boolean executeImpressionInWebContainer);
    public Uri getVideoUri();
    public void setPlaceHolderVideo(Uri uri, long duration);
    public void setPlaceHolderImage(Uri uri, long duration);

    // problem with Vast Object
//    public void setPlaceHolderVast(Vast vast);

}
