package mem.edu.meaningful;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.helper.StringUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by erikllerena on 10/20/16.
 */
public class RecordingLayout extends AsyncTask<String, Void, String[]>{

    //AsyncTask<String, Void, String[]>{
        //Activity {
    private Activity mActivity;
    private AppPreferences _sPref;

    public RecordingLayout(Activity activity) {
        this.mActivity = activity;
        this._sPref = new AppPreferences(activity.getBaseContext());
    }


    static String strStatusID;
    static int postResult;
    static String strError;

    // String[] recordingsURL=new String[3];
//    static class loopthroughLayout extends AsyncTask<String, Void, String[]>{

    @Override
    protected String[] doInBackground(String... params) {

        String wordkey = _sPref.getSmsBody("key");

        String url = "http://www.dia40.com/oodles/smartswitch.php";
        StringBuilder str = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);

        List<NameValuePair> paramx = new ArrayList<NameValuePair>();
        paramx.add(new BasicNameValuePair("aWord", wordkey));
//        paramx.add(new BasicNameValuePair("sUsername", user));
//        paramx.add(new BasicNameValuePair("sLocation", location));
//        paramx.add(new BasicNameValuePair("sVote", params[3]));

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(paramx));
            HttpResponse response = client.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) { // Status OK
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    str.append(line);
                }
            } else {
                Log.e("Log", "Failed to connect...");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //logging.setFlag(true);

        String result = str.toString();
        String[] myList = result.split("\\^");


        Log.e("Log", result);

//        if(result.equals("")){
//            postResult=0;
//            strError="Please enter your email";
////                logging.setPostResult(0);
////                logging.setStrError("Please enter your email");
//        }else {
//            /*** Default Value ***/
//            strStatusID="0";
//            strError="Unknow Status!";
////                logging.setStrStatusID("0");
////                logging.setStrError("Unknow Status!");
//
//            JSONObject c;
//            try {
//                c = new JSONObject(result);
//                strStatusID=c.getString("StatusID");
//                strError=c.getString("Error");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            // Prepare Save Data
//            if (strStatusID.equals("0")) {
////                    logging.setPostResult(1);
//                postResult=1;
//            } else {
//                postResult=2;
////                    logging.setPostResult(2);
//            }
//        }]

//            params = new String[3];
//            params[0]="on";
//            params[1]="off";
//            params[2]="on";

        params=myList;

            return params;
        }


    @Override
    protected void onPostExecute(String[] recordingsURL) {
        super.onPostExecute(recordingsURL);

        LinearLayout childLayout;

        String[] locations = new String[]{"ak","al"};
        Integer[] ly_id = new Integer[]{R.id.l1, R.id.l2};
        int index = 3;//walks through the array

        for(int i=0; i<locations.length; i++) {
            childLayout = (LinearLayout) mActivity.findViewById(ly_id[i]);
            int l_counter = 0;

            try {
                View v;
                while ( !recordingsURL[index].isEmpty() && recordingsURL[index].equals(locations[i]) ) {

                    v = childLayout.getChildAt(l_counter);
                    if (v instanceof ImageButton) {

                        if (recordingsURL[index].equals(locations[i])) {
                            v.setVisibility(View.VISIBLE);
                            index = index + 6;
                            l_counter++;
                            v = childLayout.getChildAt(l_counter);
                        }
                    }


                    if (v instanceof LinearLayout) {
                        v.setVisibility(View.VISIBLE);


                        View cv = childLayout.getChildAt(0);
                        if (index==9) {
                            vote voting = new vote(mActivity.getWindow().getContext(), locations[i]);
                            ImageButton voteup_btn = (ImageButton) mActivity.findViewById(R.id.imageButtonl1a1);
//                        ImageButton voteup_btn = (ImageButton) v.findViewById(cv.getId());
                            voteup_btn.setOnClickListener(voting);
                        }
//                        Button votedown_btn = (Button) v.findViewById(childLayout.getId());
//                        votedown_btn.setOnClickListener(voting);
                    }
                    l_counter++;
                }

            } catch (ArrayIndexOutOfBoundsException e) {
                Log.e("Done", "null array");
            }
        }
    }

    public View.OnClickListener l = new View.OnClickListener(){

        @Override
        public void onClick(View v) {

        }
    };
}
