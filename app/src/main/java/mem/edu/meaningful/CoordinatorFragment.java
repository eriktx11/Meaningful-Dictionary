package mem.edu.meaningful;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by rufflez on 6/21/15.
 */
public class CoordinatorFragment extends Fragment {

    public static RecyclerView recyclerView;

    public static List<String> data = new ArrayList<>();
    private static AppPreferences _sPref;
    public static final String FGTAG = "coordFrag";
    private static Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.coordinator_layout, container, false);
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView);
        setupRecyclerView(recyclerView);
        _sPref = new AppPreferences(getContext());
        new fetchData().execute(_sPref.getSmsBody("key"));
        return rootView;
    }

    private void setupRecyclerView(RecyclerView recyclerView){
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(new SimpleStringRecyclerViewAdapter(getActivity(),
                data));
    }

    public static class SimpleStringRecyclerViewAdapter extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder>{
        private List<String> mValues;
        public static class ViewHolder extends RecyclerView.ViewHolder {

            public final View mView;
            public final TextView mTextView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mTextView = (TextView) view.findViewById(android.R.id.text1);
                mTextView.setTypeface(MainActivity.FONT_HEADINGS);
            }
        }

        public String getValueAt(int position) {
            return mValues.get(position);
        }

        public SimpleStringRecyclerViewAdapter(Context context, List<String> items) {
            mContext = context;
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            Spanned sp = Html.fromHtml( mValues.get(position) );

            holder.mTextView.setText(sp);
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(v, Jsoup.parse(mValues.get(position)).text(), Snackbar.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

    }

    public static class fetchData extends AsyncTask<String, Void, String> {

        ProgressDialog pDialog;
        NodeList nodelist;
        NodeList nodeSound;
        NodeList nodeTitles;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressbar
            pDialog = new ProgressDialog(mContext);
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

                nodelist = doc.getElementsByTagName("def");
                nodeSound = doc.getElementsByTagName("wav");
                nodeTitles = doc.getElementsByTagName("entry");
                if(nodeSound.getLength()>0){
                    _sPref.saveSmsBody("sound", nodeSound.item(0).getTextContent()
                            .trim());
                }else {_sPref.saveSmsBody("sound", "");}

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

            data.clear();

            for (int h = 0; h < nodeTitles.getLength(); h++) {

                NodeList childTitle = nodeTitles.item(h).getChildNodes();

                for (int i = 0; i < childTitle.getLength(); i++) {

                    Node tNode = childTitle.item(i);
                    if ("ew".equals(tNode.getNodeName())) {
                        data.add("<br><h2>"+childTitle.item(i).getTextContent()
                                .trim().replace("-"," ")+"</h2>");
                    }

                    if ("def".equals(tNode.getNodeName())) {
                        NodeList childList = childTitle.item(i).getChildNodes();
                        for (int j = 0; j < childList.getLength(); j++) {
                        Node childNode = childList.item(j);
                        if ("dt".equals(childNode.getNodeName())) {
                            data.add(childList.item(j).getTextContent()
                                    .trim());
                        }
                    }
                    }
                }
            }

            if(!_sPref.getSmsBody("sound").equals("")) {
                MainActivity.adapter.addFrag(new ImagesFragment(), "IMAGES");
                MainActivity.adapter.addFrag(new ConjugateFragment(), "CONJUGATION");
                MainActivity.adapter.addFrag(new SoundFragment(), "SOUND");
                MainActivity.adapter.notifyDataSetChanged();
            }

            try {
                pDialog.dismiss();
            }catch (IllegalArgumentException e){}

            recyclerView.getAdapter().notifyDataSetChanged();
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
