package com.jvanwyk.justweather;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.appcompat.app.AlertDialog;



public class HelpFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        //return super.onPreferenceTreeClick(preference);

        String key = preference.getKey();
        if (key.equals("help")){


            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle("Help");
            builder.setMessage("A permanent notification shows the current weather in your location. \n\nWeather data refreshes every 30 minutes. \n\nRefresh button can be used to manually update the information.");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int id) {

                    //action on dialog close
                }

            });

            builder.show();

            return true;
        }
        return false;
    }
}