package mem.edu.meaningful;

import android.content.Context;
import android.graphics.Typeface;
import android.provider.SyncStateContract;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by erikllerena on 10/18/16.
 */
public class MyTextView extends TextView {

    Typeface normalTypeface = Typeface.createFromAsset(getContext().getAssets(), "CaviarDreams.ttf");
    Typeface boldTypeface = Typeface.createFromAsset(getContext().getAssets(),  "CaviarDreams.ttf");

    public MyTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTextView(Context context) {
        super(context);
    }

    public void setTypeface(Typeface tf, int style) {
        if (style == Typeface.BOLD) {
            super.setTypeface(boldTypeface/*, -1*/);
        } else {
            super.setTypeface(normalTypeface/*, -1*/);
        }
    }
}