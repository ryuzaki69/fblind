package org.project.harsh.fblind;

/**
 * Created by Harsh Gupta on 3/24/2018.
 */
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import org.project.harsh.fblind.LocaleHelper;

public class MainApp extends Activity {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }

}
