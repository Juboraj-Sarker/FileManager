package com.juborajsarker.filemanager.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import com.juborajsarker.filemanager.R;

import java.util.List;

public class PrefsActivity extends PreferenceActivity {


    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);

    }

    @Override
    protected boolean isValidFragment(String fragmentName) {

        if(Header1.class.getName().equals(fragmentName))
            return true;

        return false;
    }

    public static class Header1 extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference_common);
        }
    }


}
