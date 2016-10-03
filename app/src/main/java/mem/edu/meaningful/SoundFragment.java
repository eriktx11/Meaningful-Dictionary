package mem.edu.meaningful;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.StringCharacterIterator;

/**
 * Created by erikllerena on 9/27/16.
 */
public class SoundFragment extends Fragment {

    private static final String LOG_TAG = "AccentRecord";
    private static String mFileName = null;

    private MediaRecorder mRecorder = null;
    private MediaPlayer   mPlayer = null;

    private AppPreferences _sPref;
    ImageButton btn;
    ImageButton start;
    ImageButton stop;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.sound_view, container, false);
        btn = (ImageButton)rootView.findViewById(R.id.btnSoundId);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                _sPref = new AppPreferences(getContext());

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
        stop=(ImageButton)rootView.findViewById(R.id.rcrbtn2);

        final AudioRecorder recorder = new AudioRecorder("/audiometer/temp");



        start.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                try {
                    recorder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });

        //….wait a while

        stop.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                try {
                    recorder.stop();
                } catch (IOException e) {

                    e.printStackTrace();
                }

            }
        });


        return rootView;
    }





    public class AudioRecorder {
        final MediaRecorder recorder = new MediaRecorder();
        final String path;

        /**
         * Creates a new audio recording at the given path (relative to root of SD card).
         */
        public AudioRecorder(String path) {
            this.path = sanitizePath(path);
        }

        private String sanitizePath(String path) {
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            if (!path.contains(".")) {
                path += ".3gp";
            }
            return Environment.getExternalStorageDirectory().getAbsolutePath() + path;
        }

        /**
         * Starts a new recording.
         */
        public void start() throws IOException {
            String state = android.os.Environment.getExternalStorageState();
            if(!state.equals(android.os.Environment.MEDIA_MOUNTED))  {
                throw new IOException("SD Card is not mounted.  It is " + state + ".");
            }

            // make sure the directory we plan to store the recording in exists
            File directory = new File(path).getParentFile();
            if (!directory.exists() && !directory.mkdirs()) {
                throw new IOException("Path to file could not be created.");
            }

            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(path);
            recorder.prepare();
            recorder.start();
        }

        /**
         * Stops a recording that has been previously started.
         */
        public void stop() throws IOException {
            recorder.stop();
            recorder.release();
        }
    }

}
