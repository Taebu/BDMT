
package com.anp.ulsanfood;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Jung-Hum Cho Created by anp on 14. 11. 3..
 */
public class CustomDialog extends Dialog {

    public CustomDialog(Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 배경 투명하게
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.loading_progress);
        // setContentView(R.layout.dialog_custom_ok);
        setCancelable(false);

        // 애니메이션
        ImageView imageView = (ImageView)findViewById(R.id.loadanim);
        imageView.setBackgroundResource(R.drawable.load_anim);

        AnimationDrawable animation = (AnimationDrawable)imageView.getBackground();
        animation.start();
    }

    public CustomDialog(final Context context, final String message) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 배경 투명하게
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.dialog_custom_ok);

        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        TextView msg = (TextView)findViewById(R.id.dialog_message);
        msg.setText(message);
    }

    public CustomDialog(final Context context, final String message, final Activity activity,
            final Intent intent) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 배경 투명하게
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.dialog_custom_ok);

        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(intent);
                dismiss();
            }
        });

        TextView msg = (TextView)findViewById(R.id.dialog_message);
        msg.setText(message);
    }

    public CustomDialog(final Context context, final String message, boolean b,
            final Activity activity, final Class<?> cls, int msgFlag) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 배경 투명하게
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setCancelable(false);

        ImageView msgImg = (ImageView) findViewById(R.id.img_gcm);

        if (b) {
            setContentView(R.layout.dialog_custom_ok);

            findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.startActivity(new Intent(context, cls));
                    dismiss();
                }
            });

        } else {
            setContentView(R.layout.dialog_custom_3);

            findViewById(R.id.dialog_play).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClassName(activity.getPackageName(), activity.getPackageName()
                            + ".MainActivity");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.startActivityForResult(intent, 1);
                    PushWakeLock.releaseCpuLock();
                    activity.finish();
                }
            });

            findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PushWakeLock.releaseCpuLock();
                    dismiss();
                    activity.finish();
                }
            });

            findViewById(R.id.dialog_review).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String myPackageName = activity.getPackageName();
                    String url = "";

                    try {
                        // Check whether Google Play store is installed
                        // or
                        // not:
                        // this.getPackageManager().getPackageInfo("com.android.vending",
                        // 0);
                        url = "market://details?id=" + myPackageName;
                    } catch (final Exception e) {
                        url = "https://play.google.com/store/apps/details?id=" + myPackageName;
                    }

                    // Open the app page in Google Play store:
                    final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    activity.startActivity(intent);
                }
            });
        }

        TextView msg = (TextView)findViewById(R.id.dialog_message);
        msg.setText(message);
    }

    public CustomDialog(final Context context, final View.OnClickListener mRequestOnClickListener,
            int total) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 배경 투명하게
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.dialog_point_check);

        final CheckBox cb1 = (CheckBox)findViewById(R.id.point_terms1);
        final CheckBox cb2 = (CheckBox)findViewById(R.id.point_terms2);
        final CheckBox cb3 = (CheckBox)findViewById(R.id.point_terms3);
        final CheckBox cb4 = (CheckBox)findViewById(R.id.point_terms4);
        final CheckBox cb5 = (CheckBox)findViewById(R.id.point_terms5);
        final CheckBox cb6 = (CheckBox)findViewById(R.id.point_terms6);


        TextView give = (TextView)findViewById(R.id.dialog_point_check_give_money);

        give.setText(String.format("지급 예정 금액 : %,d원", total));

        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb1.isChecked() && cb2.isChecked() && cb3.isChecked() && cb4.isChecked()
                        && cb5.isChecked() && cb6.isChecked() ) {
                    mRequestOnClickListener.onClick(v);
                    dismiss();
                } else {
                    Toast.makeText(context, "주의사항을 읽고 모두 동의하여 주세요", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
