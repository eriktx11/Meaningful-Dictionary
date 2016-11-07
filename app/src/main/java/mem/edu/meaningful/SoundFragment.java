package mem.edu.meaningful;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

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


        Picasso.with(getContext())
                .load("http://www.dia40.com/oodles/st-flag/ak.png").resize(0, 70)
                .into((ImageView) rootView.findViewById(R.id.imageView1));

        Picasso.with(getContext())
                .load("http://www.dia40.com/oodles/st-flag/al.png").resize(0, 70)
                .into((ImageView) rootView.findViewById(R.id.imageView2));

        new RecordingLayout(getActivity(), fragment).execute();
        return rootView;
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


