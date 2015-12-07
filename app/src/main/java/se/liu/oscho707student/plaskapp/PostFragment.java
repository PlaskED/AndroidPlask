package se.liu.oscho707student.plaskapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


public class PostFragment extends android.support.v4.app.Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_post, container, false);
        final RequestQueue queue = Volley.newRequestQueue(getContext());

        Button postButton = (Button) view.findViewById(R.id.postButton);

        postButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String postText = ((EditText) view.findViewById(R.id.postText)).getText().toString();
                sendPost(queue, postText);

                android.support.v4.app.FragmentManager fm = getFragmentManager();
                android.support.v4.app.FragmentTransaction fm_t = fm.beginTransaction();
                fm_t.replace(R.id.mainView, new MainFragment());
                fm_t.commit();
            }
        });

        return view;
    };

    public static void sendPost(final RequestQueue queue, final String post){
            String url = "http://128.199.43.215:3000/api/"+post;
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) {

                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }
            );
            queue.add(postRequest);
        }

}

