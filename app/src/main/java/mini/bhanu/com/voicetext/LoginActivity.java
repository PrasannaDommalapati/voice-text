
package mini.bhanu.com.voicetext;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    LoginActivity loginActivity;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    Button mEmailSignInButton;
    private View mLoginFormView;
    String from;
    String password;
    SendMailSSL mailSSL;


    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginActivity = LoginActivity.this;
        mailSSL = new SendMailSSL();

        mLoginFormView = findViewById(R.id.login_form);
        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);

        editText(mEmailView, 100);
        editText(mPasswordView, 200);
        buttonClick(mEmailSignInButton,300);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {

            focusView.requestFocus();
        }

        if (mEmailView.getText().toString().contains("@") && (mEmailView.getText().toString().toLowerCase().contains("gmail.com") || mEmailView.getText().toString().toLowerCase().contains("yahoo.co"))) {
            if (!mPasswordView.getText().toString().equalsIgnoreCase("")) {

                from     = mEmailView.getText().toString();
                password = mPasswordView.getText().toString();

                session = mailSSL.authenticate(loginActivity, from, password);
                mailSSL.fromEmail = from;
                mailSSL.session   = session;

                try {
                    if (session != null) {
                        Intent mailActivity = new Intent(LoginActivity.this, VoiceEmailActivity.class);
                        mailActivity.putExtra("FROM", from);
                        startActivity(mailActivity);
                    }

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(getApplicationContext(), "Enter Password", Toast.LENGTH_LONG).show();

            }

        } else {

            Toast.makeText(getApplicationContext(), "Invalid/not supported Email ID", Toast.LENGTH_LONG).show();
        }
    }


    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    for (String result : results) {
                        mEmailView.setText(mailSSL.formattingEmail(result));
                    }
                }
                break;
            case 200:
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    for (String result : results) {
                        mPasswordView.setText(result);
                    }
                }
                break;

            case 300:
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    for (String result : results) {
                        if (!result.replaceAll(" ", "").equalsIgnoreCase("login")) {
                            return;
                        }
                        try {

                            attemptLogin();

                        } catch (Exception e) {

                            Toast.makeText(getApplicationContext(), "Couldn't enter email" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
                }
        }
    }

    public void editText(EditText editText, final int code) {
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent voiceInput = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                voiceInput.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
                try {
                    startActivityForResult(voiceInput, code);

                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(), "Oops! Your device doesn't support Speech to Text", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void buttonClick(Button button, final int code) {

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent voiceInput = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                voiceInput.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
                try {
                    startActivityForResult(voiceInput, code);

                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(), "Oops! Your device doesn't support Speech to Text", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public boolean connected() {

        boolean isConnected = false;

        try {
            mailSSL.sendEmail(loginActivity, "dpchowdaryd@gmail.com",null,null ,"Test", "Testing");

            isConnected = true;

        }catch (Exception e) {

            Toast.makeText(getApplicationContext(), "connected"+e.getMessage(), Toast.LENGTH_LONG).show();

            isConnected = false;
            System.out.println(e.getMessage());
        }
        return isConnected;

    }

}

