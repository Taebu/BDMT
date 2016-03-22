
package kr.co.cashqc.gcm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.Character.UnicodeBlock;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class Util {

    public static final String APP_SAVE_NAME = "Okdoublecoupon_save";

    // /////////////////////////////////////////////////////////////////////////////////////////////////
    // 공통사용 UTIL
    // /////////////////////////////////////////////////////////////////////////////////////////////////

    // 한글검사
    public static boolean checkHangul(String str) {
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            Character.UnicodeBlock unicodeBlock = Character.UnicodeBlock.of(ch);

            if (UnicodeBlock.HANGUL_SYLLABLES.equals(unicodeBlock)
                    || UnicodeBlock.HANGUL_COMPATIBILITY_JAMO.equals(unicodeBlock)
                    || UnicodeBlock.HANGUL_JAMO.equals(unicodeBlock)) {
                return true;
            }
        }
        return false;
    }

    // 특수문자
    public static boolean checkSpecialCharacter(String str) {
        Pattern ps = Pattern.compile("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]");
        if (ps.matcher(str).find()) {
            return true;
        }
        return false;
    }

    // 영문
    public static boolean checkEn(String str) {
        Pattern ps = Pattern.compile("[a-zA-Z]");
        if (ps.matcher(str).find()) {
            return true;
        }
        return false;
    }

    // 숫자
    public static boolean checkNum(String str) {
        Pattern ps = Pattern.compile("[0-9]");
        if (ps.matcher(str).find()) {
            return true;
        }
        return false;
    }

    // 토스트 메시지
    public static void toast(Context context, String msg, int sel) {
        Log.d("!!!Util.Class!!!", "--------------toast----------------");
        if (sel == 1)
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    // 토스트 메시지
    public static Toast toastImage(Context context, int imageRes) {
        Log.d("!!!Util.Class!!!", "--------------toast----------------");
        ImageView view = new ImageView(context);
        view.setImageResource(imageRes);
        Toast toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(view);
        return toast;
    }

    // 토스트 메시지
    public static Toast toastLayout(Context context, int layoutRes, int textRes, String id) {
        Log.d("!!!Util.Class!!!", "--------------toast----------------");
        LayoutInflater inflater = (LayoutInflater)context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(layoutRes, null);
        TextView txtId = (TextView)view.findViewById(textRes);
        txtId.setText(id);
        Toast toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setView(view);
        return toast;
    }

    public static String getEuckr(String str) {
        Log.d("!!!Util.Class!!!", "--------------getEuckr----------------");

        try {
            return URLEncoder.encode(str, "euc-kr");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static String getUTF8(String str) {
        Log.d("!!!Util.Class!!!", "--------------getUTF8----------------");

        try {
            return URLEncoder.encode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static String enCoderUTF(String str) {
        String enStr = "";
        try {
            enStr = java.net.URLEncoder.encode(new String(str.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return enStr;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap
                .createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 13;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    // SharedPreferences
    public static void saveSharedPreferences_string(Context context, String key, String text) {
        Log.d("!!!Util.Class!!!", "--------------saveSharedPreferences----------------");
        Log.d("!!!Util.Class!!!", "key : " + key + " text : " + text);

        SharedPreferences pref = context.getSharedPreferences(APP_SAVE_NAME, Service.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(key, text);
        edit.commit();
    }

    public static void saveSharedPreferences_int(Context context, String key, int text) {
        Log.d("!!!Util.Class!!!", "--------------saveSharedPreferences----------------");

        SharedPreferences pref = context.getSharedPreferences(APP_SAVE_NAME, Service.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putInt(key, text);
        edit.commit();
    }

    public static void saveSharedPreferences_long(Context context, String key, long text) {
        Log.d("!!!Util.Class!!!", "--------------saveSharedPreferences----------------");

        SharedPreferences pref = context.getSharedPreferences(APP_SAVE_NAME, Service.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putLong(key, text);
        edit.commit();
    }

    public static void saveSharedPreferences_boolean(Context context, String key, boolean text) {
        Log.d("!!!Util.Class!!!", "--------------saveSharedPreferences----------------");

        SharedPreferences pref = context.getSharedPreferences(APP_SAVE_NAME, Service.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putBoolean(key, text);
        edit.commit();
    }

    public static boolean loadSharedPreferencesBoolean(Context context, String key) {
        Log.d("!!!Util.Class!!!", "--------------loadSharedPreferences----------------");

        SharedPreferences pref = context.getSharedPreferences(APP_SAVE_NAME, Service.MODE_PRIVATE);
        return pref.getBoolean(key, false);
    }

    public static Long loadSharedPreferencesLong(Context context, String key) {
        Log.d("!!!Util.Class!!!", "--------------loadSharedPreferences----------------");

        SharedPreferences pref = context.getSharedPreferences(APP_SAVE_NAME, Service.MODE_PRIVATE);
        return pref.getLong(key, -1);
    }

    public static String loadSharedPreferences(Context context, String key) {
        Log.d("!!!Util.Class!!!", "--------------loadSharedPreferences----------------");
        SharedPreferences pref = context.getSharedPreferences(APP_SAVE_NAME, Service.MODE_PRIVATE);
        return pref.getString(key, null);
    }

    public static ArrayList<String> loadSharedPreferencesArrayList(Context context, String key) {
        Log.d("!!!Util.Class!!!", "--------------loadSharedPreferences----------------");

        SharedPreferences pref = context.getSharedPreferences(APP_SAVE_NAME, Service.MODE_PRIVATE);

        ArrayList<String> addressList = new ArrayList<String>();

        try {
            String[] addresses = pref.getString(key, null).split("|");
            Collections.addAll(addressList, addresses);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return addressList;
    }

    // read assets text
    public static String loadAssetsTextFile(Context context, String fileName) {
        Log.d("!!!Util.Class!!!", "--------------loadAssetsTextFile----------------");

        String text = null;
        AssetManager assetManager = context.getAssets();

        try {
            InputStream is = assetManager.open(fileName, AssetManager.ACCESS_BUFFER);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            text = new String(buffer);
        } catch (IOException e) {
        }

        return text;
    }

    // 경고 팝업
    public static void showDialog(final Activity activity, String title, String text,
            final DialogCallBack callBack) {
        Log.d("!!!Util.Class!!!", "--------------showDialog----------------");

        AlertDialog.Builder ad = new AlertDialog.Builder(activity);
        ad.setTitle(title);
        ad.setMessage(text);
        DialogInterface.OnClickListener dialogButListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.d("!!!Util.Class!!!", "--------------showDialog.onClick----------------");

                activity.setResult(Activity.RESULT_OK);
                dialog.dismiss();
                if (callBack != null)
                    callBack.onOk();
            }
        };
        ad.setPositiveButton("확인", dialogButListener);
        ad.create();
        ad.show();
    }

    public interface DialogCallBack {
        public void onOk();
    }

    // 경고 팝업
    public static void showDialog_normal(final Activity activity, String title, String text) {

        AlertDialog.Builder ad = new AlertDialog.Builder(activity);
        ad.setTitle(title);
        ad.setMessage(text);
        DialogInterface.OnClickListener dialogButListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };
        ad.setPositiveButton("확인", dialogButListener);
        ad.create();
        ad.show();
    }

    // 옵션 선택 팝업
    public static void optionDialog(final Activity activity, String title, String[] button,
            String[] items, int checkedIdx, final OptionDialogCallback callBack) {
        Log.d("!!!Util.Class!!!", "--------------optionDialog----------------");

        AlertDialog.Builder ad = new AlertDialog.Builder(activity);
        ad.setTitle(title);
        ad.setSingleChoiceItems(items, checkedIdx, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Log.d("!!!Util.Class!!!", "--------------optionDialog.onClick----------------");

                dialog.dismiss();
                if (callBack != null)
                    callBack.onSelected(item);
            }
        });
        if (button != null) {
            for (int i = 0; i < button.length; i++) {
                ad.setPositiveButton(button[i], new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        Log.d("!!!Util.Class!!!",
                                "--------------optionDialog.onClick----------------");

                        dialog.dismiss();
                        if (callBack != null)
                            callBack.onPositiveBotton(item);
                    }
                });
            }
        }
        ad.setOnCancelListener(new AlertDialog.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {

                if (dialog != null) {
                    dialog.cancel();
                    callBack.OnCancel();
                }

                if (callBack != null)
                    callBack.onPositiveBotton(0);
            }
        });
        ad.create();
        ad.show();
    }

    public interface OptionDialogCallback {
        public void onSelected(int idx);

        public void onPositiveBotton(int idx);

        public void OnCancel();
    }

    // 둘중에 선택 팝업
    public static void questionDialog(final Activity activity, String title, String msg,
            String ch1, String ch2, final QuestionDialogCallback callBack) {
        Log.d("!!!Util.Class!!!", "--------------questionDialog----------------");

        AlertDialog.Builder ad = new AlertDialog.Builder(activity);
        ad.setTitle(title);
        ad.setMessage(msg);
        ad.setCancelable(true);
        ad.setPositiveButton(ch1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d("!!!Util.Class!!!", "--------------questionDialog.onClick_1----------------");

                // Action for 'Yes' Button
                dialog.dismiss();
                if (callBack != null)
                    callBack.onSelected(dialog, 1);
            }
        });
        ad.setNegativeButton(ch2, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d("!!!Util.Class!!!", "--------------questionDialog.onClick_2----------------");

                // Action for 'NO' Button
                dialog.dismiss();
                if (callBack != null)
                    callBack.onSelected(dialog, 0);
            }
        });
        ad.setOnCancelListener(new AlertDialog.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {

                if (dialog != null)
                    dialog.dismiss();
                if (callBack != null)
                    callBack.onSelected(dialog, 0);
            }
        });
        ad.create();
        ad.show();
    }

    public interface QuestionDialogCallback {
        public void onSelected(DialogInterface dialog, int num);
    }

    // 레이팅 팝업
    public static void ratingDialog(final Activity activity, String title, String msg,
            final RatingBarDialogCallback callBack) {
        Log.d("!!!Util.Class!!!", "--------------ratingDialog----------------");

        LinearLayout ly = new LinearLayout(activity);
        ly.setOrientation(LinearLayout.VERTICAL);
        ly.setGravity(Gravity.CENTER);

        final RatingBar ratingBar = new RatingBar(activity);
        ly.addView(ratingBar, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));

        AlertDialog.Builder ad = new AlertDialog.Builder(activity);
        ad.setTitle(title);
        ad.setView(ly);
        ad.setCancelable(true);
        ad.setMessage(msg);
        ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d("!!!Util.Class!!!", "--------------ratingDialog.onClick_1----------------");

                // Action for 'Yes' Button
                dialog.dismiss();
                if (callBack != null)
                    callBack.onSetRating(dialog, ratingBar.getRating());
            }
        });
        ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d("!!!Util.Class!!!", "--------------ratingDialog.onClick_2----------------");

                // Action for 'NO' Button
                dialog.dismiss();
                if (callBack != null)
                    callBack.onCancel(dialog);
            }
        });
        ad.setOnCancelListener(new AlertDialog.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {

                if (dialog != null)
                    dialog.dismiss();
                if (callBack != null)
                    callBack.onCancel(dialog);
            }
        });
        ad.create();
        ad.show();
    }

    public interface RatingBarDialogCallback {
        public void onSetRating(DialogInterface dialog, float num);

        public void onCancel(DialogInterface dialog);
    }

    // 값 입력 팝업
    public static void editDialog(final Activity activity, String title, String msg, String button,
            final EditDialogCallback callBack) {

        LinearLayout ly = new LinearLayout(activity);
        ly.setOrientation(LinearLayout.VERTICAL);
        ly.setGravity(Gravity.CENTER);

        final EditText editText = new EditText(activity);
        if (msg != null)
            ly.addView(editText, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT));
        else {
            float density = activity.getResources().getDisplayMetrics().density;
            editText.setGravity(Gravity.LEFT | Gravity.TOP);
            ly.addView(editText, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
                    (int)(50 * density)));
        }

        AlertDialog.Builder ad = new AlertDialog.Builder(activity);
        ad.setTitle(title);
        ad.setView(ly);
        ad.setCancelable(true);
        ad.setMessage(msg);
        ad.setPositiveButton(button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Action for 'Yes' Button
                dialog.dismiss();
                if (callBack != null)
                    callBack.onEditOk(dialog, editText.getText().toString());
            }
        });
        ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Action for 'NO' Button
                dialog.dismiss();
                if (callBack != null)
                    callBack.onCancel(dialog);
            }
        });
        ad.setOnCancelListener(new AlertDialog.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                if (dialog != null)
                    dialog.dismiss();
                if (callBack != null)
                    callBack.onCancel(dialog);
            }
        });
        ad.create();
        ad.show();
    }

    public interface EditDialogCallback {
        public void onEditOk(DialogInterface dialog, String editText);

        public void onCancel(DialogInterface dialog);
    }

    // 스트링형 리소스값에서 비트맵 얻기
    public static Bitmap getResBitmap(Context context, String resIdString) {
        Log.d("!!!Util.Class!!!", "--------------getResBitmap----------------");

        Resources res = context.getResources();
        int imgId = Util.getResID(context, resIdString);
        Bitmap bmp = BitmapFactory.decodeResource(res, imgId);
        return bmp;
    }

    public static Bitmap getBitmap(Context context, int resID) {
        Log.d("!!!Util.Class!!!", "--------------getBitmap----------------");

        Resources res = context.getResources();
        Bitmap bmp = BitmapFactory.decodeResource(res, resID);
        return bmp;
    }

    public static Bitmap getBitmap(String bmpName) {
        Log.d("!!!Util.Class!!!", "--------------getBitmap----------------");

        // Log.d("img path",bmpName);
        Bitmap bmp = BitmapFactory.decodeFile(bmpName);
        return bmp;
    }

    public static Bitmap getBitmapFromAsset(Context context, String bmpName) {
        Log.d("!!!Util.Class!!!", "--------------getBitmapFromAsset----------------");

        AssetManager assetManager = context.getResources().getAssets();
        try {
            InputStream is = assetManager.open(bmpName, AssetManager.ACCESS_BUFFER);
            if (is != null) {
                return BitmapFactory.decodeStream(is);
            }
        } catch (IOException e) {
            ;
        }

        return null;
    }

    // 비트맵 리사이징
    public static Bitmap setScale(Context context, Bitmap oldBmp) {
        Log.d("!!!Util.Class!!!", "--------------setScale----------------");

        float density = context.getResources().getDisplayMetrics().density;

        Matrix matrix = new Matrix();
        matrix.postScale(density, density);
        // matrix.postRotate(45);

        Bitmap resizedBitmap = Bitmap.createBitmap(oldBmp, 0, 0, oldBmp.getWidth(),
                oldBmp.getHeight(), matrix, true);

        return resizedBitmap;
    }

    public static Drawable getDrawable(Context context, int resID) {
        Log.d("!!!Util.Class!!!", "--------------getDrawable----------------");

        return new BitmapDrawable(getBitmap(context, resID));
    }

    // 스트링형 값에서 리소스 ID 얻기
    public static int getResID(Context context, String resIDString) {
        Log.d("!!!Util.Class!!!", "--------------getResID----------------");

        // ex : resIDString = "drawable/logo" 일 경우..
        // path = "drawable"
        // resName = "logo"
        int resId;
        String path = null;
        String resName = null;

        int firstIndex = resIDString.lastIndexOf("/");
        if (firstIndex >= 0)
            resName = resIDString.substring(firstIndex + 1);

        if (resIDString.charAt(0) == '@')
            path = resIDString.substring(1, firstIndex);
        else
            path = resIDString.substring(0, firstIndex);

        resId = context.getResources().getIdentifier(resName, path, context.getPackageName());
        return resId;
    }

    // 문자열 스트링에서 소수점 아래 자릿수 잘라내기
    public static String getStringToFloat(String string, int limitFloat) {
        Log.d("!!!Util.Class!!!", "--------------getStringToFloat----------------");

        if (string == null)
            return null;

        int dotCharPos = string.lastIndexOf(".");
        String valueStr = string;
        if (dotCharPos >= 0)
            valueStr = string.substring(0, dotCharPos + limitFloat + 1);

        return valueStr;
    }

    // 자릿수 콤마 삽입
    public static String setCommaNumber(String num) {
        Log.d("!!!Util.Class!!!", "--------------setCommaNumber----------------");

        if (num == null || num.length() <= 0)
            return null;

        int cnt = 0;
        int len = num.length();
        int firstLen = len % 3;
        if (firstLen == 0)
            firstLen = 3;

        String returnValue = "";

        while (cnt < len) {
            int addLen = 3;
            if (cnt == 0)
                addLen = firstLen;

            returnValue += num.substring(cnt, cnt + addLen);

            cnt += addLen;

            if (cnt < len)
                returnValue += ",";
        }

        return returnValue;
    }

    // 문자열 잘라서 배열로 리턴
    public static String[] splite(String srcStr, String token) {
        Log.d("!!!Util.Class!!!", "--------------splite----------------");

        int cnt = 0;
        StringTokenizer st = new StringTokenizer(srcStr, token);
        String retArray[] = new String[st.countTokens()];
        while (st.hasMoreTokens()) {
            String str = st.nextToken();
            retArray[cnt] = str;
            cnt++;
        }

        return retArray;
    }

    // 큰 단위 수 연산 - 곱
    public static String getMultiply(String numStr, String numStr2) {
        Log.d("!!!Util.Class!!!", "--------------getMultiply----------------");

        if (!(numStr != null && numStr.length() > 0 && numStr2 != null && numStr2.length() > 0))
            return null;

        BigInteger op1 = new BigInteger(numStr);
        BigInteger op2 = new BigInteger(numStr2);
        BigInteger mul = op1.multiply(op2);

        return mul.toString();
    }

    // 중앙 그리기
    public static void drawRectImage(Canvas canvas, Bitmap img, Rect destRect) {
        Log.d("!!!Util.Class!!!", "--------------drawRectImage----------------");

        if (canvas == null || img == null || destRect == null)
            return;

        int x = destRect.left;
        int y = destRect.top;
        int img_width = img.getWidth();
        int img_height = img.getHeight();
        int rect_width = destRect.width();
        int rect_height = destRect.height();

        if (img_width < rect_width)
            x = (rect_width - img_width) / 2;
        if (img_height < rect_height)
            y = (rect_height - img_height) / 2;
        if (img_width > rect_width)
            x = x - ((img_width - rect_width) / 2);
        if (img_height > rect_height)
            y = y - ((img_height - rect_height) / 2);

        canvas.drawBitmap(img, x, y, null);
    }

    public static void draw(Canvas canvas, Bitmap img, int desX, int desY) {
        Log.d("!!!Util.Class!!!", "--------------draw_1----------------");

        canvas.drawBitmap(img, desX, desY, null);
    }

    public static void draw(Canvas canvas, Bitmap img, int desX, int desY, int sizeX, int sizeY) {
        Log.d("!!!Util.Class!!!", "--------------draw_1----------------");

        canvas.drawBitmap(img, desX, desY, null);
    }

    // draw
    public static void draw(Canvas canvas, Bitmap img, int desX, int desY, int srcX, int srcY,
            int w, int h) {
        Log.d("!!!Util.Class!!!", "--------------draw_2----------------");

        canvas.save(Canvas.CLIP_SAVE_FLAG);
        canvas.clipRect(desX, desY, desX + w, desY + h);
        canvas.drawBitmap(img, desX - srcX, desY - srcY, null);
        canvas.restore();
    }

    public static void drawText(Canvas canvas, String text, int x, int y, int color, int fontSize) {
        Log.d("!!!Util.Class!!!", "--------------drawText----------------");

        if (text == null || text.length() <= 0)
            return;

        Paint paint = new Paint();
        paint.setColor(color);
        paint.setTextSize(fontSize);
        paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
        canvas.drawText(text, x, y, paint);
    }

    public static int dipToPx(Context context, int dip) {
        Log.d("!!!Util.Class!!!", "--------------dipToPx----------------");

        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dip * scale + 0.5f);
    }

    // 파일 오픈

    // sd카드 경로 반환
    public static String getSDCardPath() {
        Log.d("!!!Util.Class!!!", "--------------getSDCardPath----------------");

        String ext = Environment.getExternalStorageState();
        if (ext.equals(Environment.MEDIA_MOUNTED))
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        else
            return null;
    }

    // sd카드 파일 있는지 체크
    public static boolean SDCardFileCheck(String path) {
        Log.d("!!!Util.Class!!!", "--------------SDCardFileCheck----------------");

        try {
            FileInputStream is = new FileInputStream(path);
            is.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // 파일 저장
    public static boolean saveFile2(String sMsg, String outputPath) {
        Log.d("!!!Util.Class!!!", "--------------saveFile----------------");

        boolean flag = true;
        FileOutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(outputPath);
            outputStream.write(sMsg.getBytes());
        } catch (FileNotFoundException e1) {
            flag = false;
            e1.printStackTrace();
        } catch (IOException e) {
            flag = false;
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return flag;
    }

    // 파일 저장
    public static boolean saveFile(InputStream inputstream, String outputPath) {
        Log.d("!!!Util.Class!!!", "--------------saveFile----------------");

        long filesize = 0;
        try {
            filesize = inputstream.available();
        } catch (IOException e) {
        }

        byte tempData[] = new byte[(int)filesize];
        boolean flag = true;
        FileOutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(outputPath);
            int i;
            while ((i = inputstream.read(tempData)) != -1) {
                outputStream.write(tempData, 0, i);
            }
        } catch (FileNotFoundException e1) {
            flag = false;
            e1.printStackTrace();
        } catch (IOException e) {
            flag = false;
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return flag;
    }

    public static String getPhoneNumber(Context context) {

        TelephonyManager mgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

        try {

            String num = mgr.getLine1Number();

            if (num.startsWith("+82")) {
                num = num.replace("+82", "0");
            }

            if (num.contains("-")) {
                num = num.replaceAll("-", "");
            }

            return num.trim();

        } catch (NullPointerException e) {
//            e.printStackTrace();
            return "";
        }
    }

    /**
     * 휴대폰 번호 가져오기
     * 
     * @param context
     * @return Device phone number String
     */
    public static String getDevicePhoneNumber(Context context) {
        TelephonyManager tm = null;
        tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

        return tm.getLine1Number();
    }

    public static boolean reNameFile(File file, File new_name) {

        if (file != null && file.exists() && file.renameTo(new_name)) {
            return true;
        }

        return false;
    }

    static Bitmap imgBitmap = null;

    static HttpURLConnection conn;

    static BufferedInputStream bis;

    // Image를 URL에서 Bitmap으로 가져오기
    public static Bitmap getImageFromURL(final String imageURL) {
        Log.d("Util", "getImageFromURL - imageURL : " + imageURL);

        imgBitmap = null;
        conn = null;
        bis = null;

        if (imageURL == null || imageURL.length() < 5) {
            return null;
        }

        Thread thread = new Thread(new Runnable() {

            public void run() {
                try {
                    URL url = new URL(imageURL);
                    conn = (HttpURLConnection)url.openConnection();
                    conn.connect();

                    int nSize = conn.getContentLength();
                    bis = new BufferedInputStream(conn.getInputStream(), nSize);
                    imgBitmap = BitmapFactory.decodeStream(bis);
                    Log.w("Util", "getImageFromURL - imgBitmap : " + imgBitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                        }
                    }
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.d("Util", "getImageFromURL - imgBitmap : " + imgBitmap);

        return imgBitmap;
    }

    // Bitmap 관련 객체 사이즈에 맞게 생성?? - SD_CARD
    public static Bitmap bitmapFromSdCard(String filePath) {
        int scale = 1;

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, opts);

        if (opts.outHeight > MAX_IMAGE_SIZE || opts.outWidth > MAX_IMAGE_SIZE) {
            scale = (int)Math.pow(
                    2,
                    (int)Math.round(Math.log(MAX_IMAGE_SIZE
                            / (double)Math.max(opts.outHeight, opts.outWidth))
                            / Math.log(0.5)));
        }

        if (scale > 1)
            scale /= 2;

        opts.inJustDecodeBounds = false;
        opts.inSampleSize = scale;

        return BitmapFactory.decodeFile(filePath, opts);
    }

    private static final int MAX_IMAGE_SIZE = 480;

    public static int getMaxImageSize() {
        return MAX_IMAGE_SIZE;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager manager = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

//        return mobile.isConnected() || wifi.isConnected();
        return true;
    }

}
