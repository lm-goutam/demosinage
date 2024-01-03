package com.lemma.lemmasignageclient.appspector;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.appspector.sdk.monitors.commands.BaseCommand;
import com.appspector.sdk.monitors.commands.CommandCallback;
import com.appspector.sdk.monitors.commands.Responder;
import com.appspector.sdk.monitors.commands.annotations.Argument;
import com.appspector.sdk.monitors.commands.annotations.Command;
import com.jakewharton.processphoenix.ProcessPhoenix;

@Command(value = "Restart App", category = "Application")
public class RestartAppCommand extends BaseCommand<Integer> {
    @Argument(isRequired = true)
    public String message;

    public static class RestartAppCommandExecutor implements CommandCallback<Integer, RestartAppCommand> {
        Context context;
        public RestartAppCommandExecutor(Context context) {
            this.context = context;
        }

        @Override
        public void exec(@NonNull RestartAppCommand restartAppCommand, @NonNull Responder<Integer> responder) {

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    AppspectorConfig.setMonsFlag(restartAppCommand.message);
                    ProcessPhoenix.triggerRebirth(context);
                }
            });
        }
    }
}