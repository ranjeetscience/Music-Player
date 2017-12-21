package example.com.ola_play_music;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by ranjeet on 19/12/17.
 */

public class SongPlaying extends AppCompatActivity {
    ImageView coverImage,download,favourite,play,next,previous,noNext,noPrev;
    TextView tvSongName,tvArtistName;
    ArrayList<Song> songs;
    public static final String URL="http://starlord.hackerearth.com/studio";
    private MediaPlayer mMediaPlayer;
    int currentPostion=0;
    private ProgressDialog mProgressDialog;
    public static final String My_Pref="my_pref";
    ProgressBar progressBar;
     DatabaseHandler db;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song);
        coverImage=(ImageView)findViewById(R.id.im_back_ground);
        download=(ImageView)findViewById(R.id.action_download);
        favourite=(ImageView)findViewById(R.id.action_add_favourite);
        play=(ImageView)findViewById(R.id.action_play);
        next=(ImageView)findViewById(R.id.action_play_next);
        noNext=(ImageView)findViewById(R.id.action_no_next);
        noPrev=(ImageView)findViewById(R.id.action_no_previous);
        previous=(ImageView)findViewById(R.id.action_previous);
        tvSongName=(TextView)findViewById(R.id.tv_song_name);
        tvArtistName=(TextView)findViewById(R.id.tv_artist_name);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        db = new DatabaseHandler(getApplicationContext());
        final SharedPreferences prefs = getSharedPreferences(My_Pref, MODE_PRIVATE);
        Intent intent=getIntent();
        int position=intent.getIntExtra("postion",0);
        currentPostion=position;
        final int size=intent.getIntExtra("size",0);
        mMediaPlayer=new MediaPlayer();
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        //no next present
        if(currentPostion==(size-1)){
            next.setVisibility(View.GONE);
            noNext.setVisibility(View.VISIBLE);
        }
        //no prev present
        if(currentPostion==0){
            previous.setVisibility(View.GONE);
            noPrev.setVisibility(View.VISIBLE);
        }
        getSongFromServer(currentPostion,0);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPostion=currentPostion+1;
                if(currentPostion==(size-1)){
                    next.setVisibility(View.GONE);
                    noNext.setVisibility(View.VISIBLE);
                }
                previous.setVisibility(View.VISIBLE);
                noPrev.setVisibility(View.GONE);
                getSongFromServer(currentPostion,0);
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPostion=currentPostion-1;
                if(currentPostion==0){
                    previous.setVisibility(View.GONE);
                    noPrev.setVisibility(View.VISIBLE);
                }
                getSongFromServer(currentPostion,0);
            }
        });
        play.setImageResource(R.mipmap.ic_pause_);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlayPause();
            }
        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson=new Gson();
                String json = prefs.getString("MyObject", "");
                Song song = gson.fromJson(json, Song.class);
                boolean haspermission = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
                if (haspermission) {
                    ActivityCompat.requestPermissions(SongPlaying.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);

                } else {
                    new DownloadFileFromURL().execute(song);
                }
            }
        });
        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson=new Gson();
                String json = prefs.getString("MyObject", "");
                Song song = gson.fromJson(json, Song.class);
                favourite_make(song);
            }
        });

    }


    private void setcontent(Song song)
    {
        progressBar.setVisibility(View.VISIBLE);
        if(db.getSong(song.getSong())==null)
        {
            favourite.setImageResource(R.drawable.ic_star_not_filled);
        }
        else
        {
            favourite.setImageResource(R.drawable.ic_star_filled);
        }
        try {
            Glide.with(getApplicationContext()).load(song.getCover_image()).into(coverImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        tvArtistName.setText(song.getArtists());
        tvSongName.setText(song.getSong());
        playSong(song);

    }
    private void getSongFromServer(final int postion,final int p) {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();
        JsonArrayRequest request = new JsonArrayRequest(URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null) {
                            return;
                        }
                        songs= new Gson().fromJson(response.toString(), new TypeToken<ArrayList<Song>>() {
                        }.getType());
                        pDialog.dismiss();
                        if(p==0) {
                            setcontent(songs.get(postion));
                            SharedPreferences.Editor editor = getSharedPreferences(My_Pref, MODE_PRIVATE).edit();
                            Gson gson = new Gson();
                            String json = gson.toJson(songs.get(postion));
                            editor.putString("MyObject", json);
                            editor.apply();
                        }

                        if(p==2)
                        {
                            favourite_make(songs.get(postion));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Toast.makeText(getApplicationContext(),"error occured",Toast.LENGTH_SHORT).show();
            }
        });
        AppController.getInstance().addToRequestQueue(request);
    }

    private void favourite_make(Song song) {
        Song getsongfromdb=db.getSong(song.getSong());
        if(getsongfromdb==null)
        {
            db.addPlaylist(song);
            favourite.setImageResource(R.drawable.ic_star_filled);
            Toast.makeText(getApplicationContext(),"Added to Playlist",Toast.LENGTH_SHORT).show();
        }
        else
        {
            db.deleteSong(song);
            favourite.setImageResource(R.drawable.ic_star_not_filled);
            Toast.makeText(getApplicationContext(),"Removed From PlayList",Toast.LENGTH_SHORT).show();

        }
    }

    private void playSong(final Song song) {

        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();}

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                togglePlayPause();
            }
        });
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
            }
        });

        try {
            mMediaPlayer.setDataSource(song.getUrl());
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
                    e.printStackTrace();}

        mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {

            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {

                if(percent==100)
                    progressBar.setVisibility(View.GONE);
            }
        });

    }

    private void togglePlayPause() {

    if(mMediaPlayer!=null) {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            play.setImageResource(R.mipmap.ic_play);
        } else {
            progressBar.setVisibility(View.GONE);
            play.setImageResource(R.mipmap.ic_pause_);
            mMediaPlayer.start();
        }
    }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mMediaPlayer!=null)
            mMediaPlayer.stop();
        mMediaPlayer=null;
    }

    class DownloadFileFromURL extends AsyncTask<Song, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(SongPlaying.this);
            mProgressDialog.setTitle("Downloading");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();        }
        @Override
        protected String doInBackground(Song... song) {
            int count;
            try {
                java.net.URL urlLocation = new URL(song[0].getUrl());
                URLConnection connection = urlLocation.openConnection();
                connection.connect();
                URL url = new URL(connection.getHeaderField("Location").toString());
                URLConnection conection = url.openConnection();
                conection.connect();
                // this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream
                OutputStream output = new FileOutputStream("/sdcard/"+song[0].getSong()+".mp3");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(""+(int)((total*100)/lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            mProgressDialog.setProgress(Integer.parseInt(progress[0]));
        }
        @Override
        protected void onPostExecute(String file_url) {
            mProgressDialog.dismiss();

        }

    }
}
