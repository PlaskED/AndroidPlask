package se.liu.oscho707student.plaskapp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class PostObject {
    private JSONObject json;
    public String text, likes, xpos,ypos;

    public PostObject(JSONObject json) {
        this.json = json;

        try {
            this.text = this.json.getString("text");
            this.likes = this.json.getString("likes");
            this.xpos = this.json.getString("xpos");
            this.ypos = this.json.getString("ypos");
        } catch (JSONException e) {
            Log.d("Error, JSON", e.toString());
        }
    }

    public String text() {return this.text;}
    public String likes() {return this.likes;}
    public String xpos() {return this.ypos;}
    public String ypos() {return this.xpos;}

}
