package org.project.harsh.fblind;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import junit.framework.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.microedition.khronos.opengles.GL;

import static org.project.harsh.fblind.GlobalVars.musicPlayerPlayFile;

public class yo extends Activity {
    private Context mContext;
    private Activity mActivity;

    int init = 0;
    private LinearLayout mRootLayout;
    private Button mButtonSearch;
    private Button mButtonStop;
    private ListView mListView;
    private TextView mStats;
    // TEEKAR
    TextView input;
    TextView results;
    TextView enter;
    TextView goback ;
    List<VideoItem> hash;
    ArrayList<Pair<String,String > > ans;
    String messageBody="";
//HARSH METHOD

    ArrayList<String> a = new ArrayList<String>();
    private MediaPlayer mPlayer;

    private static final int MY_PERMISSION_REQUEST_CODE = 123;

    private HashMap<Long,String> mAudioMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yo);

        // int init=0;
        // Get the application context
        mContext = getApplicationContext();
        mActivity = yo.this;

        // Get the widget reference from xml layout
        // mRootLayout = findViewById(R.id.root_layout);
        // mButtonSearch = findViewById(R.id.btn_search);
        // mButtonStop = findViewById(R.id.btn_stop);
        // mListView = findViewById(R.id.list_view);
        // mStats = findViewById(R.id.stats);


        input=(TextView)findViewById(R.id.input);
        results=(TextView)findViewById(R.id.results);
        enter=(TextView)findViewById(R.id.enter);
        goback = (TextView)findViewById(R.id.goback) ;
        // Custom method to check permission at run time
        checkPermission();



        // Set a click listener for button

    }

    @Override public void onResume()
    {
        super.onResume();
        // GlobalVars.lastActivity = language.class;
        if (GlobalVars.inputModeResult!=null)
        {
            messageBody = GlobalVars.inputModeResult;
            //GlobalVars.setText(body, false, messageBody);
            GlobalVars.inputModeResult = null;
        }
        GlobalVars.activityItemLocation=0;
        GlobalVars.activityItemLimit=4;
        GlobalVars.selectTextView(input,false);
        GlobalVars.selectTextView(results,false);
        GlobalVars.selectTextView(enter,false);
        GlobalVars.selectTextView(goback,false);

        GlobalVars.talk(getResources().getString(R.string.welcometomusic));
    }



    public void select()
    {
        switch (GlobalVars.activityItemLocation)
        {
            case 1: // PLAYING AUDIO
                GlobalVars.selectTextView(input,true);
                GlobalVars.selectTextView(results,false);
                GlobalVars.selectTextView(enter,false);
                GlobalVars.selectTextView(goback,false);
                GlobalVars.talk(getResources().getString(R.string.musicselect));
                break;

            case 2: // PLAY
                GlobalVars.selectTextView(input,false);
                GlobalVars.selectTextView(results,true);
                GlobalVars.selectTextView(enter,false);
                GlobalVars.selectTextView(goback,false);
                GlobalVars.talk(getResources().getString(R.string.musicplay));
                break;

            case 3:
                GlobalVars.selectTextView(input,false);
                GlobalVars.selectTextView(results,false);
                GlobalVars.selectTextView(enter,true);
                GlobalVars.selectTextView(goback,false);
                GlobalVars.talk(getResources().getString(R.string.musicstop));
                break;

            case 4: //goback language
                GlobalVars.selectTextView(input,false);
                GlobalVars.selectTextView(results,false);
                GlobalVars.selectTextView(enter,false);
                GlobalVars.selectTextView(goback,true);
                break;
        }
    }

    public void execute()

    {
        // Ids array for music map


        //HashMap<Long,String> mAudioMap ;
        switch (GlobalVars.activityItemLocation)
        {
            case 1: //input
                Log.e("backc","safsfs");
                GlobalVars.voice();
                Log.e("dsad",messageBody);
                //Log.e("dsad",GlobalVars.inputModeResult);
                 /*YoutubeConnector yc= new YoutubeConnector(this);
                Log.e("dsad",GlobalVars.inputModeResult);
                //jis variable me input voice ka result aa rha hai voh chahiye buss
                hash=yc.search(GlobalVars.inputModeResult);
                ans=new ArrayList<Pair<String, String>>();
                for(int i=0;i<hash.size();i++){
                    VideoItem temp=hash.get(i);
                    ans.add(new Pair(temp.getTitle(),temp.getId()));
                }*/
                break;

            case 2: //results
                /*if(init+1>ans.size()-1){
                    init=0;
                }
                else
                    init=init+1;*/
                //Pair<String,String> t=ans.get(init);
                //results.setText(t.first+t.second+ GlobalVars.inputModeResult);
                break;

            case 3: //enter
                String v_id=ans.get(init).second;
                Log.e("v_id",v_id);
                break;


            case 4: //GO BACK
                  this.finish();
                break;
        }
    }

    private void previousItem()
    {
        switch (GlobalVars.activityItemLocation)
        {
            case 2: //results
                if (init-1<0)
                {
                    init = ans.size()-1;
                }
                else
                {
                    init = init -1;
                }
                Pair<String,String> t=ans.get(init);
                results.setText(t.first);
                break;
        }
    }

    // Custom method to get all audio files list from external storage
    protected HashMap getMediaFileList(){
        // Get the content resolver
        ContentResolver contentResolver = mContext.getContentResolver();

        // Get the external storage uri of media store audio
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = contentResolver.query(
                uri, // Uri
                null, // Projection
                null, // Selection
                null, // Selection args
                null // Sor order
        );

        if (cursor == null) {
            // Query failed, handle error
        } else if (!cursor.moveToFirst()) {
            // No media on the device
        } else {

            int title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int id = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            // Loop through the musics
            do {
                // Get the current audio file id
                long thisId = cursor.getLong(id);

                // Get the current audio title
                String thisTitle = cursor.getString(title);
                // Process current music here
                a.add(thisTitle);
                mAudioMap.put(thisId,thisTitle);
            } while (cursor.moveToNext());
        }

        return mAudioMap;
    }


    @Override public boolean onTouchEvent(MotionEvent event)
    {
        int result = GlobalVars.detectMovement(event);
        switch (result)
        {
            case GlobalVars.ACTION_SELECT:
                select();
                break;

            case GlobalVars.ACTION_SELECT_PREVIOUS:
                previousItem();
                break;

            case GlobalVars.ACTION_EXECUTE:
                execute();
                break;
        }
        return super.onTouchEvent(event);
    }





    protected void checkPermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                    // Show an alert dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setMessage("Read external storage permission is required.");
                    builder.setTitle("Please grant permission");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(
                                    mActivity,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSION_REQUEST_CODE
                            );
                        }
                    });
                    builder.setNeutralButton("Cancel",null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else {
                    // Request permission
                    ActivityCompat.requestPermissions(
                            mActivity,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSION_REQUEST_CODE
                    );
                }
            }else {
                // Permission already granted
            }
        }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        int result = GlobalVars.detectKeyUp(keyCode);
        switch (result)
        {
            case GlobalVars.ACTION_SELECT:
                select();
                break;

            case GlobalVars.ACTION_SELECT_PREVIOUS:
                previousItem();
                break;

            case GlobalVars.ACTION_EXECUTE:
                execute();
                break;
        }
        return super.onKeyUp(keyCode, event);
    }


    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        return GlobalVars.detectKeyDown(keyCode);
    }


    // Custom method to stop music playing
    protected void stopPlaying(){
        if(mPlayer!=null){
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            Toast.makeText(mContext,"Stop playing",Toast.LENGTH_SHORT).show();
            //mStats.setBackgroundColor(Color.RED);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch(requestCode){
            case MY_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // Permission granted
                }else {
                    // Permission denied
                }
            }
        }
    }

    @Override
    public void onDestroy(){
        if(mPlayer!=null){
            mPlayer.release();
        }
        super.onDestroy();
    }
}
