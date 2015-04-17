
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import static kr.co.cashqc.DataBaseOpenHelper.TABLE_NAME;

/**
 * @author Jung-Hum Cho Created by anp on 15. 1. 13..
 */
public class OrderMenuDialog extends Dialog {
    // 메뉴 장바구니 선택
    public OrderMenuDialog(final Context context, final ShopMenuData data, final int groupPos,
            final int childPos, final OnDismissListener listener) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 배경 투명하게
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.dialog_order);

        TextView tvLabel = (TextView)findViewById(R.id.order_dialog_group);
        TextView tvChoice = (TextView)findViewById(R.id.order_dialog_choice);

        tvLabel.setText(data.getShopName());
        tvChoice.setText("맛 선택");

        final MenuData level1 = data.getMenu().get(groupPos);

        final MenuData level2 = data.getMenu().get(groupPos).getChild().get(childPos);

        final ListView listView = (ListView)findViewById(R.id.order_dialog_list);

        OrderMenuAdapter adapter = new OrderMenuAdapter(context, level2);

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listView.setAdapter(adapter);

        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int checkedPos = listView.getCheckedItemPosition();

                if (checkedPos != AdapterView.INVALID_POSITION) {

                    MenuData level3 = level2.getChild().get(checkedPos);

                    boolean hasLevel4 = !(level3.getChild().isEmpty());

//                    ArrayList<MenuData> level4 = level3.getChild();

//                    if (level4.size() == 0) {
                    if(hasLevel4){
                        OrderMenuDialog level4 = new OrderMenuDialog(context, data, level1, level2, level3);
                        level4.show();
                        level4.setOnDismissListener(listener);
                    } else {
                        insertMenuLevel3(context, data, level1, level2, level3);
                    }
                }

                dismiss();
            }
        });
    }

    public OrderMenuDialog(final Context context, final ShopMenuData data, final MenuData level1,
            final MenuData level2, final MenuData level3) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 배경 투명하게
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.dialog_order);

        Button btnOk = (Button)findViewById(R.id.ok);
        TextView tvLabel = (TextView)findViewById(R.id.order_dialog_group);
        TextView tvChoice = (TextView)findViewById(R.id.order_dialog_choice);

        tvLabel.setText(data.getShopName());
        tvChoice.setText("맛 선택");

        final ListView listView = (ListView)findViewById(R.id.order_dialog_list);

        OrderMenuAdapter adapter = new OrderMenuAdapter(context, level3);

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listView.setAdapter(adapter);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int checkedPos = listView.getCheckedItemPosition();

                if (checkedPos != AdapterView.INVALID_POSITION) {
                    insertMenuLevel4(context, checkedPos, data, level1, level2, level3);
                }
                dismiss();
            }
        });
    }

    private void insertMenuLevel3(Context context, ShopMenuData data, MenuData level1,
            MenuData level2, MenuData level3) {

        DataBaseOpenHelper helper = new DataBaseOpenHelper(context);

        SQLiteDatabase db = helper.getWritableDatabase();

        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);

        String shopCode = data.getShopCode();

        String menuCode = level3.getCode();

        // 동일한 메뉴인지
        boolean hasMenu = false;

        // 같은 업체인지
        boolean isDiffentShop = false;

        while (c.moveToNext()) {

            String preShopCode = c.getString(c.getColumnIndex("shop_code"));

            String preMenuCode = c.getString(c.getColumnIndex("menu_code"));

            if (!(preShopCode.equals(shopCode))) {
                isDiffentShop = true;
                helper.close();
                db.close();
                c.close();
                break;
            }

            if (preMenuCode.equals(menuCode)) {
                hasMenu = true;
                helper.close();
                db.close();
                c.close();
                break;
            }
        }

        if (!hasMenu && !isDiffentShop) {
            ContentValues values = new ContentValues();

            values.put("shop_code", data.getShopCode());
            values.put("shop_name", data.getShopName());
            values.put("shop_phone", data.getShopPhone());
            values.put("shop_vphone", data.getShopVPhone());

            values.put("menu_name",
                    level1.getLabel() + "\n" + level2.getLabel() + "\n" + level3.getLabel());

            values.put("menu_code", level3.getCode());

            int price2 = Integer.parseInt(level2.getPrice());
            int price3 = Integer.parseInt(level3.getPrice());

            values.put("price", price2 + price3);

            values.put("ea", 1);

            db.insert(TABLE_NAME, null, values);

            Log.i("cart_add", values.toString());

            Toast.makeText(context, "장바구니에 담았습니다.", Toast.LENGTH_SHORT).show();

//            setCartCount(context);

            c.close();
            db.close();
            helper.close();

        } else if (isDiffentShop) {
            Toast.makeText(context, "다른 업체입니다. 장바구니를 비워주세요.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "장바구니에 있는 메뉴입니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void insertMenuLevel4(Context context, int checkedPos, ShopMenuData data,
            MenuData level1, MenuData level2, MenuData level3) {

        DataBaseOpenHelper helper = new DataBaseOpenHelper(context);

        SQLiteDatabase db = helper.getWritableDatabase();

        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);

        // 동일한 메뉴인지
        boolean hasMenu = false;

        // 같은 업체인지
        boolean isDiffentShop = false;

        MenuData level4 = level3.getChild().get(checkedPos);

        String shopCode = data.getShopCode();

        String menuCode = level4.getCode();

        while (c.moveToNext()) {

            String preShopCode = c.getString(c.getColumnIndex("shop_code"));

            String preMenuCode = c.getString(c.getColumnIndex("menu_code"));

            if (!(preShopCode.equals(shopCode))) {
                isDiffentShop = true;
                helper.close();
                db.close();
                c.close();
                break;
            }

            if (preMenuCode.equals(menuCode)) {
                hasMenu = true;
                helper.close();
                db.close();
                c.close();
                break;
            }
        }

        // TODO 레벨4 메뉴 처리

        if (!hasMenu && !isDiffentShop) {

            ContentValues values = new ContentValues();

            values.put("shop_code", data.getShopCode());
            values.put("shop_name", data.getShopName());
            values.put("shop_phone", data.getShopPhone());
            values.put("shop_vphone", data.getShopVPhone());

            values.put("menu_name",
                    level1.getLabel() + "\n" + level2.getLabel() + "\n" + level3.getLabel() + "\n"
                            + level4.getLabel());

            values.put("menu_code", level4.getCode());

            int price2 = Integer.parseInt(level2.getPrice());
            int price3 = Integer.parseInt(level3.getPrice());
            int price4 = Integer.parseInt(level4.getPrice());

            values.put("price", price2 + price3 + price4);

            values.put("ea", 1);

            db.insert(TABLE_NAME, null, values);

            Log.i("cart_add", values.toString());

            Toast.makeText(context, "장바구니에 담았습니다.", Toast.LENGTH_SHORT).show();

//            setCartCount(context);

            c.close();
            db.close();
            helper.close();

        } else if (isDiffentShop) {
            Toast.makeText(context, "다른 업체입니다. 장바구니를 비워주세요.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "장바구니에 있는 메뉴입니다.", Toast.LENGTH_SHORT).show();
        }

    }

}
