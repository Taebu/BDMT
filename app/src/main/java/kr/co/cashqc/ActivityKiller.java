
package kr.co.cashqc;

import android.app.Activity;

import java.util.ArrayList;

/**
 * Created by anp on 14. 11. 4..
 * @author Jung-Hum Cho
 *
 */
public class ActivityKiller {
    private ActivityKiller() {
        mActivityArray = new ArrayList<Activity>();
    }

    private static ActivityKiller instance;

    private ArrayList<Activity> mActivityArray;

    public static ActivityKiller getInstance() {
        if(instance == null) {
            instance = new ActivityKiller();
        }
        return instance;
    }

    public void addActivity(Activity activity) {
        if (!isActivity(activity)) {
            mActivityArray.add(activity);
        }
    }

    public void removeActivity(Activity activity) {
        if (isActivity(activity)) {
            activity.finish();
            mActivityArray.remove(activity);
        }
    }

    public void allKillActivity() {
        for(Activity activity : mActivityArray) {
            activity.finish();
        }
    }

    private boolean isActivity(Activity targetActivity) {
        for (Activity activity : mActivityArray) {
            if (activity == targetActivity) {
                return true;
            }
        }
        return false;
    }
}
