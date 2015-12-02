package app.wingman.ui.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import app.wingman.R;
import app.wingman.utils.PreferencesUtils;

public class Settings extends AppCompatActivity {

    CheckBox mFemale,mMale;
    SwitchCompat mSearchFemale,mSearchMale,mBoth;
    SwitchCompat mPhoneVibrate,mKilometer,mMiles;
    DiscreteSeekBar mDiscreeteseekbar;
    int distanceValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        mFemale=(CheckBox)findViewById(R.id.gndrfemale);
        mMale=(CheckBox)findViewById(R.id.gndrmale);
        mSearchFemale=(SwitchCompat)findViewById(R.id.femaleSwitch);
        mSearchMale=(SwitchCompat)findViewById(R.id.maleswitch);
        mKilometer=(SwitchCompat)findViewById(R.id.kmswitch);
        mMiles=(SwitchCompat)findViewById(R.id.mileswitch);
        mBoth=(SwitchCompat)findViewById(R.id.bothstch);
        mPhoneVibrate=(SwitchCompat)findViewById(R.id.vibratephone);
        mDiscreeteseekbar=(DiscreteSeekBar) findViewById(R.id.discreetSeek);

        if(PreferencesUtils.getData("search",Settings.this).equals("female"))
            mSearchFemale.setChecked(true);
        else if(PreferencesUtils.getData("search",Settings.this).equals("male"))
            mSearchMale.setChecked(true);
        else if(PreferencesUtils.getData("search",Settings.this).equals("both"))
            mBoth.setChecked(true);

        if(PreferencesUtils.getData("vibrate",Settings.this).equals("true"))
            mPhoneVibrate.setChecked(true);

         if(PreferencesUtils.getData("distance",Settings.this).equals("miles"))
             mMiles.setChecked(true);
         else if(PreferencesUtils.getData("distance",Settings.this).equals("km"))
             mKilometer.setChecked(true);

        if((Integer.parseInt(PreferencesUtils.getData("distancevalue",Settings.this)))>0)
            mDiscreeteseekbar.setProgress(Integer.parseInt(PreferencesUtils.getData("distancevalue",Settings.this)));

        mSearchFemale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){

                    PreferencesUtils.saveData("search","female",Settings.this);
                    mSearchMale.setChecked(false);
                    mBoth.setChecked(false);
                }else{
                    PreferencesUtils.saveData("search","novalue",Settings.this);
                }
            }
        });
        mSearchMale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){

                    PreferencesUtils.saveData("search","male",Settings.this);
                    mSearchFemale.setChecked(false);
                    mBoth.setChecked(false);
                }else{
                    PreferencesUtils.saveData("search","novalue",Settings.this);
                }
            }
        });
        mBoth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){

                    PreferencesUtils.saveData("search","both",Settings.this);
                    mSearchFemale.setChecked(false);
                    mSearchMale.setChecked(false);
                }else{
                    PreferencesUtils.saveData("search","novalue",Settings.this);
                }
            }
        });
        mPhoneVibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){

                    PreferencesUtils.saveData("vibrate","true",Settings.this);

                }else{
                    PreferencesUtils.saveData("vibrate","novalue",Settings.this);
                }
            }
        });
        mMiles.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){

                    PreferencesUtils.saveData("distance","miles",Settings.this);
                    mKilometer.setChecked(false);

                }else{
                    PreferencesUtils.saveData("distance","novalue",Settings.this);
                }
            }
        });
        mKilometer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){

                    PreferencesUtils.saveData("distance","km",Settings.this);

                    mMiles.setChecked(false);

                }else{
                    PreferencesUtils.saveData("distance","novalue",Settings.this);
                }
            }
        });

        mDiscreeteseekbar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {

                distanceValue=value;
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

                PreferencesUtils.saveData("distancevalue", Integer.toString(distanceValue),Settings.this);
            }
        });

        if(PreferencesUtils.getData("gender",Settings.this).equals("female")){

            mFemale.setChecked(true);
            mMale.setChecked(false);

        }else if(PreferencesUtils.getData("gender",Settings.this).equals("male")){

            mFemale.setChecked(false);
            mMale.setChecked(true);

        }


    }


}
