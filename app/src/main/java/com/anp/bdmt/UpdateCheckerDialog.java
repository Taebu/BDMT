/*

 * Copyright (C) 2014 Pietro Rampini - PiKo Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.anp.bdmt;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.rampo.updatechecker.UpdateChecker;
import com.rampo.updatechecker.store.Store;

/**
 * Builds and show a Dialog if a new update has been found. This is the default
 * Notice. I've used the old AlertDialog API because newer APIs require
 * FragmentActivity.
 *
 * @author Pietro Rampini (rampini.pietro@gmail.com)
 * @see com.rampo.updatechecker.notice.Notice#DIALOG
 */
public class UpdateCheckerDialog extends android.app.Dialog {

    // 메뉴 장바구니 선택
    public UpdateCheckerDialog(final Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 배경 투명하게
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.dialog_custom_update);

        TextView tvLabel = (TextView)findViewById(R.id.dialog_message);

        tvLabel.setText("배달캐시큐의 새 버전이 출시 되었습니다.\n업데이트를 위해 Google Play로 이동합니다.");

        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMarket(context);
                dismiss();
            }
        });
    }

    public static void show(final Context context, final Store store,
            final String versionDownloadable, final int dialogIconResId) {
        try {
            String storeName = context.getString(com.rampo.updatechecker.R.string.googlePlay);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            String appName = null;
            try {
                appName = (String)context.getPackageManager()
                        .getApplicationLabel(
                                context.getPackageManager().getApplicationInfo(
                                        context.getPackageName(), 0));
            } catch (PackageManager.NameNotFoundException ignored) {
            }

            alertDialogBuilder.setTitle(context.getResources().getString(
                    com.rampo.updatechecker.R.string.newUpdateAvailable));

            alertDialogBuilder
                    .setMessage(
                            context.getResources().getString(
                                    com.rampo.updatechecker.R.string.downloadFor, appName,
                                    storeName))
                    .setCancelable(true)
                    .setPositiveButton(
                            context.getString(com.rampo.updatechecker.R.string.dialogPositiveButton),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    goToMarket(context);
                                    dialog.cancel();
                                }
                            })
                    .setNeutralButton(
                            context.getString(com.rampo.updatechecker.R.string.dialogNeutralButton),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                    .setNegativeButton(
                            context.getString(com.rampo.updatechecker.R.string.dialogNegativeButton),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    userHasTappedToNotShowNoticeAgain(context, versionDownloadable);
                                    dialog.cancel();
                                }

                            });
            if (dialogIconResId != 0) {
                alertDialogBuilder.setIcon(dialogIconResId);
            }
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (WindowManager.BadTokenException e) {
            e.printStackTrace();
        }
        /*
         * Happens when the library tries to open a dialog, but the activity is
         * already closed, so generates a NullPointerException,
         * IllegalStateException or BadTokenException. In this way, a force
         * close is avoided.
         */
    }

    private static void userHasTappedToNotShowNoticeAgain(Context mContext,
            String mVersionDownloadable) {
        SharedPreferences prefs = mContext.getSharedPreferences(UpdateChecker.PREFS_FILENAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(UpdateChecker.DONT_SHOW_AGAIN_PREF_KEY + mVersionDownloadable, true);
        editor.commit();
    }

    private static void goToMarket(Context mContext) {
        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                .parse(UpdateChecker.ROOT_PLAY_STORE_DEVICE + mContext.getPackageName())));

    }
}
