package mem.edu.meaningful;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.StringCharacterIterator;

/**
 * Created by erikllerena on 9/27/16.
 */
public class SoundFragment extends Fragment {


    private AppPreferences _sPref;
    ImageButton btn;
    Button savebtn;

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

//        savebtn= (Button) rootView.findViewById(R.id.btnSave);
//        savebtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(getActivity(), LoginPlease.class);
//                startActivity(i);
//            }
//        });

        Picasso.with(getContext())
                .load("http://www.dia40.com/oodles/st-flag/ak.png").resize(0, 70)
                .into((ImageView) rootView.findViewById(R.id.imageView1));

        Picasso.with(getContext())
                .load("http://www.dia40.com/oodles/st-flag/al.png").resize(0, 70)
                .into((ImageView) rootView.findViewById(R.id.imageView2));

        return rootView;
    }
}
