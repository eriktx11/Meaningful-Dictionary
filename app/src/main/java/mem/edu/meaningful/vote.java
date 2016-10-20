package mem.edu.meaningful;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by erikllerena on 10/19/16.
 */

public class vote extends Activity implements View.OnClickListener {

    public Dialog dialog;
    private Context mContex;
    String state;
    private AppPreferences _sPref;

    TextView tvError;
    ImageView image;
    EditText txtEmail;
    TextView tvEmail;
    Button logOut;
    Button cancel;
    Button ok_log_vote;
    String loc;
    String voteLabel;

    public vote(Context c){
        this.mContex=c;
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }


    public View.OnClickListener btn_register = new View.OnClickListener(){

        @Override
        public void onClick(View v) {

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


            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            switch (login.postResult) {
                case 0:
                    tvError.setText(login.strError);
                    tvError.setTextColor(Color.parseColor("#FFFC0202"));
                    break;
                case 1:
                    tvError.setText(login.strError);
                    tvError.setTextColor(Color.parseColor("#FFFC0202"));
                    break;
                case 2:
                    tvEmail.setVisibility(View.INVISIBLE);
                    tvError.setVisibility(View.INVISIBLE);
                    txtEmail.setVisibility(View.INVISIBLE);
                    _sPref.saveSmsBody("userId", txtEmail.getText().toString());
                    logOut.setVisibility(View.VISIBLE);
                    logOut.setOnClickListener(btnLogout);
                    ok_log_vote.setText(voteLabel);
                    ok_log_vote.setOnClickListener(btn_vote_now);
                    break;
            }
        }
    };

    public View.OnClickListener btn_login_stat = new View.OnClickListener(){

        @Override
        public void onClick(View v) {

            if(!_sPref.getAll().containsKey("userId")){
                txtEmail.setVisibility(View.VISIBLE);
                ok_log_vote.setText("REGISTER");
                tvEmail.setVisibility(View.VISIBLE);
                ok_log_vote.setOnClickListener(btn_register);
            }else {
                logOut.setVisibility(View.VISIBLE);
                logOut.setOnClickListener(btnLogout);
                ok_log_vote.setText(voteLabel);
                ok_log_vote.setOnClickListener(btn_vote_now);
            }
        }
    };

    public View.OnClickListener btn_vote_now = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            cancel.setText("DONE");
            ok_log_vote.setEnabled(false);
        }
    };

    public View.OnClickListener btn_cancel = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            dialog.dismiss();
        }
    };

    private View.OnClickListener btnLogout = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            _sPref.removePref("userId");
            logOut.setVisibility(View.INVISIBLE);
            txtEmail.setVisibility(View.VISIBLE);
            ok_log_vote.setText("REGISTER");
            tvEmail.setVisibility(View.VISIBLE);
            ok_log_vote.setOnClickListener(btn_register);
        }
    };

    @Override
    public void onClick(View v) {
        _sPref = new AppPreferences(mContex);
        String location = _sPref.getSmsBody("loc");
        String full_loc = _sPref.getSmsBody("full_loc");
        dialog = new Dialog(mContex);
        dialog.setContentView(R.layout.vote_layout);
        dialog.setTitle("vote");

        image = (ImageView) dialog.findViewById(R.id.imageId);
        txtEmail = (EditText) dialog.findViewById(R.id.txtEmail);
        tvEmail = (TextView) dialog.findViewById(R.id.tvEmailId);
        tvError = (TextView) dialog.findViewById(R.id.txtError);
        logOut = (Button) dialog.findViewById(R.id.logOutId);
        cancel = (Button) dialog.findViewById(R.id.btn_cancel);

        ok_log_vote = (Button) dialog.findViewById(R.id.btn_login);


            switch (v.getId()){
                case R.id.imageButtonl1a1:_sPref.saveSmsBody("loc", "ak");_sPref.saveSmsBody("full_loc", "Alaska");
                    full_loc="Alaska";voteLabel="UP VOTE";break;
                case R.id.imageButtonl1a2:_sPref.saveSmsBody("loc", "ak");_sPref.saveSmsBody("full_loc", "Alaska");
                    full_loc="Alaska";voteLabel="DOWN VOTE";break;
                case R.id.imageButtonl1b1:_sPref.saveSmsBody("loc", "ak");_sPref.saveSmsBody("full_loc", "Alaska");
                    full_loc="Alaska";voteLabel="UP VOTE";break;
                case R.id.imageButtonl1b2:_sPref.saveSmsBody("loc", "ak");_sPref.saveSmsBody("full_loc", "Alaska");
                    full_loc="Alaska";voteLabel="DOWN VOTE";break;
            }

        String labelText="Voting in: \n"+full_loc+", are you sure?\n_________________\nYou won\'t be able to change your vote";
        tvError.setText(labelText);
        ok_log_vote.setOnClickListener(btn_login_stat);
        cancel.setOnClickListener(btn_cancel);

        dialog.show();

    }
}
