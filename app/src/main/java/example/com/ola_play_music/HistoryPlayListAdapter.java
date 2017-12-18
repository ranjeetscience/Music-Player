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

public class HistoryPlayListAdapter extends RecyclerView.Adapter<HistoryPlayListViewHolder> {
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<History> histories;
    private Activity activity;
    private MediaPlayer mMediaPlayer;
    private ProgressDialog mProgressDialog;
    private static final int REQUEST_WRITE_STORAGE = 112;
    private int pageNo=0;

    public HistoryPlayListAdapter(ArrayList<History> histories, Context context, Activity activity,int pageNo)
    {
        this.context =context;
        inflater=LayoutInflater.from(context);
        this.histories=histories;
        this.pageNo=pageNo;
        this.activity=activity;
    }

    @Override
    public HistoryPlayListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HistoryPlayListViewHolder(inflater.inflate(R.layout.history_list_item,parent,false));
    }

    @Override
    public void onBindViewHolder(final HistoryPlayListViewHolder holder, final int position) {

        final History history=histories.get(position);
        holder.tvArtist.setText(history.getArtists());
        holder.tvName.setText(history.getSong());
        final DatabaseHandler db = new DatabaseHandler(context);

        try {
            Glide.with(context).load(history.getCover_image()).into(holder.thumbnail);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.addToHIistory(new History(history.getUrl(),history.getArtists(),history.getCover_image(),history.getSong(), Calendar.getInstance().getTimeInMillis()));
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                    mMediaPlayer.reset();}
                try {
                    mMediaPlayer.setDataSource(history.getUrl());
                    mMediaPlayer.prepareAsync();
                    mProgressDialog = new ProgressDialog(activity);
                    mProgressDialog.setTitle("Song Loading");
                    mProgressDialog.setMessage("Buffering, Please Wait!");
                    mProgressDialog.setIndeterminate(false);
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.show();
                } catch (IOException e) {
                    e.printStackTrace();}}});
        mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {

            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                if(percent==100)
                    mProgressDialog.dismiss();
                mProgressDialog.setProgress(percent);
            }
        });

        holder.tvTime.setText("Played At : "+DateTimeUtil.getTimeStringFromMillies(history.getTimestamp()));

        holder.btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean haspermission = ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
                if (haspermission) {
                    ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);

                } else {
                    new DownloadFileFromURL().execute(history);
                }
            }

        });

    }



    private void togglePlayPause() {

        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        } else {
            mMediaPlayer.start();
            mProgressDialog.dismiss();
            //Toast.makeText(context,"Song Start Playing",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public int getItemCount() {
        return histories.size();
    }

    public void updateList(ArrayList<History> temp) {
        histories = temp;
        notifyDataSetChanged();
    }
    class DownloadFileFromURL extends AsyncTask<History, String, String> {

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
        protected String doInBackground(History... song) {
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
