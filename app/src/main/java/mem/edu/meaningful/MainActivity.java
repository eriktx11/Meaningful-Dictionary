package mem.edu.meaningful;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import org.apache.http.impl.client.DefaultHttpClient;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {


    TextView textview;

    NodeList nodelist;
    ProgressDialog pDialog;

//    private void setData(String data){
//        textView.setText(data);
//    }


    View linealLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linealLayout =  findViewById(R.id.info);

        //textview = (TextView) findViewById(R.id.textId);
        //String dictWord =
        new FectchWords().execute("seat");
        //textView.setText(dictWord);

    }


    public class FectchWords extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressbar
            pDialog = new ProgressDialog(MainActivity.this);
            // Set progressbar title
            pDialog.setTitle("Meaningful Dictionary");
            // Set progressbar message
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            // Show progressbar
            pDialog.show();
        }


        private String getMeaningDataJson(String meaningJsonStr) throws JSONException{


                Document doc = null;
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                try {

                    DocumentBuilder db = dbf.newDocumentBuilder();

                    InputSource is = new InputSource();
                    is.setCharacterStream(new StringReader(meaningJsonStr));
                    doc = db.parse(is);

                } catch (ParserConfigurationException e) {
                    Log.e("Error: ", e.getMessage());
                    return null;
                } catch (SAXException e) {
                    Log.e("Error: ", e.getMessage());
                    return null;
                } catch (IOException e) {
                    Log.e("Error: ", e.getMessage());
                    return null;
                }

            nodelist = doc.getElementsByTagName("def");

            return null;
        }

        @Override
        protected String doInBackground(String... strings) {


            String xml = null;
            String url = "http://www.dictionaryapi.com/api/v1/references/collegiate/xml/hypocrite?key=3032f934-5c9e-46c7-9226-1bb18657343f";


            try {
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                xml = EntityUtils.toString(httpEntity);


                try {
                    return getMeaningDataJson(xml);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            for (int i = 0; i < nodelist.getLength(); i++) {
                Node nNode = nodelist.item(i);
                NodeList childList = nodelist.item(i).getChildNodes();

                for (int j = 0; j < childList.getLength(); j++) {

                    Node childNode = childList.item(j);
                    if ("dt".equals(childNode.getNodeName())) {
                        TextView valueTV = new TextView(getBaseContext());
                        Element eElement = (Element) childNode;
                        valueTV.setText(childList.item(j).getTextContent()
                                .trim());
                        valueTV.setId(j);
                        valueTV.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                        ((LinearLayout) linealLayout).addView(valueTV);
                    }
                }
            }
            // Close progressbar
            pDialog.dismiss();
        }
    }

    // getNode function
    private static String getNode(String sTag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0)
                .getChildNodes();
        Node nValue = (Node) nlList.item(0);
        return nValue.getNodeValue();
    }


}