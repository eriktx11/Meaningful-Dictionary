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

import javax.xml.transform.stream.StreamResult;


/**
 * Created by erikllerena on 9/29/16.
 */

public class LoginPlease {
    //extends Activity {

//    private
//    private
//    private
//    private

    static String strStatusID;
    static int postResult;
    static String strError;
    static boolean getFlag;
    //static ProgressDialog pDialog;
    //static boolean flag=false;

//    LoginPlease(){
//        this.strError="";
//        this.postResult=0;
//        this.strStatusID="";
//        this.getFlag=false;
//        //pDialog = new ProgressDialog(c);
//    }
//
//    public int getPostResult() {
//        return this.postResult;
//    }
//
//    public String getStrError() {
//        return this.strError;
//    }
//
//    public String getStrStatusID() {
//        return this.strStatusID;
//    }
//
//    public boolean getFlag(){
//        return this.getFlag;
//    }
//
//    public void setFlag(boolean flag){
//        this.getFlag=flag;
//    }
//
//    public void setPostResult(int result) {
//        this.postResult=result;
//    }
//
//    public void setStrError(String error) {
//        this.strError=error;
//    }
//
//    public void setStrStatusID(String status) {
//        this.strStatusID=status;
//    }


   //======================
   static class getHttpPost extends AsyncTask<String, Void, String> {

      // LoginPlease logging= new LoginPlease();
//       @Override
//       protected void onPreExecute() {
//           super.onPreExecute();
//           // Set progressbar title
//           pDialog.setTitle("Meaningful Dictionary");
//           // Set progressbar message
//           pDialog.setMessage("Loading...");
//           pDialog.setIndeterminate(false);
//           // Show progressbar
//           pDialog.show();
//       }

        @Override
        protected String doInBackground(String... params) {

//        if(params[0].equals("")){
//            return "";
//        } else {

            String url = "http://www.dia40.com/oodles/meaning.php";
            StringBuilder str = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            List<NameValuePair> paramx = new ArrayList<NameValuePair>();
            paramx.add(new BasicNameValuePair("sUsername", params[0]));
            paramx.add(new BasicNameValuePair("sLocation", params[1]));
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

            String result = str.toString();

            if(result.equals("")){
                postResult=0;
                strError="Please enter your email";
//                logging.setPostResult(0);
//                logging.setStrError("Please enter your email");
            }else {
                /*** Default Value ***/
                strStatusID="0";
                strError="Unknow Status!";
//                logging.setStrStatusID("0");
//                logging.setStrError("Unknow Status!");

                JSONObject c;
                try {
                    c = new JSONObject(result);
                    strStatusID=c.getString("StatusID");
                    strError=c.getString("Error");
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

            return str.toString();
           // }
        }

//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//
//            if(result.equals("")){
//                loging.setPostResult(0);
//                loging.setStrError("Please enter your email");
//            }else {
//                /*** Default Value ***/
//                loging.setStrStatusID("0");
//                loging.setStrError("Unknow Status!");
//
//                JSONObject c;
//                try {
//                    c = new JSONObject(result);
//                    loging.setStrStatusID(c.getString("StatusID"));
//                    loging.setStrError(c.getString("Error"));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                // Prepare Save Data
//                if (loging.getStrStatusID().equals("0")) {
//                    loging.setPostResult(1);
//                } else {
//                    loging.setPostResult(2);
//                }
//            }
//           // pDialog.dismiss();
//        }

    }
}

