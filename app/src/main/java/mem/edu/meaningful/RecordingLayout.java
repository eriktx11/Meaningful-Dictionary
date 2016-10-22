package mem.edu.meaningful;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
            LinearLayout linearLayout;
            linearLayout = (LinearLayout) mActivity.findViewById(R.id.l1);

            for (int i = 0; i < linearLayout.getChildCount(); i++){
                View v = linearLayout.getChildAt(i);
                if (v instanceof ImageButton) {
                   for (int j = 0; j < recordingsURL.length; j++) {
                        if (recordingsURL[j].equals("ak")) {
                            v.setVisibility(View.INVISIBLE);

                        }

                    }
                }
                if(v instanceof LinearLayout){
                    v.setVisibility(View.INVISIBLE);
                }
            }


        }
//    }

}
