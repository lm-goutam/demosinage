package com.lemma.lemmasignageclient.ui.live.Bean;

import androidx.annotation.NonNull;

import com.lemma.lemmasignageclient.common.logger.LMLogger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Publisher {

    private static final String TAG = "Publisher";
    public String firstName;
    public String lastName;
    public String email;
    public String organization;
    public String accessToken;
    public int geosId;
    private OkHttpClient client;


    public ArrayList<GeoDetail> countryLevelGeos;

    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Publisher(JSONObject object, String token) {

        LMLogger.v("Publisher", "Token is : " + token);
        accessToken = token;
        client = getUnsafeOkHttpClient();
		/*
		 * {"status":200,"data":{"FirstName":"Test-Pub","LastName":"","Email":"testpub@lemma.com","UserType":"publisher"
,"Platform":[1],"ProfileImage":"http://lemmatechnologies.com/mediasvc/lemma/profile/20161109100457-c3
.jpg","Organization":"Lemma"},"error":""}

		 */
    }

    public void signUp(PartnerCustomer customer,final LoginListener listener){
        /*
        {
              "first_name": "pointofContactName",
              "last_name": "customerId",
              "email": "pointofContactEmai",
              "contact_number": "contactNo",
              "password": "abc@#21 (For Ex: abc@impressico.com)",
              "organization": {
                "organization": "custName",
                "address": "-NA-",
                "city": "-NA-",
                "state": "-NA-",
                "gstn": "-NA-"
              },
              "role": 2,
              "product": [
                2
              ],
              "inventory": [
                2,
                3,
                4,
                5,
                6,
                7
              ]
            }
         */

        JSONObject paramJson = new JSONObject();
        try {
            paramJson.put("first_name", customer.name);
            paramJson.put("last_name", customer.id);
            paramJson.put("email", customer.email);
            paramJson.put("contact_number", customer.contactNumber);

            String password = customer.name+"@#21";
            paramJson.put("password", password);

            JSONObject organization = new JSONObject();
            organization.put("organization", customer.name);
            organization.put("address", "-NA-");
            organization.put("city", "-NA-");
            organization.put("state", "-NA-");
            organization.put("gstn", "-NA-");

            paramJson.put("organization", organization);
            paramJson.put("role", 2);

            JSONArray products = new JSONArray();
            products.put(2);
            paramJson.put("product", products);

            JSONArray inventories = new JSONArray();
            inventories.put(2);
            inventories.put(3);
            inventories.put(4);
            inventories.put(5);
            inventories.put(6);
            inventories.put(7);
            paramJson.put("inventory", inventories);

        } catch (Exception e) {
            Error error = new Error(e.getMessage());
            listener.onError(error);
            LMLogger.e("Publisher", e.getLocalizedMessage());
        }

        Request request = null;

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, paramJson.toString());

        request = new Request.Builder()
                .url(APIUrl.SIGNUP_API).post(body)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Error error = new Error(e.getLocalizedMessage());
                listener.onError(error);

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {

                    String result = response.body().string();
                    LMLogger.v("Publisher", "Response : " + result);

                    final JSONObject object = new JSONObject(result);
                    String err = object.getString("error");
                    if (err != null && err.length() > 0) {
                        listener.onError(new Error(err));
                    } else {

                        if (object.getInt("status") == 200) {
                            Headers headers = response.headers();
                            String token = headers.get("Access-Token");
                            Publisher pub = new Publisher(object.getJSONObject("data"), token);
                            listener.onLogin(pub);
                        } else {

                            String res = response.body().string();
                            if (res == null) {
                                res = "Non 200 HTTP status";
                            }
                            Error error = new Error(res);
                            listener.onError(error);
                        }
                    }


                } catch (Exception e) {
                    Error error = new Error(e.getMessage());
                    listener.onError(error);
                }
            }
        });
    }

    public static void login(final String userName, final String passwrod, final LoginListener listener) {

        JSONObject paramJson = new JSONObject();
        try {
            paramJson.put("email", userName);
            paramJson.put("password", passwrod);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Error error = new Error(e.getMessage());
            listener.onError(error);
            LMLogger.e("Publisher", e.getLocalizedMessage());
        }

        Request request = null;

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, paramJson.toString());


        request = new Request.Builder()
                .url(APIUrl.LOGIN_API).post(body)
                .build();

        LMLogger.d("Publisher", "Login : "+request.toString());


        OkHttpClient client = getUnsafeOkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Error error = new Error(e.getLocalizedMessage());
                listener.onError(error);

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {

                    String result = response.body().string();
                    LMLogger.d("Publisher", "Response : " + result);

                    final JSONObject object = new JSONObject(result);
                    String err = object.getString("error");
                    if (err != null && err.length() > 0) {
                        listener.onError(new Error(err));
                    } else {

                        if (object.getInt("status") == 200) {
                            Headers headers = response.headers();
                            String token = headers.get("Access-Token");
                            Publisher pub = new Publisher(object.getJSONObject("data"), token);
                            listener.onLogin(pub);
                        } else {

                            String res = response.body().string();
                            if (res == null) {
                                res = "Non 200 HTTP status";
                            }
                            Error error = new Error(res);
                            listener.onError(error);
                        }
                    }


                } catch (Exception e) {
                    Error error = new Error(e.getMessage());
                    listener.onError(error);
                }
            }
        });
    }

    public static void pdnAdTag(final Integer customerId, final String deviceId, final AdTagListener adTagListener) {

        JSONObject paramJson = new JSONObject();
        try {
            paramJson.put("cust_id", customerId);
            paramJson.put("device_id", deviceId);
        } catch (Exception e) {
            Error error = new Error(e.getMessage());
            adTagListener.onError(error);
            LMLogger.e("Publisher", e.getLocalizedMessage());
        }

        Request request = null;

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, paramJson.toString());


        String accessToken = "66ed660504466accf65dc4e72a37de75ce77d232d345b756d25ada743db64b7adbc503106d691d4c4ae68316d170cf2e9862ee5bfc46e6dfe193f81c984bf80c518ec477f797e0635c23f26501df1ead10b753fbe88554dc2f9e51570a308c5eed4628e219cc175d04343c7aa13afd1c5e2955b73784d11d09100d648b9ab118e3449aa6d162482831bf9c3ac14e8f65b2b8774587fccd7e749889aa1261193af022ba4c20e7533bd5988921a28dfda5";
        request = new Request.Builder()
                .url(APIUrl.PDN_ADTAG_API).post(body).addHeader("Access-Token", accessToken)
                .build();


        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Error error = new Error(e.getLocalizedMessage());
                adTagListener.onError(error);

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    String result = response.body().string();
                    LMLogger.v("Publisher", "Response : " + result);

						/*
						 {"status": 200,    "data": {"tag":"http://localhost/lemma/servad?pid=1&aid=11&at=3&rtb=1"} }
						 */
                    final JSONObject object = new JSONObject(result);
                    if (object.getInt("status") == 200) {

                        JSONObject dataObject = object.getJSONObject("data");

                        if (dataObject != null) {
                            String adtagUrl = dataObject.getString("tag");
                            if (adtagUrl != null) {
                                adTagListener.onSuccess(adtagUrl);
                            } else {
                                Error error = new Error("No ad tag URL found in response");
                                adTagListener.onError(error);
                            }
                        } else {
                            Error error = new Error("No ad tag URL found in response");
                            adTagListener.onError(error);
                        }

                    } else {

                        Error error = new Error(String.valueOf(object.getInt("status")));

                        if (object.has("error")) {

                            String errString = object.getString("error");
                            error = new Error(errString);
                        }
                        adTagListener.onError(error);
                    }

                } catch (Exception e) {
                    Error error = new Error(e.toString());
                    adTagListener.onError(error);
                }
            }
        });
    }

    public void adTag(Map params,String name, ArrayList<GeoDetail> geos, final AdTagListener adTagListener) {
        if (name == null) {
            name = "testName";
        }

			/*
			{
  "name": "n ditya Birla Hospital d",
  "platform": 1,
  "tags": [],
  "inventory_type": [2],
  "inventory_category": [1],
  "screen_size": 1,
  "floor_level": 2,
  "floor_value": 10.5,
  "geo": {
    "city": 7055,
    "area_name": "MIT Collage Road"
  },
  "creative_loop": []
}

			 */

        JSONObject paramJson = new JSONObject(params);
        try {
            paramJson.put("Name", name);
            paramJson.put("platform_id", 5);


   /*         JSONArray invtTypes = new JSONArray();
            invtTypes.put(7);
            paramJson.put("inventory_types", invtTypes);

            JSONArray invtCategory = new JSONArray();
            invtTypes.put(7);
            paramJson.put("inventory_category", invtCategory);
*/
            JSONArray adSizes = new JSONArray();
            adSizes.put(1);
            paramJson.put("ad_sizes", adSizes);

            if (geos.size() > 0) {
                GeoDetail geo = geos.get(0);
                paramJson.put("geo_id", geo.id);
                //paramJson.put("geo", geo.json());
            }


        } catch (Exception e) {
            Error error = new Error(e.toString());
            adTagListener.onError(error);
        }


        Request request = null;

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, paramJson.toString());


        request = new Request.Builder()
                .url(APIUrl.CREATE_AU_API).post(body).addHeader("Access-Token", accessToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Error error = new Error(e.getLocalizedMessage());
                adTagListener.onError(error);

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String result = response.body().string();
                    LMLogger.v("Publisher", "Response : " + result);

						/*
						 * {"status":200,"data":"http://lemmatechnologies.com/lemma/servad?pid=82\u0026sid=0\u0026aid=109\u0026at
=1\u0026rply=0","error":""}

{"status":500,"data":null,"error":"Error 1062: Duplicate entry '82-0-pmd test app au' for key 'publisher_id'"}
						 */
                    final JSONObject object = new JSONObject(result);
                    if (object.getInt("status") == 200) {

                        JSONObject dataObject = object.getJSONObject("data");

                        if (dataObject != null) {
                            String adtagUrl = dataObject.getString("video");
                            if (adtagUrl != null) {
                                adTagListener.onSuccess(adtagUrl);
                            } else {
                                Error error = new Error("No ad tag URL found in response");
                                adTagListener.onError(error);
                            }
                        } else {
                            Error error = new Error("No ad tag URL found in response");
                            adTagListener.onError(error);
                        }

                    } else {

                        Error error = new Error(String.valueOf(object.getInt("status")));

                        if (object.has("error")) {

                            String errString = object.getString("error");
                            error = new Error(errString);
                        }
                        adTagListener.onError(error);
                    }

                } catch (Exception e) {
                    Error error = new Error(e.toString());
                    adTagListener.onError(error);
                }
            }
        });

    }

    public static class TargetingParamItem {
        public Integer id;
        public String name;
        public Integer type;

        @NonNull
        @Override
        public String toString() {
            return name;
        }
    }
    public static class TargetingParams {

        public ArrayList<TargetingParamItem> inventoryTypes;
        public ArrayList<TargetingParamItem> inventoryCategories;
    }

    public void getTargetingParams(final DataListener<TargetingParams> listener) {


        Request requestTargetparam = null;

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        requestTargetparam = new Request.Builder()
                .url(APIUrl.TARGETINGPARAM).addHeader("Access-Token",accessToken)
                .build();

        client.newCall(requestTargetparam).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Error error = new Error(e.getLocalizedMessage());
                listener.onError(error);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {

                    String result = response.body().string();
                    LMLogger.d("Publisher", "TARGETPARAM : " + result);

                    if (result != null && result.length() > 0) {
                        final JSONObject object = new JSONObject(result);
                        final JSONObject dataObject = object.getJSONObject("data");

                        TargetingParams targetingParams = new TargetingParams();
                        JSONArray inventoryCategoriesJson = dataObject.getJSONArray("inventory_category");
                        JSONArray inventoryTypesJson = dataObject.getJSONArray("inventory_types");
                        ArrayList<TargetingParamItem> inventoryCategories = new ArrayList();
                        for (int i = 0; i < inventoryCategoriesJson.length(); ++i) {
                            JSONObject inventoryCategoryObj = inventoryCategoriesJson.getJSONObject(i);
                            TargetingParamItem itm = new TargetingParamItem();
                            itm.name = inventoryCategoryObj.getString("Category");
                            itm.id = inventoryCategoryObj.getInt("Id");
                            itm.type = inventoryCategoryObj.getInt("Type");
                            inventoryCategories.add(itm);
                        }
                        targetingParams.inventoryCategories = inventoryCategories;

                        ArrayList<TargetingParamItem> inventoryTypes = new ArrayList();
                        for (int i = 0; i < inventoryTypesJson.length(); ++i) {
                            JSONObject inventoryTypesJsonObj = inventoryTypesJson.getJSONObject(i);
                            TargetingParamItem itm = new TargetingParamItem();
                            itm.name = inventoryTypesJsonObj.getString("Type");
                            itm.id = inventoryTypesJsonObj.getInt("Id");
                            inventoryTypes.add(itm);
                        }
                        targetingParams.inventoryTypes = inventoryTypes;
                        listener.onSuccess(targetingParams);

                    }else{
                        listener.onError(new Error("Non OK Http status"));
                    }

                } catch (Exception e) {
                    LMLogger.e("Publisher", e.getLocalizedMessage());
                }
            }
        });
    }
    public void geo(GeoDetail geo, final GeoDetail.GeoListingListener listener) {


        String params = null;

        if (geo == null) {

            JSONObject paramJson = new JSONObject();
            try {

                JSONObject json = new JSONObject();
                json.put("updated", true);
                params = json.toString();

            } catch (Exception e) {
                // TODO Auto-generated catch block
                LMLogger.e("Publisher", e.getLocalizedMessage());
            }


        } else {

            if (geo.level.equalsIgnoreCase("1")) {

                JSONObject paramJson = new JSONObject();
                try {

                    JSONObject json = new JSONObject();
                    json.put("updated", true);

                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(geo.countryCode);
                    json.put("country", jsonArray);
                    params = json.toString();

                } catch (Exception e) {
                    LMLogger.e("Publisher", e.getLocalizedMessage());
                }

            }
        }

        Request request = null;

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, params);


        request = new Request.Builder()
                .url(APIUrl.GEO_API).post(body).addHeader("Access-Token", accessToken)
                .build();

        makeRequest(request, listener);

    }

    void makeRequest(Request request, final GeoDetail.GeoListingListener listener) {
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Error error = new Error(e.getLocalizedMessage());
                listener.onError(error);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                ArrayList<GeoDetail> geoDArray = new ArrayList<GeoDetail>();
                try {

                    String result = response.body().string();
						/*
						 * {"status":200,"data":"http://lemmatechnologies.com/lemma/servad?pid=82\u0026sid=0\u0026aid=109\u0026at
=1\u0026rply=0","error":""}

						 */
                    final JSONObject object = new JSONObject(result);
                    if (object.getInt("status") == 200) {

                        JSONArray array = object.getJSONArray("data");

                        for (int i = 0; i < array.length(); ++i) {
                            JSONObject geo = array.getJSONObject(i);
                            GeoDetail geoD = new GeoDetail(geo);
                            geoDArray.add(geoD);
                        }
                    }

                    listener.onSuccess(geoDArray);

                } catch (Exception e) {
                    LMLogger.e("Publisher", e.getLocalizedMessage());
                }
            }
        });

    }

    public void getAudienceData(final AudienceDataListener listener) {

        String apiURL = "http://lemmadigital.com/dmp/api/v1/liveaudience";

        JSONObject paramJson = new JSONObject();
        try {
            paramJson.put("level", 1);
        } catch (Exception e) {
            listener.onError(new Error(e));
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, paramJson.toString());

        Request request = new Request.Builder()
                .url(apiURL).post(body).addHeader("Access-Token", accessToken)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onError(new Error(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {

                    String result = response.body().string();
                    final JSONObject object = new JSONObject(result);
                    LMLogger.d("APIResponse", object.toString());
                    if (object.getInt("status") == 200) {
                        final JSONArray dataObjectArray = object.getJSONArray("data");
                        listener.onSuccess(dataObjectArray);
                    } else {
                        listener.onError(new Error("non 200 http status"));
                    }

                } catch (Exception e) {
                    listener.onError(new Error(e));
                    LMLogger.e(TAG, e.getLocalizedMessage());
                }
            }
        });

    }

    public void weatherInfo(String cityId, final WeatherInfoListener listener) {

        Request request = null;

        try {

            JSONObject mainObj = new JSONObject();
            JSONObject paramObj = new JSONObject();
            paramObj.put("CityId", cityId);
            mainObj.put("Api", "weather");
            mainObj.put("Param", paramObj.toString());
            String str = mainObj.toString();
            str = URLEncoder.encode(str, "UTF-8");

            request = new Request.Builder()
                    .url("http://lemmatechnologies.com/lemma/apisvc?query=" + str)
                    .build();

        } catch (Exception e) {
            LMLogger.e(TAG, e.getLocalizedMessage());
        }

        if (request != null) {

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LMLogger.e(TAG, e.getLocalizedMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    try {
                        String result = response.body().string();
                        final JSONObject object = new JSONObject(result);
                        if (object.getInt("status") == 200) {
                            listener.onSuccess(object.getJSONObject("data"));
                        }

                    } catch (Exception e) {
                        LMLogger.e(TAG, e.getLocalizedMessage());
                        listener.onError(new Error(e));

                    }

                }
            });
        }


    }

    public interface AudienceDataListener {
        public void onSuccess(JSONArray audienceDataArray);

        public void onError(Error error);
    }

    public interface WeatherInfoListener {
        public void onSuccess(JSONObject data);

        public void onError(Error error);
    }

    public interface DataListener<T> {
        public void onSuccess(T data);
        public void onError(Error error);
    }

    public interface LoginListener {
        public void onLogin(Publisher publisher);

        public void onError(Error error);
    }

    public interface AdTagListener {
        public void onSuccess(String adTag);

        public void onError(Error error);
    }
}


