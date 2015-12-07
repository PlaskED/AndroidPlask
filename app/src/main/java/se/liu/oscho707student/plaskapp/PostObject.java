package se.liu.oscho707student.plaskapp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class PostObject {
    private JSONObject json;
    public String text, lat, lng;
    public Integer likes, pid;

    public PostObject(JSONObject json) {
        this.json = json;

        try {
            this.text = this.json.getString("text");
            this.lat = this.json.getString("lat");
            this.lng = this.json.getString("lng");
            this.likes = 0;
            this.pid = this.json.getInt("pid");
        } catch (JSONException e) {
            Log.d("Error, JSON", e.toString());
        }
    }

    public String text() {return this.text;}
    public String lat() {return this.lat;}
    public String lng() {return this.lng;}
    public Integer likes() {return this.likes;}
    public Integer pid() {return this.pid;}

}
