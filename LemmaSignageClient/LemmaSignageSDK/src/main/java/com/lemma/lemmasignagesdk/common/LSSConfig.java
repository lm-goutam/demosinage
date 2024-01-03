package com.lemma.lemmasignagesdk.common;

import com.lemma.lemmasignagesdk.cache.LruFileCache;

import java.io.File;

public class LSSConfig {

    private static final File file = new File(LMUtils.getLemmaRootDir());
    public static LruFileCache fileCache = new LruFileCache(file, 1024);

}
