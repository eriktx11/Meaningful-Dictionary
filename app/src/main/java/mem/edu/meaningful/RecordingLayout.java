package mem.edu.meaningful;

import android.app.Activity;
import android.app.ProgressDialog;
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

    public static ProgressDialog pDialog;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        try {
            // Create a progressbar
            pDialog = new ProgressDialog(fragment.getContext());
            // Set progressbar title
            pDialog.setTitle("Meaningful Dictionary");
            // Set progressbar message
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            // Show progressbar
            pDialog.show();
        } catch (NullPointerException n) {
        }
    }

    @Override
    protected String[] doInBackground(String... params) {

        String wordkey = _sPref.getSmsBody("key");

        String url = "http://www.dia40.com/oodles/smartswitch.php";
        StringBuilder str = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);

        List<NameValuePair> paramx = new ArrayList<NameValuePair>();
        paramx.add(new BasicNameValuePair("aWord", wordkey));

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

        String result = str.toString();
        String[] myList = result.split("\\^");


        Log.e("Log", result);

        params=myList;

            return params;
        }

    //======================== Main brain to dynamically draw the Image buttons and labels for the Layout that interacts with user
    @Override
    protected void onPostExecute(String[] recordingsURL) {
        super.onPostExecute(recordingsURL);

        LinearLayout childLayout;

        //add these locations in record.java also over
        //public void onClick(View v) switch section

        //Australia au
//        Canada ca use-> cca for android
//        India in
        //Jamaica jm
        //Nigeria ng
//        Singapore sg
//        South Africa za
//        Trinidad and Tobago tt
//        United Kingdom uk

        String[] locations = new String[]{"ca", "ny", "tn", "tx", "au", "cca", "in", "jm", "ng", "sg", "za", "tt", "uk"};//world Geo locations

        Integer[] ly_id = new Integer[]{R.id.l1, R.id.l2,R.id.l3, R.id.l4,R.id.l5,R.id.l6,
                R.id.l7, R.id.l7,R.id.l8, R.id.l9,R.id.l10,R.id.l11,R.id.l12,R.id.l13};//horizontal layouts

        Integer[] AudioButtons = new Integer[]
                {
                        R.id.btnSoundId11, R.id.btnSoundId12, R.id.btnSoundId13, R.id.btnSoundId21, R.id.btnSoundId22, R.id.btnSoundId23,
                        R.id.btnSoundId31, R.id.btnSoundId32, R.id.btnSoundId33, R.id.btnSoundId41, R.id.btnSoundId42, R.id.btnSoundId43,
                        R.id.btnSoundId51, R.id.btnSoundId52, R.id.btnSoundId53, R.id.btnSoundId61, R.id.btnSoundId62, R.id.btnSoundId63,
                        R.id.btnSoundId71, R.id.btnSoundId72, R.id.btnSoundId73, R.id.btnSoundId81, R.id.btnSoundId82, R.id.btnSoundId83,
                        R.id.btnSoundId91, R.id.btnSoundId92, R.id.btnSoundId93, R.id.btnSoundId101, R.id.btnSoundId102, R.id.btnSoundId103,
                        R.id.btnSoundId111, R.id.btnSoundId112, R.id.btnSoundId113, R.id.btnSoundId121, R.id.btnSoundId122, R.id.btnSoundId123,
                        R.id.btnSoundId131, R.id.btnSoundId132, R.id.btnSoundId133
                };

        Integer[] child_ly_id =
                new Integer[]
                        {
                                R.id.imageButtonl1a1, R.id.imageButtonl1a2, R.id.imageButtonl1b1, R.id.imageButtonl1b2, R.id.imageButtonl1c1, R.id.imageButtonl1c2,
                                R.id.imageButtonl2a1, R.id.imageButtonl2a2, R.id.imageButtonl2b1, R.id.imageButtonl2b2, R.id.imageButtonl2c1, R.id.imageButtonl2c2,
                                R.id.imageButtonl3a1, R.id.imageButtonl3a2, R.id.imageButtonl3b1, R.id.imageButtonl3b2, R.id.imageButtonl3c1, R.id.imageButtonl3c2,
                                R.id.imageButtonl4a1, R.id.imageButtonl4a2, R.id.imageButtonl4b1, R.id.imageButtonl4b2, R.id.imageButtonl4c1, R.id.imageButtonl4c2,
                                R.id.imageButtonl5a1, R.id.imageButtonl5a2, R.id.imageButtonl5b1, R.id.imageButtonl5b2, R.id.imageButtonl5c1, R.id.imageButtonl5c2,
                                R.id.imageButtonl6a1, R.id.imageButtonl6a2, R.id.imageButtonl6b1, R.id.imageButtonl6b2, R.id.imageButtonl6c1, R.id.imageButtonl6c2,
                                R.id.imageButtonl7a1, R.id.imageButtonl7a2, R.id.imageButtonl7b1, R.id.imageButtonl7b2, R.id.imageButtonl7c1, R.id.imageButtonl7c2,
                                R.id.imageButtonl8a1, R.id.imageButtonl8a2, R.id.imageButtonl8b1, R.id.imageButtonl8b2, R.id.imageButtonl8c1, R.id.imageButtonl8c2,
                                R.id.imageButtonl9a1, R.id.imageButtonl9a2, R.id.imageButtonl9b1, R.id.imageButtonl9b2, R.id.imageButtonl9c1, R.id.imageButtonl9c2,
                                R.id.imageButtonl10a1, R.id.imageButtonl10a2, R.id.imageButtonl10b1, R.id.imageButtonl10b2, R.id.imageButtonl10c1, R.id.imageButtonl10c2,
                                R.id.imageButtonl11a1, R.id.imageButtonl11a2, R.id.imageButtonl11b1, R.id.imageButtonl11b2, R.id.imageButtonl11c1, R.id.imageButtonl11c2,
                                R.id.imageButtonl12a1, R.id.imageButtonl12a2, R.id.imageButtonl12b1, R.id.imageButtonl12b2, R.id.imageButtonl12c1, R.id.imageButtonl12c2,
                                R.id.imageButtonl13a1, R.id.imageButtonl13a2, R.id.imageButtonl13b1, R.id.imageButtonl13b2, R.id.imageButtonl13c1, R.id.imageButtonl13c2
                        };//up vote and down vote arrows

        Integer[] vote_ly = new Integer[]
                {
                        R.id.txtv1a, R.id.txtv1b, R.id.txtv1c, R.id.txtv2a, R.id.txtv2b, R.id.txtv2c,
                        R.id.txtv3a, R.id.txtv3b, R.id.txtv3c, R.id.txtv4a, R.id.txtv4b, R.id.txtv4c,
                        R.id.txtv5a, R.id.txtv5b, R.id.txtv5c, R.id.txtv6a, R.id.txtv6b, R.id.txtv6c,
                        R.id.txtv7a, R.id.txtv7b, R.id.txtv7c, R.id.txtv8a, R.id.txtv8b, R.id.txtv8c,
                        R.id.txtv9a, R.id.txtv9b, R.id.txtv9c, R.id.txtv10a, R.id.txtv10b, R.id.txtv10c,
                        R.id.txtv11a, R.id.txtv11b, R.id.txtv11c, R.id.txtv12a, R.id.txtv12b, R.id.txtv12c,
                        R.id.txtv13a, R.id.txtv13b, R.id.txtv13c,
                };//vote numbers per sound icon

        Integer[] audio_id = new Integer[]{R.id.rcrbtn1, R.id.rcrbtn2,
                R.id.rcrbtn3, R.id.rcrbtn4,
                R.id.rcrbtn5, R.id.rcrbtn6,
                R.id.rcrbtn7, R.id.rcrbtn8,
                R.id.rcrbtn9, R.id.rcrbtn10,
                R.id.rcrbtn11, R.id.rcrbtn12,
                R.id.rcrbtn13};//record icons
        Integer[] audio_lb_id = new Integer[]{R.id.rectxt1, R.id.rectxt2,
                R.id.rectxt3, R.id.rectxt4,
                R.id.rectxt5, R.id.rectxt6,
                R.id.rectxt7, R.id.rectxt8,
                R.id.rectxt9, R.id.rectxt10,
                R.id.rectxt11, R.id.rectxt12,
                R.id.rectxt13};//"contribute" labels

        //int index = 3;//walks through the array
        int vote_counter = 0;//array index for TextView to display current votes in each ocurrence
        int vote_index = 0;//index for up vote and down vote array
        int xy_counter = 0;//counts 3 times per horizontal layout per location
        int index = 3;//walks through the array
        int play_counter = 0;
        vote voting;//Java file
        record recording;//Java file
        play playing;//Java file

        try {


            for (int i = 0; i < locations.length; i++) {
                childLayout = (LinearLayout) mActivity.findViewById(ly_id[i]);//looping through each horizotal layout.
                int l_counter = 0;//used for index to loop every children from top to bottom in parent layout
                boolean bNegVoteFlag = false;
                try {
                    //To stop app from crashing. I am looping based on a index that will be out of size. In that case data has been read entirely.
                    View v;
                    //Checking if location is same as array and if inbound data has looped to the end when it becomes null
                    while (!recordingsURL[index].isEmpty() && recordingsURL[index].equals(locations[i])) {

                        v = childLayout.getChildAt(l_counter);//get horizontal layout
                        if (v instanceof ImageButton) {

                            if (recordingsURL[index].equals(locations[i])) {
                                v.setVisibility(View.VISIBLE);//set play icon visible
                                Integer id = v.getId();
                                //this is a example of inbound data --> 3^hi^bb@bb.com^ak^0^eu4m/aa@dd.com/hi
                                //_sPref.saveSmsBody(id.toString(),recordingsURL[index+2]);//this saves the sound url
                                if (Integer.valueOf(recordingsURL[index + 1]) < 0 && !bNegVoteFlag) {
                                    bNegVoteFlag = true;
                                }
                                playing = new play("http://www.dia40.com/oodles/" + recordingsURL[index + 2]);
                                ImageButton playaudio = (ImageButton) v.findViewById(AudioButtons[play_counter++]);
                                playaudio.setOnClickListener(playing);
                                index = index + 6;//skip to the next Geo location
                                l_counter++;//moves index to get the next ImageButton to the right.
                                v = childLayout.getChildAt(l_counter);//getting the up vote and down vote arrows
                            }
                        }

                        if (v instanceof LinearLayout) {
                            v.setVisibility(View.VISIBLE);//making up vote, down vote and counter visible
                            TextView txtVote = (TextView) v.findViewById(vote_ly[vote_counter]);//creating the TextView to show how many votes exist.
                            vote_counter++;//moves index for next counter to the right.
                            txtVote.setText(recordingsURL[index - 5]);//getting the number of votes from the inbound data and displays to user
                            voting = new vote(mActivity.getWindow().getContext(), txtVote);//sends context and TextView counter to vote Java constructor
                            ImageButton voteup_btn = (ImageButton) mActivity.findViewById(child_ly_id[vote_index]);//creates the up vote arrow
                            voteup_btn.setOnClickListener(voting);//setting onclickListener for up votting.
                            Integer id = child_ly_id[vote_index];//getting up vote ImageButton layout id
                            //Example of inbound data --> 3^hi^bb@bb.com^ak^0^eu4m/aa@dd.com/hi
                            _sPref.saveSmsBody(id.toString(), recordingsURL[index - 7]);//saving this pair ("Up arrow iD", "user's email who uploaded sound file")
                            vote_index++;//move index to the down vote arrow
                            ImageButton voteup_down = (ImageButton) mActivity.findViewById(child_ly_id[vote_index]);
                            voteup_down.setOnClickListener(voting);
                            id = child_ly_id[vote_index];
                            //3^hi^bb@bb.com^ak^0^eu4m/aa@dd.com/hi
                            _sPref.saveSmsBody(id.toString(), recordingsURL[index - 7]);//saving this pair ("Down arrow iD", "user's email who uploaded sound file")
                            vote_index++;//move index to the up vote arrow
                            xy_counter++;//this counts endlessly from 1 t0 3. It is reset to zero in the switch below
                        }
                        l_counter++;//moves index counter to next sound ButtonImage to the right.
                    }

                } catch (ArrayIndexOutOfBoundsException e) {
                    Log.e("Done", "null array");
                }

                if (xy_counter != 3) {//as long as recordings uploaded by users is not a number of 3, the ImageButton to upload new file is visible.
                    recording = new record(mActivity.getWindow().getContext(), fragment, mActivity);
                    ImageButton start = (ImageButton) mActivity.findViewById(audio_id[i]);
                    start.setVisibility(View.VISIBLE);//recording ImageButton is visible
                    TextView rcrlbl = (TextView) mActivity.findViewById(audio_lb_id[i]);
                    rcrlbl.setVisibility(View.VISIBLE);//"Contribute" TextView label is visible
                    start.setOnClickListener(recording);
                } else {//if there are three uploaded recordings by the community, the record ImageButton becomes invisible.
                    recording = new record(mActivity.getWindow().getContext(), fragment, mActivity);//mActivity.getWindow().getContext(), mActivity
                    ImageButton start = (ImageButton) mActivity.findViewById(audio_id[i]);
                    TextView rcrlbl = (TextView) mActivity.findViewById(audio_lb_id[i]);
                    if (bNegVoteFlag) {
                        start.setVisibility(View.VISIBLE);//recording ImageButton is invisible
                        rcrlbl.setVisibility(View.VISIBLE);//"Contribute" TextView label is invisible
                        start.setOnClickListener(recording);
                    } else {
                        start.setVisibility(View.INVISIBLE);//recording ImageButton is invisible
                        rcrlbl.setVisibility(View.INVISIBLE);//"Contribute" TextView label is invisible
                    }
                }

                switch (xy_counter) {//reset horizontal 1-2-3 counter to zero and increase vote counter, vote arrows counter indexes to loop from top to bottom of layout
                    case 0:
                        vote_index++;
                        vote_index++;
                        vote_index++;
                        vote_index++;
                        vote_index++;
                        vote_index++;
                        xy_counter = 0;
                        vote_counter++;
                        vote_counter++;
                        vote_counter++;
                        play_counter++;
                        play_counter++;
                        play_counter++;
                        break;
                    case 1:
                        vote_index++;
                        vote_index++;
                        vote_index++;
                        vote_index++;
                        xy_counter = 0;
                        vote_counter++;
                        vote_counter++;
                        play_counter++;
                        play_counter++;
                        break;
                    case 2:
                        vote_index++;
                        vote_index++;
                        xy_counter = 0;
                        vote_counter++;
                        play_counter++;
                        break;
                    case 3:
                        xy_counter = 0;
                        break;
                    default:
                        xy_counter = 0;//eliminates the chance the xy_counter may be 4 or 5 and there is nothing to reset it to zero.
                }
            }
        } catch (NullPointerException e) {}

        try {
            pDialog.dismiss();
        } catch (IllegalArgumentException e) {
        } catch (NullPointerException n){}
    }
}