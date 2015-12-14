package se.liu.oscho707student.plaskapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PostObjectListAdapter extends ArrayAdapter<PostObject> {
    public PostObjectListAdapter(Context context, ArrayList<PostObject> postData) {
        super(context, 0, postData);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        PostObject data = (PostObject) getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.post_view, parent, false);
        }

        TextView title = (TextView) convertView.findViewById(R.id.title);
        title.setText(data.text);
        TextView likes = (TextView) convertView.findViewById(R.id.likes);
        likes.setText("+" + String.valueOf(data.likes));

        return convertView;
    }
}
