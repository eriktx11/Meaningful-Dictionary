package mem.edu.meaningful;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by erikllerena on 9/21/16.
 */
public class XMLdict extends AsyncTask<String, Void, String>{

    ProgressDialog pDialog;
    NodeList nodelist;
    View linearLayout;
    TextView tagWord;

    private Context c;
    public XMLdict (Context context){
        c = context;
    }


    public String[] data=new String[10];

    public interface AsyncResponse {
        void processFinish(String[] output);
    }

    public AsyncResponse delegate = null;

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);

//        _sPref = new AppPreferences(c);
//        new fetchMeaning().execute(_sPref.getSmsBody("key"));
//    }

    //private class fetchMeaning extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressbar
            pDialog = new ProgressDialog(c);
            // Set progressbar title
            pDialog.setTitle("Meaningful Dictionary");
            // Set progressbar message
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            // Show progressbar
            pDialog.show();
        }


        private String getMeaningDataJson(String meaningJsonStr) throws JSONException {


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
            String url = "http://www.dictionaryapi.com/api/v1/references/collegiate/xml/";

            Uri buildUri = Uri.parse(url).buildUpon()
                    .appendPath(strings[0])
                    .appendQueryParameter("key", BuildConfig.OPEN_API_KEY)
                    .build();
            url = buildUri.toString();


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
            int cont = 1;

            for (int i = 0; i < nodelist.getLength(); i++) {
                Node nNode = nodelist.item(i);
                NodeList childList = nodelist.item(i).getChildNodes();


                for (int j = 0; j < childList.getLength(); j++) {

                    Node childNode = childList.item(j);
                    if ("sn".equals(childNode.getNodeName())) {
                        //tagWord = new TextView(c);
                        Element eElement = (Element) childNode;
                       // tagWord.setText(String.valueOf(cont)
//                                .trim());
//                        tagWord.setId(j);
//                        tagWord.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//
//                        ((LinearLayout) linearLayout).addView(tagWord);
//                        cont++;
                    }

                    if ("dt".equals(childNode.getNodeName())) {
                       // TextView meaningWord = new TextView(c);
                        Element eElement = (Element) childNode;
//                        meaningWord.setText(childList.item(j).getTextContent()
//                                .trim());
//                        meaningWord.setId(j);
//                        meaningWord.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//
//                        ((LinearLayout) linearLayout).addView(meaningWord);
                        cont++;
                    }

                    data[cont]=childList.item(j).getTextContent()
                            .trim();
                }
            }
            // Close progressbar
            pDialog.dismiss();
            delegate.processFinish(data);
        }


    // getNode function
    private static String getNode(String sTag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0)
                .getChildNodes();
        Node nValue = (Node) nlList.item(0);
        return nValue.getNodeValue();
    }
}

