package se.liu.oscho707student.plaskapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ViewFragment extends android.support.v4.app.Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_view, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        final SwipeFlingAdapterView cardFrame = (SwipeFlingAdapterView) view.findViewById(R.id.cardContainer);
        final ArrayList<PostObject> posts = new ArrayList<PostObject>();
        final PostObjectAdapter arrayAdapter = new PostObjectAdapter(getActivity(), posts);
        cardFrame.setAdapter(arrayAdapter);

        Bundle bundle = this.getArguments();
        String text = bundle.getCharSequence("text").toString();
        Integer likes = bundle.getInt("likes");
        Integer pid = bundle.getInt("pid");
        Map jsonmap = new HashMap<String, String>();
        jsonmap.put("text",text);
        jsonmap.put("likes", likes);
        jsonmap.put("pid", pid);
        JSONObject jsonBody = new JSONObject(jsonmap);
        PostObject data = new PostObject(jsonBody);
        arrayAdapter.add(data);
        arrayAdapter.notifyDataSetChanged();

        cardFrame.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
                                       private Object dataObject;

                                       @Override
                                       public void removeFirstObjectInAdapter() {
                                           posts.remove(0);
                                           arrayAdapter.notifyDataSetChanged();
                                       }

                                       @Override
                                       public void onLeftCardExit(Object dataObject) {
                                           getFragmentManager().popBackStackImmediate();
                                           //In future maybe implement so its possbile to up/downvote here aswell?
                                           //PostObject jsondata = (PostObject) dataObject;
                                           //MainActivity.ratePost(queue, jsondata, "down");


                                       }

                                       @Override
                                       public void onRightCardExit(Object dataObject) {
                                           getFragmentManager().popBackStackImmediate();
                                           //In future maybe implement so its possbile to up/downvote here aswell?
                                           //PostObject jsondata = (PostObject) dataObject;
                                           //MainActivity.ratePost(queue, jsondata, "up");
                                       }

                                       @Override
                                       public void onAdapterAboutToEmpty(int itemsInAdapter) {
                                           //Not needed for single card
                                       }

                                       @Override
                                       public void onScroll(float scrollProgressPercent) {
                                           //Not needed but must be implemented for SwipeFling
                                       }
                                   }

        );

        // Not needed but must be implemented for SwipeFling
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

