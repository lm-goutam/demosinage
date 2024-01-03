package com.lemma.lemmasignageclient.ui.live.Bean;

import com.lemma.lemmasignageclient.common.logger.LMLogger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class GeoDetail {

   private static String TAG = "GeoDetail";

    public Integer id;
    public String level;
    String country;
    String areaName;
    String regionName;
    String city;
    String countryCode;
    String regionCode;
    String zipcode;

    public GeoDetail(JSONObject object) throws JSONException {

		/*
		 * {
        "Id": 1,
        "GeoLevel": "1",
        "CountryCode": "*",
        "CountryName": "All"
      }

       "id": 108,
            "level": 1,
            "name": "INDIA",
            "country_code": "IN",
            "lat": 0,
            "lon": 0
		 */


        id = object.getInt("id");
        level = object.getString("level");

        if (level != null) {
            int levelInt = Integer.parseInt(level);

            if (object.has("name")) {

                if (levelInt == 1) {
                    country = object.getString("name");
                } else if (levelInt == 2) {
                    regionName = object.getString("name");
                } else if (levelInt == 3) {
                    city = object.getString("name");
                } else if (levelInt == 4) {
                    areaName = object.getString("name");
                }
            }

        }

        if (object.has("city")) {
            city = object.getString("city");
        }
//
        if (object.has("country_code")) {
            countryCode = object.getString("country_code");
        }
//
        if (object.has("region_code")) {
            regionCode = object.getString("region_code");
        }
//
//		if (object.has("ZipCode")) {
//			zipcode = object.getString("ZipCode");
//		}


    }

    public ArrayList<GeoDetail> filterGeoDetails(ArrayList<GeoDetail> passedGeos) {

        int lev = Integer.parseInt(level);

        if (lev <= 1) {
            return null;
        }
        ArrayList<GeoDetail> filteredGeos = new ArrayList<>();
        int newLevel = lev + 1;

        for (GeoDetail geoD : passedGeos) {

            int listGeoLevel = Integer.parseInt(geoD.level);

            if (listGeoLevel == newLevel && isImmediateChildFor(geoD, newLevel)) {
                filteredGeos.add(geoD);
            }
        }
        return filteredGeos;
    }

    boolean isImmediateChildFor(GeoDetail geo, int level) {

        if (level == 3) {
            return regionCode.equalsIgnoreCase(geo.regionCode);
        } else if (level == 4) {
            return city.equalsIgnoreCase(geo.city);
        }
        return false;
    }

    public String geoName() {

        int lev = Integer.parseInt(level);
        switch (lev) {
            case 1:
                return country;

            case 2:
                return regionName;

            case 3:
                return city;

            case 4:
                return areaName;

            default:
                break;
        }
        return country;
    }

    public JSONObject json() {

        JSONObject json = new JSONObject();
        try {
            json.put("id", id);
        } catch (Exception e) {
            LMLogger.e(TAG, e.getLocalizedMessage());
        }
        return json;
    }

    public JSONObject requetString() {

        JSONObject object = new JSONObject();
        try {

            if (countryCode != null) {

                object.put("CountryCode", countryCode);

            }

            if (regionCode != null) {

                object.put("RegionCode", regionCode);

            }

            if (city != null) {

                object.put("City", city);

            }

            return object;

        } catch (JSONException e) {
            LMLogger.e(TAG, e.getLocalizedMessage());
        }
        return new JSONObject();
    }

    public interface GeoListingListener {
        public void onSuccess(ArrayList<GeoDetail> geoDetails);

        public void onError(Error error);
    }
}
