package se.liu.oscho707student.plaskapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private ListView menuList;
    private ArrayAdapter<String> mlAdapter;
    private android.support.v7.app.ActionBarDrawerToggle menuToggle;
    private DrawerLayout menuLayout;
    private String activityTitle;
    private GoogleApiClient mGoogleApiClient;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        menuList = (ListView) findViewById(R.id.navList);
        menuLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        activityTitle = getTitle().toString();
        addMenuItems();
        setupMenu();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fm_t = fm.beginTransaction();
        fm_t.add(R.id.mainView, new MainFragment());
        fm_t.commit();

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            buildGoogleApiClient();
        }

        //Start google api
        onStart();



    }

    private void addMenuItems() {
        String[] optionArray = { "Main", "New Post", "Top List", "Filter", "Settings" };
        mlAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, optionArray);
        menuList.setAdapter(mlAdapter);

        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectOption(position);
            }
        });
    }

    private void selectOption(int position) {
        //Create fragment for the selected option
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fm_t = fm.beginTransaction();
        if (position == 0) {
            fm_t.replace(R.id.mainView, new MainFragment());
        }
        else if (position == 1) {
            fm_t.replace(R.id.mainView, new PostFragment());
        }
        else if (position == 2) {
            fm_t.replace(R.id.mainView, new TopFragment());
        }
        fm_t.addToBackStack(null);
        fm_t.commit();
        menuLayout.closeDrawers();
    }

    private void setupMenu() {
        menuToggle = new android.support.v7.app.ActionBarDrawerToggle(this, menuLayout,
                R.string.menu_open, R.string.menu_close) {
            public void onMenuOpen(View view) {
                super.onDrawerOpened(view);
                getSupportActionBar().setTitle("Navigate");
                invalidateOptionsMenu();
            }
            public void onMenuClose(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(activityTitle);
                invalidateOptionsMenu();
            }
        };
        menuToggle.setDrawerIndicatorEnabled(true);
        menuLayout.setDrawerListener(menuToggle);
    }

    protected void onPostCreate(Bundle savedInstanceState) {
        //Syncs indicator to current state of our menu IE: open/closed states
        super.onPostCreate(savedInstanceState);
        menuToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        menuToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return menuToggle.onOptionsItemSelected(item);
    }



    public void FABClicked(View v) {
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fm_t = fm.beginTransaction();
        fm_t.replace(R.id.mainView, new PostFragment());
        fm_t.addToBackStack(null);
        fm_t.commit();
    }

    public void getJsonData(RequestQueue queue, final PostObjectAdapter arr) {
        JSONObject json;
        String url = "http://128.199.43.215:3000/api/getall";

        JsonArrayRequest jsonRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONArray res = response;
                            Log.d("res", response.toString());
                            for (int n = 0 ; n < response.length() ; n++) {
                                PostObject data = new PostObject(res.getJSONObject(n));
                                arr.add(data);
                            }
                        } catch (JSONException e) {
                            Log.d("Exception", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        arr.notifyDataSetChanged();
        queue.add(jsonRequest);
    }

    public void getPost(RequestQueue queue, final PostObjectAdapter arr, final int pid) {
        JSONObject json;
        String url = "http://128.199.43.215:3000/api/getpost/"+pid;

        JsonArrayRequest jsonRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONArray res = response;
                            //Log.d("res", response.toString());
                            PostObject data = new PostObject(res.getJSONObject(0));
                            arr.add(data);

                        } catch (JSONException e) {
                            Log.d("Exception", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        arr.notifyDataSetChanged();
        queue.add(jsonRequest);
    }

    public static void ratePost(final RequestQueue queue, final PostObject obj, final String op) {
        String url = "http://128.199.43.215:3000/api/rate";
        Map jsonmap = new HashMap<String, String>();
        jsonmap.put("pid",obj.pid());
        jsonmap.put("likes",obj.likes());
        jsonmap.put("op",op);
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

    public void getTopData(RequestQueue queue, final PostObjectListAdapter arr, final SwipeRefreshLayout swipeLayout) {
        JSONObject json;
        String url = "http://128.199.43.215:3000/api/popular";

        JsonArrayRequest jsonRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONArray res = response;
                            //Log.d("res", response.toString());
                            arr.clear();
                            for (int n = 0 ; n < response.length() ; n++) {
                                //Log.d("res", response.getJSONObject(n).toString());
                                PostObject data = new PostObject(res.getJSONObject(n));
                                if (data.likes > 0) {
                                    arr.add(data);
                                }
                            }
                            arr.notifyDataSetChanged();
                            swipeLayout.setRefreshing(false);
                        } catch (JSONException e) {
                            Log.d("Exception", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        queue.add(jsonRequest);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        location = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (location != null) {
            Log.d("latitude ","val:"+location.getLatitude());
            Log.d("longitude ","val:"+location.getLongitude());
        }
        else {
            Log.d("err","no location");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("Error", "Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }
}