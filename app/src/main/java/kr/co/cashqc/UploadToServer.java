
package kr.co.cashqc;

/**
 * @ Jung-Hum Cho
 * Created by anp on 15. 10. 16..
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UploadToServer {

    public UploadToServer(final Activity activity, int type) {

        mActivity = activity;

        if (type == 0) {
            // review
            mUploadServerUrl = "http://cashq.co.kr/adm/upload/review/UploadToServer.php";
        } else if (type == 1) {
            // coupon book
            mUploadServerUrl = "http://cashq.co.kr/adm/upload/cbook/UploadToServer.php";
        }

    }

    private final String TAG = getClass().getSimpleName();

    private String mUploadServerUrl;

    private int serverResponseCode = 0;

    private Activity mActivity;

    public void uploadFile(String uri) {

        new UploadTask(uri).execute();

    } // End else block

    private class UploadTask extends AsyncTask<String, Void, Integer> {

        public UploadTask(String uri) {

            fileName = uri;
        }

        private ProgressDialog dialog;

        String fileName;

        HttpURLConnection conn = null;

        DataOutputStream dos = null;

        String lineEnd = "\r\n";

        String twoHyphens = "--";

        String boundary = "*****";

        int bytesRead, bytesAvailable, bufferSize;

        byte[] buffer;

        int maxBufferSize = 5 * 1024 * 1024;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.e("upload", " uri : " + fileName);

            dialog = ProgressDialog.show(mActivity, "", "Uploading file...", true);

            new Thread(new Runnable() {
                public void run() {
                    mActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(mActivity, "uploading started.....", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
                }
            }).start();
        }

        @Override
        protected Integer doInBackground(String... params) {

            File sourceFile = new File(fileName);

            if (!sourceFile.isFile()) {

                dialog.dismiss();

                Log.e("uploadFile", "Source File not exist :" + fileName);

                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(mActivity, "Source File not exist :" + fileName,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                try {
                    // open a URL connection to the Servlet
                    FileInputStream fileInputStream = new FileInputStream(sourceFile);
                    URL url = new URL(mUploadServerUrl);

                    // Open a HTTP connection to the URL
                    conn = (HttpURLConnection)url.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="
                            + boundary);
                    conn.setRequestProperty("uploaded_file", fileName);

                    dos = new DataOutputStream(conn.getOutputStream());

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=uploaded_file; filename="
                            + fileName + "" + lineEnd);

                    dos.writeBytes(lineEnd);

                    // create a buffer of maximum size
                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {

                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    }

                    // send multipart form data necesssary after file data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    // Responses from the server (code and message)
                    serverResponseCode = conn.getResponseCode();
                    String serverResponseMessage = conn.getResponseMessage();

                    Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": "
                            + serverResponseCode);

                    if (serverResponseCode == 200) {

                        mActivity.runOnUiThread(new Runnable() {
                            public void run() {

                                String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                        + fileName;

                                Toast.makeText(mActivity, "File Upload Complete.\n" + msg,
                                        Toast.LENGTH_SHORT).show();

                                Log.i("uploadFile", "File Upload Complete.\n" + msg);
                            }
                        });
                    }

                    // close the streams //
                    fileInputStream.close();
                    dos.flush();
                    dos.close();

                } catch (MalformedURLException ex) {

                    // dialog.dismiss();
                    ex.printStackTrace();

                    mActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(mActivity, "MalformedURLException", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });

                    Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
                } catch (Exception e) {

                    dialog.dismiss();
                    e.printStackTrace();

                    mActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(mActivity, "Got Exception : see logcat ",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.e("Upload file to server Exception", "Exception : " + e.getMessage(), e);
                }
                dialog.dismiss();
                return serverResponseCode;

            }
            return 0;

        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            Log.e(TAG, "Upload Code : " + integer);

            String imageUrl = mUploadServerUrl.substring(0, mUploadServerUrl.lastIndexOf("/")) + "/";
            String imageName = fileName.substring(fileName.lastIndexOf("/")+1);
            Log.e(TAG, "Upload Image : " + imageUrl + imageName);
        }
    }
}
