package mini.bhanu.com.voicetext;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public abstract class ClickListener implements View.OnClickListener {

    /**
     * @param editText
     * @param Activity
     * @param code
     */
    public static void inputClick(EditText editText, final Activity Activity, final int code) {

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent voiceInput = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                voiceInput.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
                try {
                    Activity.startActivityForResult(voiceInput, code);

                } catch (ActivityNotFoundException a) {

                    toastMessage(Activity,"Oops! Your device doesn't support Speech to Text");

                }
            }
        });
    }


    /**
     * @param activity
     * @param editText
     * @param resultCode
     * @param data
     */
    public static void setVoiceResult(final Activity activity, EditText editText, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && null != data) {

            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            for (String result : results) {

                if (!results.get(0).equalsIgnoreCase("clean")) {
                    try {

                        editText.setText(result);

                    } catch (Exception e) {

                        toastMessage(activity,"Couldn't convert voice to text");
                    }
                } else {
                    editText.setText("");
                }
            }
        }
    }

    /**
     * @param activity
     * @param message
     */
    public static void toastMessage(final Activity activity, String message) {

        Toast.makeText(activity.getApplicationContext(), message , Toast.LENGTH_LONG).show();
    }

    /**
     * @param view
     * @param message
     */
    public static void snackMessage(View view, String message) {

        Snackbar.make(view, message, Toast.LENGTH_SHORT).show();
    }
}
