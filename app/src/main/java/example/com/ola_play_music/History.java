package example.com.ola_play_music;

/**
 * Created by ranjeet on 17/12/17.
 */

public class History implements Comparable<History> {

    private String song;
    private String url;
    private String artists;
    private String cover_image;

    public History(String url, String artists, String cover_image, String song, long timestamp) {
        this.url = url;
        this.artists = artists;
        this.cover_image = cover_image;
        this.song = song;
        this.timestamp = timestamp;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    private long timestamp;


    public History(){}

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getArtists() {
        return artists;
    }

    public void setArtists(String artists) {
        this.artists = artists;
    }

    public String getCover_image() {
        return cover_image;
    }

    public void setCover_image(String cover_image) {
        this.cover_image = cover_image;
    }

    @Override
    public int compareTo(History o) {
        if(this.timestamp > o.getTimestamp())
            return -1;
        else if(this.timestamp<o.getTimestamp())
            return 1;
        else
            return 0;
    }
}
