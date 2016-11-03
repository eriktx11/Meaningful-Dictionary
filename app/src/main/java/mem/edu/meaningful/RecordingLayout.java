package mem.edu.meaningful;

import android.app.Activity;
import android.media.AudioManager;
import android.media.ImageReader;
import android.media.MediaPlayer;
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

    //======================== Main brain to dynamically draw the Image buttons and labels for the Layout that interacts with user
    @Override
    protected void onPostExecute(String[] recordingsURL) {
        super.onPostExecute(recordingsURL);

        LinearLayout childLayout;

        String[] locations = new String[]{"ak","al"};//world Geo locations

        Integer[] ly_id = new Integer[]{R.id.l1, R.id.l2};//horizontal layouts

        Integer[] AudioButtons = new Integer[]
        {
        R.id.btnSoundId11,R.id.btnSoundId12,R.id.btnSoundId13,R.id.btnSoundId21,R.id.btnSoundId22,R.id.btnSoundId23
        };

        Integer[] child_ly_id =
        new Integer[]
        {
        R.id.imageButtonl1a1, R.id.imageButtonl1a2, R.id.imageButtonl1b1, R.id.imageButtonl1b2, R.id.imageButtonl1c1, R.id.imageButtonl1c2,
        R.id.imageButtonl2a1, R.id.imageButtonl2a2, R.id.imageButtonl2b1, R.id.imageButtonl2b2, R.id.imageButtonl2c1, R.id.imageButtonl2c2
        };//up vote and down vote arrows

        Integer[] vote_ly = new Integer[]
        {
        R.id.txtv1a,R.id.txtv1b,R.id.txtv1c,R.id.txtv2a,R.id.txtv2b,R.id.txtv2c
        };//vote numbers per sound icon

        Integer[] audio_id=new Integer[]{R.id.rcrbtn1,R.id.rcrbtn2};//record icons
        Integer[] audio_lb_id=new Integer[]{R.id.rectxt1,R.id.rectxt2};//"contribute" labels

        //int index = 3;//walks through the array
        int vote_counter=0;//array index for TextView to display current votes in each ocurrence
        int vote_index=0;//index for up vote and down vote array
        int xy_counter = 0;//counts 3 times per horizontal layout per location
        int index = 3;//walks through the array
        int play_counter=0;
        vote voting;//Java file
        record recording;//Java file
        play playing;//Java file

        for(int i=0; i<locations.length; i++) {
            childLayout = (LinearLayout) mActivity.findViewById(ly_id[i]);//looping through each horizotal layout.
            int l_counter = 0;//used for index to loop every children from top to bottom in parent layout
            boolean bNegVoteFlag = false;
            try {
                //To stop app from crashing. I am looping based on a index that will be out of size. In that case data has been read entirely.
                View v;
                //Checking if location is same as array and if inbound data has looped to the end when it becomes null
                while ( !recordingsURL[index].isEmpty() && recordingsURL[index].equals(locations[i]) ) {

                    v = childLayout.getChildAt(l_counter);//get horizontal layout
                    if (v instanceof ImageButton) {

                        if (recordingsURL[index].equals(locations[i])) {
                            v.setVisibility(View.VISIBLE);//set play icon visible
                            Integer id = v.getId();
                            //this is a example of inbound data --> 3^hi^bb@bb.com^ak^0^eu4m/aa@dd.com/hi
                            //_sPref.saveSmsBody(id.toString(),recordingsURL[index+2]);//this saves the sound url
                            if(Integer.valueOf(recordingsURL[index+1]) < 0 && !bNegVoteFlag){
                                bNegVoteFlag=true;
                            }
                            playing = new play("http://www.dia40.com/oodles/"+recordingsURL[index+2]);
                            ImageButton playaudio = (ImageButton) v.findViewById(AudioButtons[play_counter++]);
                            playaudio.setOnClickListener(playing);
                            index = index + 6;//skip to the next Geo location
                            l_counter++;//moves index to get the next ImageButton to the right.
                            v = childLayout.getChildAt(l_counter);//getting the up vote and down vote arrows
                        }
                    }

                    if (v instanceof LinearLayout) {
                        v.setVisibility(View.VISIBLE);//making up vote, down vote and counter visible
                        TextView txtVote = (TextView)v.findViewById(vote_ly[vote_counter]);//creating the TextView to show how many votes exist.
                        vote_counter++;//moves index for next counter to the right.
                        txtVote.setText(recordingsURL[index-5]);//getting the number of votes from the inbound data and displays to user
                        voting = new vote(mActivity.getWindow().getContext(), txtVote);//sends context and TextView counter to vote Java constructor
                        ImageButton voteup_btn = (ImageButton) mActivity.findViewById(child_ly_id[vote_index]);//creates the up vote arrow
                        voteup_btn.setOnClickListener(voting);//setting onclickListener for up votting.
                        Integer id = child_ly_id[vote_index];//getting up vote ImageButton layout id
                        //Example of inbound data --> 3^hi^bb@bb.com^ak^0^eu4m/aa@dd.com/hi
                        _sPref.saveSmsBody(id.toString(),recordingsURL[index-7]);//saving this pair ("Up arrow iD", "user's email who uploaded sound file")
                        vote_index++;//move index to the down vote arrow
                        ImageButton voteup_down = (ImageButton) mActivity.findViewById(child_ly_id[vote_index]);
                        voteup_down.setOnClickListener(voting);
                        id = child_ly_id[vote_index];
                        //3^hi^bb@bb.com^ak^0^eu4m/aa@dd.com/hi
                        _sPref.saveSmsBody(id.toString(),recordingsURL[index-7]);//saving this pair ("Down arrow iD", "user's email who uploaded sound file")
                        vote_index++;//move index to the up vote arrow
                        xy_counter++;//this counts endlessly from 1 t0 3. It is reset to zero in the switch below
                    }
                    l_counter++;//moves index counter to next sound ButtonImage to the right.
                }

            } catch (ArrayIndexOutOfBoundsException e) {
                Log.e("Done", "null array");
            }

            if(xy_counter!=3) {//as long as recordings uploaded by users is not a number of 3, the ImageButton to upload new file is visible.
                recording = new record(mActivity.getWindow().getContext(), fragment, mActivity);
                ImageButton start = (ImageButton) mActivity.findViewById(audio_id[i]);
                start.setVisibility(View.VISIBLE);//recording ImageButton is visible
                TextView rcrlbl = (TextView) mActivity.findViewById(audio_lb_id[i]);
                rcrlbl.setVisibility(View.VISIBLE);//"Contribute" TextView label is visible
                start.setOnClickListener(recording);
            }
            else{//if there are three uploaded recordings by the community, the record ImageButton becomes invisible.
                recording=new record(mActivity.getWindow().getContext(), fragment, mActivity);//mActivity.getWindow().getContext(), mActivity
                ImageButton start = (ImageButton)mActivity.findViewById(audio_id[i]);
                TextView rcrlbl = (TextView)mActivity.findViewById(audio_lb_id[i]);
                if(bNegVoteFlag){
                    start.setVisibility(View.VISIBLE);//recording ImageButton is invisible
                    rcrlbl.setVisibility(View.VISIBLE);//"Contribute" TextView label is invisible
                    start.setOnClickListener(recording);
                }else {
                    start.setVisibility(View.INVISIBLE);//recording ImageButton is invisible
                    rcrlbl.setVisibility(View.INVISIBLE);//"Contribute" TextView label is invisible
                }
            }

            switch (xy_counter){//reset horizontal 1-2-3 counter to zero and increase vote counter, vote arrows counter indexes to loop from top to bottom of layout
                case 0:vote_index++;vote_index++;vote_index++;
                    vote_index++;vote_index++;vote_index++;xy_counter=0;
                    vote_counter++;vote_counter++;vote_counter++;
                    play_counter++;play_counter++;play_counter++;break;
                case 1:vote_index++;vote_index++;
                    vote_index++;vote_index++;xy_counter=0;
                    vote_counter++;vote_counter++;
                    play_counter++;play_counter++;break;
                case 2:vote_index++;vote_index++;xy_counter=0;vote_counter++;
                    play_counter++;break;
                case 3:xy_counter=0;break;
                default:xy_counter=0;//eliminates the chance the xy_counter may be 4 or 5 and there is nothing to reset it to zero.
            }
        }
    }
}