package mem.edu.meaningful;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by erikllerena on 9/26/16.
 */
public class ConjugateFragment extends Fragment{

    private AppPreferences _sPref;
    private WebView webView;
    private static final String LOG_TAG = ConjugateFragment.class.getSimpleName();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.conjugate_page, container, false);
        webView = (WebView) v.findViewById(R.id.webView);

        _sPref = new AppPreferences(getContext());
        if(_sPref.getSmsBody("sound")!=""){
            new FetchImgList().execute(_sPref.getSmsBody("key"));
        }
        return v;
    }

    public class FetchImgList extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String ConjugationStr = null;
            try {

                final String WORD_BASE_URL = strings[0];

                String Verbixurl = "http://api.verbix.com/conjugator/html?language=eng&tableurl=http://tools.verbix.com/webverbix/personal/template.htm&verb=";

//processing this url
//http://api.verbix.com/conjugator/html?language=eng&tableurl=http://tools.verbix.com/webverbix/personal/template.htm&verb=go

                StringBuilder urlChartBuilder = new StringBuilder();

                urlChartBuilder.append(Verbixurl)
                        .append(WORD_BASE_URL);


                URL url = new URL(urlChartBuilder.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {

                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {

                    return null;
                }
                ConjugationStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);

                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return ConjugationStr;
        }

        @Override
        protected void onPostExecute(String result) {

            //this text is remove below
            //<a href="http://www.kolumbus.fi/toti/conjugue/test.htm">Back to the form</a>
            //This service is provided by <a href=http://www.verbix.com/ class=set2>Verbix</a>

            Document doc = Jsoup.parse(result);
            Elements e = doc.select("a[href]").remove();
            String content = doc.html().replaceAll(e.text(), "");
            content = doc.html().replaceAll("This service is provided by", "");
            webView.loadDataWithBaseURL(null, content, "text/html", "UTF-8", null);
        }
    }
}
