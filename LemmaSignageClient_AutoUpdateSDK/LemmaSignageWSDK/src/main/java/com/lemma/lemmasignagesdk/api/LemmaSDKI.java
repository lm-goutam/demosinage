package com.lemma.lemmasignagesdk.api;

import android.content.Context;

public interface LemmaSDKI {
    public String getVersion();

    public void init(Context context, LMSDKInitializationCallback callback);

    public void init(Context context);

}
