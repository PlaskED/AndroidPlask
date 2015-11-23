package se.liu.oscho707student.plaskapp;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainFragment extends android.support.v4.app.Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        final RequestQueue queue = Volley.newRequestQueue(getContext());
        ArrayList<PostObject> postArr = new ArrayList<PostObject>();
        final PostObjectAdapter posts = new PostObjectAdapter(getContext(), postArr);
        getJsonData(queue, posts);

        //HorizontalScrollView hsw = (HorizontalScrollView) view.findViewById(R.id.HorizontalView);
        ListView lw = (ListView) view.findViewById(R.id.listView);
        lw.setAdapter(posts);

        return view;
    }


    void getJsonData(RequestQueue queue, final PostObjectAdapter arr){
        JSONObject json;
        String url = "http://128.199.43.215:3000/api/getposts";

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray posts = response.getJSONArray("result");
                            arr.clear();
                            for (int n = response.length()-1 ; 0 <= n ; n--) {
                                PostObject data = new PostObject(posts.getJSONObject(n));
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

        queue.add(jsonRequest);
    }
}
