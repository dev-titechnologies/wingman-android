package app.wingman.ui.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.wingman.R;
import app.wingman.ui.fragment.Connections;
import app.wingman.ui.fragment.YourGroups;
import app.wingman.utils.PreferencesUtils;

/**
 * class used for displaying search result as well as group activity directed from sidemenu
 * @author Ajay,Karthika
 *
 */

public class GroupsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private boolean searchclassChecker=false;
    String query;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if( PreferencesUtils.getData("callfromgroup", getApplicationContext()).equals( "false"))
            searchclassChecker=true;

        // if(searchclassChecker)
        handleIntent(getIntent());

        setContentView(R.layout.activity_groups);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onNewIntent(Intent intent) {

        //if(searchclassChecker)
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
             query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(getApplicationContext(),"search for"+query,Toast.LENGTH_LONG).show();
            //use the query to search your data somehow
        }
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        if(searchclassChecker)
        adapter.addFragment(new Connections(query), "Connections");
        else
            adapter.addFragment(new Connections(), "Connections");
        adapter.addFragment(new YourGroups(), "Your Groups");

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
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

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
