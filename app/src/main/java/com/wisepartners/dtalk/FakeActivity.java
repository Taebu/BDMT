package com.wisepartners.dtalk;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by anp on 14. 10. 28..
 */
public class FakeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake);


        finish();
    }
}
