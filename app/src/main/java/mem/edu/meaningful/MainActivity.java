package mem.edu.meaningful;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.CoordinatorLayout;
import android.support.multidex.MultiDex;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    EditText editText;
    ImageButton button;
    public String searchWord="";
    public AppPreferences _sPref;
    public ViewPager viewPager;
    public static Typeface FONT_HEADINGS;

    View v;

    private View.OnClickListener SearchListener = new View.OnClickListener() {
        public void onClick(View v) {

            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            if(isConnected) {//If not network don't run service

                searchWord = editText.getText().toString();
                _sPref.saveSmsBody("key", searchWord);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                v = findViewById(R.id.info);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                viewPager = (ViewPager) findViewById(R.id.tab_viewpager);
                if (viewPager != null) {
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
        v =  findViewById(R.id.info);
        editText = (EditText) findViewById(R.id.editText);
        editText.setTypeface(MainActivity.FONT_HEADINGS);
        button = (ImageButton) findViewById(R.id.button);
        button.setOnClickListener(SearchListener);
        _sPref = new AppPreferences(getBaseContext());
        if (viewPager != null) {
                setupViewPager(viewPager);
            }

        PagerTitleStrip titleStrip = (PagerTitleStrip) findViewById(R.id.pager_title_strip);
        titleStrip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
    }

    @Override
    public void onStop() {
        super.onStop();
        CoordinatorFragment.pDialog.dismiss();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("word", searchWord);
        viewPager=null;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        searchWord = savedInstanceState.getString("word");
        editText.setText(searchWord);
        _sPref.saveSmsBody("key", searchWord);
        viewPager = (ViewPager) findViewById(R.id.tab_viewpager);
        findViewById(R.id.welcomeSVId).setVisibility(View.GONE);
        findViewById(R.id.dictImgId).setVisibility(View.GONE);
        setupViewPager(viewPager);
    }

    static ViewPagerAdapter adapter;

    private void setupViewPager(ViewPager viewPage){

            adapter = new ViewPagerAdapter(getSupportFragmentManager());
            adapter.addFrag(new CoordinatorFragment(), searchWord.toUpperCase());
            viewPage.setAdapter(adapter);
    }

    static class ViewPagerAdapter extends FragmentStatePagerAdapter {//FragmentPagerAdapter {
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
         return mFragmentTitleList.size();
//            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title){
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }


        @Override
        public int getItemPosition(Object object){
                return POSITION_UNCHANGED;
        }

        public void rmFrag(Fragment fragment, String title){
            mFragmentTitleList.remove(title);
            mFragmentList.remove(fragment);
        }

        @Override
        public CharSequence getPageTitle(int position){
            return mFragmentTitleList.get(position);
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//    }
}