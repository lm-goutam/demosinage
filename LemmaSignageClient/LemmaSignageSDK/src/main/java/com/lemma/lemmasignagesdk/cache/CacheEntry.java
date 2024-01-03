package com.lemma.lemmasignagesdk.cache;

import java.util.Date;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class CacheEntry {

    @Id
    long id;
    String urlString;
    String localUriString;
    String urlCRC32Hash;
    String fileName;
    Date lastAccessed;
    Date createdAt;
    Boolean inEditMode = false;

}
