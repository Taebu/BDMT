
package com.anp.bdmt;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * @author Jung-Hum Cho Created by anp on 15. 1. 5..
 */
public class AddressListDialog extends Dialog {

    public AddressListDialog(final Context context, final View.OnClickListener onClickListener) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 배경 투명하게
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.dialog_address);

        final EditText dong = (EditText)findViewById(R.id.dialog_address_field);

        final ListView listView = (ListView)findViewById(R.id.address_list);

        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = listView.getCheckedItemPosition();

                if (position != ListView.INVALID_POSITION) {
                    AddressData item = (AddressData)listView.getItemAtPosition(position);
                    v.setTag(R.id.zipcode, item.getZipcode());
                    v.setTag(R.id.address1, item.getAddress());
                    v.setTag(R.id.address2, "");
                    v.setTag(R.id.full_address, item.getResult());
                    onClickListener.onClick(v);
                    dismiss();
                }
            }
        };

        findViewById(R.id.address_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(dong.getText().toString())) {
                    Toast.makeText(context, "읍, 면, 동을 입력하세요", Toast.LENGTH_SHORT).show();
                } else {

                    new AsyncTask<String, Void, ArrayList<AddressData>>() {

                        CustomDialog mLoading = new CustomDialog(context);

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            if (!mLoading.isShowing()) {
                                mLoading.show();
                            }
                        }

                        @Override
                        protected ArrayList<AddressData> doInBackground(String... params) {

                            String dong = params[0].trim();

                            final String apiUrl = "http://biz.epost.go.kr/KpostPortal/openapi";

                            final String myApiKey = "a314996f3c4a87fb71420174091437";

                            ArrayList<AddressData> addressInfo = new ArrayList<AddressData>();

                            HttpURLConnection conn = null;

                            try {

                                StringBuilder sb = new StringBuilder(3);
                                sb.append(apiUrl);
                                sb.append("?regkey=").append(myApiKey);
                                sb.append("&target=post");
                                sb.append("&query=").append(URLEncoder.encode(dong, "euc-kr"));
                                String query = sb.toString();

                                URL url = new URL(query);
                                conn = (HttpURLConnection)url.openConnection();
                                conn.setRequestProperty("accept-language", "ko");

                                DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance()
                                        .newDocumentBuilder();
                                byte[] bytes = new byte[4096];
                                InputStream in = conn.getInputStream();
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                                while (true) {
                                    int red = in.read(bytes);
                                    if (red < 0) {
                                        break;
                                    }
                                    baos.write(bytes, 0, red);
                                }

                                String xmlData = baos.toString("utf-8");
                                baos.close();
                                in.close();
                                conn.disconnect();

                                Document doc = docBuilder.parse(new InputSource(new StringReader(
                                        xmlData)));
                                Element el = (Element)doc.getElementsByTagName("itemlist").item(0);

                                for (int i = 0; i < el.getChildNodes().getLength(); i++) {

                                    Node node = el.getChildNodes().item(i);

                                    if (!node.getNodeName().equals("item")) {
                                        continue;
                                    }

                                    AddressData data = new AddressData();

                                    String address = node.getChildNodes().item(1).getFirstChild()
                                            .getNodeValue();
                                    String post = node.getChildNodes().item(3).getFirstChild()
                                            .getNodeValue();

                                    data.setAddress(address);
                                    data.setZipcode(post.substring(0, 3) + "-" + post.substring(3));
                                    data.setResult(address + "\n 우편번호 : " + post.substring(0, 3)
                                            + "-" + post.substring(3));

                                    addressInfo.add(data);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                if (conn != null) {
                                    conn.disconnect();
                                }
                            }

                            return addressInfo;
                        }

                        @Override
                        protected void onPostExecute(ArrayList<AddressData> addressInfo) {
                            super.onPostExecute(addressInfo);

                            AddressListAdapter adapter = new AddressListAdapter(context,
                                    addressInfo, mOnClickListener);
                            listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                            listView.setAdapter(adapter);

                            for (AddressData s : addressInfo) {
                                Log.e("zipcode", s.getResult());
                                // adapter.add(s);
                            }
                            if (mLoading.isShowing()) {
                                mLoading.dismiss();
                            }
                        }
                    }.execute(dong.getText().toString());
                }
            }
        });

        findViewById(R.id.ok).setOnClickListener(mOnClickListener);

    }

}
