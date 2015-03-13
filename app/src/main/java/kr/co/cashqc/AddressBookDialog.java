
package kr.co.cashqc;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * @author Jung-Hum Cho Created by anp on 15. 1. 13..
 */
public class AddressBookDialog extends Dialog {
    // 메뉴 장바구니 선택
    public AddressBookDialog(final Context context, ArrayList<String> addressList,
            final View.OnClickListener onClickListener) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 배경 투명하게
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.dialog_addressbook);

        TextView tvLabel = (TextView)findViewById(R.id.addressbook_dialog_group);

        final ListView listView = (ListView)findViewById(R.id.addressbook_dialog_list);

        AddressBookAdapter adapter = new AddressBookAdapter(context, addressList);

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listView.setAdapter(adapter);

        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = listView.getCheckedItemPosition();
                String item = (String)listView.getItemAtPosition(position);

                String[] address = item.split("_");

                v.setTag(R.id.zipcode, address[0]);
                v.setTag(R.id.address1, address[1]);
                v.setTag(R.id.address2, address[2]);
                onClickListener.onClick(v);
                dismiss();
            }
        });
    }
}
