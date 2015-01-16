
package kr.co.cashqc;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import static kr.co.cashqc.DataBaseOpenHelper.TABLE_NAME;

/**
 * @author Jung-Hum Cho Created by anp on 15. 1. 13..
 */
public class OrderMenuDialog extends Dialog {
    // 메뉴 장바구니 선택
    public OrderMenuDialog(final Context context, final ShopMenuData data, final int groupPosition,
            final int childPosition) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 배경 투명하게
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.dialog_order);

        TextView tvLabel = (TextView)findViewById(R.id.order_dialog_group);
        TextView tvChoice = (TextView)findViewById(R.id.order_dialog_choice);

        tvLabel.setText(data.getShopName());
        tvChoice.setText("맛 선택");

        final MenuData level1 = data.getMenu().get(groupPosition);

        final ListView listView = (ListView)findViewById(R.id.order_dialog_list);

        final MenuData level2 = data.getMenu().get(groupPosition).getChild().get(childPosition);

        OrderMenuAdapter adapter = new OrderMenuAdapter(context, level2);

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listView.setAdapter(adapter);

        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listView.getCheckedItemPosition() != AdapterView.INVALID_POSITION) {

                    boolean hasMenu = false;
                    boolean isDiffentShop = false;

                    DataBaseOpenHelper helper = new DataBaseOpenHelper(context);

                    SQLiteDatabase db = helper.getWritableDatabase();

                    MenuData level3 = level2.getChild().get(listView.getCheckedItemPosition());

                    Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);

                    while (c.moveToNext()) {
                        if ((c.getString(c.getColumnIndex("shop_code")).equals(data.getShopCode()))) {
                            //TODO 다른 샵 처리
                        }

                        if ((c.getString(c.getColumnIndex("menu_code")).equals(level3.getCode()))) {
                            hasMenu = true;
                            helper.close();
                            db.close();
                            c.close();
                            break;
                        }
                    }

                    if (!hasMenu) {

                        ContentValues values = new ContentValues();

                        values.put("shop_code", data.getShopCode());
                        values.put("shop_name", data.getShopName());
                        values.put("shop_phone", data.getShopPhone());
                        values.put("shop_vphone", data.getShopVPhone());

                        values.put("menu_name", level1.getLabel() + "\n" + level2.getLabel() + "\n"
                                + level3.getLabel());

                        values.put("menu_code", level3.getCode());

                        values.put("price", level3.getPrice());

                        values.put("ea", 1);

                        db.insert(TABLE_NAME, null, values);
                        Log.i("cart_add", values.toString());
                        Toast.makeText(context, "장바구니에 담았습니다.", Toast.LENGTH_SHORT).show();
                        new BaseActivity().setCartCount(context);
                    } else {
                        Toast.makeText(context, "장바구니에 있는 메뉴입니다.", Toast.LENGTH_SHORT).show();
                    }

                }
                dismiss();
            }
        });
    }
}
