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
    public PostObjectAdapter(Context context, ArrayList<PostObject> postData) {
        super(context, 0, postData);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        PostObject data = (PostObject) getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.card, parent, false);
        }

        //R.layout.card, R.id.cardText
        TextView title = (TextView) convertView.findViewById(R.id.cardText);
        title.setText(data.text);
        TextView likes = (TextView) convertView.findViewById(R.id.cardLikes);
        likes.setText("+"+data.likes);
        TextView pid = (TextView) convertView.findViewById(R.id.cardPid);
        pid.setText("#123");//+data.pid);

        return convertView;
    }
}
