package example.com.ola_play_music;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by ranjeet on 16/12/17.
 */

public class PlayListAdapter extends RecyclerView.Adapter<SongListAdapterViewHolder> {
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<Song> songs;
    private Activity activity;
    private MediaPlayer mMediaPlayer;
    private ProgressDialog mProgressDialog;
    private static final int REQUEST_WRITE_STORAGE = 112;

    int pageNo=0;
    public PlayListAdapter(ArrayList<Song> songs, Context context, Activity activity,int pageNo)
    {
        this.context =context;
        inflater=LayoutInflater.from(context);
        this.songs=songs;
        this.pageNo=pageNo;
        this.activity=activity;
    }

    @Override
    public SongListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SongListAdapterViewHolder(inflater.inflate(R.layout.song_list_item,parent,false));
    }

    @Override
    public void onBindViewHolder(final SongListAdapterViewHolder holder, final int position) {

        final Song song=songs.get(position);
        holder.tvArtist.setText(song.getArtists());
        holder.tvName.setText(song.getSong());
        final DatabaseHandler db = new DatabaseHandler(context);
        if(db.getSong(song.getSong())==null)
        {
            holder.btDownload.setImageResource(R.drawable.ic_star_not_filled);
        }
        else
        {
            holder.btDownload.setImageResource(R.drawable.ic_star_filled);
        }

        try {
            Glide.with(context).load(song.getCover_image()).into(holder.thumbnail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                togglePlayPause(song);
            }
        });
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.addToHIistory(new History(song.getUrl(),song.getArtists(),song.getCover_image(),song.getSong(), Calendar.getInstance().getTimeInMillis()));

                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                    mMediaPlayer.reset();}

                try {
                    mMediaPlayer.setDataSource(song.getUrl());
                    mMediaPlayer.prepareAsync();
                    mProgressDialog = new ProgressDialog(activity);
                    mProgressDialog.setTitle("Song Loading");
                    mProgressDialog.setIndeterminate(false);
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.show();
                } catch (IOException e) {
                    e.printStackTrace();}
            }
        });


        mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {

            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {

                if(percent==100)
                    mProgressDialog.dismiss();
                mProgressDialog.setProgress(percent);
            }
        });

        holder.btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean haspermission = ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
                if (haspermission) {
                    ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);

                } else {
                    new DownloadFileFromURL().execute(song);
                }
            }

        });
        holder.btDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Song getsongfromdb=db.getSong(song.getSong());
                if(getsongfromdb==null)
                {
                    db.addPlaylist(song);
                    holder.btDownload.setImageResource(R.drawable.ic_star_filled);
                    Toast.makeText(context,"Added to Playlist",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    db.deleteSong(song);
                    holder.btDownload.setImageResource(R.drawable.ic_star_not_filled);
                    //Log.v("size",String.valueOf(db.getAllPlayList().size()));
                    Toast.makeText(context,"Removed From PlayList",Toast.LENGTH_SHORT).show();
                    if(pageNo==1)
                    {
                        updateView(position);
                    }

                }
            }
        });
    }

    private void updateView(int position) {
        songs.remove(position);
        //recycler.removeViewAt(position);
        notifyDataSetChanged();
        //mAdapter.notifyItemRangeChanged(position, list.size());
    }


    private void togglePlayPause(Song song) {

        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        } else {
            mMediaPlayer.start();
            mProgressDialog.dismiss();
            Toast.makeText(context,"Song Start Playing",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public int getItemCount() {
        return songs.size();
    }

    public void updateList(ArrayList<Song> temp) {
        songs = temp;
        notifyDataSetChanged();
    }
    class DownloadFileFromURL extends AsyncTask<Song, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(activity);
            mProgressDialog.setTitle("Downloading");
            mProgressDialog.setMessage("Downloading, Please Wait!");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();        }
        @Override
        protected String doInBackground(Song... song) {
            int count;
            try {
                URL urlLocation = new URL(song[0].getUrl());
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
