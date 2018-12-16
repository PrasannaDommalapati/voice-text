package mini.bhanu.com.voicetext;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class VoiceEmailActivity extends AppCompatActivity {

    Activity vmActivity;
    SendMailSSL mailSSL;
    private FloatingActionButton btnSpeakTO;
    private boolean backClickedTwice = false;

    private EditText editTxtTO;
    private EditText editTxtCC;
    private EditText editTxtBCC;
    private EditText editTxtSub;
    private EditText editTxtEB;
    private String subjectEmail = null;
    private String bodyEmail = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(mini.bhanu.com.voicetext.R.layout.activity_email);
        vmActivity = VoiceEmailActivity.this;
        mailSSL = new SendMailSSL();

        getSupportActionBar().setIcon(R.drawable.microphone);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll()
                    .build();
            StrictMode.setThreadPolicy(policy);
        }

        //initialize text fields
        editTxtTO = (EditText) findViewById(R.id.to_email);
        editTxtCC = (EditText) findViewById(R.id.cc_email);
        editTxtBCC = (EditText) findViewById(R.id.bcc_email);
        editTxtSub = (EditText) findViewById(R.id.subject_email);
        editTxtEB = (EditText) findViewById(R.id.body_email);
        //initialize send button
        btnSpeakTO = (FloatingActionButton) findViewById(mini.bhanu.com.voicetext.R.id.send_button);

        //add click input click listener
        ClickListener.inputClick(editTxtTO, vmActivity, 1);
        ClickListener.inputClick(editTxtCC, vmActivity, 2);
        ClickListener.inputClick(editTxtBCC, vmActivity, 3);
        ClickListener.inputClick(editTxtSub, vmActivity, 4);
        ClickListener.inputClick(editTxtEB, vmActivity, 5);

        //add click to send button
        btnSpeakTO.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent voiceInput = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                voiceInput.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
                try {
                    startActivityForResult(voiceInput, 1000);

                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(), "Oops! Your device doesn't support Speech to Text", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_email, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.logout) {
            mailSSL.logout(vmActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (backClickedTwice) {
            mailSSL.logout(vmActivity);
            return;
        }

        this.backClickedTwice = true;
        Toast.makeText(getApplicationContext(), "Please click BACK(<-)again to exit/logout", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                backClickedTwice = false;
            }
        }, 2000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                ClickListener.setVoiceResult(vmActivity, editTxtTO, resultCode, data);
                break;
            case 2:
                ClickListener.setVoiceResult(vmActivity, editTxtCC, resultCode, data);
                break;
            case 3:
                ClickListener.setVoiceResult(vmActivity, editTxtBCC, resultCode, data);
                break;
            case 4:
                ClickListener.setVoiceResult(vmActivity, editTxtSub, resultCode, data);
                break;
            case 5:
                ClickListener.setVoiceResult(vmActivity, editTxtEB, resultCode, data);
                break;
            case 1000:
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    for (String result : results) {
                        if (!result.replaceAll(" ", "").equalsIgnoreCase("send")) {
                            return;
                        }
                        try {

                            String toEmail = editTxtTO.getText().toString();
                            String ccEmail = editTxtCC.getText().toString();
                            String bccEmail = editTxtBCC.getText().toString();

                            subjectEmail = editTxtSub.getText().toString();
                            bodyEmail = editTxtEB.getText().toString();

                            mailSSL.sendEmail(
                                    vmActivity,
                                    toEmail,
                                    ccEmail,
                                    bccEmail,
                                    subjectEmail,
                                    bodyEmail);
                        } catch (Exception e) {

                            Toast.makeText(getApplicationContext(), "Couldn't enter email" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
                }
        }
    }
}

