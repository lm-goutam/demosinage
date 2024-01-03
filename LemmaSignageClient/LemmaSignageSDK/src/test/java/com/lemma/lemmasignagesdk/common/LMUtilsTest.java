package com.lemma.lemmasignagesdk.common;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.HashMap;

@RunWith(RobolectricTestRunner.class)
public class LMUtilsTest {

    @Test
    public void testReplaceUriParameter() {
        String url = "https://test.com?a=orinal&b=duplicate";
        HashMap map = new HashMap() {{
            put("a", "changed");
            put("c", "new");
        }};
        String updatedUrl = LMUtils.replaceUriParameter(url, map);
        Assert.assertTrue(updatedUrl.contains("a=changed"));
        Assert.assertTrue(updatedUrl.contains("b=duplicate"));
        Assert.assertTrue(updatedUrl.contains("c=new"));
    }

    @Test
    public void testGetFilePathInRootDir() {
        String updatedUrl = LMUtils.getFilePathInRootDir("test.png", "ID123");
        Assert.assertNotNull(updatedUrl);
    }
}