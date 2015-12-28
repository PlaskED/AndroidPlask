package se.liu.oscho707student.plaskapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MainFragment extends android.support.v4.app.Fragment {
    private RequestQueue queue;
    private PostObjectAdapter arrayAdapter;
    private HashMap<String, Boolean> settings = new HashMap<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        queue = Volley.newRequestQueue(getContext());
        settings = ((MainActivity) getActivity()).getSettings();
        final SwipeFlingAdapterView cardFrame = (SwipeFlingAdapterView) view.findViewById(R.id.cardContainer);
        final ArrayList<PostObject> posts = new ArrayList<PostObject>();
        arrayAdapter = new PostObjectAdapter(getActivity(), posts);
        cardFrame.setAdapter(arrayAdapter);

        cardFrame.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
                                       private Object dataObject;

                                       @Override
                                       public void removeFirstObjectInAdapter() {
                                           // this is the simplest way to delete an object from the Adapter (/AdapterView)
                                           posts.remove(0);
                                           arrayAdapter.notifyDataSetChanged();
                                       }

                                       @Override
                                       public void onLeftCardExit(Object dataObject) {
                                           if (((MainActivity) getActivity()).isLoggedIn()) {
                                               PostObject jsondata = (PostObject) dataObject;
                                               MainActivity.ratePost(queue, jsondata, "down");
                                           }
                                       }

                                       @Override
                                       public void onRightCardExit(Object dataObject) {
                                           if (((MainActivity) getActivity()).isLoggedIn()) {
                                               PostObject jsondata = (PostObject) dataObject;
                                               MainActivity.ratePost(queue, jsondata, "up");
                                           }
                                       }

                                       @Override
                                       public void onAdapterAboutToEmpty(int itemsInAdapter) {
                                           // Ask for more data here
                                           if (settings.get("all").booleanValue()) {
                                               ((MainActivity) getActivity()).getAllData(queue, arrayAdapter);
                                           } else {
                                               if (settings.get("top").booleanValue()) {
                                                   ((MainActivity) getActivity()).getTopData(queue, arrayAdapter);
                                               }
                                               if (settings.get("local").booleanValue()) {
                                                   initiateRequest();
                                               }
                                           }
                                       }

                                       @Override
                                       public void onScroll(float scrollProgressPercent) {
                                           //View view = cardFrame.getSelectedView();
                                           //view.findViewById(R.id.background).setAlpha(0);
                                           //view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0); view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
                                       }
                                   }

        );

            // Optionally add an OnItemClickListener
            cardFrame.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener()

            {
                @Override
                public void onItemClicked(int itemPosition, Object dataObject) {
                    //makeToast(getContext(), "Clicked!");
                }

                void makeToast(Context ctx, String s) {
                    Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
                }
            });


        return view;
    }

    private void initiateRequest() {
        if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    10);
        } else {
            sendRequest();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == 10
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            sendRequest();
        }
    }

    public void sendRequest() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        LocationManager locationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            String lat = String.format(Locale.US, "%.4f", location.getLatitude());
            String lng = String.format(Locale.US, "%.4f", location.getLongitude());
            ((MainActivity) getActivity()).getLocalData(queue, arrayAdapter, lat, lng);
        }
    }

}
