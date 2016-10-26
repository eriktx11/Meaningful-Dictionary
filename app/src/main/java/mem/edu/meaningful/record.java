package mem.edu.meaningful;

import android.app.Activity;
import android.app.Dialog;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    private Activity activity;
    private Fragment fg;

    public record(Context c, Fragment a, Activity ac) {
        this.mContex=c;
        this.activity=ac;
        this.fg=a;
    }

    private AppPreferences _sPref;

    EditText txtEmail;
    TextView tvEmail;
    TextView tvError;

    public Dialog dialog;

    Button choose;
    Button upload;
    Button logOut;
    String locationStr;

    public View.OnClickListener btnChoose = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (!_sPref.getAll().containsKey("userId")) {

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

                // while (!login.getFlag()) {

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // }

                switch (login.postResult) {
                    case 0:
                        tvError.setVisibility(View.VISIBLE);
                        tvError.setText(login.strError);
                        break;
                    case 1:
                        tvError.setVisibility(View.VISIBLE);
                        tvError.setText(login.strError);
                        break;
                    case 2:
                        tvEmail.setVisibility(View.INVISIBLE);
                        tvError.setVisibility(View.INVISIBLE);
                        txtEmail.setVisibility(View.INVISIBLE);
                        _sPref.saveSmsBody("userId", txtEmail.getText().toString());
                        upload.setEnabled(true);
                        logOut.setVisibility(View.VISIBLE);
                        logOut.setOnClickListener(btnLogout);
                        // this will open audio folder to choose file.
                        Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                        fg.startActivityForResult(Intent.createChooser(openGallery, "Select Audio"), GALLEY_REQUEST_CODE);
                        break;
                }
            } else {
                logOut.setVisibility(View.VISIBLE);
                logOut.setOnClickListener(btnLogout);
                // this will open audio folder to choose file.
                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                fg.startActivityForResult(Intent.createChooser(openGallery, "Select Audio"), GALLEY_REQUEST_CODE);
                upload.setEnabled(true);
            }
        }
    };

    @Override
    public void onClick(View v) {

        dialog = new Dialog(mContex);
        dialog.setContentView(R.layout.recording_box);
        dialog.setTitle("Upload");

        _sPref = new AppPreferences(mContex);

        image = (ImageView) dialog.findViewById(R.id.imageId);
        txtEmail = (EditText) dialog.findViewById(R.id.txtEmail);
        tvEmail = (TextView) dialog.findViewById(R.id.tvEmailId);
        tvError = (TextView) dialog.findViewById(R.id.txtError);
        logOut = (Button) dialog.findViewById(R.id.logOutId);

        if (!_sPref.getAll().containsKey("userId")) {

            switch (v.getId()){
                case R.id.rcrbtn1:_sPref.saveSmsBody("loc", "ak");_sPref.saveSmsBody("full_loc", "Alaska");break;
                case R.id.rcrbtn2:_sPref.saveSmsBody("loc", "al");_sPref.saveSmsBody("full_loc", "Alabama");break;
            }

            tvEmail.setVisibility(View.VISIBLE);
            txtEmail.setVisibility(View.VISIBLE);

            if (txtEmail.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        }else {logOut.setVisibility(View.VISIBLE);}

        choose = (Button) dialog.findViewById(R.id.btn_choose);
        upload = (Button) dialog.findViewById(R.id.btn_upload);

        choose.setOnClickListener(btnChoose);
        upload.setOnClickListener(btnUpload);

        dialog.show();
    }


    //            HttpClient client = new DefaultHttpClient();
//            HttpPost post = new HttpPost(urlString);
//            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
//
//            final File file = new File(existingFileName);
//            FileBody fb = new FileBody(file);
//
//            builder.addPart("file", fb);
//            builder.addTextBody("email", params[0]);
//            final HttpEntity yourEntity = builder.build();
//
//
//            class ProgressiveEntity implements HttpEntity {
//                @Override
//                public void consumeContent() throws IOException {
//                    yourEntity.consumeContent();
//                }
//
//                @Override
//                public InputStream getContent() throws IOException,
//                        IllegalStateException {
//                    return yourEntity.getContent();
//                }
//
//                @Override
//                public org.apache.http.Header getContentEncoding() {
//                    return yourEntity.getContentEncoding();
//                }
//
//                @Override
//                public long getContentLength() {
//                    return yourEntity.getContentLength();
//                }
//
//                @Override
//                public org.apache.http.Header getContentType() {
//                    return yourEntity.getContentType();
//                }
//
//                @Override
//                public boolean isChunked() {
//                    return yourEntity.isChunked();
//                }
//
//                @Override
//                public boolean isRepeatable() {
//                    return yourEntity.isRepeatable();
//                }
//
//                @Override
//                public boolean isStreaming() {
//                    return yourEntity.isStreaming();
//                }
//
//                @Override
//                public void writeTo(OutputStream outstream) throws IOException {
//
//                    class ProxyOutputStream extends FilterOutputStream {
//                        /**
//                         * @author Stephen Colebourne
//                         */
//
//                        public ProxyOutputStream(OutputStream proxy) {
//                            super(proxy);
//                        }
//
//                        public void write(int idx) throws IOException {
//                            out.write(idx);
//                        }
//
//                        public void write(byte[] bts) throws IOException {
//                            out.write(bts);
//                        }
//
//                        public void write(byte[] bts, int st, int end) throws IOException {
//                            out.write(bts, st, end);
//                        }
//
//                        public void flush() throws IOException {
//                            out.flush();
//                        }
//
//                        public void close() throws IOException {
//                            out.close();
//                        }
//                    }
//
//                    class ProgressiveOutputStream extends ProxyOutputStream {
//                        public ProgressiveOutputStream(OutputStream proxy) {
//                            super(proxy);
//                        }
//
//                        public void write(byte[] bts, int st, int end) throws IOException {
//
//                            // FIXME  Put your progress bar stuff here!
//
//                            out.write(bts, st, end);
//                        }
//                    }
//
//                    yourEntity.writeTo(new ProgressiveOutputStream(outstream));
//                }
//
//            }
//            ;
//            ProgressiveEntity myEntity = new ProgressiveEntity();
//            post.setEntity(myEntity);
//
//
//            HttpResponse response = null;
//            try {
//                response = client.execute(post);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//
//            BufferedReader rd = null;
//            try {
//                rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            String body = "";
//            String content = "";
//
//            try {
//                while ((body = rd.readLine()) != null) {
//                    content += body + "\n";
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

    public class doFileUpload extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {


//---file
            String existingFileName = SoundFragment.realUri.toString();
//            FileInputStream fileInputStream = null;
//            byte[] buffer;
//            int maxBufferSize = 1 * 1024 * 1024;
//            DataOutputStream dos = null;
//            int bytesRead, bytesAvailable, bufferSize;
//            try {
//
//                fileInputStream = new FileInputStream(new File(existingFileName));
//
//
//                // create a buffer of maximum size
//                bytesAvailable = fileInputStream.available();
//                bufferSize = Math.min(bytesAvailable, maxBufferSize);
//                buffer = new byte[bufferSize];
//                // read file and write it into form...
//                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//
//                while (bytesRead > 0) {
//
//                    bytesAvailable = fileInputStream.available();
//                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
//                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//
//                }
//
//                // close streams
//                Log.e("Debug", "File is written");
//                fileInputStream.close();
//
//                        } catch (MalformedURLException ex) {
//                Log.e("Debug", "error: " + ex.getMessage(), ex);
//            } catch (IOException ioe) {
//                Log.e("Debug", "error: " + ioe.getMessage(), ioe);
//            }
            //--


            String urldata = "http://www.dia40.com/oodles/fileUpload.php";
            StringBuilder strdata = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(urldata);
            String result = "null";


//            List<NameValuePair> paramx = new ArrayList<NameValuePair>();
//            paramx.add(new BasicNameValuePair("aWord", _sPref.getSmsBody("key")));
//            paramx.add(new BasicNameValuePair("strUsername", _sPref.getSmsBody("userId")));
            //paramx.add(new BasicNameValuePair("uploadedfile", fileInputStream));

            try {
                //httpPost.setEntity(new UrlEncodedFormEntity(paramx));
//                HttpResponse response = client.execute(httpPost);
//                StatusLine statusLine = response.getStatusLine();
//                int statusCode = statusLine.getStatusCode();


                //--

                MultipartEntity entityFile = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                //entityFile.addPart("title", new StringBody(existingFileName, Charset.forName("UTF-8")));
                File myFile = new File(existingFileName);
                FileBody fileBody = new FileBody(myFile);
                entityFile.addPart("uploadedfile", fileBody);
                entityFile.addPart("aWord", new StringBody(_sPref.getSmsBody("key")));
                entityFile.addPart("strUsername", new StringBody(_sPref.getSmsBody("userId")));
                entityFile.addPart("strLocation", new StringBody(_sPref.getSmsBody("loc")));

                httpPost.setEntity(entityFile);
                //paramx.add(new BasicNameValuePair("uploadedfile", fileBody.getFilename()));

                //---

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

//
//            HttpURLConnection conn = null;
//            DataOutputStream dos = null;
//            DataInputStream inStream = null;
//            String existingFileName = realUri.toString(); //Environment.getExternalStorageDirectory().getAbsolutePath() +
//            String lineEnd = "\r\n";
//            String twoHyphens = "--";
//            String boundary = "*****";
//            int bytesRead, bytesAvailable, bufferSize;
//            byte[] buffer;
//            int maxBufferSize = 1 * 1024 * 1024;
//            String responseFromServer = "";
//            String urlString = "http://www.dia40.com/oodles/fileUpload.php";
//
//
//            try {
//                //------------------ CLIENT REQUEST
//
//                FileInputStream fileInputStream = new FileInputStream(new File(existingFileName));
//                // open a URL connection to the Servlet
//                URL url = new URL(urlString);
//                // Open a HTTP connection to the URL
//                conn = (HttpURLConnection) url.openConnection();
//                // Allow Inputs
//                conn.setDoInput(true);
//                // Allow Outputs
//                conn.setDoOutput(true);
//                // Don't use a cached copy.
//                conn.setUseCaches(false);
//                // Use a post method.
//                conn.setRequestMethod("POST");
//                conn.setRequestProperty("Connection", "Keep-Alive");
//                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
//                dos = new DataOutputStream(conn.getOutputStream());
//                dos.writeBytes(twoHyphens + boundary + lineEnd);
//                dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + existingFileName + "\"" + lineEnd);
//                dos.writeBytes(lineEnd);
//                // create a buffer of maximum size
//                bytesAvailable = fileInputStream.available();
//                bufferSize = Math.min(bytesAvailable, maxBufferSize);
//                buffer = new byte[bufferSize];
//                // read file and write it into form...
//                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//
//                while (bytesRead > 0) {
//
//                    dos.write(buffer, 0, bufferSize);
//                    bytesAvailable = fileInputStream.available();
//                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
//                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//
//                }
//
//                // send multipart form data necesssary after file data...
//                dos.writeBytes(lineEnd);
//                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
//
//                // close streams
//                Log.e("Debug", "File is written");
//                fileInputStream.close();
//                dos.flush();
//                dos.close();
//
//
//            } catch (MalformedURLException ex) {
//                Log.e("Debug", "error: " + ex.getMessage(), ex);
//            } catch (IOException ioe) {
//                Log.e("Debug", "error: " + ioe.getMessage(), ioe);
//            }
//
//
//            //------------------ read the SERVER RESPONSE
//            try {
//
//                inStream = new DataInputStream(conn.getInputStream());
//                String str;
//
//                while ((str = inStream.readLine()) != null) {
//                    Log.e("Debug", "Server Response " + str);
//                }
//                inStream.close();
//
//            } catch (IOException ioex) {
//                Log.e("Debug", "error: " + ioex.getMessage(), ioex);
//            }

            return null;
        }

    }


//    URL url = new URL("http://yoururl.com");
//    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
//    conn.setReadTimeout(10000);
//    conn.setConnectTimeout(15000);
//    conn.setRequestMethod("POST");
//    conn.setDoInput(true);
//    conn.setDoOutput(true);
//
//    List<NameValuePair> params = new ArrayList<NameValuePair>();
//    params.add(new BasicNameValuePair("firstParam", paramValue1));
//    params.add(new BasicNameValuePair("secondParam", paramValue2));
//    params.add(new BasicNameValuePair("thirdParam", paramValue3));
//
//    OutputStream os = conn.getOutputStream();
//    BufferedWriter writer = new BufferedWriter(
//            new OutputStreamWriter(os, "UTF-8"));
//    writer.write(getQuery(params));
//    writer.flush();
//    writer.close();
//    os.close();
//
//    conn.connect();

//    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
//    {
//        StringBuilder result = new StringBuilder();
//        boolean first = true;
//
//        for (NameValuePair pair : params)
//        {
//            if (first)
//                first = false;
//            else
//                result.append("&");
//
//            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
//            result.append("=");
//            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
//        }
//
//        return result.toString();
//    }

    private View.OnClickListener btnUpload = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //String ss =_sPref.getSmsBody("userId");

            if (_sPref.getAll().containsKey("userId")) {
                new doFileUpload().execute(_sPref.getSmsBody("userId"));
            }
            //return;
        }
    };

    private View.OnClickListener btnLogout = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            _sPref.removePref("userId");
            logOut.setVisibility(View.INVISIBLE);
            tvEmail.setVisibility(View.VISIBLE);
            txtEmail.setVisibility(View.VISIBLE);
            upload.setEnabled(false);
        }
    };

}
