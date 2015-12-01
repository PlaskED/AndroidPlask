package se.liu.oscho707student.plaskapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by oscho707 on 11/18/15.
 */
public class PostObjectAdapter extends ArrayAdapter<PostObject> {
    public PostObjectAdapter(Context context) {
        super(context, R.layout.card);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        PostObject data = (PostObject) getItem(position);

        return convertView;
    }
}
