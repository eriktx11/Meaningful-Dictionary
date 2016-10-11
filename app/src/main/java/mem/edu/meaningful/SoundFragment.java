package mem.edu.meaningful;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.StringCharacterIterator;


/**
 * Created by erikllerena on 9/27/16.
 */
public class SoundFragment extends Fragment {

    private static final String LOG_TAG = "AccentRecord";
    private static String mFileName = null;

    //private MediaRecorder mRecorder = null;
    //private MediaPlayer   mPlayer = null;

    private AppPreferences _sPref;
    ImageButton btn;
    ImageButton start;
    EditText txtEmail;
    TextView tvEmail;
    TextView tvError;

    //ImageButton stop;
    View rootView;

    // gallery request code.
    public static final int GALLEY_REQUEST_CODE = 10;
    String ROOT_URL = "http://www.dia40.com";
    // tag to print logs.
    private String TAG = MainActivity.class.getSimpleName();
    private ImageView image;
    private Uri realUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        rootView = inflater.inflate(R.layout.sound_view, container, false);
        btn = (ImageButton)rootView.findViewById(R.id.btnSoundId);

        _sPref = new AppPreferences(getContext());

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StringCharacterIterator characterIterator = new StringCharacterIterator(_sPref.getSmsBody("sound"));
                char s = characterIterator.first();
                String url;
                url="http://media.merriam-webster.com/soundc11/"+s+"/"+_sPref.getSmsBody("sound");

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


        start=(ImageButton)rootView.findViewById(R.id.rcrbtn1);
        start.setOnClickListener(btnClick);

        return rootView;
    }


    public View.OnClickListener btnChoose = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // this will open gallery to choose image.
            Intent openGallery = new Intent(Intent.ACTION_PICK,MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(openGallery, "Select Audio"), GALLEY_REQUEST_CODE);
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLEY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            //image.setImageURI(data.getData()); // set image to image view
            try{
                // Get real path to make File
                realUri = Uri.parse(getPath(data.getData()));
                Log.d(TAG,"Image path :- "+realUri);
            }
            catch (Exception e){
                Log.e(TAG,e.getMessage());
            }
        }
    }

    private String getPath(Uri uri) throws Exception {
        // this method will be used to get real path of Image chosen from gallery.
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public class doFileUpload extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            DataInputStream inStream = null;
            String existingFileName = realUri.toString(); //Environment.getExternalStorageDirectory().getAbsolutePath() +
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            String responseFromServer = "";
            String urlString = "http://www.dia40.com/oodles/fileUpload.php";

            try {

                //------------------ CLIENT REQUEST
                FileInputStream fileInputStream = new FileInputStream(new File(existingFileName));
                // open a URL connection to the Servlet
                URL url = new URL(urlString);
                // Open a HTTP connection to the URL
                conn = (HttpURLConnection) url.openConnection();
                // Allow Inputs
                conn.setDoInput(true);
                // Allow Outputs
                conn.setDoOutput(true);
                // Don't use a cached copy.
                conn.setUseCaches(false);
                // Use a post method.
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + existingFileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);
                // create a buffer of maximum size
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                // close streams
                Log.e("Debug", "File is written");
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {
                Log.e("Debug", "error: " + ex.getMessage(), ex);
            } catch (IOException ioe) {
                Log.e("Debug", "error: " + ioe.getMessage(), ioe);
            }

            //------------------ read the SERVER RESPONSE
            try {

                inStream = new DataInputStream(conn.getInputStream());
                String str;

                while ((str = inStream.readLine()) != null) {

                    Log.e("Debug", "Server Response " + str);

                }

                inStream.close();

            } catch (IOException ioex) {
                Log.e("Debug", "error: " + ioex.getMessage(), ioex);
            }

            return null;
        }
    }

    public Dialog dialog;

    private View.OnClickListener btnUpload = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

           // String ss =_sPref.getSmsBody("userId");

            if(!_sPref.getAll().containsKey("userId")){

                LoginPlease login = new LoginPlease();
                switch (login.SaveData(txtEmail.getText().toString()) ){
                    case 0: tvEmail.setVisibility(View.VISIBLE);break;
                    case 1: tvEmail.setText("Unknown Error");break;
                    case 2:
                    {
                        tvEmail.setVisibility(View.INVISIBLE);
                        tvError.setVisibility(View.INVISIBLE);
                        txtEmail.setVisibility(View.INVISIBLE);
                        _sPref.saveSmsBody("userId", txtEmail.getText().toString());
                        new doFileUpload().execute();
                        break;
                    }
                }
                return;
            }
            new doFileUpload().execute();
        }
    };


    private View.OnClickListener btnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.recording_box);
            dialog.setTitle("upload");

            image = (ImageView) dialog.findViewById(R.id.imageId);
            txtEmail = (EditText) dialog.findViewById(R.id.txtEmail);
            tvEmail = (TextView) dialog.findViewById(R.id.tvEmailId);
            tvError = (TextView) dialog.findViewById(R.id.txtError);

            if(!_sPref.getAll().containsKey("userId")) {

                tvEmail.setVisibility(View.VISIBLE);
                txtEmail.setVisibility(View.VISIBLE);
                if (txtEmail.requestFocus()) {
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }

            Button choose = (Button) dialog.findViewById(R.id.btn_choose);
            Button upload = (Button) dialog.findViewById(R.id.btn_upload);

            choose.setOnClickListener(btnChoose);
            upload.setOnClickListener(btnUpload);
            dialog.show();
        }
    };


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

}
