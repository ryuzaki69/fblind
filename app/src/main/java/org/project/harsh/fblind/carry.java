package org.project.harsh.fblind;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

public class carry extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;
    private MyPlayerStateChangeListener playerStateChangeListener;
    private MyPlaybackEventListener playbackEventListener;
    private YouTubePlayer player;

    public TextView pl , pa , g ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browserpageviewer);

        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize(Config.YOUTUBE_API_KEY, this);

        //   Button b = (Button) findViewById(R.id.button);

        playerStateChangeListener = new MyPlayerStateChangeListener();
        playbackEventListener = new MyPlaybackEventListener();

        //final EditText seekToText = (EditText) findViewById(R.id.seek_to_text);
        //Button seekToButton = (Button) findViewById(R.id.seek_to_button);

      /*  seekToButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int skipToSecs = Integer.valueOf(seekToText.getText().toString());
                player.seekToMillis(skipToSecs * 1000);
            }
        });
        */
        //Button b = (Button) findViewById(R.id.button);

        //    b.setOnClickListener(new View.OnClickListener() {
        //       @Override
        //     public void onClick(View view) {
//
        //               player.pause();
        //          }
        //      });



        pl = (TextView)findViewById(R.id.play);
        pa =(TextView)findViewById(R.id.pause);
        g =(TextView)findViewById(R.id.goback);

    }

    public void onResume()
    {
        super.onResume();
        // GlobalVars.lastActivity = language.class;
        GlobalVars.activityItemLocation=0;
        GlobalVars.activityItemLimit=3;
        GlobalVars.selectTextView(pl,false);
        GlobalVars.selectTextView(pa,false);
        GlobalVars.selectTextView(g,false);
        // GlobalVars.selectTextView(gobacklang,false);

        GlobalVars.talk(getResources().getString(R.string.welcometoyoutube));
    }

    public void select () {

        switch (GlobalVars.activityItemLocation) {
            case 1: // PLAYING VIDEO WTTH ENABLE TOUCH
                GlobalVars.selectTextView(pl, true);
                GlobalVars.selectTextView(pa, false);
                GlobalVars.selectTextView(g, false);
                //GlobalVars.selectTextView(gobacklang,false);
                GlobalVars.talk(getResources().getString(R.string.carryplay));
                break;

            case 2: // PAUSE
                GlobalVars.selectTextView(pl, false);
                GlobalVars.selectTextView(pa, true);
                GlobalVars.selectTextView(g, false);
                // GlobalVars.selectTextView(gobacklang,false);
                GlobalVars.talk(getResources().getString(R.string.carrypause));
                break;

            case 3: //GO BACK TO THE PREVIOUS MENU
                GlobalVars.selectTextView(pl, false);
                GlobalVars.selectTextView(pa, false);
                GlobalVars.selectTextView(g, true);
                Log.e("tea","ad");
                //GlobalVars.selectTextView(gobacklang,false);
                GlobalVars.talk(getResources().getString(R.string.backToPreviousMenu));
                break;
        }
    }

    public void execute() {
        switch (GlobalVars.activityItemLocation) {
            case 1: //READ PAGE TITLE
                //GlobalVars.talk(getResources().getString(R.string.layoutBrowserPageViewerReadTitle2) + GlobalVars.browserWebTitle);
                Log.e("sds","hua");
                player.play();

                break;

            case 2: //READ PAGE TEXT

                //GlobalVars.talk();
                player.pause();
                break;

            case 3: //PAGE LINKS

                this.finish();
                break;

            //break;
        }}

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
        this.player = player;
        player.setPlayerStateChangeListener(playerStateChangeListener);
        player.setPlaybackEventListener(playbackEventListener);


        if (!wasRestored) {

            player.cueVideo(GlobalVars.h); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
        }
    }

    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            //String error = String.format(getString(R.string.player_error), errorReason.toString());
            // Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(Config.YOUTUBE_API_KEY, this);
        }
    }

    protected Provider getYouTubePlayerProvider() {
        return youTubeView;
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private final class MyPlaybackEventListener implements YouTubePlayer.PlaybackEventListener {

        @Override
        public void onPlaying() {
            // Called when playback starts, either due to user action or call to play().
            showMessage("Playing");
        }

        @Override
        public void onPaused() {
            // Called when playback is paused, either due to user action or call to pause().
            showMessage("Paused");
        }

        @Override
        public void onStopped() {
            // Called when playback stops for a reason other than being paused.
            showMessage("Stopped");
        }

        @Override
        public void onBuffering(boolean b) {
            // Called when buffering starts or ends.
        }

        @Override
        public void onSeekTo(int i) {
            // Called when a jump in playback position occurs, either
            // due to user scrubbing or call to seekRelativeMillis() or seekToMillis()
        }
    }

    private final class MyPlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener {

        @Override
        public void onLoading() {
            // Called when the player is loading a video
            // At this point, it's not ready to accept commands affecting playback such as play() or pause()
        }

        @Override
        public void onLoaded(String s) {
            // Called when a video is done loading.
            // Playback methods such as play(), pause() or seekToMillis(int) may be called after this callback.
        }

        @Override
        public void onAdStarted() {
            // Called when playback of an advertisement starts.
        }

        @Override
        public void onVideoStarted() {
            // Called when playback of the video starts.
        }

        @Override
        public void onVideoEnded() {
            // Called when the video reaches its end.
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {
            // Called when an error occurs.
        }
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
                //  previousItem();
                break;

            case GlobalVars.ACTION_EXECUTE:
                execute();
                break;
        }
        return super.onTouchEvent(event);
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
                // previousItem();
                break;

            case GlobalVars.ACTION_EXECUTE:
                // execute();
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        return GlobalVars.detectKeyDown(keyCode);
    }
}

//package org.project.harsh.fblind;
//
///**
// * Created by carry' on 4/5/2018.
// */
//
//import android.app.*;
//import android.os.*;
//import android.view.*;
//import android.widget.*;
//
//import com.google.android.youtube.player.YouTubePlayer;
//import com.google.android.youtube.player.YouTubePlayerView;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import com.google.android.youtube.player.YouTubeBaseActivity;
//import com.google.android.youtube.player.YouTubeInitializationResult;
//import com.google.android.youtube.player.YouTubePlayer;
//import com.google.android.youtube.player.YouTubePlayer.Provider;
//import com.google.android.youtube.player.YouTubePlayerView;
//
//
//public class carry extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener
//{
//    private TextView pagetitle;
//
//    private TextView pagetext;
//    private TextView pagelinks;
//    private TextView linksgoto;
//    private TextView addtobookmarks;
//    private TextView goback;
//    private TextView pause ;
//    public static int linkLocation = -1;
//
//    private static final int RECOVERY_REQUEST = 1;
//    private YouTubePlayerView youTubeView;
//    private MyPlayerStateChangeListener playerStateChangeListener;
//    private MyPlaybackEventListener playbackEventListener;
//    private YouTubePlayer player;
//
//    @Override protected void onCreate(Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.musicplayer);
//        GlobalVars.lastActivity = carry.class;
//        pause = (TextView) findViewById(R.id.pause);
//        youTubeView = (YouTubePlayerView) findViewById(R.id.you);
//        youTubeView.initialize(Config.YOUTUBE_API_KEY, this);
//
//        playerStateChangeListener = new MyPlayerStateChangeListener();
//        playbackEventListener = new MyPlaybackEventListener();
//
//        //final EditText seekToText = (EditText) findViewById(R.id.seek_to_text);
//        //Button seekToButton = (Button) findViewById(R.id.seek_to_button);
//			/*
//			seekToButton.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					int skipToSecs = Integer.valueOf(seekToText.getText().toString());
//					player.seekToMillis(skipToSecs * 1000);
//				}
//			});
//
//			*/
//
//        goback = (TextView) findViewById(R.id.goback);
//        GlobalVars.activityItemLocation=0;
//        GlobalVars.activityItemLimit=2;
//    }
//
//    @Override public void onResume()
//    {
//        super.onResume();
//        try{GlobalVars.alarmVibrator.cancel();}catch(NullPointerException e){}catch(Exception e){}
//        GlobalVars.lastActivity = carry.class;
//        GlobalVars.activityItemLocation=0;
//        GlobalVars.activityItemLimit=2;
//        GlobalVars.selectTextView(pause,false);
//        GlobalVars.selectTextView(goback,false);
//        //GlobalVars.selectTextView(pagelinks,false);
//        //GlobalVars.selectTextView(linksgoto,false);
//        //GlobalVars.selectTextView(addtobookmarks,false);
//        //GlobalVars.selectTextView(goback,false);
//        GlobalVars.talk(getResources().getString(R.string.layoutBrowserPageViewerOnResume));
//    }
//
//    public void select()
//    {
//        switch (GlobalVars.activityItemLocation) {
//            case 1: //READ PAGE TITLE
//                GlobalVars.selectTextView(pause, true);
//                GlobalVars.selectTextView(goback, false);
//                //GlobalVars.selectTextView(goback,false);
//                GlobalVars.talk(getResources().getString(R.string.layoutBrowserPageViewerReadTitle));
//                break;
//
//            case 2: //READ PAGE TEXT
//                GlobalVars.selectTextView(pause, true);
//                GlobalVars.selectTextView(goback, false);
//                //GlobalVars.selectTextView(pagelinks,false);
//                GlobalVars.talk(getResources().getString(R.string.layoutBrowserPageViewerReadText));
//                break;
//
//        }
//    }
//
//    public void execute() {
//        switch (GlobalVars.activityItemLocation) {
//            case 1: //READ PAGE TITLE
//                GlobalVars.talk(getResources().getString(R.string.layoutBrowserPageViewerReadTitle2) + GlobalVars.browserWebTitle);
//
//                player.pause();
//
//                break;
//
//            case 2: //READ PAGE TEXT
//
//                //GlobalVars.talk();
//                this.finish();
//                break;
//			/*
//			case 3: //PAGE LINKS
//			if (GlobalVars.browserWebLinks.size()==0)
//				{
//				GlobalVars.talk(getResources().getString(R.string.layoutBrowserPageViewerPageLinks4));
//				}
//				else
//				{
//				if (linkLocation+1==GlobalVars.browserWebLinks.size())
//					{
//					linkLocation=-1;
//					}
//				linkLocation = linkLocation + 1;
//				GlobalVars.talk(getResources().getString(R.string.layoutBrowserPageViewerPageLinks3) + String.valueOf(linkLocation + 1) + getResources().getString(R.string.layoutBrowserPageViewerPageLinks3Of) + String.valueOf(GlobalVars.browserWebLinks.size()) + ". " + GlobalVars.browserWebLinks.get(linkLocation).substring(0,GlobalVars.browserWebLinks.get(linkLocation).indexOf("|")));
//				}
//			break;
//
//			case 4: //GO TO LINK
//			if (linkLocation==-1)
//				{
//				GlobalVars.talk(getResources().getString(R.string.layoutBrowserPageViewerPageLinkSelectError));
//				}
//				else
//				{
//				new BrowserThreadGoTo().execute(GlobalVars.browserWebLinks.get(linkLocation).substring(GlobalVars.browserWebLinks.get(linkLocation).indexOf("|") + 1, GlobalVars.browserWebLinks.get(linkLocation).length()));
//				}
//			break;
//
//			case 5: //ADD TO BOOKMARKS
//			String newBookmark = GlobalVars.browserWebTitle + "|" + GlobalVars.browserWebURL;
//			boolean repeatedBookmark = false;
//			for (int i=0;i<GlobalVars.browserBookmarks.size();i++)
//				{
//				if (GlobalVars.browserBookmarks.get(i).toLowerCase().equals(newBookmark.toLowerCase()))
//					{
//					repeatedBookmark = true;
//					i = GlobalVars.browserBookmarks.size();
//					}
//				}
//			if (repeatedBookmark==true)
//				{
//				GlobalVars.talk(getResources().getString(R.string.layoutBrowserPageViewerAddToBookmarksRepeated));
//				}
//				else
//				{
//				GlobalVars.browserBookmarks.add(newBookmark);
//				GlobalVars.sortBookmarksDatabase();
//				GlobalVars.saveBookmarksDatabase();
//				GlobalVars.talk(getResources().getString(R.string.layoutBrowserPageViewerAddToBookmarksAdded));
//				}
//			break;
//
//			case 6: //GO BACK TO THE PREVIOUS MENU
//			this.finish();
//			break;
//			}
//			*/
//        }
//    }
//
//
//    @Override
//    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
//        this.player = player;
//        player.setPlayerStateChangeListener(playerStateChangeListener);
//        player.setPlaybackEventListener(playbackEventListener);
//
//
//        if (!wasRestored) {
//            player.cueVideo("cLTXQxiQPgE"); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
//        }
//    }
//
//    @Override
//    public void onInitializationFailure(Provider provider, YouTubeInitializationResult errorReason) {
//        if (errorReason.isUserRecoverableError()) {
//            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
//        } else {
//            //String error = String.format(getString(R.string.player_error), errorReason.toString());
//            //Toast.makeText(this, error, Toast.LENGTH_LONG).show();
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == RECOVERY_REQUEST) {
//            // Retry initialization if user performed a recovery action
//            getYouTubePlayerProvider().initialize(Config.YOUTUBE_API_KEY, this);
//        }
//    }
//
//    protected Provider getYouTubePlayerProvider() {
//        return youTubeView;
//    }
//
//    private void showMessage(String message) {
//        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
//    }
//
//    private final class MyPlaybackEventListener implements YouTubePlayer.PlaybackEventListener {
//
//        @Override
//        public void onPlaying() {
//            // Called when playback starts, either due to user action or call to play().
//            showMessage("Playing");
//        }
//
//        @Override
//        public void onPaused() {
//            // Called when playback is paused, either due to user action or call to pause().
//            showMessage("Paused");
//        }
//
//        @Override
//        public void onStopped() {
//            // Called when playback stops for a reason other than being paused.
//            showMessage("Stopped");
//        }
//
//        @Override
//        public void onBuffering(boolean b) {
//            // Called when buffering starts or ends.
//        }
//
//        @Override
//        public void onSeekTo(int i) {
//            // Called when a jump in playback position occurs, either
//            // due to user scrubbing or call to seekRelativeMillis() or seekToMillis()
//        }
//    }
//
//
//    private final class MyPlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener {
//
//        @Override
//        public void onLoading() {
//            // Called when the player is loading a video
//            // At this point, it's not ready to accept commands affecting playback such as play() or pause()
//        }
//
//        @Override
//        public void onLoaded(String s) {
//            // Called when a video is done loading.
//            // Playback methods such as play(), pause() or seekToMillis(int) may be called after this callback.
//        }
//
//        @Override
//        public void onAdStarted() {
//            // Called when playback of an advertisement starts.
//        }
//
//        @Override
//        public void onVideoStarted() {
//            // Called when playback of the video starts.
//        }
//
//        @Override
//        public void onVideoEnded() {
//            // Called when the video reaches its end.
//        }
//
//        @Override
//        public void onError(YouTubePlayer.ErrorReason errorReason) {
//            // Called when an error occurs.
//        }
//    }
//
//
//    private void previousItem()
//    {
//        switch (GlobalVars.activityItemLocation)
//        {
//            case 3: //PAGE LINKS
//                if (GlobalVars.browserWebLinks.size()==0)
//                {
//                    GlobalVars.talk(getResources().getString(R.string.layoutBrowserPageViewerPageLinks4));
//                }
//                else
//                {
//                    if (linkLocation-1<0)
//                    {
//                        linkLocation = GlobalVars.browserWebLinks.size();
//                    }
//                    linkLocation = linkLocation - 1;
//                    GlobalVars.talk(getResources().getString(R.string.layoutBrowserPageViewerPageLinks3) + String.valueOf(linkLocation + 1) + getResources().getString(R.string.layoutBrowserPageViewerPageLinks3Of) + String.valueOf(GlobalVars.browserWebLinks.size()) + ". " + GlobalVars.browserWebLinks.get(linkLocation).substring(0,GlobalVars.browserWebLinks.get(linkLocation).indexOf("|")));
//                }
//                break;
//        }
//    }
//
//    @Override public boolean onTouchEvent(MotionEvent event)
//    {
//        int result = GlobalVars.detectMovement(event);
//        switch (result)
//        {
//            case GlobalVars.ACTION_SELECT:
//                select();
//                break;
//
//            case GlobalVars.ACTION_SELECT_PREVIOUS:
//                previousItem();
//                break;
//
//            case GlobalVars.ACTION_EXECUTE:
//                execute();
//                break;
//        }
//        return super.onTouchEvent(event);
//    }
//
//    public boolean onKeyUp(int keyCode, KeyEvent event)
//    {
//        int result = GlobalVars.detectKeyUp(keyCode);
//        switch (result)
//        {
//            case GlobalVars.ACTION_SELECT:
//                select();
//                break;
//
//            case GlobalVars.ACTION_SELECT_PREVIOUS:
//                previousItem();
//                break;
//
//            case GlobalVars.ACTION_EXECUTE:
//                execute();
//                break;
//        }
//        return super.onKeyUp(keyCode, event);
//    }
//
//    public boolean onKeyDown(int keyCode, KeyEvent event)
//    {
//        return GlobalVars.detectKeyDown(keyCode);
//    }
//}
