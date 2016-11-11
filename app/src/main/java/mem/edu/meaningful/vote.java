package mem.edu.meaningful;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
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

    public static Dialog dialog;
    private Context mContex;
    private static AppPreferences _sPref;

    public static TextView tvError;
    public static ImageView image;
    public static EditText txtEmail;
    public static TextView tvEmail;
    public static Button logOut;
    public static Button cancel;
    public static Button ok_log_vote;
    public static String voteLabel;

    public static String vote;
    public static String candidate;
    public static TextView voteView;
    public static Fragment fg;
    public static Activity activity;


    public vote(Context c, TextView vote, Fragment a, Activity ac){
        this.mContex=c;
        voteView=vote;
        activity=ac;
        fg=a;
    }

    public static View.OnClickListener btn_register = new View.OnClickListener(){

        @Override
        public void onClick(View v) {

            String[] strFirstTime=new String[3];
            strFirstTime[0] =  txtEmail.getText().toString();
            strFirstTime[1] = _sPref.getSmsBody("loc");
            strFirstTime[2] = _sPref.getSmsBody("full_loc");

            new getHttpPost().execute(strFirstTime);
        }
    };


    //======================
    public static class getHttpPost extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {


            String url = "http://www.dia40.com/oodles/meaning.php";
            StringBuilder str = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            List<NameValuePair> paramx = new ArrayList<NameValuePair>();
            paramx.add(new BasicNameValuePair("sUsername", params[0]));
            paramx.add(new BasicNameValuePair("sLocation", params[1]));
            paramx.add(new BasicNameValuePair("sCountry", params[2]));
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
                    Log.e("Log", "Failed to download result..");
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //logging.setFlag(true);

            String strStatusID="0";
            String strError="Please enter your email";
            String result = str.toString();

            if(result.equals("")){
                strStatusID="0";
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            params[0] = strStatusID;
            params[1] = strError;

            return params;
        }

        @Override
        protected void onPostExecute(String[] s) {
            super.onPostExecute(s);


            switch (s[0]) {
                case "0":
                    if(s[1].equals("Email Exists!") || s[1].equals("Email needs validation!"))
                    {
                        tvError.setVisibility(View.VISIBLE);
                        tvError.setTextColor(Color.parseColor("#FFF50B0B"));//red
                        tvError.setText(s[1]+" request passcode");
                        tvEmail.setVisibility(View.INVISIBLE);
                        txtEmail.setEnabled(false);
                        ok_log_vote.setText("Email me passcode");
                        ok_log_vote.setOnClickListener(sendCode);
                        cancel.setEnabled(true);
                        cancel.setText("Cancel");
                        cancel.setOnClickListener(btn_cancel);
                    }
                    else {
                        tvError.setText(s[1]);
                        tvError.setTextColor(Color.parseColor("#FFFC0202"));
                    }
                    break;
                case "1":
                    tvEmail.setText(s[1]);
                    txtEmail.setVisibility(View.VISIBLE);
                    tvEmail.setVisibility(View.VISIBLE);
                    txtEmail.setEnabled(false);
                    _sPref.saveSmsBody("emailflaw", txtEmail.getText().toString());
                    ok_log_vote.setText("Email me passcode");
                    ok_log_vote.setOnClickListener(sendCode);
                    cancel.setEnabled(true);
                    cancel.setText("Cancel");
                    cancel.setOnClickListener(btn_cancel);
                    break;
            }
        }
    }

    public static View.OnClickListener sendCode = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            _sPref.saveSmsBody("emailflaw",txtEmail.getText().toString());
            String emailflaw = _sPref.getSmsBody("emailflaw");
            new sendPassCodeTask().execute(emailflaw,"");
        }
    };



    //===== start of sendPassCodeTask
    public static class sendPassCodeTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {


            String urldata = "http://www.dia40.com/oodles/wflag.php";
            StringBuilder strdata = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(urldata);
            String result = "null";

            try {
                MultipartEntity entityFile = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                entityFile.addPart("strUsername", new StringBody(params[0]));

                httpPost.setEntity(entityFile);

                HttpResponse response = client.execute(httpPost);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();

                if (statusCode == 200) {

                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        strdata.append(line);
                    }
                } else {
                    Log.e("Log", "Failed to insert...");
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            result = strdata.toString();
            Log.e("Debug", result);

            String strStatusID="0";
            String strError="";

            if(result.equals("")){
                strStatusID="0";
                strError="Can not read Server";
            }else {
                JSONObject c;
                try {
                    c = new JSONObject(result);
                    strStatusID=c.getString("StatusID");
                    strError=c.getString("Error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            params[0] = strStatusID;
            params[1] = strError;
            return params;

        }

        @Override
        protected void onPostExecute(String[] val) {
            super.onPostExecute(val);

            txtEmail.setText("");
            tvError.setVisibility(View.INVISIBLE);
            tvEmail.setVisibility(View.VISIBLE);
            tvEmail.setText("Passcode sent! Check your email");
            ok_log_vote.setText("OK");
            txtEmail.setEnabled(true);
            ok_log_vote.setOnClickListener(validateCode);
        }
    }
    //===== end of sendPassCodeTask


    public static View.OnClickListener validateCode = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            new validateCodeTask().execute(txtEmail.getText().toString(),"");
        }
    };


    //===== start of validateCodeTask
    public static class validateCodeTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {


            String urldata = "http://www.dia40.com/oodles/vflag.php";
            StringBuilder strdata = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(urldata);
            String result = "null";

            try {
                MultipartEntity entityFile = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                String emailflaw = _sPref.getSmsBody("emailflaw");
                entityFile.addPart("strPass", new StringBody(params[0]));
                entityFile.addPart("strUser", new StringBody(emailflaw));

                httpPost.setEntity(entityFile);

                HttpResponse response = client.execute(httpPost);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();

                if (statusCode == 200) {

                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        strdata.append(line);
                    }
                } else {
                    Log.e("Log", "Failed to insert...");
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.e("Debug", result);

            result = strdata.toString();
            String strError="Please enter your email";
            String strStatusID ="Error in request";

            if(result.equals("")){
                strStatusID="0";
                strError="Error in request";
            }else {
                JSONObject c;
                try {
                    c = new JSONObject(result);
                    strStatusID=c.getString("StatusID");
                    strError=c.getString("Error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            params[0] = strStatusID;
            params[1] = strError;
            return params;
        }

        @Override
        protected void onPostExecute(String[] val) {
            super.onPostExecute(val);

            int postResult;
            if(val[0].equals("0"))
            {
                postResult=0;
            }else {
                postResult=1;
            }

            switch (postResult){
                case 0:tvError.setText("Wrong code");
                    tvError.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    txtEmail.setVisibility(View.INVISIBLE);
                    logOut.setVisibility(View.VISIBLE);
                    logOut.setOnClickListener(btnLogout);
                    _sPref.saveSmsBody("userId",_sPref.getSmsBody("emailflaw"));
                    tvError.setVisibility(View.INVISIBLE);
                    tvEmail.setVisibility(View.VISIBLE);
                    tvEmail.setText("Welcome back");
                    cancel.setText("CANCEL");
                    cancel.setVisibility(View.VISIBLE);
                    ok_log_vote.setText(voteLabel);
                    ok_log_vote.setOnClickListener(btn_vote_now);
                    break;
            }
        }
    }
    //===== end of validateCodeTask


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
                cancel.setText("CANCEL");
                cancel.setOnClickListener(btn_cancel);
            }
        }
    };

    public static View.OnClickListener btn_vote_now = new View.OnClickListener(){

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

    public static View.OnClickListener btn_cancel = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            dialog.dismiss();
        }
    };

    private static View.OnClickListener btnLogout = new View.OnClickListener() {
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
        dialog.setTitle("Vote");

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

                case R.id.imageButtonl5a1:_sPref.saveSmsBody("loc", "au");_sPref.saveSmsBody("full_loc", "Australia");
                    full_loc="Australia";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl5a2:_sPref.saveSmsBody("loc", "au");_sPref.saveSmsBody("full_loc", "Australia");
                    full_loc="Australia";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl5b1:_sPref.saveSmsBody("loc", "au");_sPref.saveSmsBody("full_loc", "Australia");
                    full_loc="Australia";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl5b2:_sPref.saveSmsBody("loc", "au");_sPref.saveSmsBody("full_loc", "Australia");
                    full_loc="Australia";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl5c1:_sPref.saveSmsBody("loc", "au");_sPref.saveSmsBody("full_loc", "Australia");
                    full_loc="Australia";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl5c2:_sPref.saveSmsBody("loc", "au");_sPref.saveSmsBody("full_loc", "Australia");
                    full_loc="Australia";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;

                case R.id.imageButtonl6a1:_sPref.saveSmsBody("loc", "cca");_sPref.saveSmsBody("full_loc", "Canada");
                    full_loc="Canada";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl6a2:_sPref.saveSmsBody("loc", "cca");_sPref.saveSmsBody("full_loc", "Canada");
                    full_loc="Canada";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl6b1:_sPref.saveSmsBody("loc", "cca");_sPref.saveSmsBody("full_loc", "Canada");
                    full_loc="Canada";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl6b2:_sPref.saveSmsBody("loc", "cca");_sPref.saveSmsBody("full_loc", "Canada");
                    full_loc="Canada";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl6c1:_sPref.saveSmsBody("loc", "cca");_sPref.saveSmsBody("full_loc", "Canada");
                    full_loc="Canada";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl6c2:_sPref.saveSmsBody("loc", "cca");_sPref.saveSmsBody("full_loc", "Canada");
                    full_loc="Canada";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;

                case R.id.imageButtonl7a1:_sPref.saveSmsBody("loc", "in");_sPref.saveSmsBody("full_loc", "India");
                    full_loc="India";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl7a2:_sPref.saveSmsBody("loc", "in");_sPref.saveSmsBody("full_loc", "India");
                    full_loc="India";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl7b1:_sPref.saveSmsBody("loc", "in");_sPref.saveSmsBody("full_loc", "India");
                    full_loc="India";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl7b2:_sPref.saveSmsBody("loc", "in");_sPref.saveSmsBody("full_loc", "India");
                    full_loc="India";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl7c1:_sPref.saveSmsBody("loc", "in");_sPref.saveSmsBody("full_loc", "India");
                    full_loc="India";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl7c2:_sPref.saveSmsBody("loc", "in");_sPref.saveSmsBody("full_loc", "India");
                    full_loc="India";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;

                case R.id.imageButtonl8a1:_sPref.saveSmsBody("loc", "jm");_sPref.saveSmsBody("full_loc", "Jamaica");
                    full_loc="Jamaica";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl8a2:_sPref.saveSmsBody("loc", "jm");_sPref.saveSmsBody("full_loc", "Jamaica");
                    full_loc="Jamaica";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl8b1:_sPref.saveSmsBody("loc", "jm");_sPref.saveSmsBody("full_loc", "Jamaica");
                    full_loc="Jamaica";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl8b2:_sPref.saveSmsBody("loc", "jm");_sPref.saveSmsBody("full_loc", "Jamaica");
                    full_loc="Jamaica";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl8c1:_sPref.saveSmsBody("loc", "jm");_sPref.saveSmsBody("full_loc", "Jamaica");
                    full_loc="Jamaica";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl8c2:_sPref.saveSmsBody("loc", "jm");_sPref.saveSmsBody("full_loc", "Jamaica");
                    full_loc="Jamaica";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;

                case R.id.imageButtonl9a1:_sPref.saveSmsBody("loc", "ng");_sPref.saveSmsBody("full_loc", "Nigeria");
                    full_loc="Nigeria";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl9a2:_sPref.saveSmsBody("loc", "ng");_sPref.saveSmsBody("full_loc", "Nigeria");
                    full_loc="Nigeria";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl9b1:_sPref.saveSmsBody("loc", "ng");_sPref.saveSmsBody("full_loc", "Nigeria");
                    full_loc="Nigeria";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl9b2:_sPref.saveSmsBody("loc", "ng");_sPref.saveSmsBody("full_loc", "Nigeria");
                    full_loc="Nigeria";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl9c1:_sPref.saveSmsBody("loc", "ng");_sPref.saveSmsBody("full_loc", "Nigeria");
                    full_loc="Nigeria";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl9c2:_sPref.saveSmsBody("loc", "ng");_sPref.saveSmsBody("full_loc", "Nigeria");
                    full_loc="Nigeria";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;

                case R.id.imageButtonl10a1:_sPref.saveSmsBody("loc", "sg");_sPref.saveSmsBody("full_loc", "Singapore");
                    full_loc="Singapore";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl10a2:_sPref.saveSmsBody("loc", "sg");_sPref.saveSmsBody("full_loc", "Singapore");
                    full_loc="Singapore";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl10b1:_sPref.saveSmsBody("loc", "sg");_sPref.saveSmsBody("full_loc", "Singapore");
                    full_loc="Singapore";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl10b2:_sPref.saveSmsBody("loc", "sg");_sPref.saveSmsBody("full_loc", "Singapore");
                    full_loc="Singapore";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl10c1:_sPref.saveSmsBody("loc", "sg");_sPref.saveSmsBody("full_loc", "Singapore");
                    full_loc="Singapore";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl10c2:_sPref.saveSmsBody("loc", "sg");_sPref.saveSmsBody("full_loc", "Singapore");
                    full_loc="Singapore";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;

                case R.id.imageButtonl11a1:_sPref.saveSmsBody("loc", "za");_sPref.saveSmsBody("full_loc", "South Africa");
                    full_loc="South Africa";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl11a2:_sPref.saveSmsBody("loc", "za");_sPref.saveSmsBody("full_loc", "South Africa");
                    full_loc="South Africa";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl11b1:_sPref.saveSmsBody("loc", "za");_sPref.saveSmsBody("full_loc", "South Africa");
                    full_loc="South Africa";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl11b2:_sPref.saveSmsBody("loc", "za");_sPref.saveSmsBody("full_loc", "South Africa");
                    full_loc="South Africa";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl11c1:_sPref.saveSmsBody("loc", "za");_sPref.saveSmsBody("full_loc", "South Africa");
                    full_loc="South Africa";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl11c2:_sPref.saveSmsBody("loc", "za");_sPref.saveSmsBody("full_loc", "South Africa");
                    full_loc="South Africa";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;

                case R.id.imageButtonl12a1:_sPref.saveSmsBody("loc", "tt");_sPref.saveSmsBody("full_loc", "Trinidad and Tobago");
                    full_loc="Trinidad and Tobago";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl12a2:_sPref.saveSmsBody("loc", "tt");_sPref.saveSmsBody("full_loc", "Trinidad and Tobago");
                    full_loc="Trinidad and Tobago";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl12b1:_sPref.saveSmsBody("loc", "tt");_sPref.saveSmsBody("full_loc", "Trinidad and Tobago");
                    full_loc="Trinidad and Tobago";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl12b2:_sPref.saveSmsBody("loc", "tt");_sPref.saveSmsBody("full_loc", "Trinidad and Tobago");
                    full_loc="Trinidad and Tobago";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl12c1:_sPref.saveSmsBody("loc", "tt");_sPref.saveSmsBody("full_loc", "Trinidad and Tobago");
                    full_loc="Trinidad and Tobago";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl12c2:_sPref.saveSmsBody("loc", "tt");_sPref.saveSmsBody("full_loc", "Trinidad and Tobago");
                    full_loc="Trinidad and Tobago";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;

                case R.id.imageButtonl13a1:_sPref.saveSmsBody("loc", "uk");_sPref.saveSmsBody("full_loc", "United Kingdom");
                    full_loc="United Kingdom";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl13a2:_sPref.saveSmsBody("loc", "uk");_sPref.saveSmsBody("full_loc", "United Kingdom");
                    full_loc="United Kingdom";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl13b1:_sPref.saveSmsBody("loc", "uk");_sPref.saveSmsBody("full_loc", "United Kingdom");
                    full_loc="United Kingdom";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl13b2:_sPref.saveSmsBody("loc", "uk");_sPref.saveSmsBody("full_loc", "United Kingdom");
                    full_loc="United Kingdom";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl13c1:_sPref.saveSmsBody("loc", "uk");_sPref.saveSmsBody("full_loc", "United Kingdom");
                    full_loc="United Kingdom";voteLabel="UP VOTE";vote="1";candidate=_sPref.getSmsBody(id.toString());break;
                case R.id.imageButtonl13c2:_sPref.saveSmsBody("loc", "uk");_sPref.saveSmsBody("full_loc", "United Kingdom");
                    full_loc="United Kingdom";voteLabel="DOWN VOTE";vote="-1";candidate=_sPref.getSmsBody(id.toString());break;
            }

        //Australia au
//        Canada ca use-> cca for android
//        India in
        //Jamaica jm
        //Nigeria ng
//        Singapore sg
//        South Africa za
//        Trinidad and Tobago tt
//        United Kingdom uk

        String labelText="Voting in: \n"+full_loc+", are you sure?\n_________________\nYou won\'t be able to change your vote or Location";
        tvError.setText(labelText);
        ok_log_vote.setOnClickListener(btn_login_stat);
        cancel.setOnClickListener(btn_cancel);


        if(MainActivity.network) {
            dialog.show();
        }else {
            Toast.makeText(mContex, "No Network connexion", Toast.LENGTH_LONG).show();
        }

    }

    //======================
    public static class postVote extends AsyncTask<String, Void, String[]> {


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
            String strStatusID="0";
            String strError="";
            String strVote="0";

            if(result.equals("")){
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
            }
            params[0] = strStatusID;
            params[1] = strVote;
            params[2] = strError;

            return params;
        }

        @Override
        protected void onPostExecute(String[] s) {
            super.onPostExecute(s);

            if(s[0].equals("0")){
                tvError.setTextColor(Color.parseColor("#FFFC0202"));
                tvError.setVisibility(View.VISIBLE);
                tvError.setText(s[2]);
            }
            if(s[0].equals("1")){
                tvEmail.setText("Counted!");
                tvEmail.setVisibility(View.VISIBLE);
                new RecordingLayout(activity, fg).execute();
            }
        }
    }
}
