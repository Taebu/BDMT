
package com.wisepartners.dtalk;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

/**
 * @author Jung-Hum Cho Created by Jung-Hum Cho on 2016. 4. 26..
 */
public class LifeDialog extends Dialog {

    public LifeDialog(final Context context, View.OnClickListener onClickListener) {
        super(context);

        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        // requestWindowFeature(Window.FEATURE_);

        // 배경 투명하게
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.dialog_life);

        findViewById(R.id.life_09).setOnClickListener(onClickListener);
        findViewById(R.id.life_10).setOnClickListener(onClickListener);
        findViewById(R.id.life_11).setOnClickListener(onClickListener);
        findViewById(R.id.life_12).setOnClickListener(onClickListener);
        findViewById(R.id.life_13).setOnClickListener(onClickListener);
        findViewById(R.id.life_14).setOnClickListener(onClickListener);
        findViewById(R.id.life_15).setOnClickListener(onClickListener);
        findViewById(R.id.life_16).setOnClickListener(onClickListener);
        findViewById(R.id.life_18).setOnClickListener(onClickListener);
        findViewById(R.id.life_19).setOnClickListener(onClickListener);

    }

}
