package mem.edu.meaningful;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    private Activity mActivity;
    private Fragment fragment;
    private AppPreferences _sPref;

    public RecordingLayout(Activity activity, Fragment fg) {
        this.mActivity = activity;
        this._sPref = new AppPreferences(activity.getBaseContext());
        this.fragment=fg;
    }

    static String strStatusID;
    static int postResult;
    static String strError;

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
        Integer[] child_ly_id =
        new Integer[]
        {
        R.id.imageButtonl1a1, R.id.imageButtonl1a2, R.id.imageButtonl1b1, R.id.imageButtonl1b2, R.id.imageButtonl1c1, R.id.imageButtonl1c2,
        R.id.imageButtonl2a1, R.id.imageButtonl2a2, R.id.imageButtonl2b1, R.id.imageButtonl2b2, R.id.imageButtonl2c1, R.id.imageButtonl2c2,

        };
        Integer[] audio_id=new Integer[]{R.id.rcrbtn1,R.id.rcrbtn2};
        Integer[] audio_lb_id=new Integer[]{R.id.rectxt1,R.id.rectxt2};

        int index = 3;//walks through the array
        int vote_index=0;
        int xy_counter = 0;
        vote voting;
        record recording;

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
                            Integer id = v.getId();
                            //3^hi^bb@bb.com^ak^0^/eu4m/aa@dd.com/hi
                            _sPref.saveSmsBody(id.toString(),recordingsURL[index+2]);
                            index = index + 6;
                            l_counter++;
                            v = childLayout.getChildAt(l_counter);
                        }
                    }

                    if (v instanceof LinearLayout) {
                        v.setVisibility(View.VISIBLE);
                        voting = new vote(mActivity.getWindow().getContext(), locations[i]);
                        ImageButton voteup_btn = (ImageButton) mActivity.findViewById(child_ly_id[vote_index]);
                        voteup_btn.setOnClickListener(voting);
                        Integer id = child_ly_id[vote_index];
                        //3^hi^bb@bb.com^ak^0^/eu4m/aa@dd.com/hi
                        _sPref.saveSmsBody(id.toString(),recordingsURL[index-7]);
                        vote_index++;
                        ImageButton voteup_down = (ImageButton) mActivity.findViewById(child_ly_id[vote_index]);
                        voteup_down.setOnClickListener(voting);
                        id = child_ly_id[vote_index];
                        //3^hi^bb@bb.com^ak^0^/eu4m/aa@dd.com/hi
                        _sPref.saveSmsBody(id.toString(),recordingsURL[index-7]);
                        vote_index++;
                        xy_counter++;
                    }
                    l_counter++;
                }

            } catch (ArrayIndexOutOfBoundsException e) {
                Log.e("Done", "null array");
            }

            if(xy_counter!=3) {
                recording = new record(mActivity.getWindow().getContext(), fragment, mActivity);//mActivity.getWindow().getContext(), mActivity
                ImageButton start = (ImageButton) mActivity.findViewById(audio_id[i]);
                start.setVisibility(View.VISIBLE);
                TextView rcrlbl = (TextView) mActivity.findViewById(audio_lb_id[i]);
                rcrlbl.setVisibility(View.VISIBLE);
                start.setOnClickListener(recording);
            }
            else{
                recording=new record(mActivity.getWindow().getContext(), fragment, mActivity);//mActivity.getWindow().getContext(), mActivity
                ImageButton start = (ImageButton)mActivity.findViewById(audio_id[i]);
                start.setVisibility(View.INVISIBLE);
                TextView rcrlbl = (TextView)mActivity.findViewById(audio_lb_id[i]);
                rcrlbl.setVisibility(View.INVISIBLE);
                start.setOnClickListener(recording);
            }

            switch (xy_counter){
                case 0:vote_index++;vote_index++;vote_index++;
                    vote_index++;vote_index++;vote_index++;xy_counter=0;break;
                case 1:vote_index++;vote_index++;
                    vote_index++;vote_index++;xy_counter=0;break;
                case 2:vote_index++;vote_index++;xy_counter=0;break;
                case 3:xy_counter=0;break;
                default:xy_counter=0;
            }
        }
    }

    public View.OnClickListener l = new View.OnClickListener(){

        @Override
        public void onClick(View v) {

        }
    };
}
