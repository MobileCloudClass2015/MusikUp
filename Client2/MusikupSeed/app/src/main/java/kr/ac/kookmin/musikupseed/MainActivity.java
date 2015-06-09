package kr.ac.kookmin.musikupseed;

import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    Spinner listArtists = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<String> artists = getArtists();
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, artists);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.activity_main, artists);
        listArtists = (Spinner) findViewById(R.id.spinner);
        //setListAdapter(adapter);
        listArtists.setAdapter(adapter);
    }

    public List<String> getArtists() {
        List<String> list = new ArrayList<String>();
        String[] cursorColumns = new String[] {
                MediaStore.Audio.Artists._ID,
                MediaStore.Audio.Artists.ARTIST
        };
        Cursor cursor = (Cursor) getContentResolver().query(
                MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                cursorColumns, null, null, null);

        if (cursor == null) {
            return list;
        }
        if (cursor.moveToFirst()) {
            int idColumn = cursor.getColumnIndex(MediaStore.Audio.Artists._ID);
            int artistColumn = cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST);
            do
            {
                String artist = cursor.getString(artistColumn);
                list.add(artist);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
