package example.com.ola_play_music;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ranjeet on 17/12/17.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "playlistManager";

    private static final String TABLE_PLAYLIST = "playlist";
    private static final String KEY_ID = "song_name";
    private static final String KEY_ARTIST_NAME = "artist";
    private static final String KEY_URL = "url";
    private static final String KEY_PHOTO="photo";

    private static final String TABLE_HISTORY = "history1";
    private static final String KEY_ID_HISTORY="history";
    private static final String KEY_SONG = "song_name";
    private static final String KEY_ARTIST_NAME_1 = "artist";
    private static final String KEY_URL_1 = "url";
    private static final String KEY_PHOTO_1="photo";
    private static final String KEY_TIMESTAMP="created";



    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_PLAYLIST + "("
                + KEY_ID + " TEXT PRIMARY KEY," + KEY_ARTIST_NAME + " TEXT,"
                + KEY_URL + " TEXT,"
                + KEY_PHOTO + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);


        String HISTORY_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_HISTORY + "("
                + KEY_ID_HISTORY + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_SONG + " TEXT,"+KEY_ARTIST_NAME_1+" TEXT,"
                +KEY_URL_1+" TEXT,"
                +KEY_PHOTO+" TEXT,"
                + KEY_TIMESTAMP + " TEXT" + ")";
        db.execSQL(HISTORY_TABLE);


    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(db);
    }

    public void addPlaylist(Song song) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID,song.getSong());
        values.put(KEY_ARTIST_NAME, song.getArtists());
        values.put(KEY_URL, song.getUrl());
        values.put(KEY_PHOTO,song.getCover_image());
        db.insert(TABLE_PLAYLIST, null, values);
        db.close();
    }
    public Song getSong(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PLAYLIST, new String[] { KEY_ID,
                        KEY_ARTIST_NAME,KEY_URL,KEY_PHOTO }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null && cursor.getCount()!=0 )
            cursor.moveToFirst();
        else
            return null;

        Song plalist = new Song(cursor.getString(0),
                cursor.getString(1), cursor.getString(2),cursor.getString(3));
        return plalist;
    }

    public ArrayList<Song> getAllPlayList() {
        ArrayList<Song> plalList = new ArrayList<Song>();
        String selectQuery  = "SELECT  * FROM " + TABLE_PLAYLIST;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor==null || cursor.getCount()==0)
            return null;

        if (cursor.moveToFirst()) {
            do {
                Song song = new Song();
                song.setSong(cursor.getString(0));
                song.setArtists(cursor.getString(1));
                song.setUrl(cursor.getString(2));
                song.setCover_image(cursor.getString(3));
                plalList.add(song);
            } while (cursor.moveToNext());
        }

        return plalList;
    }
    public void deleteSong(Song song) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PLAYLIST, KEY_ID + " = ?",
                new String[] { String.valueOf(song.getSong()) });
        db.close();
    }




    public void addToHIistory(History dbModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SONG,dbModel.getSong());
        values.put(KEY_ARTIST_NAME_1, dbModel.getArtists());
        values.put(KEY_URL_1,dbModel.getUrl());
        values.put(KEY_PHOTO_1,dbModel.getCover_image());
        values.put(KEY_TIMESTAMP,String.valueOf(dbModel.getTimestamp()));


        // Inserting Row
        db.insert(TABLE_HISTORY, null, values);
        db.close(); // Closing database connection
    }

    public List<History> getAllHistory()
    {
        List<History> dbModelsList=new ArrayList<History>();
        String selectQuery = "SELECT  * FROM " + TABLE_HISTORY;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor==null || cursor.getCount()==0)
            return null;

        if (cursor.moveToFirst())
        {
            do {
                History dbModel = new History();
                dbModel.setId(Integer.parseInt(cursor.getString(0)));
                dbModel.setSong(cursor.getString(1));
                dbModel.setArtists(cursor.getString(2));
                dbModel.setUrl(cursor.getString(3));
                dbModel.setCover_image(cursor.getString(4));
                dbModel.setTimestamp(Long.parseLong(cursor.getString(5)));
                dbModelsList.add(dbModel);
            } while (cursor.moveToNext());
        }
        db.close();
        return dbModelsList;

    }


}