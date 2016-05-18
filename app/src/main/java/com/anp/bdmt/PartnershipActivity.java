package com.anp.bdmt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by anp on 14. 12. 9..
 */
public class PartnershipActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partnership);
        killer.addActivity(this);

        findViewById(R.id.btn_agency).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PartnershipActivity.this, AgencyActivity.class));

            }
        });

        findViewById(R.id.btn_franchise).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PartnershipActivity.this, FranchiseActivity.class));
            }
        });
    }
}
