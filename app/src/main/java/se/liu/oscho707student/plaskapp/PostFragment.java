package se.liu.oscho707student.plaskapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class PostFragment extends android.support.v4.app.Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_post, container, false);

        Button postButton = (Button) view.findViewById(R.id.postButton);

        postButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String postText = ((EditText) getView().findViewById(R.id.postText)).getText().toString();
                if (postText.length() >= 3) {
                    initiateRequest();
                }
                else {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Atleast 3 characters.", Toast.LENGTH_SHORT);
                    toast.show();
                }
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
            RequestQueue queue = Volley.newRequestQueue(getContext());
            String postText = ((EditText) getView().findViewById(R.id.postText)).getText().toString();
            String lat = String.format(Locale.US, "%.4f", location.getLatitude());
            String lng = String.format(Locale.US, "%.4f", location.getLongitude());
            sendPost(queue, postText, lat, lng);

            android.support.v4.app.FragmentManager fm = getFragmentManager();
            android.support.v4.app.FragmentTransaction fm_t = fm.beginTransaction();
            fm_t.replace(R.id.mainView, new MainFragment());
            fm_t.commit();
        }
    }

    public static void sendPost(final RequestQueue queue, final String text, final String lat, final String lng) {
        String url = "http://128.199.43.215:3000/api/add";
        //String url = "http://127.0.0.1:3000/api/add";
        Map jsonmap = new HashMap<String, String>();
        jsonmap.put("text",text);
        jsonmap.put("lat",lat);
        jsonmap.put("lng",lng);
        JSONObject jsonBody = new JSONObject(jsonmap);
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, jsonBody ,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(req);
    }

}

