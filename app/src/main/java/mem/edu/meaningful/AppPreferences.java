package mem.edu.meaningful;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * Created by erikllerena on 6/20/16.
 */
public class AppPreferences {

    public static final String KEY_PREFS_SMS_BODY = "sms_body";
    private static final String APP_SHARED_PREFS = AppPreferences.class.getSimpleName(); //  Name of the file -.xml
    private SharedPreferences _sharedPrefs;
    private SharedPreferences.Editor _prefsEditor;
private Context context;

    public AppPreferences(Context context) {
        this._sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        this._prefsEditor = _sharedPrefs.edit();
    }

    public String getSmsBody(String symbol) {
        return _sharedPrefs.getString(symbol, "");
    }

    public int getIntBody() {
        return _sharedPrefs.getInt("selction", -1);
    }
    public void saveIntVal(int val) {
        _prefsEditor.putInt("selction", val);
        _prefsEditor.apply();
    }

    public void saveSmsBody(String key, String val) {
        _prefsEditor.putString(key, val);
        _prefsEditor.apply();
    }


    public void removePref(String val) {
        _prefsEditor.remove(val);
        _prefsEditor.apply();
    }

    public Map<String, ?> getAll() {
        return _sharedPrefs.getAll();
    }

}
