
package kr.co.cashqc;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * @author Jung-Hum Cho Created by anp on 14. 10. 31..
 */
public class Utils {
    public static double sLAT = 0;

    public static double sLNG = 0;

    public static final String RegisterKey222 = "register";

    public static final String IMG_URL = "http://cashq.co.kr/adm/upload/";

    public static void setDisplayName(Activity activity, String displayName, String phoneNumber) {

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
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
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
}
