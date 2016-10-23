package mem.edu.meaningful;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.Header;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * Created by erikllerena on 9/27/16.
 */
public class SoundFragment extends Fragment implements View.OnClickListener {

    private static final String LOG_TAG = "AccentRecord";
    private static String mFileName = null;

    //private MediaRecorder mRecorder = null;
    //private MediaPlayer   mPlayer = null;

    private AppPreferences _sPref;
    ImageButton btn;
    ImageButton voteup_btn;
    ImageButton votedown_btn;
    ImageButton start;
    EditText txtEmail;
    TextView tvEmail;
    TextView tvError;

    public Dialog dialog;

    Button choose;
    Button upload;
    Button logOut;
    String locationStr;

    View rootView;

    // gallery request code.
    public static final int GALLEY_REQUEST_CODE = 10;
    String ROOT_URL = "http://www.dia40.com";
    // tag to print logs.
    private String TAG = MainActivity.class.getSimpleName();
    private ImageView image;
    private Uri realUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.sound_view, container, false);
        btn = (ImageButton) rootView.findViewById(R.id.btnSoundId);

        _sPref = new AppPreferences(getContext());

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StringCharacterIterator characterIterator = new StringCharacterIterator(_sPref.getSmsBody("sound"));
                char s = characterIterator.first();
                String url;
                url = "http://media.merriam-webster.com/soundc11/" + s + "/" + _sPref.getSmsBody("sound");

                try {
                    MediaPlayer player = new MediaPlayer();
                    player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    player.setDataSource(url);
                    player.prepare();
                    player.start();
                } catch (Exception e) {
                }
//reference
//http://media.merriam-webster.com/soundc11/s/seat0001.wav
            }
        });


        Picasso.with(getContext())
                .load("http://www.dia40.com/oodles/st-flag/ak.png").resize(0, 70)
                .into((ImageView) rootView.findViewById(R.id.imageView1));

        Picasso.with(getContext())
                .load("http://www.dia40.com/oodles/st-flag/al.png").resize(0, 70)
                .into((ImageView) rootView.findViewById(R.id.imageView2));


        start = (ImageButton) rootView.findViewById(R.id.rcrbtn1);
        voteup_btn = (ImageButton) rootView.findViewById(R.id.imageButtonl1a1);
        votedown_btn = (ImageButton) rootView.findViewById(R.id.imageButtonl1a2);

//        vote voting = new vote(getContext());
        start.setOnClickListener(SoundFragment.this);
//        voteup_btn.setOnClickListener(voting);
//        votedown_btn.setOnClickListener(voting);

        new RecordingLayout(getActivity()).execute();

        return rootView;
    }

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
                        startActivityForResult(Intent.createChooser(openGallery, "Select Audio"), GALLEY_REQUEST_CODE);
                        break;
                }
            } else {
                logOut.setVisibility(View.VISIBLE);
                logOut.setOnClickListener(btnLogout);
                // this will open audio folder to choose file.
                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(openGallery, "Select Audio"), GALLEY_REQUEST_CODE);
                upload.setEnabled(true);
            }
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLEY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            //image.setImageURI(data.getData()); // set image to image view
            try {
                // Get real path to make File
                realUri = Uri.parse(getPath(data.getData()));
                Log.d(TAG, "Audio path :- " + realUri);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private String getPath(Uri uri) throws Exception {
        // this method will be used to get real path of Image chosen from gallery.
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    public void onClick(View v) {

        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.recording_box);
        dialog.setTitle("upload");

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
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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
            String existingFileName = realUri.toString();
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


//    private View.OnClickListener btnClick = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//
//            dialog = new Dialog(getContext());
//            dialog.setContentView(R.layout.recording_box);
//            dialog.setTitle("upload");
//
//            image = (ImageView) dialog.findViewById(R.id.imageId);
//            txtEmail = (EditText) dialog.findViewById(R.id.txtEmail);
//            tvEmail = (TextView) dialog.findViewById(R.id.tvEmailId);
//            tvError = (TextView) dialog.findViewById(R.id.txtError);
//            logOut = (Button) dialog.findViewById(R.id.logOutId);
//
//            if (!_sPref.getAll().containsKey("userId")) {
//
//                tvEmail.setVisibility(View.VISIBLE);
//                txtEmail.setVisibility(View.VISIBLE);
//
//                if (txtEmail.requestFocus()) {
//                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//                }
//            }
//
//            choose = (Button) dialog.findViewById(R.id.btn_choose);
//            upload = (Button) dialog.findViewById(R.id.btn_upload);
//
//            choose.setOnClickListener(btnChoose);
//            upload.setOnClickListener(btnUpload);
//
//            dialog.show();
//        }
//    };

}

//----recording voice - working code in case is needed.

//    private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
//    private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
//    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
//    private MediaRecorder recorder = null;
//    private int currentFormat = 0;
//    private int output_formats[] = { MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.OutputFormat.THREE_GPP };
//    private String file_exts[] = { AUDIO_RECORDER_FILE_EXT_MP4, AUDIO_RECORDER_FILE_EXT_3GP };
//
//    private String getFilename() {
//        String filepath = Environment.getExternalStorageDirectory().getPath();
//        File file = new File(filepath, AUDIO_RECORDER_FOLDER);
//        if (!file.exists()) {
//            file.mkdirs();
//        }
//        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + file_exts[currentFormat]);
//    }
//
//    private void startRecording() {
//        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        recorder.setOutputFormat(output_formats[currentFormat]);
//        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//        recorder.setAudioEncodingBitRate(16);
//        recorder.setAudioSamplingRate(44100);
//        recorder.setOutputFile(getFilename());
//        recorder.setOnErrorListener(errorListener);
//        recorder.setOnInfoListener(infoListener);
//        try {
//            recorder.prepare();
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void stopRecording() {
//        try {
//            recorder.stop();
//        }catch (RuntimeException ex){
//
//        }
//        recorder.release();
//        recorder = null;
//    }
//
//    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
//        @Override
//        public void onError(MediaRecorder mr, int what, int extra) {
//            Toast.makeText(getContext(), "Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
//        }
//    };
//
//    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
//        @Override
//        public void onInfo(MediaRecorder mr, int what, int extra) {
//            Toast.makeText(getContext(), "Warning: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
//        }
//    };
//
//    private View.OnClickListener btnClick = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//
//            final Dialog dialog = new Dialog(getContext());
//
//                    Toast.makeText(getContext(), "Speak now !", Toast.LENGTH_SHORT).show();
//                    dialog.setContentView(R.layout.recording_box);
//                    dialog.setTitle("Recording...");
//
//                    recorder = new MediaRecorder();
//                    startRecording();
//                    recorder.start();
//
//                    // set the custom dialog components - text, image and button
//                    TextView text = (TextView) dialog.findViewById(R.id.textId);
//                    text.setText("Android custom dialog example!");
//                    ImageView image = (ImageView) dialog.findViewById(R.id.imageId);
//                    image.setImageResource(R.drawable.record_button);
//
//                    Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
//                    // if button is clicked, close the custom dialog
//                    dialogButton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            stopRecording();
//                            dialog.dismiss();
//                        }
//                    });
//                    dialog.show();
//        }
//    };


