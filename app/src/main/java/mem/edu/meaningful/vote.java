package mem.edu.meaningful;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by erikllerena on 10/19/16.
 */

public class vote extends Activity implements View.OnClickListener {

    public Dialog dialog;
    private Context mContex;
    private AppPreferences _sPref;

    TextView tvError;
    ImageView image;
    EditText txtEmail;
    TextView tvEmail;
    Button logOut;
    Button cancel;
    Button ok_log_vote;
    String voteLabel;
    RecordingLayout ci;

    String strStatusID;
    String strVote;
    int postResult;
    String strError;
    String vote;
    String candidate;
    private TextView voteView;

    public vote(Context c, TextView vote){
        this.mContex=c;
        this.voteView=vote;
    }

    public View.OnClickListener btn_register = new View.OnClickListener(){

        @Override
        public void onClick(View v) {

            String[] strFirstTime=new String[2];
            strFirstTime[0] =  txtEmail.getText().toString();
            strFirstTime[1] = _sPref.getSmsBody("loc");

            LoginPlease login = new LoginPlease();

            try {
                new LoginPlease.getHttpPost().execute(strFirstTime).get(3000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }


            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            switch (login.postResult) {
                case 0:
                    tvError.setText(login.strError);
                    tvError.setTextColor(Color.parseColor("#FFFC0202"));
                    break;
                case 1:
                    tvError.setText(login.strError);
                    tvError.setTextColor(Color.parseColor("#FFFC0202"));
                    break;
                case 2:
                    tvEmail.setVisibility(View.INVISIBLE);
                    tvError.setVisibility(View.INVISIBLE);
                    txtEmail.setVisibility(View.INVISIBLE);
                    _sPref.saveSmsBody("userId", txtEmail.getText().toString());
                    logOut.setVisibility(View.VISIBLE);
                    logOut.setOnClickListener(btnLogout);
                    ok_log_vote.setText(voteLabel);
                    ok_log_vote.setOnClickListener(btn_vote_now);
                    break;
            }
        }
    };

    public View.OnClickListener btn_login_stat = new View.OnClickListener(){

        @Override
        public void onClick(View v) {

            if(!_sPref.getAll().containsKey("userId")){
                txtEmail.setVisibility(View.VISIBLE);
                ok_log_vote.setText("REGISTER");
                tvEmail.setVisibility(View.VISIBLE);
                ok_log_vote.setOnClickListener(btn_register);
            }else {
                logOut.setVisibility(View.VISIBLE);
                logOut.setOnClickListener(btnLogout);
                ok_log_vote.setText(voteLabel);
                ok_log_vote.setOnClickListener(btn_vote_now);
            }
        }
    };

    public View.OnClickListener btn_vote_now = new View.OnClickListener(){

        @Override
        public void onClick(View v) {

            String[] strFirstTime=new String[5];
            strFirstTime[0] = _sPref.getSmsBody("key");
            strFirstTime[1] = _sPref.getSmsBody("userId");
            strFirstTime[2] = _sPref.getSmsBody("loc");
            strFirstTime[3] = vote;
            strFirstTime[4] = candidate;

            new postVote().execute(strFirstTime);
            cancel.setText("DONE");
            ok_log_vote.setEnabled(false);
        }
    };

    public View.OnClickListener btn_cancel = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            dialog.dismiss();
        }
    };

    private View.OnClickListener btnLogout = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            _sPref.removePref("userId");
            logOut.setVisibility(View.INVISIBLE);
            txtEmail.setVisibility(View.VISIBLE);
            ok_log_vote.setText("REGISTER");
            tvEmail.setVisibility(View.VISIBLE);
            ok_log_vote.setOnClickListener(btn_register);
        }
    };

    @Override
    public void onClick(View v) {
        _sPref = new AppPreferences(mContex);
        String location = _sPref.getSmsBody("loc");
        String full_loc = _sPref.getSmsBody("full_loc");
        dialog = new Dialog(mContex);
        dialog.setContentView(R.layout.vote_layout);
        dialog.setTitle("vote");

        image = (ImageView) dialog.findViewById(R.id.imageId);
        txtEmail = (EditText) dialog.findViewById(R.id.txtEmail);
        tvEmail = (TextView) dialog.findViewById(R.id.tvEmailId);
        tvError = (TextView) dialog.findViewById(R.id.txtError);
        logOut = (Button) dialog.findViewById(R.id.logOutId);
        cancel = (Button) dialog.findViewById(R.id.btn_cancel);

        ok_log_vote = (Button) dialog.findViewById(R.id.btn_login);
        Integer id =v.getId();

            switch (id){
                case R.id.imageButtonl1a1:_sPref.saveSmsBody("loc", "ca");_sPref.saveSmsBody("full_loc", "California");
                    full_loc="California";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl1a2:_sPref.saveSmsBody("loc", "ca");_sPref.saveSmsBody("full_loc", "California");
                    full_loc="California";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl1b1:_sPref.saveSmsBody("loc", "ca");_sPref.saveSmsBody("full_loc", "California");
                    full_loc="California";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl1b2:_sPref.saveSmsBody("loc", "ca");_sPref.saveSmsBody("full_loc", "California");
                    full_loc="California";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl1c1:_sPref.saveSmsBody("loc", "ca");_sPref.saveSmsBody("full_loc", "California");
                    full_loc="California";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl1c2:_sPref.saveSmsBody("loc", "ca");_sPref.saveSmsBody("full_loc", "California");
                    full_loc="California";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;

                case R.id.imageButtonl2a1:_sPref.saveSmsBody("loc", "ny");_sPref.saveSmsBody("full_loc", "New York");
                    full_loc="New York";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl2a2:_sPref.saveSmsBody("loc", "ny");_sPref.saveSmsBody("full_loc", "New York");
                    full_loc="New York";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl2b1:_sPref.saveSmsBody("loc", "ny");_sPref.saveSmsBody("full_loc", "New York");
                    full_loc="New York";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl2b2:_sPref.saveSmsBody("loc", "ny");_sPref.saveSmsBody("full_loc", "New York");
                    full_loc="New York";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl2c1:_sPref.saveSmsBody("loc", "ny");_sPref.saveSmsBody("full_loc", "New York");
                    full_loc="New York";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl2c2:_sPref.saveSmsBody("loc", "ny");_sPref.saveSmsBody("full_loc", "New York");
                    full_loc="New York";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;

                case R.id.imageButtonl3a1:_sPref.saveSmsBody("loc", "tn");_sPref.saveSmsBody("full_loc", "Tennessee");
                    full_loc="Tennessee";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl3a2:_sPref.saveSmsBody("loc", "tn");_sPref.saveSmsBody("full_loc", "Tennessee");
                    full_loc="Tennessee";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl3b1:_sPref.saveSmsBody("loc", "tn");_sPref.saveSmsBody("full_loc", "Tennessee");
                    full_loc="Tennessee";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl3b2:_sPref.saveSmsBody("loc", "tn");_sPref.saveSmsBody("full_loc", "Tennessee");
                    full_loc="Tennessee";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl3c1:_sPref.saveSmsBody("loc", "tn");_sPref.saveSmsBody("full_loc", "Tennessee");
                    full_loc="Tennessee";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl3c2:_sPref.saveSmsBody("loc", "tn");_sPref.saveSmsBody("full_loc", "Tennessee");
                    full_loc="Tennessee";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;

                case R.id.imageButtonl4a1:_sPref.saveSmsBody("loc", "tx");_sPref.saveSmsBody("full_loc", "Texas");
                    full_loc="Texas";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl4a2:_sPref.saveSmsBody("loc", "tx");_sPref.saveSmsBody("full_loc", "Texas");
                    full_loc="Texas";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl4b1:_sPref.saveSmsBody("loc", "tx");_sPref.saveSmsBody("full_loc", "Texas");
                    full_loc="Texas";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl4b2:_sPref.saveSmsBody("loc", "tx");_sPref.saveSmsBody("full_loc", "Texas");
                    full_loc="Texas";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl4c1:_sPref.saveSmsBody("loc", "tx");_sPref.saveSmsBody("full_loc", "Texas");
                    full_loc="Texas";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl4c2:_sPref.saveSmsBody("loc", "tx");_sPref.saveSmsBody("full_loc", "Texas");
                    full_loc="Texas";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;

                case R.id.imageButtonl5a1:_sPref.saveSmsBody("loc", "jm");_sPref.saveSmsBody("full_loc", "Jamaica");
                    full_loc="Jamaica";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl5a2:_sPref.saveSmsBody("loc", "jm");_sPref.saveSmsBody("full_loc", "Jamaica");
                    full_loc="Jamaica";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl5b1:_sPref.saveSmsBody("loc", "jm");_sPref.saveSmsBody("full_loc", "Jamaica");
                    full_loc="Jamaica";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl5b2:_sPref.saveSmsBody("loc", "jm");_sPref.saveSmsBody("full_loc", "Jamaica");
                    full_loc="Jamaica";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl5c1:_sPref.saveSmsBody("loc", "jm");_sPref.saveSmsBody("full_loc", "Jamaica");
                    full_loc="Jamaica";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl5c2:_sPref.saveSmsBody("loc", "jm");_sPref.saveSmsBody("full_loc", "Jamaica");
                    full_loc="Jamaica";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;

                case R.id.imageButtonl6a1:_sPref.saveSmsBody("loc", "ng");_sPref.saveSmsBody("full_loc", "Nigeria");
                    full_loc="Nigeria";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl6a2:_sPref.saveSmsBody("loc", "ng");_sPref.saveSmsBody("full_loc", "Nigeria");
                    full_loc="Nigeria";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl6b1:_sPref.saveSmsBody("loc", "ng");_sPref.saveSmsBody("full_loc", "Nigeria");
                    full_loc="Nigeria";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl6b2:_sPref.saveSmsBody("loc", "ng");_sPref.saveSmsBody("full_loc", "Nigeria");
                    full_loc="Nigeria";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl6c1:_sPref.saveSmsBody("loc", "ng");_sPref.saveSmsBody("full_loc", "Nigeria");
                    full_loc="Nigeria";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl6c2:_sPref.saveSmsBody("loc", "ng");_sPref.saveSmsBody("full_loc", "Nigeria");
                    full_loc="Nigeria";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;
            }

        String labelText="Voting in: \n"+full_loc+", are you sure?\n_________________\nYou won\'t be able to change your vote";
        tvError.setText(labelText);
        ok_log_vote.setOnClickListener(btn_login_stat);
        cancel.setOnClickListener(btn_cancel);

        dialog.show();

    }


    //======================
    public class postVote extends AsyncTask<String, Void, String[]> {


        @Override
        protected String[] doInBackground(String... params) {

            String url = "http://www.dia40.com/oodles/vtvmean.php";
            StringBuilder str = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            List<NameValuePair> paramx = new ArrayList<NameValuePair>();
            paramx.add(new BasicNameValuePair("aWord", params[0]));
            paramx.add(new BasicNameValuePair("sUsername", params[1]));
            paramx.add(new BasicNameValuePair("sLocation", params[2]));
            paramx.add(new BasicNameValuePair("sVote", params[3]));
            paramx.add(new BasicNameValuePair("sCandidate", params[4]));

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
                    Log.e("Log", "Failed to connect to server...");
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String result = str.toString();

            if(result.equals("")){
                postResult=0;
                strError="Please enter your email";
            }else {
                /*** Default Value ***/
                strStatusID="0";
                strError="Unknow Status!";

                JSONObject c;
                try {
                    c = new JSONObject(result);
                    strStatusID=c.getString("StatusID");
                    strError=c.getString("Error");
                    strVote=c.getString("Vote");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Prepare Save Data
                if (strStatusID.equals("0")) {
//                    logging.setPostResult(1);
                    postResult=1;
                } else {
                    postResult=2;
//                    logging.setPostResult(2);
                }
            }

            params[0] = strStatusID;
            params[1] = strVote;

            return params;
        }

        @Override
        protected void onPostExecute(String[] s) {
            super.onPostExecute(s);

            if(s[0].equals("0")){
                String labelText="Your vote already exists for that word. No vote was registered. Thank you";
                tvError.setTextColor(Color.parseColor("#FFFC0202"));
                tvError.setText(labelText);
            }
            if(s[0].equals("1")){
                voteView.setText(s[1]);
            }
        }
    }
}
