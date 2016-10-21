package mem.edu.meaningful;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by erikllerena on 10/20/16.
 */
public class RecordingLayout extends AsyncTask<String, Void, String[]>{

    //AsyncTask<String, Void, String[]>{
        //Activity {
    private Activity mActivity;

    public RecordingLayout(Activity activity) {
        this.mActivity = activity;
    }


    // String[] recordingsURL=new String[3];
//    static class loopthroughLayout extends AsyncTask<String, Void, String[]>{

        @Override
        protected String[] doInBackground(String... params) {

            params = new String[3];
            params[0]="on";
            params[1]="off";
            params[2]="on";

            return params;
        }

        @Override
        protected void onPostExecute(String[] recordingsURL) {
            super.onPostExecute(recordingsURL);
            LinearLayout linearLayout;
            linearLayout = (LinearLayout) mActivity.findViewById(R.id.l1);

            for (int i = 0; i < linearLayout.getChildCount(); i++){
                View v = linearLayout.getChildAt(i);
                if (v instanceof ImageButton) {
                    for (int j = 0; j < 3; j++) {
                        if (recordingsURL[j].equals("on")) {
                            v.setVisibility(View.INVISIBLE);

                        }

                    }
                }
            }


        }
//    }

}
