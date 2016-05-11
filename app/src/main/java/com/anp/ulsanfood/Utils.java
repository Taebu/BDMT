
package com.anp.ulsanfood;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * @author Jung-Hum Cho Created by anp on 14. 10. 31..
 */
public class Utils {

    private static final String TAG = "GLOBAL UTILS";

    public static double sLAT = 0;

    public static double sLNG = 0;

    public static final String REGISTER = "register";

    public static final String IMG_URL = "http://cashq.co.kr/adm/upload/";

    public static void checkContact(Activity activity, String name, String num) {

        ArrayList<ContactData> contactDataList = getContactList(activity);

        boolean hasContact = true;

        if (contactDataList.size() == 0) {
            setDisplayName(activity, name, num);
        } else {
            for (ContactData a : contactDataList) {
                if (a.getName().equals(name) || a.getNum().equals(num)) {
                    hasContact = true;
                    break;
                } else {
                    hasContact = false;
                }
            }
            if (!hasContact) {
                setDisplayName(activity, name, num);
            }
        }
    }

    private static void setDisplayName(Activity activity, String displayName, String phoneNumber) {

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        /* 2014-02-26 지우기 선행 */
        // phoneNumber = phoneNumber.replace("tel:", "");
        Log.e("tel", phoneNumber);
        ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.CommonDataKinds.Phone.NUMBER + "=?", new String[] {
                    String.valueOf(phoneNumber)
                }).build());

        // 여기서 실제 지우게 됨
        try {
            activity.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        ArrayList<ContentProviderOperation> ops2 = new ArrayList<ContentProviderOperation>();

        int rawContactInsertIndex = ops2.size();

        // 위에서 했던 것처럼 연락처(계정)을 먼저 하나 만들고 그 연락처에 정보를 입력 하는 방식이 아니고
        // ContentProviderOperation에 계정과 계정에 연락처 정보를 셋팅하여 한번에 insert하는 방식

        ops2.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, "basic")
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, "캐시큐").build());

        ops2.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                        displayName).build());

        // phoneNumber = phoneNumber.replace("tel:", "");
        Log.e("tel", phoneNumber);

        ops2.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build());

        // 여기서 실제 입력을 하게 됨
        try {
            activity.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops2);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static ArrayList<ContactData> getContactList(Activity activity) {

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID, // 연락처 ID ->
                // 사진 정보
                // 가져오는데 사용
                ContactsContract.CommonDataKinds.Phone.NUMBER, // 연락처
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
        }; // 연락처 이름.

        String[] selectionArgs = null;

        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                + " COLLATE LOCALIZED ASC";

        Cursor contactCursor = activity.managedQuery(uri, projection, null, selectionArgs,
                sortOrder);

        ArrayList<ContactData> contactlist = new ArrayList<ContactData>();

        if (contactCursor.moveToFirst()) {
            do {
                String phonenumber = contactCursor.getString(1).replaceAll("-", "");

                // if (phonenumber.length() == 10) {
                // phonenumber = phonenumber.substring(0, 3) + "-"
                // + phonenumber.substring(3, 6) + "-"
                // + phonenumber.substring(6);
                // } else if (phonenumber.length() > 8) {
                // phonenumber = phonenumber.substring(0, 3) + "-"
                // + phonenumber.substring(3, 7) + "-"
                // + phonenumber.substring(7);
                // }

                ContactData acontact = new ContactData();
                acontact.setNum(phonenumber);
                acontact.setName(contactCursor.getString(2));

                Log.e("contact", "num : " + phonenumber);
                Log.e("contact", "name : " + contactCursor.getString(2));

                contactlist.add(acontact);
            } while (contactCursor.moveToNext());
        }

        return contactlist;

    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();

        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();

            Log.e(TAG, "i : " + i + " listItem : " + listItem.getMeasuredHeight()
                    + " totalHeight : " + totalHeight);
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        // params.height = totalHeight + (listView.getDividerHeight() *
        // (listAdapter.getCount() - 1));
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount()));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static void setExpandableListViewHeight(ExpandableListView listView, int group) {

        ExpandableListAdapter adapter = listView.getExpandableListAdapter();

        if (adapter == null) {
            return;
        }

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View groupview = null;
        int childrenCount = 0;

        for (int i = 0; i < adapter.getGroupCount(); i++) {

            Log.e("Utils.setExpandableListViewHeight", "groupCount : " + adapter.getGroupCount());

            groupview = adapter.getGroupView(i, false, groupview, listView);

            if (i == 0) {
                groupview.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
            }

            groupview.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += groupview.getMeasuredHeight();

            if (((listView.isGroupExpanded(i)) && (i != group))
                    || ((!listView.isGroupExpanded(i)) && (i == group))) {

                View childView = null;

                for (int j = 0; j < adapter.getChildrenCount(i); j++) {

                    Log.e("Utils.setExpandableListViewHeight", "childrenCount(" + i + ") : "
                            + adapter.getChildrenCount(i));

                    childView = adapter.getChildView(i, j, false, childView, listView);

                    childView.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
                            View.MeasureSpec.UNSPECIFIED));

                    childView.measure(
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

                    totalHeight += childView.getMeasuredHeight();
                    childrenCount += adapter.getChildrenCount(i);
                }
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (adapter.getGroupCount() + childrenCount));
        listView.setLayoutParams(params);
        listView.requestLayout();

    }

    public static void initExpandableListViewHeight(ExpandableListView listView) {

        ExpandableListAdapter adapter = listView.getExpandableListAdapter();

        if (adapter == null) {
            return;
        }

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View groupview = null;
        int childrenCount = 0;

        for (int i = 0; i < adapter.getGroupCount(); i++) {

            Log.e("Utils.setExpandableListViewHeight", "groupCount : " + adapter.getGroupCount());

            groupview = adapter.getGroupView(i, false, groupview, listView);

            if (i == 0) {
                groupview.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
            }

            groupview.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += groupview.getMeasuredHeight();

            View childView = null;

            for (int j = 0; j < adapter.getChildrenCount(i); j++) {

                Log.e("Utils.setExpandableListViewHeight",
                        "childrenCount(" + i + ") : " + adapter.getChildrenCount(i));

                childView = adapter.getChildView(i, j, false, childView, listView);

                childView.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
                        View.MeasureSpec.UNSPECIFIED));

                childView.measure(
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

                totalHeight += childView.getMeasuredHeight();
                childrenCount += adapter.getChildrenCount(i);

            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (adapter.getGroupCount() + childrenCount));
        listView.setLayoutParams(params);
        listView.requestLayout();

    }

    public static void insertMenuLevel2(Context context, ShopMenuData data, int groupPos,
                                        int childPos) {

        DataBaseOpenHelper helper = new DataBaseOpenHelper(context);

        SQLiteDatabase db = helper.getWritableDatabase();

        Cursor c = db.query(DataBaseOpenHelper.TABLE_NAME, null, null, null, null, null, null);

        MenuData level1 = data.getMenu().get(groupPos);

        MenuData level2 = data.getMenu().get(groupPos).getChild().get(childPos);

        String shopCode = data.getShopCode();

        String menuCode = level2.getCode();

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

            values.put("menu_name", level1.getLabel() + "\n" + level2.getLabel());

            values.put("menu_code", level2.getCode());

            int price2;

            if(level2.isDeal()) {
                price2 = level2.getDiscountPrice();
            } else {
                price2 = level2.getPrice();
            }

            values.put("price", price2);

            values.put("ea", 1);

            db.insert(DataBaseOpenHelper.TABLE_NAME, null, values);

            Log.i("cart_add", values.toString());

            Toast.makeText(context, "장바구니에 담았습니다.", Toast.LENGTH_SHORT).show();

            c.close();
            db.close();
            helper.close();

        } else if (isDiffentShop) {
            Toast.makeText(context, "다른 업체입니다. 장바구니를 비워주세요.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "장바구니에 있는 메뉴입니다.", Toast.LENGTH_SHORT).show();
        }

    }
    // public static void

    public static int getDisplayHeightSize(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        int displayHeight = display.getHeight();// 전체 스크리 사이즈 높이

        Window window = activity.getWindow();
        int topBarHeight = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();// 상태바와
        // 타이틀바의
        // 높이
        // 총합입니다.

        return displayHeight - topBarHeight;
    }

    public static int getDisplayWidthSize(Activity activity) {
        return activity.getWindowManager().getDefaultDisplay().getWidth();
    }

    public static void setExpandableListViewHeight1(ExpandableListView listView, int group) {
        ExpandableListAdapter listAdapter = listView.getExpandableListAdapter();
        if (listAdapter == null) {
            return;
        }

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            view = listAdapter.getGroupView(i, false, view, listView);
            if (i == 0) {
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
            if (((listView.isGroupExpanded(i)) && (i != group))
                    || ((!listView.isGroupExpanded(i)) && (i == group))) {
                View listItem = null;
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    listItem = listAdapter.getChildView(i, j, false, listItem, listView);
                    listItem.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
                            View.MeasureSpec.UNSPECIFIED));
                    listItem.measure(
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                    totalHeight += listItem.getMeasuredHeight();
                }
            }
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static String getVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return String.valueOf(packageInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}
