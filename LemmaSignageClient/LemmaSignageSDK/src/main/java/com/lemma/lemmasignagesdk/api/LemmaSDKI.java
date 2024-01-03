package com.lemma.lemmasignagesdk.api;

import android.content.Context;

public interface LemmaSDKI {
    String getVersion();

//    public void init(Context context, Boolean useInternalStorage);
//
//    public void init(Context context, String rootDirectory, Boolean useInternalStorage);
//
//    public void init(Context context, String rootDirectory, Boolean useInternalStorage, LMSDKInitializationCallback callback);

    void init(Context context, LMSDKInitializationCallback callback);

    void init(Context context);

}
