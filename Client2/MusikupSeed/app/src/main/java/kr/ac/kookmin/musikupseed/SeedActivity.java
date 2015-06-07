package kr.ac.kookmin.musikupseed;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;


public class SeedActivity extends ListActivity {

    private static final String MEDIA_PATH = new String("/sdcard/Music"); // 파일 경로 지정
    private List<String> songs = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seed);

        updateSongList();
    }

    class Mp3Filter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return (name.endsWith(".mp3")); // 확장자가 mp3인지 확인
        }
    }

    public void updateSongList() {
        File musicfiles = new File(MEDIA_PATH);
        ArrayAdapter<String> musicList = new ArrayAdapter<String>(this, R.layout.activity_seed,songs);

        if (musicfiles.listFiles( new Mp3Filter()).length > 0) {
            for (File file : musicfiles.listFiles( new Mp3Filter())) {
                songs.add(file.getName()); // mp3파일을 ArrayList에 추가
            }

            setListAdapter(musicList); // ArrayAdapter를 ListView에 바인딩
        }

    }
}
