package org.androidtown.media.audio.player;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


public class MusicListActivity extends ActionBarActivity {

    TextView selectMusicInfo = null;

    String filePath = null;
    String fileTitle = null;
    String fileArtist = null;

    private ListView mListView;
    private MusicAdapter mMusicAdapter;
    Button selectBtn;
    private MediaPlayer mMediaPlayer = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);

         /* Layout으로 부터 ListView에 대한 객체를 얻는다. */
        mListView = (ListView) findViewById(R.id.mylist);

        mMusicAdapter = new MusicAdapter(this);

        selectMusicInfo = (TextView) findViewById(R.id.textView);

        mListView.setAdapter(mMusicAdapter);

        System.out.println("1");
        /* Listener for selecting a item */
        mListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parentView, View childView, int position, long id) {

                // 음악파일 제목, 가수 정보 보낸다,
                String musicInfoSeed = null;
                fileTitle = mMusicAdapter.getTitle(position);
                fileArtist = mMusicAdapter.getSinger(position);
                musicInfoSeed = fileTitle + " "  + fileArtist;
                System.out.println(musicInfoSeed);

                Toast.makeText(getApplicationContext(), "\""+fileArtist+"\"의 "
                        + "\""+fileTitle+"\"" + " 곡이 선택되었습니다.", Toast.LENGTH_SHORT).show();

                selectMusicInfo.setText("선택된곡 : "+ fileTitle + "-" + fileArtist);

                //음악파일을 보낸다.
                Uri musicURI = Uri.withAppendedPath(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" + mMusicAdapter.getMusicID(position));

                //playMusic(musicURI);

                filePath = getPathFromUri(musicURI);

                System.out.println(filePath);
            }
        });

        selectBtn = (Button) findViewById(R.id.selectBtn);
        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = getIntent();
                intent.putExtra("filepath", filePath);
                intent.putExtra("title" , fileTitle );
                intent.putExtra("artist", fileArtist);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        /* Release resources allocated to player */
        mMediaPlayer.reset();
        mMediaPlayer.release();
        mMediaPlayer = null;
    }

    // get file path from uri
    public String getPathFromUri(Uri uri){
        Cursor cursor = getContentResolver().query(uri, null, null, null, null );
        cursor.moveToNext();
        String path = cursor.getString( cursor.getColumnIndex( "_data" ) );
        //c.close();


        return path;
    }

    /**
     * ==========================================
     * Adapter class
     * ==========================================
     */
    public class MusicAdapter extends BaseAdapter {
        private ArrayList<String> mMusicIDList;
        private ArrayList<String> mAlbumartIDList;
        private ArrayList<String> mMusiceTitleList;
        private ArrayList<String> mSingerList;
        private Context mContext;

        MusicAdapter(Context c) {
            mContext = c;
            mMusicIDList = new ArrayList<String>();
            mAlbumartIDList = new ArrayList<String>();
            mMusiceTitleList = new ArrayList<String>();
            mSingerList = new ArrayList<String>();
            getMusicInfo();
        }

        public boolean deleteSelected(int sIndex) {
            return true;
        }

        public int getCount() {
            return mMusicIDList.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public int getMusicID(int position) {
            return Integer.parseInt((mMusicIDList.get(position)));
        }

        public String getTitle(int position){
            return mMusiceTitleList.get(position);
        }

        public String getSinger(int position){
            return mSingerList.get(position);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View listViewItem = convertView;
            if (listViewItem == null) {
                /* Item.xml을 Inflate해 Layout 구성된 View를 얻는다. */
                LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                listViewItem = vi.inflate(R.layout.item, null);
            }
            /* Album Art Bitmap을 얻는다. */
            ImageView iv = (ImageView) listViewItem.findViewById(R.id.albumart);
            Bitmap albumArt = MusicListActivity.getArtworkQuick(mContext, Integer.parseInt((mAlbumartIDList.get(position))), 50, 50);
            iv.setImageBitmap(albumArt);

            /* Title 설정 */
            TextView tv = (TextView) listViewItem.findViewById(R.id.title);
            tv.setText(mMusiceTitleList.get(position));

            /* Singer 설정 */
            TextView tv1 = (TextView) listViewItem.findViewById(R.id.singer);
            tv1.setText(mSingerList.get(position));

            /* 구성된 ListView Item을 리턴해 준다. */
            return listViewItem;
        }

        private void getMusicInfo() {
            /* 이 예제에서는 단순히 ID (Media의 ID, Album의 ID)만 얻는다.*/
            String[] proj = {MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.ALBUM_ID,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST
            };

            Cursor musicCursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    proj, null, null, null);

            if (musicCursor != null && musicCursor.moveToFirst()) {
                String musicID;
                String albumID;
                String musicTitle;
                String singer;

                int musicIDCol = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int albumIDCol = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                int musicTitleCol = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int singerCol = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                do {
                    musicID = musicCursor.getString(musicIDCol);
                    albumID = musicCursor.getString(albumIDCol);
                    musicTitle = musicCursor.getString(musicTitleCol);
                    singer = musicCursor.getString(singerCol);
                    /* Media ID와 Album ID를 각각의 리스트에 저장해 둔다
                     *
                     */
                    mMusicIDList.add(musicID);
                    mAlbumartIDList.add(albumID);
                    mMusiceTitleList.add(musicTitle);
                    mSingerList.add(singer);
                } while (musicCursor.moveToNext());
            }
            musicCursor.close();
            return;
        }
    }


    /* Album ID로 부터 Bitmap을 생성해 리턴해 주는 메소드 */
    private static final BitmapFactory.Options sBitmapOptionsCache = new BitmapFactory.Options();
    private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");

    // Get album art for specified album. This method will not try to
    // fall back to getting artwork directly from the file, nor will
    // it attempt to repair the database.
    private static Bitmap getArtworkQuick(Context context, int album_id, int w, int h) {
        // NOTE: There is in fact a 1 pixel frame in the ImageView used to
        // display this drawable. Take it into account now, so we don't have to
        // scale later.
        w -= 2;
        h -= 2;
        ContentResolver res = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
        if (uri != null) {
            ParcelFileDescriptor fd = null;
            try {
                fd = res.openFileDescriptor(uri, "r");
                int sampleSize = 1;

                // Compute the closest power-of-two scale factor
                // and pass that to sBitmapOptionsCache.inSampleSize, which will
                // result in faster decoding and better quality
                sBitmapOptionsCache.inJustDecodeBounds = true;
                BitmapFactory.decodeFileDescriptor(
                        fd.getFileDescriptor(), null, sBitmapOptionsCache);
                int nextWidth = sBitmapOptionsCache.outWidth >> 1;
                int nextHeight = sBitmapOptionsCache.outHeight >> 1;
                while (nextWidth > w && nextHeight > h) {
                    sampleSize <<= 1;
                    nextWidth >>= 1;
                    nextHeight >>= 1;
                }

                sBitmapOptionsCache.inSampleSize = sampleSize;
                sBitmapOptionsCache.inJustDecodeBounds = false;
                Bitmap b = BitmapFactory.decodeFileDescriptor(
                        fd.getFileDescriptor(), null, sBitmapOptionsCache);

                if (b != null) {
                    // finally rescale to exactly the size we need
                    if (sBitmapOptionsCache.outWidth != w || sBitmapOptionsCache.outHeight != h) {
                        Bitmap tmp = Bitmap.createScaledBitmap(b, w, h, true);
                        b.recycle();
                        b = tmp;
                    }
                }

                return b;
            } catch (FileNotFoundException e) {
            } finally {
                try {
                    if (fd != null)
                        fd.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

}
