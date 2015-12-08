package se.liu.oscho707student.plaskapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PostObjectAdapter extends ArrayAdapter<PostObject> {
    public PostObjectAdapter(Context context, ArrayList<PostObject> postData) {
        super(context, 0, postData);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        PostObject data = (PostObject) getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.card, parent, false);
        }

        TextView title = (TextView) convertView.findViewById(R.id.cardText);
        title.setText(data.text);
        TextView likes = (TextView) convertView.findViewById(R.id.cardLikes);
        if(data.likes < 0) {
            likes.setText(String.valueOf(data.likes));
        }
        else {
            likes.setText("+" + String.valueOf(data.likes));
        }
        TextView pid = (TextView) convertView.findViewById(R.id.cardPid);
        pid.setText("#"+String.valueOf(data.pid));

        return convertView;
    }
}
