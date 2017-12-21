package example.com.ola_play_music;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ranjeet on 15/12/17.
 */

public class PlayListClass extends AppCompatActivity {
    RecyclerView recyclerView;
    PlayListAdapter adapter;
    ArrayList<Song> songs=new ArrayList<>();
    EditText etSearch;
    TextView tvResutFound,tvResultNotFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_detail);
        recyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        etSearch=(EditText)findViewById(R.id.et_search);
        tvResutFound=(EditText)findViewById(R.id.tv_result_found);
        tvResultNotFound=(TextView)findViewById(R.id.tv_result_not_found);

        DatabaseHandler db=new DatabaseHandler(this);
        if(db.getAllPlayList()!=null)
        {
            songs=db.getAllPlayList();
            tvResultNotFound.setVisibility(View.GONE);
            fillSongList(songs);
        }
        else {
            tvResultNotFound.setVisibility(View.VISIBLE);
        }

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
        adapter=new PlayListAdapter(songs,getApplicationContext(),this,1);
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

