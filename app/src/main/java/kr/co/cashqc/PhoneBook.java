
package kr.co.cashqc;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;

/**
 * @author Jung-Hum Cho Created by anp on 14. 12. 23..
 */
public class PhoneBook {

    public static ArrayList<ContactData> getContactList(Activity activity) {

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

                contactlist.add(acontact);
            } while (contactCursor.moveToNext());
        }

        return contactlist;

    }

}

