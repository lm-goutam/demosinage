package com.lemma.lemmasignagesdk.common;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.lemma.lemmasignagesdk.LMConfig;
import com.lemma.lemmasignagesdk.vast.VastBuilder.AdI;
import com.lemma.lemmasignagesdk.vast.VastBuilder.LinearAd;
import com.lemma.lemmasignagesdk.vast.VastBuilder.MediaFile;
import com.lemma.lemmasignagesdk.vast.VastBuilder.Vast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;


public class LMUtils {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    private static final String TAG = "LMUtils";
    static String STORAGE_PATH;
    static String LEMMA_ROOT_PATH;
    //Note : Always point to production server, while testing point to staging
    static String PRODUCTION_SERVER_URL = "http://lemmadigital.com";
    public static String SERVER_URL = PRODUCTION_SERVER_URL;
    private static SimpleDateFormat dateFormatter = null;
    private static SimpleDateFormat partDayFormatter = null;

    static {

        // TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        TimeZone.setDefault(TimeZone.getDefault());
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        partDayFormatter = new SimpleDateFormat("yyyy-MM-dd-a", Locale.US);
    }

    public static int convertDpToPixel(int value) {
        return (int) (value * Resources.getSystem().getDisplayMetrics().density);
    }

    public static String writeStringToFile(String string, String filePath) {
        File file = new File(filePath);
        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(file);
            byte[] b = string.getBytes();//converting string into byte array
            fOut.write(b);
            fOut.close();
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
        return null;
    }

    public static String getLemmaRootDir() {
        return LEMMA_ROOT_PATH;
    }

    public static String getFilePathInRootDir(String fileName, String urlKey) {
        return LEMMA_ROOT_PATH + urlKey + "_" + fileName;
    }

    public static void setUpDirectories(Context context, String rootDirectory) {
        STORAGE_PATH = Environment.getExternalStorageDirectory().getPath();
        LEMMA_ROOT_PATH = STORAGE_PATH + rootDirectory;
        LMUtils.createPubDirectory();
        LMUtils.createRTBParamXML();
    }

    public static void setUpDirectories(Context context, String rootDirectory, boolean internalStorage) {
        if (internalStorage) {
            STORAGE_PATH = context.getFilesDir().getAbsolutePath();
        } else {

            boolean mExternalStorageAvailable = false;
            boolean mExternalStorageWriteable = false;
            String state = Environment.getExternalStorageState();

            if (Environment.MEDIA_MOUNTED.equals(state)) {
                // We can read and write the media
                mExternalStorageAvailable = mExternalStorageWriteable = true;
            } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                // We can only read the media
                mExternalStorageAvailable = true;
                mExternalStorageWriteable = false;
            } else {
                // Something else is wrong. It may be one of many other states, but all we need
                //  to know is we can neither read nor write
                mExternalStorageAvailable = mExternalStorageWriteable = false;
            }

            if (mExternalStorageWriteable) {
                File mediaStorageDir = context.getExternalFilesDir(null);
                STORAGE_PATH = mediaStorageDir.getAbsolutePath();
            } else {
                STORAGE_PATH = Environment.getExternalStorageDirectory().getPath();
            }


        }
        LEMMA_ROOT_PATH = STORAGE_PATH + rootDirectory;
        LMUtils.createPubDirectory();
        LMUtils.createRTBParamXML();

        String logDir = LMUtils.getLemmaLogsDirPath();
        File mPath = new File(logDir);
        if (mPath != null && !mPath.exists()) {
            mPath.mkdirs();
        }

    }

    public static void createRTBParamXML() {

        String rtbParamFilePath = getLemmaRootDir() + "/RTBParameter.xml";
        File file = new File(rtbParamFilePath);

        if (file != null && !file.exists()) {
            try {
                file.createNewFile();
                writeStringToFile("<RTBParameterList>\n" +
                        "<apurl>https://play.google.com/store/apps/details?id=com.lemma.digital</apurl>\n" +
                        "<iploc>18.5246036,73.7929268</iploc>\n" +
                        "<apbndl>com.lemma.digital</apbndl>\n" +
                        "</RTBParameterList>", rtbParamFilePath);
            } catch (IOException e) {
                Log.d(TAG, e.getLocalizedMessage());
            }
        }
    }

    public static void createPubDirectory() {
        String directoryName = getLemmaRootDir() + "Pub/";
        File folder = new File(directoryName);
        if (!folder.exists()) {
            boolean res = folder.mkdirs();
        }
    }

    public static String getFileNameFromURL(String url) {

        String name = url;
        int pos = name.indexOf("/");
        while (pos != -1) {
            name = name.substring(++pos);
            pos = name.indexOf("/");
        }
        return name;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String readFile(String filaPath) {

        File file = new File(filaPath);

        //write the bytes in file
        if (file.exists()) {
            FileInputStream fIn;
            try {
                fIn = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(fIn);

                int size = fIn.available();
                char[] buffer = new char[size];

                inputStreamReader.read(buffer);

                fIn.close();
                return new String(buffer);
            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }
            return null;

        }
        return null;
    }

    /**
     * Returns the long value in milli second for given time string. It accepts
     * input in HH:mm:ss.SSS as well as HH:mm:ss else return -1.
     *
     * @param time
     * @return
     */
    public static long convertTimeToMillis(String time) {
        double seconds = 0.0;
        if (time != null && !time.isEmpty()) {
            String[] splitTime = time.split(":", -1);
            int index = splitTime.length - 1;
            while (index >= 0) {
                try {
                    seconds += Double.parseDouble(splitTime[index]) * Math.pow(60, splitTime.length - 1 - index);
                } catch (NumberFormatException ex) {
                    LMLog.e(TAG, "Invalid time string");
                }
                index--;
            }
        }
        return (long) seconds * 1000;
    }

    public static boolean isImageType(String value) {
        if (value == null) {
            return false;
        }
        return value.endsWith("png") || value.endsWith("jpeg") || value.endsWith("jpg");
    }

    public static ArrayList<MediaFile> filteredListWithMimeType(ArrayList<MediaFile> mediaFiles, String mimeType) {

        ArrayList<MediaFile> filteredMediaFiles = new ArrayList<>();
        for (MediaFile mf : mediaFiles) {

            if (mimeType.equalsIgnoreCase(mf.getType())) {
                filteredMediaFiles.add(mf);
            }
        }
        return filteredMediaFiles;
    }

    public static ArrayList<MediaFile> filteredListForBestMatchingMedia(ArrayList<MediaFile> mediaFiles) {

        final String[] supportedMediaTypes = {"video/mp4", "video/3gpp", "video/webm",
                "image/jpeg", "image/jpg", "image/png"};

        for (String mimeType : supportedMediaTypes) {

            ArrayList<MediaFile> fMediaFiles = filteredListWithMimeType(mediaFiles, mimeType);
            if (fMediaFiles.size() > 0) {

                ArrayList<MediaFile> sMediaFiles = sortedListByWidth(fMediaFiles);
                MediaFile finalMediaFile = sMediaFiles.get(0);

                ArrayList<MediaFile> finalMediaFileArray = new ArrayList<>();
                finalMediaFileArray.add(finalMediaFile);
                return finalMediaFileArray;
            }
        }
        return mediaFiles;
    }

    public static void filterUnsupportedAds(Vast vast) {
        for (AdI ad : vast.ads) {
            if (ad instanceof LinearAd) {
                ((LinearAd) ad).filter();
            }
        }
    }

    public static ArrayList<MediaFile> sortedListByWidth(ArrayList<MediaFile> mediaFiles) {
        Collections.sort(mediaFiles, new Comparator<MediaFile>() {
            @Override
            public int compare(MediaFile mediaFile, MediaFile t1) {
                Integer width1 = Integer.parseInt(mediaFile.getWidth());
                Integer width2 = Integer.parseInt(t1.getHeight());
                return width2 - width1;
            }
        });
        return mediaFiles;
    }

    public static String getCurrentTimeStamp() {
        return dateFormatter.format(new Date());
    }

    public static String getPartDayTimeStamp() {
        return partDayFormatter.format(new Date());
    }

    public static String getCurrentPlainTimeStamp() {
        return sdf.format(new Date());
    }

    public static Context getApplicationContext() {
        Application application = null;
        try {
            application = (Application) Class.forName("android.app.ActivityThread")
                    .getMethod("currentApplication").invoke(null, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return application.getApplicationContext();
    }

    public static String getCrc32(String input) {
        CRC32 crc = new CRC32();
        crc.update(input.getBytes());
        String enc = String.format("%08X", crc.getValue());
        return enc;
    }

    public static String getLemmaLogsDirPath() {
        return LMUtils.getLemmaRootDir() + "/Logs";
    }

    public static void delete(ArrayList<File> files) {
        for (File f : files) {
            delete(f);
        }
    }

    private static void delete(File f) {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                delete(c);
            }
        } else {
            if (!f.delete()) {
                LMLog.i(TAG, "Failed to delete file: " + f);
            }
        }
    }

    public static ArrayList<File> listFiles(File f, ArrayList<String> excludeFiles, int counts, int limit) {

        ArrayList<File> files = new ArrayList<>();
        if (counts >= limit) {
            return files;
        }

        if (f.isDirectory()) {
            for (File file : f.listFiles()) {
                ArrayList<File> intermediateFiles = listFiles(file, excludeFiles, counts + files.size(), limit);
                files.addAll(intermediateFiles);
            }

        } else {
            String path = f.getAbsolutePath();
            if (!excludeFiles.contains(path)) {
                LMLog.i(TAG, "Deleting  " + f.getAbsolutePath());
                files.add(f);
            } else {
                LMLog.i(TAG, "Excluding file from deletion: " + f);
            }
        }
        return files;
    }

    public static String getResourceFileContent(String name) {
        InputStream stream = LMUtils.class.getResourceAsStream("/" + name);
        int bufferSize = 1024;
        char[] buffer = new char[bufferSize];
        StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(stream, StandardCharsets.UTF_8);
        try {
            for (int numRead; (numRead = in.read(buffer, 0, buffer.length)) > 0; ) {
                out.append(buffer, 0, numRead);
            }
        } catch (Exception e) {
            return "";
        }
        return out.toString();
    }

    public static void attachToParentAndMatch(ViewGroup view, ViewGroup parentView) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        parentView.addView(view, params);
    }

    public static long getAvailableExternalMemorySize() {
        long freeBytesExternal = new File(STORAGE_PATH).getFreeSpace();
        return freeBytesExternal;
    }

    public static Date getDateFromString(String dateString) throws ParseException {
        return new SimpleDateFormat("yyyyMMddHHmmss").parse(dateString);
    }


    public static String replaceUriParameter(String url, String key, String newValue) {
        if (url == null || key == null || newValue == null) {
            return url;
        }
        Uri uri = Uri.parse(url);
        final Set<String> params = uri.getQueryParameterNames();
        final Uri.Builder newUri = uri.buildUpon().clearQuery();
        for (String param : params) {
            newUri.appendQueryParameter(param,
                    param.equals(key) ? newValue : uri.getQueryParameter(param));
        }
        return newUri.build().toString();
    }

    public static String replaceUriParameter(String url, HashMap<String, Object> map) {
        if (map != null) {

            Uri uri = Uri.parse(url);
            final Set<String> params = uri.getQueryParameterNames();
            final Uri.Builder newUri = uri.buildUpon().clearQuery();
            HashMap<String, String> finalQueryParams = new HashMap();
            for (String param : params) {
                finalQueryParams.put(param, uri.getQueryParameter(param));
            }
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = String.valueOf(entry.getValue());
                finalQueryParams.put(key, value);
            }

            for (Map.Entry<String, String> entry : finalQueryParams.entrySet()) {
                String key = entry.getKey();
                String value = String.valueOf(entry.getValue());
                newUri.appendQueryParameter(key, value);
            }

            return newUri.build().toString();

        }
        return url;
    }

    public static boolean isDeviceMemoryAvailable(float ratio) {
        long totalExtMemoryInMb = getTotalExternalMemorySize();
        long availableExtMemoryInMb = getAvailableExternalMemorySize();
        double available = (double) availableExtMemoryInMb / (double) totalExtMemoryInMb;
        return availableExtMemoryInMb > 0 && ratio < available;
    }

    public static long getTotalExternalMemorySize() {
        long ttlBytesExternal = new File(STORAGE_PATH).getTotalSpace();
        return ttlBytesExternal;
    }

    public static Date getCurrentTime() {
        if (LMConfig.useThirdPartyTime()){
            return DateTimeProvider.instance.getCurrentTime();
        }
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    public static Date dateByAddingSeconds(Date input, Integer seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(input);
        calendar.add(Calendar.SECOND, seconds);
        return calendar.getTime();
    }

    public static long intervalFromCurrentTime(Date scheduleDate) {
        Date currentTime = getCurrentTime();
        if (scheduleDate.after(currentTime)) {
            long diffInMs = scheduleDate.getTime() - currentTime.getTime();
            LMLog.i("diffInMs -> %d",diffInMs);
            long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs);
            return diffInSec;
        }
        return -1;
    }
}
