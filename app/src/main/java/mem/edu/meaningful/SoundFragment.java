package mem.edu.meaningful;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.http.client.utils.URIBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.text.StringCharacterIterator;


/**
 * Created by erikllerena on 9/27/16.
 */
public class SoundFragment extends Fragment {//
// implements View.OnClickListener {

    private static final String LOG_TAG = "AccentRecord";
    public static final String FGTAG = "soundFrag";
    private static String mFileName = null;


    private AppPreferences _sPref;
    ImageButton btn;
    View rootView;

    // gallery request code.
    public static final int GALLEY_REQUEST_CODE = 10;
    // tag to print logs.
    private String TAG = SoundFragment.class.getSimpleName();
    private ImageView image;
    public static Uri realUri;
    Fragment fragment=this;


    @Override
    public void onStop() {
        super.onStop();
        RecordingLayout.pDialog.dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLEY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            try {
                // Get real path to make File
                realUri = Uri.parse(getPath(data.getData()));
                if(realUri.toString()!=null){
                    record.upload.setEnabled(true);
                }
                Log.d(TAG, "Audio path :- " + realUri);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getPath(Uri uri) throws Exception {
        // this method will be used to get real path of audio chosen from device.
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


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
                String url="http://media.merriam-webster.com/soundc11/" + s + "/" + _sPref.getSmsBody("sound");
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


        //list of USA states (total 4) //.resize(115, 0)
        Picasso.with(getContext())
                .load("http://www.dia40.com/oodles/st-flag/ca.png").resize(105, 0)
                .into((ImageView) rootView.findViewById(R.id.imageView1));

        Picasso.with(getContext())
                .load("http://www.dia40.com/oodles/st-flag/ny.png").resize(105, 0)
                .into((ImageView) rootView.findViewById(R.id.imageView2));

        Picasso.with(getContext())
                .load("http://www.dia40.com/oodles/st-flag/tn.png").resize(105, 0)
                .into((ImageView) rootView.findViewById(R.id.imageView3));

        Picasso.with(getContext())
                .load("http://www.dia40.com/oodles/st-flag/tx.png").resize(105, 0)
                .into((ImageView) rootView.findViewById(R.id.imageView4));
        //end of states



        //list of countries
        Picasso.with(getContext())
                .load("http://www.dia40.com/oodles/wd-flag/au.png").resize(105, 0)
                .into((ImageView) rootView.findViewById(R.id.imageView5));
        Picasso.with(getContext())
                .load("http://www.dia40.com/oodles/wd-flag/ca.png").resize(105, 0)
                .into((ImageView) rootView.findViewById(R.id.imageView6));
        Picasso.with(getContext())
                .load("http://www.dia40.com/oodles/wd-flag/in.png").resize(105, 0)
                .into((ImageView) rootView.findViewById(R.id.imageView7));

        Picasso.with(getContext())
                .load("http://www.dia40.com/oodles/wd-flag/jm.png").resize(105, 0)
                .into((ImageView) rootView.findViewById(R.id.imageView8));

        Picasso.with(getContext())
                .load("http://www.dia40.com/oodles/wd-flag/ng.png").resize(105, 0)
                .into((ImageView) rootView.findViewById(R.id.imageView9));

        Picasso.with(getContext())
                .load("http://www.dia40.com/oodles/wd-flag/sg.png").resize(105, 0)
                .into((ImageView) rootView.findViewById(R.id.imageView10));
        Picasso.with(getContext())
                .load("http://www.dia40.com/oodles/wd-flag/za.png").resize(105, 0)
                .into((ImageView) rootView.findViewById(R.id.imageView11));
        Picasso.with(getContext())
                .load("http://www.dia40.com/oodles/wd-flag/tt.png").resize(105, 0)
                .into((ImageView) rootView.findViewById(R.id.imageView12));
        Picasso.with(getContext())
                .load("http://www.dia40.com/oodles/wd-flag/gb.png").resize(105, 0)
                .into((ImageView) rootView.findViewById(R.id.imageView13));


//        Australia au
//        Canada ca use-> cca for android
//        India in
        //Jamaica jm
        //Nigeria ng
//        Singapore sg
//        South Africa za
//        Trinidad and Tobago tt
//        The United Kingdom uk

        if(MainActivity.network) {
            new RecordingLayout(getActivity(), fragment).execute();
            return rootView;
        }else {
            Toast.makeText(getContext(), getString(R.string.network_toast), Toast.LENGTH_LONG).show();
            return null;
        }

    }

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


