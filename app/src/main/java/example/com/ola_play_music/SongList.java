package example.com.ola_play_music;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by ranjeet on 19/12/17.
 */

public class SongList{
    public static final String URL="http://starlord.hackerearth.com/studio";


    public SongList(){}

    public static void getSongs(final int position, final ResponseListener<Song> songResponseListener)
    {

        JsonArrayRequest request = new JsonArrayRequest(URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null) {
                            return;
                        }

                        ArrayList<Song>  songs= new Gson().fromJson(response.toString(), new TypeToken<ArrayList<Song>>() {
                        }.getType());
                        songResponseListener.response(songs.get(position));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getInstance().addToRequestQueue(request);

    }
}
