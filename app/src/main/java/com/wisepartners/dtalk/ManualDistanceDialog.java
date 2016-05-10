
package com.wisepartners.dtalk;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import static com.wisepartners.dtalk.MainActivity.sDistance;

/**
 * @author Jung-Hum Cho Created by anp on 15. 1. 13..
 */
public class ManualDistanceDialog extends Dialog {
    // 메뉴 장바구니 선택
    public ManualDistanceDialog(final Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 배경 투명하게
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.dialog_order);

        Button btnOk = (Button)findViewById(R.id.ok);
        TextView tvLabel = (TextView)findViewById(R.id.order_dialog_group);
        TextView tvChoice = (TextView)findViewById(R.id.order_dialog_choice);
        final ListView listView = (ListView)findViewById(R.id.order_dialog_list);

        tvLabel.setText("거리 선택");
        tvChoice.setText("기본값 : 2km");

        String[] distance = {
                "2km", "3km"
        };

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listView.setAdapter(new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_single_choice, distance));

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = listView.getCheckedItemPosition();
                if(position == 0) {
                    sDistance = 2;
                } else if(position == 1) {
                    sDistance = 3;
                }
                dismiss();
            }
        });
    }


}
