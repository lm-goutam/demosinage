package com.lemma.lemmasignagesdk;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class TestData {

    //vast,image,vast
    String data = "{\n" +
            "  \"status\": 200,\n" +
            "  \"data\": {\n" +
            "    \"dur\": 92355,\n" +
            "    \"refresh_interval\": 3600,\n" +
            "    \"is_weather\": 1,\n" +
            "    \"end_time\": \"01:15\",\n" +
            "    \"trk\": \"https://track.lemmatechnologies.com/lemma/impr?ts=20191126132228&iid=99d73717-1021-11ea-b09c-02cc2a1e6ba0&pid=178&agid=1160&aid=14392&sid=${SITE_ID}&at=3&szid=2&did=178&cid=539&lid=1923&crid=4557&dur=15&gid=224472&aud=0&cp=0.001000&pp=0.000010&lf=0.000000&if=0.000000&impr=1.000000&bt=1&ic=0.000010&sec=0\",\n" +
            "    \"schedule\": [\n" +
            "      {\n" +
            "        \"id\": 1,\n" +
            "        \"lid\": 11999,\n" +
            "        \"crid\": 21059,\n" +
            "        \"cid\": 2815,\n" +
            "        \"org_id\": 75,\n" +
            "        \"ag_id\": 1160,\n" +
            "        \"ad_type\": 3,\n" +
            "        \"cr_type\": 4,\n" +
            "        \"Type\": \"video/mp4\",\n" +
            "        \"Duration\": 15,\n" +
            "        \"sdate\": \"20220320000015\",\n" +
            "        \"Creative\": \"https://media.lemmatechnologies.com/media/178/20220316165410-Maharashta_2.mp4\",\n" +
            "        \"cksum\": \"\",\n" +
            "        \"pub_fee\": 0,\n" +
            "        \"adv_fee\": 1.67976,\n" +
            "        \"billing_type\": 1\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": 1,\n" +
            "        \"lid\": 11999,\n" +
            "        \"crid\": 21059,\n" +
            "        \"cid\": 2815,\n" +
            "        \"org_id\": 75,\n" +
            "        \"ag_id\": 1160,\n" +
            "        \"ad_type\": 3,\n" +
            "        \"cr_type\": 4,\n" +
            "        \"Type\": \"image/jpeg\",\n" +
            "        \"Duration\": 15,\n" +
            "        \"sdate\": \"20220320000015\",\n" +
            "        \"Creative\": \"https://media.lemmatechnologies.com/media/178/20211007140716-NIA-1.jpg\",\n" +
            "        \"cksum\": \"\",\n" +
            "        \"pub_fee\": 0,\n" +
            "        \"adv_fee\": 1.67976,\n" +
            "        \"billing_type\": 1\n" +
            "      },\n" +
            "      {\n" +
            "        \"id\": 1,\n" +
            "        \"lid\": 11999,\n" +
            "        \"crid\": 21059,\n" +
            "        \"cid\": 2815,\n" +
            "        \"org_id\": 75,\n" +
            "        \"ag_id\": 1160,\n" +
            "        \"ad_type\": 3,\n" +
            "        \"cr_type\": 4,\n" +
            "        \"Type\": \"video\",\n" +
            "        \"Duration\": 15,\n" +
            "        \"sdate\": \"20220320000015\",\n" +
            "        \"Creative\": \"https://pubads.g.doubleclick.net/gampad/ads?iu=/21775744923/external/single_preroll_skippable&sz=640x480&ciu_szs=300x250%2C728x90&gdfp_req=1&output=vast&unviewed_position_start=1&env=vp&impl=s&correlator=\",\n" +
            "        \"cksum\": \"\",\n" +
            "        \"pub_fee\": 0,\n" +
            "        \"adv_fee\": 1.67976,\n" +
            "        \"billing_type\": 1\n" +
            "      }\n" +
            "    ],\n" +
            "    \"chk_Sum\": \"9ef057b872a3fa54b825e94dd5dbbf7ddc0beb0c8c6738d9a92c6437cbce8dd0\"\n" +
            "  },\n" +
            "  \"error\": \"\"\n" +
            "}";

    public static String fileContent(String name) {
        InputStream stream = Test.class.getResourceAsStream("/" + name);
        String text = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining(""));

        return text;

    }

    public static String getInconsistentScheduleData() {
        return fileContent("response1.json");
    }
}
