
package kr.co.cashqc;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import java.util.Timer;
import java.util.TimerTask;

public class ShowMsg extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        final String seq;
        final String msg;
        final String title;
        final String wr_id;
        final String bo_table;
        final String board;
        final String get_biz_code;
        final String link;

        int MSG_TYPE = 0;

        // 0 = common
        // 1 = order success
        // 2 = order cancel

        Bundle bun = getIntent().getExtras();
        seq = bun.getString("seq");
        title = bun.getString("title");
        msg = bun.getString("msg");
        wr_id = bun.getString("wr_id");
        bo_table = bun.getString("bo_table");
        board = bun.getString("board");
        link = bun.getString("link");
        get_biz_code = bun.getString("get_biz_code");

        String auth = "[캐시큐] 인증번호";
        Log.d("JAY", "msg : " + msg);

        CustomDialog dialog;
        // if (msg.startsWith(auth)) {
        // dialog = new CustomDialog(this, msg);
        // } else {
        dialog = new CustomDialog(this, msg, false, this, MainActivity.class, MSG_TYPE);
        // }

//        dialog.show();

        // 폰 설정의 조명시간을 가져와서 해당 시간만큼만 화면을 켠다.
        int defTimeOut = Settings.System.getInt(getContentResolver(),
                Settings.System.SCREEN_OFF_TIMEOUT, 15000);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                PushWakeLock.releaseCpuLock();
            }
        };

        Timer timer = new Timer();
        timer.schedule(task, defTimeOut);

    }
}
