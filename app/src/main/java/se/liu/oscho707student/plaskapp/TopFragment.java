package se.liu.oscho707student.plaskapp;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

public class TopFragment extends android.support.v4.app.Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_top, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        final RequestQueue queue = Volley.newRequestQueue(getContext());
        final SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        ArrayList<PostObject> postArr = new ArrayList<PostObject>();
        final PostObjectAdapter arr = new PostObjectAdapter(getContext(), postArr);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //doJodelData(queue, arr, swipeLayout);
            }
        });

        return view;
    }

    public void getPopular(RequestQueue queue, final PostObjectAdapter arr) {
        ((MainActivity)getActivity()).getJsonData(queue,arr);
    }


}
