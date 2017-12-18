package example.com.ola_play_music;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by ranjeet on 16/12/17.
 */
public class SongListAdapterViewHolder extends RecyclerView.ViewHolder {
    public ImageView thumbnail,btPlay,btDownload,btFavorite;
    public TextView tvName,tvArtist;
    public CardView cardView;

    public SongListAdapterViewHolder(View itemView) {
        super(itemView);
        tvName=(TextView)itemView.findViewById(R.id.tv_song_name);
        tvArtist=(TextView)itemView.findViewById(R.id.tv_artist_name);
        thumbnail=(ImageView)itemView.findViewById(R.id.song_image_view);
        btPlay=(ImageView)itemView.findViewById(R.id.play_button);
        btDownload=(ImageView)itemView.findViewById(R.id.pause_button);
        cardView=(CardView)itemView.findViewById(R.id.card_view);
        btFavorite=(ImageView) itemView.findViewById(R.id.overflow);
    }
}
