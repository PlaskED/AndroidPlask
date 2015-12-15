package se.liu.oscho707student.plaskapp;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

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
        final PostObjectListAdapter arr = new PostObjectListAdapter(getContext(), postArr);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((MainActivity) getActivity()).getTopData(queue, arr, swipeLayout);
            }
        });

        ((MainActivity)getActivity()).getTopData(queue, arr, swipeLayout);

        ListView lw = (ListView) view.findViewById(R.id.topList);

        lw.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PostObject data = (PostObject) arr.getItem(position);
                android.support.v4.app.FragmentManager fm = getFragmentManager();
                android.support.v4.app.FragmentTransaction fm_t = fm.beginTransaction();

                android.support.v4.app.Fragment fragment = new ViewFragment();
                Bundle bundle = new Bundle();
                bundle.putCharSequence("text", data.text);
                bundle.putInt("likes", data.likes);
                bundle.putInt("pid", data.pid);
                fragment.setArguments(bundle);

                fm_t.replace(R.id.mainView, fragment);
                fm_t.addToBackStack(null);
                fm_t.commit();
            }
        });

        lw.setAdapter(arr);

        return view;
    }

    public void getPopular(RequestQueue queue, final PostObjectListAdapter arr, SwipeRefreshLayout swipeLayout) {
        ((MainActivity)getActivity()).getTopData(queue, arr, swipeLayout);
    }


}
