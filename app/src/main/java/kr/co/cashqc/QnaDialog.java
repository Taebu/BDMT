
package kr.co.cashqc;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Jung-Hum Cho Created by anp on 15. 1. 5..
 */
public class QnaDialog extends Dialog {

    private int mScore = 0;

    public QnaDialog(final Context context, final String phone) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 배경 투명하게
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.dialog_custom_qna);

        TextView ok = (TextView)findViewById(R.id.qna_ok);
        ok.setText("쓰기");

        final EditText content = (EditText)findViewById(R.id.dialog_qna_content);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new WriteTask().execute(content.getText().toString(), phone);
                dismiss();
            }
        });

    }

    private class WriteTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String content = params[0];
            String phone = params[1];

            // String url =
            // Uri.parse("http://cashq.co.kr/g5/bbs/write_update.php").buildUpon()
            // post.add("mobile_mode", "cashq")
            // post.add("wr_name", "test")
            // post.add("bo_table", "qna_1")
            // post.add("wr_subject", "문의사항")post.add("wr_1", phone)
            // post.add("wr_content", content).build().toString();

            // Log.e("QNADialog", "url : " + url);

            ArrayList<NameValuePair> post = new ArrayList<NameValuePair>();
            post.add(new BasicNameValuePair("mobile_mode", "cashq"));
            post.add(new BasicNameValuePair("wr_name", phone));
            post.add(new BasicNameValuePair("bo_table", "qna_1"));
            post.add(new BasicNameValuePair("wr_subject", "문의사항"));
            post.add(new BasicNameValuePair("wr_1", phone));
            post.add(new BasicNameValuePair("wr_content", content));

            HttpClient client = new DefaultHttpClient();

            HttpParams httpParams = client.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
            HttpConnectionParams.setSoTimeout(httpParams, 5000);

            HttpPost httpPost = new HttpPost("http://cashq.co.kr/g5/bbs/write_update.php");

            try {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(post, "UTF-8");
                httpPost.setEntity(entity);
                client.execute(httpPost);

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // byte[] bytes = EncodingUtils.getBytes(url, "BASE64");

            // String post = EncodingUtils.getString(bytes, "BASE64");
            // Log.e("QNADialog", "post : " + post);

            return null;
        }
    }
}
