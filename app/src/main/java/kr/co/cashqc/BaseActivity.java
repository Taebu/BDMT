
package kr.co.cashqc;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.gc.android.market.api.MarketSession;
import com.gc.android.market.api.model.Market;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import kr.co.cashqc.menu.Fragment1;
import kr.co.cashqc.menu.Fragment2;
import kr.co.cashqc.menu.Fragment3;
import kr.co.cashqc.menu.Fragment4;

import static kr.co.cashqc.DataBaseOpenHelper.TABLE_NAME;
import static kr.co.cashqc.Utils.getDisplayWidthSize;

/**
 * Created by anp on 14. 11. 4..
 *
 * @author Jung-Hum Cho
 */

public class BaseActivity extends SlidingFragmentActivity {

    //    public static final boolean sIsTTSmode = true;
    public static final boolean sIsTTSmode = false;

    private final String TAG = getClass().getSimpleName();

    private static final Uri URI = Uri.parse("content://com.google.android.gsf.gservices");

    private static final String ID_KEY = "android_id";

    CustomDialog mDialog;

    ListFragment mFrag;

    ActivityKiller killer;

    private Activity mThis = this;

    public static int CART_COUNT;

    public static TextView TV_CART_COUNT;

    public BaseActivity() {
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        killer = ActivityKiller.getInstance();

        stopService(new Intent(this, CallService.class));

        // set the Behind View
        setBehindContentView(R.layout.menu_frame);

        if (savedInstanceState == null) {
            FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
            mFrag = new MenuListFragment();

            t.replace(R.id.menu_frame, mFrag);
            t.commit();

        } else {
            mFrag = (ListFragment)this.getSupportFragmentManager()
                    .findFragmentById(R.id.menu_frame);
        }

        int halfWidth = getDisplayWidthSize(this) / 2;

        // customize the SlidingMenu
        final SlidingMenu sm = getSlidingMenu();
        sm.setMode(SlidingMenu.LEFT);
        sm.setShadowWidthRes(R.dimen.shadow_width);
        sm.setShadowDrawable(R.drawable.shadow);
        sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        // sm.setBehindOffsetRes(halfWidth);
        sm.setFadeDegree(0.35f);
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        setSlidingActionBarEnabled(false);

        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (Build.VERSION.SDK_INT > 11) {
            View homeIcon = findViewById(android.R.id.home);
            homeIcon.setVisibility(View.GONE);
            getSupportActionBar().setIcon(new ColorDrawable(Color.TRANSPARENT));
        }
        getSupportActionBar().setIcon(new ColorDrawable(Color.TRANSPARENT));

        View view = getLayoutInflater().inflate(R.layout.actionbar_custom, null);
        getSupportActionBar().setCustomView(view);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        TV_CART_COUNT = (TextView)findViewById(R.id.cart_count);
        TV_CART_COUNT.setText(String.valueOf(CART_COUNT));

        setCartCount(this);

        findViewById(R.id.btn_slidingmenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });

        // 장바구니 숨김
        if (sIsTTSmode) {
            TV_CART_COUNT.setVisibility(View.VISIBLE);
            findViewById(R.id.btn_cart).setVisibility(View.VISIBLE);
        }
        findViewById(R.id.btn_cart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CART_COUNT > 0) {
                    startActivity(new Intent(BaseActivity.this, CartActivity.class));
                } else {
                    Toast.makeText(getApplicationContext(), "장바구니가 비어있어요", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    // @Override
    // public boolean onOptionsItemSelected(MenuItem item) {
    // switch (item.getItemId()) {
    // case android.R.id.home:
    // toggle();
    // return true;
    // case R.id.action_settings:
    // return true;
    // }
    // return super.onOptionsItemSelected(item);
    // }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getSupportMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void fragmentReplace(int reqNewFragmentIndex) {

        Fragment newFragment = null;

        newFragment = getFragment(reqNewFragmentIndex);

        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // transaction.replace(R.id.main_circle_layout, newFragment);

        getSlidingMenu().showContent();
        transaction.commit();
    }

    private Fragment getFragment(int idx) {
        Fragment newFragment = null;

        switch (idx) {
            case 0:
                newFragment = new Fragment1();
                break;
            case 1:
                newFragment = new Fragment2();
                break;
            case 2:
                newFragment = new Fragment3();
                break;
            case 3:
                newFragment = new Fragment4();
                break;
            default:
                break;
        }

        return newFragment;
    }

    @Override
    protected void onStop() {
        super.onStop();
        new kr.co.cashqc.lazylist.ImageLoader(this).clearCache();
        // toggle();
        Log.e("BaseActivity", "!!! onStop !!!");
    }

    @Override
    protected void onResume() {

        setCartCount(this);
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, CallService.class));
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 0:
                return ProgressDialog.show(getApplicationContext(), "",
                        "위치정보를 수집하고 있습니다.\n잠시만 기다려 주세요...", true);
            case 1:
                final Dialog dialog = new Dialog(BaseActivity.this,
                        android.R.style.Theme_Translucent);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.loading_progress);
                dialog.setCancelable(false);

                return dialog;

            default:
                return null;
        }
    }

    public void activityAnimation(Boolean isStart) {
        if (isStart) {
            // overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
        } else {
            // overridePendingTransition(R.anim.end_enter, R.anim.end_exit);
        }
    }

    public static void setCartCount(Activity act) {

        DataBaseOpenHelper helper = new DataBaseOpenHelper(act);
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor c = database.query(TABLE_NAME, null, null, null, null, null, null);

        CART_COUNT = c.getCount();
        Log.i("cart_count", "" + CART_COUNT);

        TV_CART_COUNT = (TextView)act.findViewById(R.id.cart_count);
        TV_CART_COUNT.setText(String.valueOf(CART_COUNT));
        c.close();
        database.close();
        helper.close();

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

    public static String getAndroidId(Context context) {

        String[] params = {
            ID_KEY
        };

        Cursor c = context.getContentResolver().query(URI, null, null, params, null);

        if (!c.moveToFirst() || c.getColumnCount() < 2) {
            return null;
        }

        try {
            Log.e("BaseActivity",
                    "getAndroidId() : " + Long.toHexString(Long.parseLong(c.getString(1))));
            return Long.toHexString(Long.parseLong(c.getString(1)));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String getMarketVersionName(Context context) {

        String email = "anptown";
        String password = "town2151";

        try {

            MarketSession session = new MarketSession();
            session.login(email, password);

            session.getContext().setAndroidId(getAndroidId(context));
            String query = "kr.co.cashqc";

            Market.AppsRequest appsRequest = Market.AppsRequest.newBuilder().setQuery(query)
                    .setStartIndex(0).setEntriesCount(1)
                    .setOrderType(Market.AppsRequest.OrderType.NEWEST).setWithExtendedInfo(false)
                    .build();

            session.append(appsRequest, new MarketSession.Callback<Market.AppsResponse>() {
                @Override
                public void onResult(Market.ResponseContext responseContext,
                        Market.AppsResponse appsResponse) {

                    for (int i = 0; i < appsResponse.getAppCount(); i++) {

                        Log.e("BaseActivity", "title : " + appsResponse.getApp(i).getTitle());
                        Log.e("BaseActivity", "creator : " + appsResponse.getApp(i).getCreator());
                        Log.e("BaseActivity", "pkgName : "
                                + appsResponse.getApp(i).getPackageName());
                        Log.e("BaseActivity", "version : " + appsResponse.getApp(i).getVersion());
                        Log.e("BaseActivity", "versionCode : "
                                + appsResponse.getApp(i).getVersionCode());

                    }

                }
            });

            session.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void mOnClick(View view) {

        switch (view.getId()) {

            case R.id.actionbar_btn_gps:
                break;

            case R.id.actionbar_manual_location:
                break;

            case R.id.actionbar_manual_distance:
                break;

        }
    }

}
