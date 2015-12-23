package se.liu.oscho707student.plaskapp;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.HashMap;


public class FilterFragment extends android.support.v4.app.Fragment  {
    private HashMap<String, Boolean> settings = new HashMap<String, Boolean>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_filter, container, false);
        settings = ((MainActivity) getActivity()).getSettings();
        Switch switchAll = (Switch) view.findViewById(R.id.switchAll);
        switchAll.setChecked(settings.get("all").booleanValue());
        Switch switchTop = (Switch) view.findViewById(R.id.switchToplist);
        switchTop.setChecked(settings.get("top").booleanValue());
        Switch switchLocal = (Switch) view.findViewById(R.id.switchLocal);
        switchLocal.setChecked(settings.get("local").booleanValue());

        switchAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ((MainActivity) getActivity()).switchKey("all", isChecked);
                settings = ((MainActivity) getActivity()).getSettings();
            }
        });

        switchTop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ((MainActivity) getActivity()).switchKey("top", isChecked);
                settings = ((MainActivity) getActivity()).getSettings();
            }
        });

        switchLocal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ((MainActivity) getActivity()).switchKey("local", isChecked);
                settings = ((MainActivity) getActivity()).getSettings();
            }
        });

        return view;
    }

}
