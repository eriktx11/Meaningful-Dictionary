package mem.edu.meaningful;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.IOException;

/**
 * Created by erikllerena on 9/27/16.
 */
public class SoundFragment extends Fragment {


    private AppPreferences _sPref;
    Button btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.sound_view, container, false);
        btn = (Button)rootView.findViewById(R.id.btnSoundId);

        _sPref = new AppPreferences(getContext());

        try {
            MediaPlayer player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource("http://media.merriam-webster.com/soundc11/s/seat0001.wav");
            player.prepare();
            player.start();
        } catch (Exception e) {
        }

//reference
//http://media.merriam-webster.com/soundc11/s/seat0001.wav

        return rootView;
    }
}
