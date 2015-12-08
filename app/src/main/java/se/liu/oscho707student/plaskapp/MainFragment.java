package se.liu.oscho707student.plaskapp;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainFragment extends android.support.v4.app.Fragment {
    private int i;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        final RequestQueue queue = Volley.newRequestQueue(getContext());

        final SwipeFlingAdapterView cardFrame = (SwipeFlingAdapterView) view.findViewById(R.id.cardContainer);
        final ArrayList<PostObject> posts = new ArrayList<PostObject>();
        final PostObjectAdapter arrayAdapter = new PostObjectAdapter(getActivity(), posts);
        cardFrame.setAdapter(arrayAdapter);
        ((MainActivity) getActivity()).getJsonData(queue, arrayAdapter);



        cardFrame.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            private Object dataObject;

            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                posts.remove(0);
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                //When swiping left, the post should be disliked
                HashMap<String,String> jsonmap = new HashMap<>();
                Toast.makeText(getActivity(), "Left!", Toast.LENGTH_SHORT).show();

                PostObject jsondata = (PostObject) dataObject;
                Log.d("PID", jsondata.pid().toString());
                MainActivity.ratePost(queue, jsondata, "down");

            }

            @Override
            public void onRightCardExit(Object dataObject) {
                //When swiping right, like the post
                Toast.makeText(getActivity(), "Right!", Toast.LENGTH_SHORT).show();

                PostObject jsondata = (PostObject) dataObject;
                Log.d("PID", jsondata.pid().toString());
                MainActivity.ratePost(queue, jsondata, "up");
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
                ((MainActivity)getActivity()).getJsonData(queue, arrayAdapter);
                //posts.add("XML ".concat(String.valueOf(i)));
                //arrayAdapter.notifyDataSetChanged();
                //Log.d("LIST", "notified");
                //i++;
            }

            @Override
            public void onScroll(float scrollProgressPercent) {

                //View view = cardFrame.getSelectedView();
                //view.findViewById(R.id.background).setAlpha(0);
                //view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0); view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        });

        // Optionally add an OnItemClickListener
        cardFrame.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                makeToast(getContext(), "Clicked!");
            }

            void makeToast(Context ctx, String s) {
                Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

}
