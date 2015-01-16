
package kr.co.cashqc;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import kr.co.cashqc.menu.Fragment1;
import kr.co.cashqc.menu.Fragment2;
import kr.co.cashqc.menu.Fragment3;
import kr.co.cashqc.menu.Fragment4;

import static kr.co.cashqc.DataBaseOpenHelper.TABLE_NAME;

/**
 * Created by anp on 14. 11. 4..
 * 
 * @author Jung-Hum Cho
 */

public class BaseActivity extends SlidingFragmentActivity {

    CustomDialog mDialog;

    ListFragment mFrag;

    ActivityKiller killer;

    public static int CART_COUNT;

    public static TextView TV_CART_COUNT;

    public BaseActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        killer = ActivityKiller.getInstance();

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

        // customize the SlidingMenu
        SlidingMenu sm = getSlidingMenu();
        sm.setMode(SlidingMenu.LEFT);
        sm.setShadowWidthRes(R.dimen.shadow_width);
        sm.setShadowDrawable(R.drawable.shadow);
        sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
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

        TV_CART_COUNT = (TextView) findViewById(R.id.cart_count);
        TV_CART_COUNT.setText(String.valueOf(CART_COUNT));

        setCartCount(this);

        findViewById(R.id.btn_slidingmenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });


        //
        TV_CART_COUNT.setVisibility(View.INVISIBLE);
        findViewById(R.id.btn_cart).setVisibility(View.INVISIBLE);



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
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    public String getPhoneNumber() {
        TelephonyManager mgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);

        try {
            String num = mgr.getLine1Number();
            if (num.startsWith("+82")) {
                return num.replace("+82", "0");
            } else {
                return num;
            }
        } catch (NullPointerException e) {
            return "";
        }
    }

    public void setCartCount(Context context) {
        DataBaseOpenHelper helper = new DataBaseOpenHelper(context);
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor c = database.query(TABLE_NAME, null, null, null, null, null, null);
        CART_COUNT = c.getCount();
        TV_CART_COUNT.setText(String.valueOf(CART_COUNT));
        c.close();
    }
}
