package se.liu.oscho707student.plaskapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;

public class MainFragment extends android.support.v4.app.Fragment {
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
                                           PostObject jsondata = (PostObject) dataObject;
                                           MainActivity.ratePost(queue, jsondata, "down");
                                       }

                                       @Override
                                       public void onRightCardExit(Object dataObject) {
                                           PostObject jsondata = (PostObject) dataObject;
                                           MainActivity.ratePost(queue, jsondata, "up");
                                       }

                                       @Override
                                       public void onAdapterAboutToEmpty(int itemsInAdapter) {
                                           // Ask for more data here
                                           ((MainActivity) getActivity()).getAllData(queue, arrayAdapter);

                                           //Behöver requesta samt hämta postionsdata först. Ska enbart hämta lokal data sen om inställd på det.
                                           //String lng =
                                           //String lat =
                                           //((MainActivity) getActivity()).getLocalData(queue, arrayAdapter, lng, lat);
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

}
