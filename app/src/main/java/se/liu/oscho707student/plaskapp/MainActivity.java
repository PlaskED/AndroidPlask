package se.liu.oscho707student.plaskapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.images.ImageManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private ListView menuList;
    private ArrayAdapter<String> mlAdapter;
    private android.support.v7.app.ActionBarDrawerToggle menuToggle;
    private DrawerLayout menuLayout;
    private String activityTitle;
    private LocationManager locationManager;
    private HashMap<String, Boolean> settings;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private View nameView;
    private ArrayList<String> optionArray;
    private boolean signedOn = false;
    private static String token;

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
        settings = new HashMap<String, Boolean>();
        initSettings();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestIdToken("903897751193-ribbhe2r2st90dd7knapnjq2tsesfh8g.apps.googleusercontent.com")
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fm_t = fm.beginTransaction();
        fm_t.add(R.id.mainView, new MainFragment());
        fm_t.commit();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }

        sendRequest(10); //Initiate GPS request and setup listener

    }

    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            if (Build.VERSION.SDK_INT >= 23 &&
                    ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            locationManager.removeUpdates(this);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    public void initSettings() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        boolean settingAll = sharedPref.getBoolean("all", true);
        boolean settingTop = sharedPref.getBoolean("top", true);
        boolean settingLocal = sharedPref.getBoolean("local", true);
        boolean settingDefault = sharedPref.getBoolean("default", true);
        settings.put("all", settingAll);
        settings.put("top", settingTop);
        settings.put("local", settingLocal);
        settings.put("default", settingLocal);
    }

    public void switchKey(String key, Boolean val) {
        settings.put(key, val);
    }

    public HashMap<String, Boolean> getSettings() {
        return settings;
    }

    public static String getToken() { return token; }

    public void sendRequest(int reqCode) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, reqCode);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 9001) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            TextView name = (TextView) nameView.findViewById(R.id.name);
            name.setText(acct.getDisplayName());

            String uri = acct.getPhotoUrl().toString();
            String url = uri.substring(0, uri.length() - 2) + 400;
            token = acct.getIdToken();
            new LoadProfileImage().execute(url);

            RequestQueue queue = Volley.newRequestQueue(this);
            authToken(queue);

            updateUI(true);
        } else {
            updateUI(false);
        }
    }

    private class LoadProfileImage extends AsyncTask<String, Void, RoundedBitmapDrawable> {
        protected RoundedBitmapDrawable doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bitm = null;
            try {
                InputStream in = new URL(urldisplay).openStream();
                bitm = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            RoundedBitmapDrawable pic = RoundedBitmapDrawableFactory.create(getResources(), bitm);
            return pic;
        }

        protected void onPostExecute(RoundedBitmapDrawable result) {
            ImageView picture = (ImageView) nameView.findViewById(R.id.picture);
            result.setCircular(true);
            result.setAntiAlias(true);
            picture.setImageDrawable(result);
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            nameView.setVisibility(View.VISIBLE);
            optionArray.set(4, "Sign out");
            mlAdapter.notifyDataSetChanged();
            signedOn = true;
        } else {
            nameView.setVisibility(View.GONE);
            optionArray.set(4, "Sign in");
            mlAdapter.notifyDataSetChanged();
            TextView name = (TextView) nameView.findViewById(R.id.name);
            name.setText("");
            ImageView picture = (ImageView) nameView.findViewById(R.id.picture);
            picture.setImageResource(android.R.color.transparent);
            signedOn = false;
        }
    }

    public boolean isLoggedIn() { return signedOn; }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Signing in");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 10: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (Build.VERSION.SDK_INT >= 23 &&
                            ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    }
                    locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

                    if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 1000,
                                locationListener);
                    }
                    else {
                        Toast toast = Toast.makeText(getApplicationContext(),"Locations is required to view/post locally.",Toast.LENGTH_SHORT);
                        toast.show();
                    }

                }
                return;
            }
        }
    }

    private void addMenuItems() {
        optionArray = new ArrayList<>();
        optionArray.addAll(Arrays.asList("Main", "New Post", "Top List", "Filter", "Sign In"));
        mlAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, optionArray);;
        nameView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.menu_footer, null, false);
        nameView.setVisibility(View.GONE);
        menuList.addFooterView(nameView);
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
        if(position == 4) {

            if (!signedOn) {
                signIn();
            }
            else {
                signOut();
            }
        }
        else {
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fm_t = fm.beginTransaction();
            if (position == 0) {
                fm_t.replace(R.id.mainView, new MainFragment());
            } else if (position == 1) {
                fm_t.replace(R.id.mainView, new PostFragment());
            } else if (position == 2) {
                fm_t.replace(R.id.mainView, new TopFragment());
            } else if (position == 3) {
                fm_t.replace(R.id.mainView, new FilterFragment());
            }

            fm_t.addToBackStack(null);
            fm_t.commit();
        }
        menuLayout.closeDrawers();
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, 9001);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(false);
                    }
                });
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(false);

                    }
                });
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

    public void getAllData(RequestQueue queue, final PostObjectAdapter arr) {
        String lastpid;
        if(!arr.isEmpty()) {
            lastpid = (arr.getItem(arr.getCount()-1).pid).toString();
        }
        else {
            lastpid = "0";
        }
        String url = "http://128.199.43.215:3000/api/getall/"+lastpid;

        JsonArrayRequest jsonRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONArray res = response;
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
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("token", getToken() );
                return headers;
            }

        };

        arr.notifyDataSetChanged();
        queue.add(jsonRequest);
    }

    public void getPost(RequestQueue queue, final PostObjectAdapter arr, final int pid) {
        String url = "http://128.199.43.215:3000/api/getpost/"+pid;

        JsonArrayRequest jsonRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONArray res = response;
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
        jsonmap.put("pid", obj.pid());
        jsonmap.put("likes", obj.likes());
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
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("token", getToken() );
                return headers;
            }

        };

        queue.add(req);
    }

    public void authToken(final RequestQueue queue) {
        String url = "http://128.199.43.215:3000/api/auth";
        //Log.d("id_token", ":"+token);
        Map jsonmap = new HashMap<String, String>();
        jsonmap.put("id_token", token);
        JSONObject jsonBody = new JSONObject(jsonmap);
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, jsonBody ,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("res",":"+response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.d("err",":"+error);
            }
        });

        queue.add(req);
    }

    public void getTopList(RequestQueue queue, final PostObjectListAdapter arr, final SwipeRefreshLayout swipeLayout) {
        JSONObject json;
        String url = "http://128.199.43.215:3000/api/popular";

        JsonArrayRequest jsonRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONArray res = response;
                            arr.clear();
                            for (int n = 0 ; n < response.length() ; n++) {
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

    public void getTopData(RequestQueue queue, final PostObjectAdapter arr) {
        JSONObject json;
        String url = "http://128.199.43.215:3000/api/popular";

        JsonArrayRequest jsonRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONArray res = response;
                            arr.clear();
                            for (int n = 0 ; n < response.length() ; n++) {
                                PostObject data = new PostObject(res.getJSONObject(n));
                                if (data.likes > 0) {
                                    arr.add(data);
                                }
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

    public void getLocalData(RequestQueue queue, final PostObjectAdapter arr, final String lat, final String lng) {
        JSONObject json;
        String url = "http://128.199.43.215:3000/api/getlocal/"+lng+"/"+lat;
        JsonArrayRequest jsonRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONArray res = response;
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

    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("all", settings.get("all").booleanValue());
        editor.putBoolean("top", settings.get("top").booleanValue());
        editor.putBoolean("local", settings.get("local").booleanValue());
        editor.putBoolean("default", settings.get("default").booleanValue());
        editor.commit();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}