
package kr.co.cashqc;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @ Jung-Hum Cho Created by anp on 15. 7. 14..
 */
public class CompareTime {

    public CompareTime(String compareStringOne, String compareStringTwo) {
        this.compareStringOne = compareStringOne;
        this.compareStringTwo = compareStringTwo;
    }

    private final String TAG = getClass().getSimpleName();

    public static final String inputFormat = "HH:MM";

    private Date now;

    private Date open;

    private Date close;

    private String compareStringOne;

    private String compareStringTwo;

    private SimpleDateFormat inputParser = new SimpleDateFormat(inputFormat, Locale.KOREAN);

    public boolean compareDates() {
        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        int ampm = calendar.get(Calendar.AM_PM);

        now = parseDate(hour + ":" + minute);

        open = parseDate(compareStringOne);

        close = parseDate(compareStringTwo);

        Log.d(TAG, "now : " + now);
        Log.d(TAG, "open : " + open);
        Log.d(TAG, "close : " + close);
        Log.d(TAG, "AMPM : " + ampm);


        if(open == null || close == null) {
            return true;
        }

        if(1 == 1) {
            if(open.before(now) && close.after(now)) {
                return true;
            }
        } else if (ampm == 1) {
            if(open.before(now) && close.before(now)) {
                return true;
            }
        }




        return false;

    }

    private Date parseDate(String date) {

        try {
            return inputParser.parse(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

}
