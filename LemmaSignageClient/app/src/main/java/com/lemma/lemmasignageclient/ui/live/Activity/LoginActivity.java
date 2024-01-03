package com.lemma.lemmasignageclient.ui.live.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lemma.lemmasignageclient.common.AppManager;
import com.lemma.lemmasignageclient.common.AppUtil;
import com.lemma.lemmasignageclient.databinding.ActivityLoginBinding;
import com.lemma.lemmasignageclient.ui.live.Bean.Publisher;

public class LoginActivity extends Activity {
    private static final int REQUEST_SIGNUP = 0;
    ActivityLoginBinding binding;

    EditText _emailText;
    EditText _passwordText;
    Button _loginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        _emailText = binding.inputEmail;
        _passwordText =  binding.inputPassword;
        _loginButton = binding.btnLogin;

        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    public void login() {

        if (!validate()) {
            return;
        }
        _loginButton.setEnabled(false);
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        final ProgressDialog progress = AppUtil.showDialog(this, "Logging in...");
        Publisher.login(email, password, new Publisher.LoginListener() {

            @Override
            public void onLogin(Publisher publisher) {
                AppManager.getInstance().publisher = publisher;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.dismiss();
                        onLoginSuccess();
                    }
                });
            }

            @Override
            public void onError(Error error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.dismiss();
                        onLoginFailed();
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        AppUtil.launchActivity(this, AdTagCreationAct.class);
        finish();
    }

    public void onLoginFailed() {
        AppUtil.showMsg(this,"Login failed");
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty()) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }
        return valid;
    }
}
