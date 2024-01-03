package com.lemma.lemmasignagesdk.core;

import android.os.Handler;
import android.os.Looper;

import com.lemma.lemmasignagesdk.common.logger.LMWLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DexLoadingTask  {
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());

    private InputStream inputStream;
    private File updateDexFile;
    private File sdkDexFile;

    public DexLoadingTask(InputStream inputStream, File updateDexFile, File sdkDexFile) {
        this.inputStream = inputStream;
        this.updateDexFile = updateDexFile;
        this.sdkDexFile = sdkDexFile;
    }

    public void execute(CompletionCallback completionCallback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                doInBackground(completionCallback);
            }
        });
    }

    private Error moveFile(File file, File newFile) {
        Boolean result =  file.renameTo(newFile);
        if (result){
            return null;
        }
        return new Error("Unknown error in moving file");
    }

    private void onCompletion(CompletionCallback completionCallback,
                              Error error,
                              File file) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                completionCallback.onCompletion(error, file);
            }
        });
    }

    void doInBackground(CompletionCallback completionCallback) {

        // Update dex file
        if (this.updateDexFile !=null) {
            if (this.updateDexFile.exists()) {
                Error err = moveFile(this.updateDexFile, this.sdkDexFile);
                if (err!=null) {
                    onCompletion(completionCallback,err, null);
                    return;
                }
            }
        }

        // Use default file for first boot
        if (this.sdkDexFile !=null) {
            if (!this.sdkDexFile.exists()) {
                Error err = copyDex(this.inputStream, this.sdkDexFile);
                if (err!=null) {
                    onCompletion(completionCallback,err, null);
                    return;
                }
            }
        }

        // Return sdk dex file
        if (this.sdkDexFile !=null) {
            if (this.sdkDexFile.exists()) {
                onCompletion(completionCallback,null, this.sdkDexFile);
            }else {
                onCompletion(completionCallback,new Error("Unexpected error"), null);
            }
        }
    }

    public static Error copyDex(InputStream is, File output) {

        if (!output.exists()) {
            try {
                output.getParentFile().mkdirs();
                output.createNewFile();
            } catch (IOException e) {
                LMWLog.e(e.getMessage());
                return new Error(e);
            }
        }

        InputStream in = null;
        OutputStream out = null;
        try {
            in = is;   // if files resides inside the "Files" directory itself
            out = new FileOutputStream(output);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            LMWLog.e("tag", e.getMessage());
            return new Error(e);
        }
        return null;
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public  static interface CompletionCallback {
        public void onCompletion(Error error, File file);
    }

}

