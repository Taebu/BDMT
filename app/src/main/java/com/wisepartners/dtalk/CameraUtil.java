
package com.wisepartners.dtalk;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.wisepartners.dtalk.gcm.Util.getPhoneNumber;

/**
 * @ Jung-Hum Cho Created by anp on 15. 10. 21..
 */
public class CameraUtil {

    private final String TAG = getClass().getSimpleName();

    public CameraUtil(Activity activity) {

        mActivity = activity;

    }

    private Activity mActivity;

    public static final int TAKE_CAMERA = 0;

    public static final int TAKE_ALBUM = 1;

    public static final int CROP_CAMERA = 2;

    public static final int CROP_ALBUM = 3;

    public void takeCamera() {

        // 외부저장소 경로
        String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        // 디렉토리 이름, 경로
        String directoryName = "cashq";
        String directoryPath = sdcardPath + File.separator + directoryName;

        // 파일 이름, 경로
        String fileName = "user_" + String.valueOf(System.currentTimeMillis()) + "_"
                + getPhoneNumber(mActivity) + ".jpg";
        String filePath = sdcardPath + File.separator + directoryName + File.separator + fileName;

        // 저장 디렉토리 지정 및 생성
        File directoryPathFile = new File(directoryPath);
        directoryPathFile.mkdir();

        // 파일 이름 지정
        File file = new File(filePath);
        Uri outputFileUri = Uri.fromFile(file);

        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra("filePath", filePath);

        mActivity.setResult(mActivity.RESULT_OK, intent);
        mActivity.startActivityForResult(intent, TAKE_CAMERA);

    }

    public void takeAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);

        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        mActivity.startActivityForResult(intent, TAKE_ALBUM);
    }

    public void takePhoto(Intent data, ImageView imageView) {

        if (data == null) {
            return;
        }

        Uri uri = data.getData();

        Log.e(TAG, "uri : " + uri);

        final String uriPath = getRealPathFromUri(uri);
        Log.e(TAG, "uriPath : " + uriPath);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeFile(uriPath, options);
        imageView.setImageBitmap(bitmap);

        Bitmap bitmap1 = decodeBitmapFile(uriPath);

        saveBitmapToJpegFile(bitmap, uriPath);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UploadToServer uploadToServer = new UploadToServer(mActivity, 0);
                uploadToServer.uploadFile(uriPath);

            }
        });

        // File file = new File(mImageCaptureUri.getPath());
        // if (file.exists()) {
        // file.delete();
        // }
    }

    public void crop(Intent data) {
        // if (requestCode == 1) {
        Uri uri = data.getData();
        // }

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        intent.putExtra("outputX", 90);
        intent.putExtra("outputY", 90);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);

        // mActivity.startActivityForResult(intent, );

    }

    private String getRealPathFromUri(Uri contentUri) {
        String[] proj = {
            MediaStore.Images.Media.DATA
        };
        Cursor cursor = mActivity.managedQuery(contentUri, proj, null, null, null);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(columnIndex);
    }

    private Bitmap decodeBitmapFile(String filePath) {

        final int IMAGEMAXSIZE = 1 * 1024 * 1024;

        File file = new File(filePath);

        if (!file.exists()) {
            Log.e("file !!!", "null  !!!");
            return null;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(filePath, options);

        if (options.outHeight * options.outWidth >= IMAGEMAXSIZE) {
            options.inSampleSize = (int)Math.pow(
                    2,
                    (int)Math.round(Math.log(IMAGEMAXSIZE
                            / (double)Math.max(options.outHeight, options.outWidth))
                            / Math.log(0.5)));
        }

        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    private void saveBitmapToJpegFile(Bitmap bitmap, String filePath) {

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, fileOutputStream);
            fileOutputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//0x000000f4
}
