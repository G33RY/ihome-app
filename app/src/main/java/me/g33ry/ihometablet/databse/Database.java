package me.g33ry.ihometablet.databse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

import androidx.annotation.Nullable;

import org.json.JSONObject;

import java.util.ArrayList;

import me.g33ry.ihometablet.ui.home.scenes.Scene;

public class Database extends SQLiteOpenHelper {
    private static Database instance;
    public static synchronized Database getInstance(Context context){
        if(instance == null) instance = new Database(context.getApplicationContext());
        return instance;
    }

    private static final String DATABASE_NAME = "ihome";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_SCENES = "scenes";

    // Post Table Columns
    private static final String KEY_SCENE_ID = "id";
    private static final String KEY_SCENE_JSON = "json";


    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      String CREATE_SCENES_TABLE = "CREATE TABLE " + TABLE_SCENES +
                "(" +
                    KEY_SCENE_ID + " TEXT PRIMARY KEY," +
                    KEY_SCENE_JSON + " TEXT" +
                ")";

      db.execSQL(CREATE_SCENES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCENES);
            onCreate(db);
        }
    }

    public long addOrUpdateScene(Scene scene) {
        // The database connection is cached so it's not expensive to call getWriteableDatabase() multiple times.
        SQLiteDatabase db = getWritableDatabase();
        long sceneId = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_SCENE_ID, scene.getId());
            values.put(KEY_SCENE_JSON, scene.toJson());

            int rows = db.update(TABLE_SCENES, values, KEY_SCENE_ID + "= ?", new String[]{String.valueOf(scene.getId())});

            // Check if update succeeded
            if (rows == 1) {
                // Get the primary key of the user we just updated
                String usersSelectQuery = String.format("SELECT %s FROM %s WHERE %s = ?",
                        KEY_SCENE_ID, TABLE_SCENES, KEY_SCENE_ID);
                Cursor cursor = db.rawQuery(usersSelectQuery, new String[]{String.valueOf(scene.getId())});
                try {
                    if (cursor.moveToFirst()) {
                        sceneId = cursor.getInt(0);
                        db.setTransactionSuccessful();
                    }
                } finally {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                }
            } else {
                // user with this userName did not already exist, so insert new user
                sceneId = db.insertOrThrow(TABLE_SCENES, null, values);
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return sceneId;
    }


    public void deleteScene(Scene scene) {
        // The database connection is cached so it's not expensive to call getWriteableDatabase() multiple times.
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("DELETE FROM " + TABLE_SCENES + " WHERE " + KEY_SCENE_ID + "=?", new String[]{String.valueOf(scene.getId())});
        System.out.println(cursor.getCount());
        cursor.close();
    }



    public ArrayList<Scene> getAllScenes() {
        ArrayList<Scene> posts = new ArrayList<>();

        String POSTS_SELECT_QUERY = "SELECT * FROM " + TABLE_SCENES;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(POSTS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Scene scene = new Scene(new JSONObject(cursor.getString(cursor.getColumnIndex(KEY_SCENE_JSON))));
                    posts.add(scene);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return posts;
    }


}
