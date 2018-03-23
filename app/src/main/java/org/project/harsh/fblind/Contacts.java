package org.project.harsh.fblind;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.*;
import android.widget.TextView;
import android.app.Activity;

public class Contacts extends Activity implements TextToSpeech.OnInitListener {
	private TextView list;
	private TextView create;
	private TextView goback;
	
    @Override protected void onCreate(Bundle savedInstanceState)
    	{
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.contacts);
		GlobalVars.lastActivity = Contacts.class;
		list = (TextView) findViewById(R.id.contactslist);
		create = (TextView) findViewById(R.id.contactscreate);
		goback = (TextView) findViewById(R.id.goback);
		GlobalVars.activityItemLocation=0;
		GlobalVars.activityItemLimit=1;
			GlobalVars.context = this;
			GlobalVars.startTTS(GlobalVars.tts);
			GlobalVars.tts = new TextToSpeech(this,this);
			GlobalVars.tts.setPitch((float) 1.0);
    	}


		public void onInit(int status)
		{
			if (status == TextToSpeech.SUCCESS)
			{
				GlobalVars.talk(getResources().getString(R.string.mainWelcome));
				GlobalVars.tts.setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener()
				{
					@Override public void onUtteranceCompleted(String utteranceId)
					{
						try
						{
							GlobalVars.musicPlayer.setVolume(1f,1f);
						}
						catch(NullPointerException e)
						{
						}
						catch(Exception e)
						{
						}
					}
				});
			}
			else
			{
				new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.mainNoTTSInstalledTitle)).setMessage(getResources().getString(R.string.mainNoTTSInstalledMessage)).setPositiveButton(getResources().getString(R.string.mainNoTTSInstalledButton),new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog,int which)
					{
						try
						{
							startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=id=com.google.android.tts")));
						}
						catch (ActivityNotFoundException e)
						{
							try
							{
								startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.tts")));
							}
							catch (ActivityNotFoundException e2)
							{
							}
						}
					}
				}).show();
			}
		}
	@Override public void onResume()
		{
		super.onResume();
		try{GlobalVars.alarmVibrator.cancel();}catch(NullPointerException e){}catch(Exception e){}
		GlobalVars.lastActivity = Contacts.class;
		GlobalVars.activityItemLocation=0;
		GlobalVars.activityItemLimit=2;
		GlobalVars.selectTextView(list,false);
		//GlobalVars.selectTextView(create,false);
		GlobalVars.selectTextView(goback,false);
		if (GlobalVars.contactWasCreated==true)
			{
			GlobalVars.talk(getResources().getString(R.string.layoutContactsOnResumeCreated));
			GlobalVars.contactWasCreated=false;
			}
			else
			{
			GlobalVars.talk(getResources().getString(R.string.layoutContactsOnResume));
			}
		}
		
	public void select()
		{
		switch (GlobalVars.activityItemLocation)
			{
			case 1: //LIST CONTACTS
			GlobalVars.selectTextView(list,true);
			GlobalVars.selectTextView(create,false);
			GlobalVars.selectTextView(goback,false);
			GlobalVars.talk(getResources().getString(R.string.layoutContactsList));
			break;

			case 2: //CREATE CONTACT
			GlobalVars.selectTextView(goback, true);
			GlobalVars.selectTextView(list,false);
				GlobalVars.talk(getResources().getString(R.string.backToMainMenu));
		//	GlobalVars.talk(getResources().getString(R.string.layoutContactsCreate));
			break;

		}}
		
	public void execute()
		{
		switch (GlobalVars.activityItemLocation)
			{
			case 1: //LIST CONTACTS
			GlobalVars.startActivity(ContactsList.class);
			break;


			case 2: //GO BACK TO THE MAIN MENU
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
