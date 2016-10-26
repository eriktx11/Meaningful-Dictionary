package mem.edu.meaningful;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.multidex.MultiDex;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView textview;
    EditText editText;
    ImageButton button;
    String searchWord;
    TextView tagWord;
    ScrollView welcomeSV;
    private AppPreferences _sPref;
    private ViewPager viewPager;
    public static Typeface FONT_HEADINGS;

    View linearLayout;

    private View.OnClickListener SearchListener = new View.OnClickListener() {
        public void onClick(View v) {

            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            if(isConnected){//If not network don't run service

                searchWord = editText.getText().toString();
                _sPref = new AppPreferences(getBaseContext());
                _sPref.saveSmsBody("key", searchWord);
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                linearLayout = findViewById(R.id.info);
                imm.hideSoftInputFromWindow(linearLayout.getWindowToken(), 0);
                viewPager = (ViewPager)findViewById(R.id.tab_viewpager);

                if (viewPager != null){
                    findViewById(R.id.welcomeSVId).setVisibility(View.GONE);
                    findViewById(R.id.dictImgId).setVisibility(View.GONE);
                    setupViewPager(viewPager);
                }
            }else {
                Toast.makeText(getBaseContext(), getString(R.string.network_toast), Toast.LENGTH_LONG).show();
            }
        }
    };

    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        FONT_HEADINGS = Typeface.createFromAsset(this.getAssets(), "MavenPro-Medium.ttf");//CaviarDreams.ttf
        setContentView(R.layout.activity_main);
        linearLayout =  findViewById(R.id.info);
        editText = (EditText) findViewById(R.id.editText);
        editText.setTypeface(MainActivity.FONT_HEADINGS);
        button = (ImageButton) findViewById(R.id.button);
        button.setOnClickListener(SearchListener);

        if (viewPager != null){
            setupViewPager(viewPager);
        }

        PagerTitleStrip titleStrip = (PagerTitleStrip) findViewById(R.id.pager_title_strip);
        titleStrip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
    }


    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new CoordinatorFragment(), searchWord.toUpperCase());
        adapter.addFrag(new ImagesFragment(), "IMAGES");
        adapter.addFrag(new ConjugateFragment(), "CONJUGATION");
        adapter.addFrag(new SoundFragment(), "SOUND");
        viewPager.setAdapter(adapter);
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager){
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title){
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position){
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}