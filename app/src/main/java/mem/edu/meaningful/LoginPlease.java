package mem.edu.meaningful;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
import org.w3c.dom.NodeList;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by erikllerena on 9/29/16.
 */

public class LoginPlease {
    //extends Activity {

    static String strStatusID;
    static int postResult;
    static ProgressDialog pDialog;

    LoginPlease(Context c){
        pDialog = new ProgressDialog(c);
    }

//    @Override
//    public Context getApplicationContext() {
//        return null;
//    }


//    @SuppressLint("NewApi")
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        //setContentView(R.layout.save_accent);
//
//        // Permission StrictMode
////        if (android.os.Build.VERSION.SDK_INT > 9) {
////            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
////            StrictMode.setThreadPolicy(policy);
////        }
//        pDialog = new ProgressDialog(LoginPlease.this);
//    }


//
//    static int getHttpPost(String params) {
//
//        String url = "http://www.dia40.com/oodles/meaning.php";
//        StringBuilder str = new StringBuilder();
//        HttpClient client = new DefaultHttpClient();
//        HttpPost httpPost = new HttpPost(url);
//
//        List<NameValuePair> paramx = new ArrayList<NameValuePair>();
//        paramx.add(new BasicNameValuePair("sUsername", params));
//
//        try {
//            httpPost.setEntity(new UrlEncodedFormEntity(paramx));
//            HttpResponse response = client.execute(httpPost);
//            StatusLine statusLine = response.getStatusLine();
//            int statusCode = statusLine.getStatusCode();
//            if (statusCode == 200) { // Status OK
//                HttpEntity entity = response.getEntity();
//                InputStream content = entity.getContent();
//                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    str.append(line);
//                }
//            } else {
//                Log.e("Log", "Failed to download result..");
//            }
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        String result= str.toString();
//
//
//        /*** Default Value ***/
//        strStatusID = "0";
//        String strError = "Unknow Status!";
//
//        JSONObject c;
//        try {
//            c = new JSONObject(result);
//            strStatusID = c.getString("StatusID");
//            strError = c.getString("Error");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        // Prepare Save Data
//        if (strStatusID.equals("0")) {
//            postResult = 1;
//        }else {
//            postResult = 2;
//        }
//
//        return postResult;
//
//    }


   //======================
   static class getHttpPost extends AsyncTask<String, Void, String> {


       @Override
       protected void onPreExecute() {
           super.onPreExecute();
           // Create a progressbar

           // Set progressbar title
           pDialog.setTitle("Meaningful Dictionary");
           // Set progressbar message
           pDialog.setMessage("Loading...");
           pDialog.setIndeterminate(false);
           // Show progressbar
           pDialog.show();
       }

        @Override
        protected String doInBackground(String... params) {

        if(params[0].equals("")){
            return "";
        } else {

            String url = "http://www.dia40.com/oodles/meaning.php";
            StringBuilder str = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            List<NameValuePair> paramx = new ArrayList<NameValuePair>();
            paramx.add(new BasicNameValuePair("sUsername", params[0]));
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
            return str.toString();
            }

        }


        @Override
        protected void onPostExecute(String result) {

            if(result.equals("")){
                postResult = 0;
            }else {
                /*** Default Value ***/
                strStatusID = "0";
                String strError = "Unknow Status!";

                JSONObject c;
                try {
                    c = new JSONObject(result);
                    strStatusID = c.getString("StatusID");
                    strError = c.getString("Error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Prepare Save Data
                if (strStatusID.equals("0")) {
                    postResult = 1;
                } else {
                    postResult = 2;
                }
            }
            pDialog.dismiss();
        }

    }

    //==================================


//    public class SaveData()
//    {
//
//        // Check Email
//        if(userInput.length() == 0)
//        {
//            postResult = 0;
//        }
//
//        new getHttpPost().execute(userInput);
//        //return null;
//    }


}

//    @SuppressLint("NewApi")
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.save_accent);
//
//        // Permission StrictMode
//        if (android.os.Build.VERSION.SDK_INT > 9) {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//        }
//
//        // btnSave
//        final Button btnSave = (Button) findViewById(R.id.btnSave);
//        // Perform action on click
//        btnSave.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if(SaveData())
//                {
//                    //When Save Complete
//                }
//            }
//        });
//
//    }
//
//    public String strStatusID;
//
//    public boolean SaveData()
//    {
//
//        // txtUsername,txtPassword,txtName,txtEmail,txtTel
//        final EditText txtUsername = (EditText)findViewById(R.id.txtUsername);
//        final EditText txtPassword = (EditText)findViewById(R.id.txtPassword);
//        final EditText txtConPassword = (EditText)findViewById(R.id.txtConPassword);
//        final EditText txtName = (EditText)findViewById(R.id.txtName);
//        final EditText txtEmail = (EditText)findViewById(R.id.txtEmail);
//        final EditText txtTel = (EditText)findViewById(R.id.txtTel);
//
//
//        // Dialog
//        final AlertDialog.Builder ad = new AlertDialog.Builder(this);
//
//        ad.setTitle("Error! ");
//        ad.setIcon(android.R.drawable.btn_star_big_on);
//        ad.setPositiveButton("Close", null);
//
//        // Check Username
//        if(txtUsername.getText().length() == 0)
//        {
//            ad.setMessage("Please input [Username] ");
//            ad.show();
//            txtUsername.requestFocus();
//            return false;
//        }
//        // Check Password
//        if(txtPassword.getText().length() == 0 || txtConPassword.getText().length() == 0 )
//        {
//            ad.setMessage("Please input [Password/Confirm Password] ");
//            ad.show();
//            txtPassword.requestFocus();
//            return false;
//        }
//        // Check Password and Confirm Password (Match)
//        if(!txtPassword.getText().toString().equals(txtConPassword.getText().toString()))
//        {
//            ad.setMessage("Password and Confirm Password Not Match! ");
//            ad.show();
//            txtConPassword.requestFocus();
//            return false;
//        }
//        // Check Name
//        if(txtName.getText().length() == 0)
//        {
//            ad.setMessage("Please input [Name] ");
//            ad.show();
//            txtName.requestFocus();
//            return false;
//        }
//        // Check Email
//        if(txtEmail.getText().length() == 0)
//        {
//            ad.setMessage("Please input [Email] ");
//            ad.show();
//            txtEmail.requestFocus();
//            return false;
//        }
//        // Check Tel
//        if(txtTel.getText().length() == 0)
//        {
//            ad.setMessage("Please input [Tel] ");
//            ad.show();
//            txtTel.requestFocus();
//            return false;
//        }
//
//
//        String url = "http://www.dia40.com/oodles/meaning.php";
//
//        List<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair("sUsername", txtUsername.getText().toString()));
//        params.add(new BasicNameValuePair("sPassword", txtPassword.getText().toString()));
//        params.add(new BasicNameValuePair("sName", txtName.getText().toString()));
//        params.add(new BasicNameValuePair("sEmail", txtEmail.getText().toString()));
//        params.add(new BasicNameValuePair("sTel", txtTel.getText().toString()));
//
//        /** Get result from Server (Return the JSON Code)
//         * StatusID = ? [0=Failed,1=Complete]
//         * Error	= ?	[On case error return custom error message]
//         *
//         * Eg Save Failed = {"StatusID":"0","Error":"Email Exists!"}
//         * Eg Save Complete = {"StatusID":"1","Error":""}
//         */
//
//        String resultServer  = getHttpPost(url,params);
//
//        /*** Default Value ***/
//        strStatusID = "0";
//        String strError = "Unknow Status!";
//
//        JSONObject c;
//        try {
//            c = new JSONObject(resultServer);
//            strStatusID = c.getString("StatusID");
//            strError = c.getString("Error");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        // Prepare Save Data
//        if(strStatusID.equals("0"))
//        {
//            ad.setMessage(strError);
//            ad.show();
//        }
//        else
//        {
//            _sPref.saveSmsBody( "userId",txtEmail.getText().toString() );
//            Toast.makeText(LoginPlease.this, "Save Data Successfully", Toast.LENGTH_SHORT).show();
//            txtUsername.setText("");
//            txtPassword.setText("");
//            txtConPassword.setText("");
//            txtName.setText("");
//            txtEmail.setText("");
//            txtTel.setText("");
//        }
//        return true;
//    }
//
//
//    public String getHttpPost(String url,List<NameValuePair> params) {
//        StringBuilder str = new StringBuilder();
//        HttpClient client = new DefaultHttpClient();
//        HttpPost httpPost = new HttpPost(url);
//
//        try {
//            httpPost.setEntity(new UrlEncodedFormEntity(params));
//            HttpResponse response = client.execute(httpPost);
//            StatusLine statusLine = response.getStatusLine();
//            int statusCode = statusLine.getStatusCode();
//            if (statusCode == 200) { // Status OK
//                HttpEntity entity = response.getEntity();
//                InputStream content = entity.getContent();
//                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    str.append(line);
//                }
//            } else {
//                Log.e("Log", "Failed to download result..");
//            }
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return str.toString();
//    }
//
//}

