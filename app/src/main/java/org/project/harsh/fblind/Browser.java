package org.project.harsh.fblind;

import android.app.*;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.*;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Browser extends Activity
	{
	private TextView input;
	private TextView select;
	private TextView result;
	private TextView gback;
	HashMap<String,String> hash;
	private List<VideoItem> searchResults;
	int init=0;
		@Override protected void onCreate(Bundle savedInstanceState)
    	{
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.browser);
		GlobalVars.lastActivity = Browser.class;
		input=(TextView)findViewById(R.id.input);
		select = (TextView) findViewById(R.id.select);
		result = (TextView) findViewById(R.id.result);
		gback = (TextView) findViewById(R.id.goback);
		GlobalVars.activityItemLocation=0;
		GlobalVars.activityItemLimit=4;
    	}
    
	@Override public void onResume()
		{
		super.onResume();
		try{GlobalVars.alarmVibrator.cancel();}catch(NullPointerException e){}catch(Exception e){}
		GlobalVars.lastActivity = Browser.class;
		GlobalVars.activityItemLocation=0;
		GlobalVars.activityItemLimit=3;
		GlobalVars.selectTextView(input,false);
		GlobalVars.selectTextView(result,false);
		GlobalVars.selectTextView(gback,false);
		GlobalVars.selectTextView(select,false);
		if (GlobalVars.inputModeResult!=null)
			{
			if (GlobalVars.browserRequestInProgress==false)
				{
				GlobalVars.browserRequestInProgress=true;
				new BrowserThreadGoTo().execute("https://www.youtube.com/results?search_query=" + GlobalVars.inputModeResult);
				}
				else
				{
				GlobalVars.talk(getResources().getString(R.string.layoutBrowserErrorPendingRequest));
				}
			GlobalVars.inputModeResult = null;
			}
			else
			{
			GlobalVars.talk(getResources().getString(R.string.layoutBrowserOnResume));
			}
		}
		
	public void select()
		{
		switch (GlobalVars.activityItemLocation)
			{
			case 1: //SEARCH IN GOOGLE
			GlobalVars.selectTextView(input,true);
			GlobalVars.selectTextView(result,false);
			GlobalVars.selectTextView(gback,false);
			if (GlobalVars.browserRequestInProgress==true)
				{
				GlobalVars.talk("Searching in Youtube" +
								getResources().getString(R.string.layoutBrowserAWebPageItsBeenLoading));
				}
				else
				{
				GlobalVars.talk("Search in Google");
				}
			break;

				case 2: //LIST BOOKMARKS
					GlobalVars.selectTextView(result, true);
					GlobalVars.selectTextView(input,false);
					GlobalVars.selectTextView(select,false);
					GlobalVars.talk(getResources().getString(R.string.layoutBrowserListBookmarks));
					break;

				case 3: //LIST BOOKMARKS
					GlobalVars.selectTextView(select, true);
					GlobalVars.selectTextView(result,false);
					GlobalVars.selectTextView(gback,false);
					GlobalVars.talk(getResources().getString(R.string.layoutBrowserListBookmarks));
					break;

				case 4: //GO BACK TO THE MAIN MENU
			GlobalVars.selectTextView(gback,true);
			GlobalVars.selectTextView(input,false);
			GlobalVars.selectTextView(select,false);
			GlobalVars.talk(getResources().getString(R.string.backToMainMenu));
			break;
			}
		}

		private void promptSpeechInput() {
			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
					RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
			intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
					"Hello");
			try {
				startActivityForResult(intent, 100);
			} catch (ActivityNotFoundException a) {
				Toast.makeText(getApplicationContext(),
						"Error",
						Toast.LENGTH_SHORT).show();
			}
		}

		/**
		 * Receiving speech input
		 * */
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);

			switch (requestCode) {
				case 100: {
					if (resultCode == RESULT_OK && null != data) {

						ArrayList<String> result = data
								.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

						String f=result.get(0);

						Log.e("String",f);
					}
					break;
				}

			}
		}
	public void calling(String keyword){
		YoutubeConnector yc = new YoutubeConnector(Browser.this);
		searchResults = yc.search(keyword);
		hash=new HashMap<String,String>();
		for(int i=0;i<searchResults.size();i++){
			VideoItem temp=searchResults.get(i);
			hash.put(temp.getTitle(),temp.getId());
			//Log.e("hash",temp.getTitle()+temp.getId());
		}
	}
	public void execute()
		{
		switch (GlobalVars.activityItemLocation)
			{
			case 1: //SEARCH IN GOOGLE
			if (GlobalVars.browserRequestInProgress==false)
				{
					GlobalVars.voice();
					String key=GlobalVars.inputModeResult;
					calling(key);
				}
				else
				{
				GlobalVars.talk(getResources().getString(R.string.layoutBrowserErrorPendingRequest));
				}
			break;

			case 2: //result
				if(init+1>hash.size()-1){
					init=0;
				}
				else
					init=init+1;


			break;

			case 4:
			this.finish();
			break;
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
	}