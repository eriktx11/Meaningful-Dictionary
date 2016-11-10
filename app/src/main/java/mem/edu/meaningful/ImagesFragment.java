package mem.edu.meaningful;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by erikllerena on 9/23/16.
 */
public class ImagesFragment extends Fragment {

    private AppPreferences _sPref;

    private static final String LOG_TAG = ImagesFragment.class.getSimpleName();
    public static final String FGTAG = "imagesFrag";

    private PosterAdapter mGridAdapter;
    private GridView mGridView;
    private ArrayList<GridItem> mGridData;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if(isConnected) {
        View v = inflater.inflate(R.layout.imgs_grid, container, false);
        mGridView = (GridView) v.findViewById(R.id.imgsGridId);
        _sPref = new AppPreferences(getContext());

//These are back up API in case are needed in the future
//http://images.google.com/search?num=10&hl=en&site‌​=&tbm=isch&source=hp‌​&biw=1366&bih=667&q=‌​cars&oq=cars&gs_l=im‌​g.3..0l10.748.1058.0‌​.1306.4.4.0.0.0.0.16‌​5.209.2j1.3.0...0.0.‌​..1ac.1.8RNsNEqlcZc
//http://api.pixplorer.co.uk/image?word=love&amount=55&size=tb

        //mGridView = inflater.inflate(R.layout.imgs_grid, container, false);
        mGridData = new ArrayList<>();
        mGridAdapter = new PosterAdapter(getContext(), R.layout.one_img, mGridData);
        mGridView.setAdapter(mGridAdapter);

        new FetchImgList().execute(_sPref.getSmsBody("key"));

        return v;
        }
        else {
            Toast.makeText(getContext(), getString(R.string.network_toast), Toast.LENGTH_LONG).show();
            return null;
        }
    }

    public class FetchImgList extends AsyncTask<String, Void, String[]> {


        private String[] getImgDataFromJson(String imagesJsonStr)
                throws JSONException {


            final String PAGES = "hits";
            final String POSTER = "webformatURL";

//            final String OVERVIEW = "overview";
//            final String TITLE = "original_title";
//            final String RATE = "vote_average";
//            final String DATE = "release_date";
            //final String OWM_MIN = "min";
            //final String OWM_DESCRIPTION = "main";

            JSONObject imagesGroupJson = new JSONObject(imagesJsonStr);
            JSONArray imagesArray = imagesGroupJson.getJSONArray(PAGES);

            GridItem item;
            String[] resultStrs = new String[imagesArray.length()];
            for (int i = 0; i < imagesArray.length(); i++) {

                String JPGimg;
                String MovieOverview;
                String MovieTitle;
                String MovieRate;
                String MovieDate;
                JSONObject imgPoster = imagesArray.getJSONObject(i);
                item = new GridItem();
                JPGimg = imgPoster.getString(POSTER);
//                MovieOverview = moviePoster.getString(OVERVIEW);
//                MovieTitle = moviePoster.getString(TITLE);
//                MovieRate = moviePoster.getString(RATE);
//                MovieDate = moviePoster.getString(DATE);
                resultStrs[i] = JPGimg;
                item.setImage(resultStrs[i]);
//                item.setOverview(MovieOverview);
//                item.setTitle(MovieTitle);
//                item.setRate(MovieRate);
//                item.setDate(MovieDate);
                mGridData.add(item);

            }

            return null;
        }


        @Override
        protected String[] doInBackground(String... strings) {


            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String imagesJsonStr = null;

            try {

                final String WORD_BASE_URL = strings[0];

                String PIXAurl = "https://pixabay.com/api/";

//usage example
//https://pixabay.com/api/?key=[MY KEY]&q=yellow+flowers&image_type=photo&pretty=true";

                StringBuilder urlChartBuilder = new StringBuilder();

                urlChartBuilder.append(PIXAurl)
                        .append("?key=").append(BuildConfig.PIXABAY_API_KEY)
                        .append("&q=").append(WORD_BASE_URL)
                        .append("&image_type=photo");



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
                imagesJsonStr = buffer.toString();

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

            try {
                return getImgDataFromJson(imagesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {

            mGridAdapter.setGridData(mGridData);

        }

    }
}
