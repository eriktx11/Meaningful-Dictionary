package mem.edu.meaningful;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
    private AppPreferences _sPref;
    private ViewPager viewPager;

    View linearLayout;

    private View.OnClickListener SearchListener = new View.OnClickListener() {
        public void onClick(View v) {

            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            if(isConnected){//If not network don't run Google service

                searchWord = editText.getText().toString();
                _sPref = new AppPreferences(getBaseContext());
                _sPref.saveSmsBody("key", searchWord);
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                linearLayout = findViewById(R.id.info);
                imm.hideSoftInputFromWindow(linearLayout.getWindowToken(), 0);
                viewPager = (ViewPager)findViewById(R.id.tab_viewpager);

                if (viewPager != null){
                    findViewById(R.id.dictImgId).setVisibility(View.GONE);
                    setupViewPager(viewPager);
                }


            }else {
                Toast.makeText(getBaseContext(), getString(R.string.network_toast), Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        linearLayout =  findViewById(R.id.info);
        editText = (EditText) findViewById(R.id.editText);
        //setWord(searchWord);
        button = (ImageButton) findViewById(R.id.button);
        button.setOnClickListener(SearchListener);

        if (viewPager != null){
            setupViewPager(viewPager);
        }

//        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabLayout);
//        tabLayout.setupWithViewPager(viewPager);
//
//        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                viewPager.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });

        PagerTitleStrip titleStrip = (PagerTitleStrip) findViewById(R.id.pager_title_strip);
        titleStrip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
    }


    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new CoordinatorFragment(), searchWord);
        adapter.addFrag(new ImagesFragment(), "Images");
        viewPager.setAdapter(adapter);
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
       // private final List<String> mFragmentKeyWord = new ArrayList<>();

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
           // mFragmentKeyWord.add(APIkeyWord);
        }

        @Override
        public CharSequence getPageTitle(int position){
            return mFragmentTitleList.get(position);
        }
    }


}