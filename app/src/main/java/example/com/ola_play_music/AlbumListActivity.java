package example.com.ola_play_music;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by ranjeet on 15/12/17.
 */

public class AlbumListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RequestQueue requestQueue;
    SongListAdapter adapter;
    ArrayList<Song> songs=new ArrayList<>();
    public static final String URL="http://starlord.hackerearth.com/studio";
    EditText etSearch;
    TextView tvResutFound,tvResultNotFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_detail);
        recyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        etSearch=(EditText)findViewById(R.id.et_search);
        tvResultNotFound=(TextView)findViewById(R.id.tv_result_not_found);
        tvResutFound=(EditText)findViewById(R.id.tv_result_found);
        requestQueue = Volley.newRequestQueue(this);

        try {
            Glide.with(this).load("http://hck.re/kWWxUI").into((ImageView) findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();
        JsonArrayRequest request = new JsonArrayRequest(URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null) {
                            tvResultNotFound.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), "Couldn't fetch the songs! Please try again.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        pDialog.dismiss();
                        tvResultNotFound.setVisibility(View.GONE);
                        songs = new Gson().fromJson(response.toString(), new TypeToken<ArrayList<Song>>() {
                        }.getType());

                        fillSongList(songs);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error in getting json
                pDialog.dismiss();
                //LoadFromCache(URL);
                tvResultNotFound.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Some Error Occurred", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(request);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }
            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

    }
    private void fillSongList(ArrayList<Song> songs) {
        adapter=new SongListAdapter(songs,getApplicationContext(),this,0);
        recyclerView.setAdapter(adapter);
    }
    private void filter(String song) {
        ArrayList<Song> temp = new ArrayList();
        if (songs.size() > 0 && songs != null) {
            for (Song d : songs) {
                //or use .equal(text) with you want equal match
                //use .toLowerCase() for better matches
                if (d.getSong().toLowerCase().contains(song.toLowerCase())) {
                    temp.add(d);
                }
            }
            //update recyclerview

            tvResutFound.setVisibility(View.VISIBLE);
            tvResutFound.setText(temp.size()+" Result Found");
            adapter.updateList(temp);
        }
    }

}

