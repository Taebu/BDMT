package com.wisepartners.dtalk;

import android.os.Bundle;


public class InfoActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        // activity killer activity add.
        killer.addActivity(this);

        activityAnimation(true);
    }

    @Override
    public void finish() {
        super.finish();
        activityAnimation(true);
    }
}
