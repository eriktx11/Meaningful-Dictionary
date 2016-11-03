package mem.edu.meaningful;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by erikllerena on 11/3/16.
 */
public class play extends Activity implements View.OnClickListener {

    private String url;

    public play(String soundurl){
        this.url=soundurl;
    }

    @Override
    public void onClick(View v) {
//url example  "http://www.dia40.com/oodles/eu4m/bb@bb.com/hi/wind.mp3";//or Voice 001.m4a";
        try {
            MediaPlayer player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(url);
            player.prepare();
            player.start();
        } catch (Exception e) {
        }
    }
}
