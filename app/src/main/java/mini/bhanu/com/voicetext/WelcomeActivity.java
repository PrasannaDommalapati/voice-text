package mini.bhanu.com.voicetext;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class WelcomeActivity extends AppCompatActivity {

    Button goTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        goTo = (Button) findViewById(R.id.goToLogin);

        goTo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent goToLogin = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                goToLogin.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

                try {
                    startActivityForResult(goToLogin, 900);
                } catch (ActivityNotFoundException ae) {

                    Toast.makeText(getApplicationContext(), "Could not navigate to login screen.... :(", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 900:
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    for (String result : results) {
                        if (!result.replaceAll(" ", "").equalsIgnoreCase("gotologin")) {
                            return;
                        }
                        try {

                            Toast.makeText(getApplicationContext(), "received voice input: " + result, Toast.LENGTH_LONG).show();
                            Intent login = new Intent(this, LoginActivity.class);

                            startActivity(login);

                        } catch (Exception e) {

                            Toast.makeText(getApplicationContext(), "Couldn't enter email" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
                break;
        }
    }
}
