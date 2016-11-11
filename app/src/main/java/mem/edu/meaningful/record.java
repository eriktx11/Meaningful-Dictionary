package mem.edu.meaningful;

import android.app.Activity;
import android.app.Dialog;
import android.content.UriPermission;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by erikllerena on 10/24/16.
 */
public class record extends Activity implements View.OnClickListener {


    // gallery request code.
    public static final int GALLEY_REQUEST_CODE = 10;
    String ROOT_URL = "http://www.dia40.com";
    // tag to print logs.
    private String TAG = record.class.getSimpleName();
    private ImageView image;

    int rc_id;
    private Context mContex;
    public static Activity activity;
    public static Fragment fg;


    public record(Context c, Fragment a, Activity ac) {
        this.mContex=c;
        activity=ac;
        fg=a;
    }

    public static AppPreferences _sPref;

    public static EditText txtEmail;
    public static TextView tvEmail;
    public static TextView tvError;

    public static Dialog dialog;

    public static Button choose;
    public static Button upload;
    public static Button logOut;

    public static int postResult;
    public static String strStatusID;
    public static String strError;

    public static View.OnClickListener btnChoose = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (!_sPref.getAll().containsKey("userId")) {

                String[] strFirstTime=new String[3];
                strFirstTime[0] =  txtEmail.getText().toString();
                strFirstTime[1] = _sPref.getSmsBody("loc");
                strFirstTime[2] = _sPref.getSmsBody("full_loc");
                new getHttpPost().execute(strFirstTime);

            } else {
                tvError.setVisibility(View.INVISIBLE);
                logOut.setVisibility(View.VISIBLE);
                logOut.setOnClickListener(btnLogout);
                // this will open audio folder to choose file.
                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                fg.startActivityForResult(Intent.createChooser(openGallery, "Select Audio"), GALLEY_REQUEST_CODE);
            }
        }
    };


    //======================
    static class getHttpPost extends AsyncTask<String, Void, String[]> {

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

            String result = str.toString();

            if(result.equals("")){
                strStatusID="0";
                strError="Unknown error";
            }else {
                /*** Default Value ***/
                strStatusID="0";
                strError="Unknown error";
                JSONObject c;
                try {
                    c = new JSONObject(result);
                    strStatusID=c.getString("StatusID");
                    strError=c.getString("Error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            params[0]=strStatusID;
            params[1]=strError;

            return params;
        }

        @Override
        protected void onPostExecute(String[] s) {
            super.onPostExecute(s);

            if(s[0].equals("0")){
                postResult=1;
            }else {
                postResult=2;
            }

            switch (postResult) {
                case 1:
                    if (s[1].equals("Email Exists!") || s[1].equals("Email needs validation!")) {
                        tvError.setVisibility(View.VISIBLE);
                        tvError.setTextColor(Color.parseColor("#FFF50B0B"));//red
                        tvError.setText(s[1] + " request passcode");
                        tvEmail.setVisibility(View.INVISIBLE);
                        txtEmail.setEnabled(false);
                        choose.setText("Email me passcode");
                        choose.setOnClickListener(sendCode);
                        upload.setEnabled(true);
                        upload.setText("Cancel");
                        upload.setOnClickListener(cancel);
                    }else
                    {
                        tvError.setVisibility(View.VISIBLE);
                        tvError.setTextColor(Color.parseColor("#FFF50B0B"));//red
                        tvError.setText(s[1]);
                    }
                    break;
                case 2:
                    tvEmail.setText(s[1]);
                    txtEmail.setVisibility(View.VISIBLE);
                    tvEmail.setVisibility(View.VISIBLE);
                    txtEmail.setEnabled(false);
                    _sPref.saveSmsBody("emailflaw", txtEmail.getText().toString());
                    choose.setText("Email me passcode");
                    choose.setOnClickListener(sendCode);
                    upload.setEnabled(true);
                    upload.setText("Cancel");
                    upload.setOnClickListener(cancel);
//                        tvEmail.setVisibility(View.INVISIBLE);
//                        txtEmail.setVisibility(View.INVISIBLE);
//                        tvError.setVisibility(View.INVISIBLE);
//                        _sPref.saveSmsBody("userId", _sPref.getSmsBody("emailflaw"));
//                        logOut.setVisibility(View.VISIBLE);
//                        logOut.setOnClickListener(btnLogout);
//                        upload.setEnabled(false);
//                        upload.setOnClickListener(btnUpload);
//                        // this will open audio folder to choose file.
//                        Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
//                        fg.startActivityForResult(Intent.createChooser(openGallery, "Select Audio"), GALLEY_REQUEST_CODE);
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
                    choose.setText("OK");
                    txtEmail.setEnabled(true);
                    choose.setOnClickListener(validateCode);
        }
    }
    //===== end of sendPassCodeTask


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

            result = strdata.toString();
            Log.e("Debug", result);

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

            if(val[0].equals("0"))
            {
                postResult=0;
            }else {
                postResult=1;
            }

            switch (postResult){
                case 0:tvError.setText("Wrong code");
                       tvError.setVisibility(View.VISIBLE);break;
                case 1:
                    txtEmail.setVisibility(View.INVISIBLE);
                    logOut.setVisibility(View.VISIBLE);
                    logOut.setOnClickListener(btnLogout);
                    _sPref.saveSmsBody("userId",_sPref.getSmsBody("emailflaw"));
                    tvError.setVisibility(View.INVISIBLE);
                    tvEmail.setVisibility(View.VISIBLE);
                    tvEmail.setText("Welcome back");
                    choose.setText("CHOOSE AUDIO");
                    choose.setOnClickListener(btnChoose);
                    choose.setVisibility(View.VISIBLE);
                    choose.setEnabled(true);
                    upload.setText("UPLOAD");
                    upload.setEnabled(false);
                    upload.setOnClickListener(btnUpload);
                    upload.setVisibility(View.VISIBLE);break;
            }
        }
    }
    //===== end of validateCodeTask

    public static View.OnClickListener validateCode = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            new validateCodeTask().execute(txtEmail.getText().toString(),"");
        }
    };

    public static View.OnClickListener cancel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            dialog.dismiss();
        }
    };

    @Override
    public void onClick(View v) {

        _sPref = new AppPreferences(mContex);
        switch (v.getId()){//{"ca", "ny", "tn", "tx", "jm", "ng"}
            case R.id.rcrbtn1:_sPref.saveSmsBody("loc", "ca");_sPref.saveSmsBody("full_loc", "California");break;
            case R.id.rcrbtn2:_sPref.saveSmsBody("loc", "ny");_sPref.saveSmsBody("full_loc", "New York");break;
            case R.id.rcrbtn3:_sPref.saveSmsBody("loc", "tn");_sPref.saveSmsBody("full_loc", "Tennessee");break;
            case R.id.rcrbtn4:_sPref.saveSmsBody("loc", "tx");_sPref.saveSmsBody("full_loc", "Texas");break;

            case R.id.rcrbtn5:_sPref.saveSmsBody("loc", "au");_sPref.saveSmsBody("full_loc", "Australia");break;
            case R.id.rcrbtn6:_sPref.saveSmsBody("loc", "cca");_sPref.saveSmsBody("full_loc", "Canada");break;
            case R.id.rcrbtn7:_sPref.saveSmsBody("loc", "in");_sPref.saveSmsBody("full_loc", "India");break;
            case R.id.rcrbtn8:_sPref.saveSmsBody("loc", "jm");_sPref.saveSmsBody("full_loc", "Jamaica");break;
            case R.id.rcrbtn9:_sPref.saveSmsBody("loc", "ng");_sPref.saveSmsBody("full_loc", "Nigeria");break;
            case R.id.rcrbtn10:_sPref.saveSmsBody("loc", "sg");_sPref.saveSmsBody("full_loc", "Singapore");break;
            case R.id.rcrbtn11:_sPref.saveSmsBody("loc", "za");_sPref.saveSmsBody("full_loc", "South Africa");break;
            case R.id.rcrbtn12:_sPref.saveSmsBody("loc", "tt");_sPref.saveSmsBody("full_loc", "Trinidad and Tobago");break;
            case R.id.rcrbtn13:_sPref.saveSmsBody("loc", "uk");_sPref.saveSmsBody("full_loc", "United Kingdom");break;
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

        dialog = new Dialog(mContex);
        dialog.setContentView(R.layout.recording_box);
        dialog.setTitle("Upload");

        SoundFragment.realUri=null;


        image = (ImageView) dialog.findViewById(R.id.imageId);
        txtEmail = (EditText) dialog.findViewById(R.id.txtEmail);
        tvEmail = (TextView) dialog.findViewById(R.id.tvEmailId);
        tvError = (TextView) dialog.findViewById(R.id.txtError);
        logOut = (Button) dialog.findViewById(R.id.logOutId);

        choose = (Button) dialog.findViewById(R.id.btn_choose);
        upload = (Button) dialog.findViewById(R.id.btn_upload);

        if (!_sPref.getAll().containsKey("userId")) {

            String labelText="Uploading in: \n"+_sPref.getSmsBody("full_loc")+", are you sure?\n_________________\nYou won\'t be able to change your vote or Location";
            tvEmail.setVisibility(View.VISIBLE);
            tvEmail.setText(labelText);
            choose.setText("OK");
            choose.setOnClickListener(btn_agree);
            upload.setText("CANCEL");
            upload.setEnabled(true);
            upload.setOnClickListener(btn_cancel);
        }else {
            logOut.setVisibility(View.VISIBLE);
            logOut.setOnClickListener(btnLogout);

            choose.setOnClickListener(btnChoose);
            upload.setOnClickListener(btnUpload);
        }


        if(MainActivity.network) {
            dialog.show();
        }else {
            Toast.makeText(mContex, "No Network connexion", Toast.LENGTH_LONG).show();
        }
    }

    public static View.OnClickListener btn_agree = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            choose.setText("CHOOSE AUDIO");
            choose.setOnClickListener(btnChoose);
            upload.setEnabled(false);
            upload.setText("UPLOAD");
            upload.setOnClickListener(btnUpload);
            txtEmail.setVisibility(View.VISIBLE);
            tvEmail.setText("User not recognized. Please enter your valid email");
//            if (txtEmail.requestFocus()) {
//                activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//            }
        }
    };

    public static View.OnClickListener btn_cancel = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            dialog.dismiss();
        }
    };


    public static class doFileUpload extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {

            params = new String[2];
            params[0] = "0";
            params[1] = "Error: Please select a file";
         try {

                String existingFileName = SoundFragment.realUri.toString();
                if(existingFileName==null){
                   return params;
                  }

                String urldata = "http://www.dia40.com/oodles/fileUpload.php";
                StringBuilder strdata = new StringBuilder();
                HttpClient client = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(urldata);
                String result = "null";

              try {
                MultipartEntity entityFile = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                File myFile = new File(existingFileName);
                FileBody fileBody = new FileBody(myFile);
                entityFile.addPart("uploadedfile", fileBody);
                entityFile.addPart("aWord", new StringBody(_sPref.getSmsBody("key")));
                entityFile.addPart("strUsername", new StringBody(_sPref.getSmsBody("userId")));
                entityFile.addPart("strLocation", new StringBody(_sPref.getSmsBody("loc")));

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

                if(result.equals("")){
                    strStatusID="0";
                    strError="Can not overwrite. File already exists\n" +
                            "If down vote, it will be deleted";
                    params[0] = strStatusID;
                    params[1] = strError;
                }else {
                    JSONObject c;
                    try {
                        c = new JSONObject(result);
                        strStatusID=c.getString("StatusID");
                        strError=c.getString("Error");
                        params[0] = strStatusID;
                        params[1] = strError;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
          }catch (RuntimeException e){
             e.printStackTrace();
          }

            return params;

        }

        @Override
        protected void onPostExecute(String[] val) {
            super.onPostExecute(val);

            if(val[0].equals("1")){
                postResult=1;
            }
            else {
                postResult=0;
            }

            switch (postResult){
                case 0:
                tvError.setVisibility(View.VISIBLE);
                tvError.setTextColor(Color.parseColor("#FFF50B0B"));//red
                tvError.setText(val[1]);//all kinds of errors
                upload.setEnabled(false);break;
                case 1:
                tvError.setVisibility(View.VISIBLE);
                tvError.setTextColor(Color.parseColor("#FF25E248"));//green color
                tvError.setText(val[1]);//upload success
                upload.setEnabled(false);
                new RecordingLayout(activity, fg).execute();
                break;
            }
        }
    }


    public static View.OnClickListener btnUpload = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (_sPref.getAll().containsKey("userId")) {
                 new doFileUpload().execute(_sPref.getSmsBody("userId"));
            }
        }
    };

    public static View.OnClickListener btnLogout = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            _sPref.removePref("userId");
            tvEmail.setText("User not detected. Please enter a valid email address");
            txtEmail.setText("");
            tvError.setText("");
            logOut.setVisibility(View.INVISIBLE);
            tvEmail.setVisibility(View.VISIBLE);
            txtEmail.setVisibility(View.VISIBLE);
        }
    };

}
