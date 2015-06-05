
package kr.co.cashqc;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

/**
 * @author Jung-Hum Cho Created by ssenn_000 on 2015-02-06.
 */
public class ZoomService extends Service {

    private final String TAG = getClass().getSimpleName();
    private Button zoom;

    private ZoomService mThis = this;

    private Intent mIntent;
    private String img1;
    private String img2;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mIntent = intent;
        img1 = mIntent.getStringExtra("img1");
        img2 = mIntent.getStringExtra("img2");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        final Point point = new Point();
        final WindowManager windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        // display.getSize(point);
        display.getRealSize(point);

        LayoutInflater layoutInflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);

        View v = layoutInflater.inflate(R.layout.overlay_zoom, null);

        zoom = (Button)v.findViewById(R.id.zoom);

        addTopView(point, windowManager);

        zoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Zoom", "click !!!");
                stopSelf();

                mIntent = new Intent(mThis, MenuZoomActivity.class);

                mIntent.putExtra("img1", img1);
                mIntent.putExtra("img2", img2);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                Log.e(TAG, "img1 : " + img1 + " img2 : " + img2);

                startActivity(mIntent);
            }
        });
    }

    private void addTopView(Point point, WindowManager windowManager) {

        int xPos = (int)(point.x / 3.0);
        int yPos = (int)(point.y / 2.5);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, xPos,
                yPos, WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

        // Log.e("size", "params xPos : " + xPos);

        windowManager.addView(zoom, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (zoom != null) {
            ((WindowManager)getSystemService(WINDOW_SERVICE)).removeViewImmediate(zoom);
            zoom = null;
        }
    }

}
