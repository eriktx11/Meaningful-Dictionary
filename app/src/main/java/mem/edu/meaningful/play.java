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
    private ImageButton playBtn;

    public play(String soundurl){//}, ImageButton btnplay){
        this.url=soundurl;
//        this.playBtn=btnplay;
    }

    @Override
    public void onClick(View v) {
//        "http://www.dia40.com/oodles/eu4m/bb@bb.com/hi/wind.mp3";//wind.mp3";//Voice 001.m4a";
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
