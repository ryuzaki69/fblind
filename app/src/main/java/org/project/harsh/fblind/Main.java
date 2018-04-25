package org.project.harsh.fblind;

import android.Manifest;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.*;
import android.net.Uri;
import android.os.*;
import android.speech.tts.*;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.telephony.*;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.google.api.services.youtube.YouTube;

import java.util.*;

public class Main extends Activity implements TextToSpeech.OnInitListener
{
	private static boolean speakOnResume = false;
	public static TextView messages;
	public static TextView calls;
	private TextView contacts;
	private TextView music;
	private TextView internet;
	public static TextView alarms;
	private TextView voicerecorder;
	private TextView applications;
	private TextView settings;
	private TextView status;
	private  TextView language;
	private boolean okToFinish = false;
	Locale myLocale;
	public static final String MainPP_SP = "MainPP_data";
	public static final int R_PERM = 2822;
	private static final int REQUEST= 112;

	Context mContext = this;

	private static final int MY_PERMISSION_REQUEST_CODE = 123;
	@Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        GlobalVars.lastActivity = Main.class;
        speakOnResume = false;
        messages = (TextView) findViewById(R.id.messages);
        calls = (TextView) findViewById(R.id.calls);
        contacts = (TextView) findViewById(R.id.contacts);
        music = (TextView) findViewById(R.id.music);
        internet = (TextView) findViewById(R.id.browser);
        alarms = (TextView) findViewById(R.id.alarms);
        applications = (TextView) findViewById(R.id.apps);
        settings = (TextView) findViewById(R.id.settings);
        status = (TextView) findViewById(R.id.status);
        language = (TextView) findViewById(R.id.language);
        GlobalVars.activityItemLocation = 0;
        GlobalVars.activityItemLimit = 10;

        GlobalVars.contextApp = getApplicationContext();

        GlobalVars.context = this;
        GlobalVars.startTTS(GlobalVars.tts);
        GlobalVars.tts = new TextToSpeech(this, this);
        GlobalVars.tts.setPitch((float) 1.2);

        SharedPreferences settings = getSharedPreferences(MainPP_SP, 0);
        HashMap<String, String> map = (HashMap<String, String>) settings.getAll();

        if (Build.VERSION.SDK_INT >= 23) {
            Log.d("TAG", "@@@ IN IF Build.VERSION.SDK_INT >= 23");
            String[] PERMISSIONS = {
                    android.Manifest.permission.READ_PHONE_STATE,
                    android.Manifest.permission.INTERNET,
                    android.Manifest.permission.ACCESS_NETWORK_STATE,
                    android.Manifest.permission.ACCESS_WIFI_STATE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_CONTACTS,
                    android.Manifest.permission.WRITE_CALL_LOG,
                    android.Manifest.permission.READ_SMS,
                    android.Manifest.permission.READ_LOGS,
                    android.Manifest.permission.READ_CALL_LOG,
                    android.Manifest.permission.WRITE_CALL_LOG,
                    android.Manifest.permission.CALL_PHONE,
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
                    android.Manifest.permission.PROCESS_OUTGOING_CALLS,
                    android.Manifest.permission.READ_PHONE_STATE,
                    android.Manifest.permission.ACCESS_NETWORK_STATE,
                    android.Manifest.permission.CHANGE_WIFI_STATE,
                    android.Manifest.permission.ACCESS_WIFI_STATE,
                    android.Manifest.permission.WRITE_SETTINGS,

            };

            if (!hasPermissions(mContext, PERMISSIONS)) {
                Log.d("TAG","@@@ IN IF hasPermissions");
                ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, REQUEST );
            } else {
                Log.d("TAG","@@@ IN ELSE hasPermissions");
            //    callNextActivity();
            }
        } else {
            Log.d("TAG","@@@ IN ELSE  Build.VERSION.SDK_INT >= 23");
            //callNextActivity();
        }
            //SETS THE ALARM VIBRATOR VARIABLE
            GlobalVars.alarmVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

            //SETS PROFILE TO NORMAL
            AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

            GlobalVars.alarmAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            GlobalVars.openAndLoadAlarmFile();
            //GlobalVars.setText(alarms,false, getResources().getString(R.string.mainAlarms) + " (" + GlobalVars.getPendingAlarmsForTodayCount() + ")");

            //LIST EVERY MUSIC FILE WITH THE MEDIA INFORMATION TO USE IT WITH THE MUSIC PLAYER
            new MusicPlayerThreadRefreshDatabase().execute("");

            //READ WEB BOOKMARKS D
            GlobalVars.readBookmarksDatabase();

            if (GlobalVars.deviceIsAPhone() == true) {
                messages.setText(GlobalVars.context.getResources().getString(R.string.mainMessages) + " (" + String.valueOf(GlobalVars.getMessagesUnreadCount()) + ")");
            } else {
                messages.setText(GlobalVars.context.getResources().getString(R.string.mainMessages) + " (0)");
            }

            if (GlobalVars.deviceIsAPhone() == true) {
                calls.setText(GlobalVars.context.getResources().getString(R.string.mainCalls) + " (" + String.valueOf(GlobalVars.getCallsMissedCount()) + ")");
            } else {
                calls.setText(GlobalVars.context.getResources().getString(R.string.mainCalls) + " (0)");
            }

            //GETS EVERY ALARM TONE
            try {
                RingtoneManager manager = new RingtoneManager(this);
                manager.setType(RingtoneManager.TYPE_ALARM);
                Cursor cursorAlarms = manager.getCursor();
                if (cursorAlarms != null) {
                    if (cursorAlarms.moveToFirst()) {
                        while (!cursorAlarms.isAfterLast()) {
                            GlobalVars.settingsToneAlarmTitle.add(cursorAlarms.getString(RingtoneManager.TITLE_COLUMN_INDEX));
                            GlobalVars.settingsToneAlarmUri.add(cursorAlarms.getString(RingtoneManager.URI_COLUMN_INDEX));
                            GlobalVars.settingsToneAlarmID.add(cursorAlarms.getString(RingtoneManager.ID_COLUMN_INDEX));
                            cursorAlarms.moveToNext();
                        }
                    }
                    //cursorAlarms.close();
                }
            } catch (NullPointerException e) {
            } catch (Exception e) {
            }

            //GETS EVERY NOTIFICATION TONE
            try {
                RingtoneManager manager = new RingtoneManager(this);
                manager.setType(RingtoneManager.TYPE_NOTIFICATION);
                Cursor cursorNotificationTone = manager.getCursor();
                if (cursorNotificationTone != null) {
                    if (cursorNotificationTone.moveToFirst()) {
                        while (!cursorNotificationTone.isAfterLast()) {
                            GlobalVars.settingsToneNotificationTitle.add(cursorNotificationTone.getString(RingtoneManager.TITLE_COLUMN_INDEX));
                            GlobalVars.settingsToneNotificationUri.add(cursorNotificationTone.getString(RingtoneManager.URI_COLUMN_INDEX));
                            GlobalVars.settingsToneNotificationID.add(cursorNotificationTone.getString(RingtoneManager.ID_COLUMN_INDEX));
                            cursorNotificationTone.moveToNext();
                        }
                    }
                    //cursorNotificationTone.close();
                }
            } catch (NullPointerException e) {
            } catch (Exception e) {
            }

            //GETS EVERY CALL TONE
            try {
                RingtoneManager manager = new RingtoneManager(this);
                manager.setType(RingtoneManager.TYPE_RINGTONE);
                Cursor cursorCallTone = manager.getCursor();
                if (cursorCallTone != null) {
                    if (cursorCallTone.moveToFirst()) {
                        while (!cursorCallTone.isAfterLast()) {
                            GlobalVars.settingsToneCallTitle.add(cursorCallTone.getString(RingtoneManager.TITLE_COLUMN_INDEX));
                            GlobalVars.settingsToneCallUri.add(cursorCallTone.getString(RingtoneManager.URI_COLUMN_INDEX));
                            GlobalVars.settingsToneCallID.add(cursorCallTone.getString(RingtoneManager.ID_COLUMN_INDEX));
                            cursorCallTone.moveToNext();
                        }
                    }
                    //cursorCallTone.close();
                }
            } catch (NullPointerException e) {
            } catch (Exception e) {
            }

            //GETS READING SPEED VALUE
            String readingSpeedString = GlobalVars.readFile("readingspeed.cfg");
            if (readingSpeedString == "") {
                GlobalVars.settingsTTSReadingSpeed = 1;
                GlobalVars.tts.setSpeechRate(GlobalVars.settingsTTSReadingSpeed);
                GlobalVars.writeFile("readingspeed.cfg", String.valueOf(GlobalVars.settingsTTSReadingSpeed));
            } else {
                try {
                    GlobalVars.settingsTTSReadingSpeed = Integer.valueOf(readingSpeedString);
                    GlobalVars.tts.setSpeechRate(GlobalVars.settingsTTSReadingSpeed);
                } catch (Exception e) {
                    GlobalVars.settingsTTSReadingSpeed = 1;
                    GlobalVars.tts.setSpeechRate(GlobalVars.settingsTTSReadingSpeed);
                    GlobalVars.writeFile("readingspeed.cfg", String.valueOf(GlobalVars.settingsTTSReadingSpeed));
                }
            }

            //GETS INPUT MODE VALUE
            String inputModeString = GlobalVars.readFile("inputmode.cfg");
            if (inputModeString == "") {
                GlobalVars.inputMode = GlobalVars.INPUT_KEYBOARD;
                GlobalVars.writeFile("inputmode.cfg", String.valueOf(GlobalVars.INPUT_KEYBOARD));
            } else {
                try {
                    GlobalVars.inputMode = Integer.valueOf(inputModeString);
                } catch (Exception e) {
                    GlobalVars.inputMode = GlobalVars.INPUT_KEYBOARD;
                    GlobalVars.writeFile("inputmode.cfg", String.valueOf(GlobalVars.INPUT_KEYBOARD));
                }
            }

            //GETS SCREEN TIMEOUT POSSIBLE VALUES
            int[] arr = getResources().getIntArray(R.array.screenTimeOutSeconds);
            for (int i = 0; i < arr.length; i++) {
                GlobalVars.settingsScreenTimeOutValues.add(String.valueOf(arr[i]));
            }

            //GETS TIME VALUES FOR ALARMS
            String[] arr2 = getResources().getStringArray(R.array.timeHourValues);
            for (int i = 0; i < arr2.length; i++) {
                GlobalVars.alarmTimeHoursValues.add(String.valueOf(arr2[i]));
            }
            String[] arr3 = getResources().getStringArray(R.array.timeMinutesValues);
            for (int i = 0; i < arr3.length; i++) {
                GlobalVars.alarmTimeMinutesValues.add(String.valueOf(arr3[i]));
            }

            //GETS SCREEN TIMEOUT VALUE
            String screenTimeOutString = GlobalVars.readFile("screentimeout.cfg");
            if (screenTimeOutString == "") {
                GlobalVars.settingsScreenTimeOut = Integer.valueOf(GlobalVars.settingsScreenTimeOutValues.get(GlobalVars.settingsScreenTimeOutValues.size() - 1));
                GlobalVars.writeFile("screentimeout.cfg", String.valueOf(GlobalVars.settingsScreenTimeOut));
            } else {
                try {
                    GlobalVars.settingsScreenTimeOut = Integer.valueOf(screenTimeOutString);
                } catch (Exception e) {
                    GlobalVars.settingsScreenTimeOut = Integer.valueOf(GlobalVars.settingsScreenTimeOutValues.get(GlobalVars.settingsScreenTimeOutValues.size() - 1));
                    GlobalVars.writeFile("screentimeout.cfg", String.valueOf(GlobalVars.settingsScreenTimeOut));
                }
            }

            //SETS BLUETOOTH VALUE STATE
            GlobalVars.bluetoothEnabled = GlobalVars.isBluetoothEnabled();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    speakOnResume = true;
                }
            }, 2000);

            GlobalVars.startService(Drishti.class);
        }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG","@@@ PERMISSIONS grant");
                  //  callNextActivity();
                } else {
                    Log.d("TAG","@@@ PERMISSIONS Denied");
                    Toast.makeText(mContext, "PERMISSIONS Denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }







	@Override public void onResume()
	{
		Log.e("Language  Konsi hai  = ",LocaleHelper.getLanguage(this));
		super.onResume();
		try{GlobalVars.alarmVibrator.cancel();}catch(NullPointerException e){}catch(Exception e){}
		GlobalVars.lastActivity = Main.class;
		GlobalVars.activityItemLocation=0;
		GlobalVars.activityItemLimit=7;
		GlobalVars.selectTextView(messages,false);
		GlobalVars.selectTextView(calls,false);
		GlobalVars.selectTextView(contacts,false);
		GlobalVars.selectTextView(music,false);
		GlobalVars.selectTextView(internet,false);
		//GlobalVars.selectTextView(alarms,false);
		//GlobalVars.selectTextView(voicerecorder,false);
		//GlobalVars.selectTextView(applications,false);
		//GlobalVars.selectTextView(settings,false);
		GlobalVars.selectTextView(status,false);

		//UPDATE ALARM COUNTER
		try
		{
			GlobalVars.setText(alarms,false, getResources().getString(R.string.mainAlarms) + " (" + GlobalVars.getPendingAlarmsForTodayCount() + ")");
		}
		catch(IllegalStateException e)
		{
		}
		catch(Exception e)
		{
		}

		try
		{
			if (GlobalVars.deviceIsAPhone()==true)
			{
				messages.setText(GlobalVars.context.getResources().getString(R.string.mainMessages) + " (" + String.valueOf(GlobalVars.getMessagesUnreadCount()) + ")");
			}
		}
		catch(IllegalStateException e)
		{
		}
		catch(Exception e)
		{
		}

		if (speakOnResume==true)
		{
			GlobalVars.talk(getResources().getString(R.string.layoutMainOnResume));
		}

		try
		{
			if (GlobalVars.deviceIsAPhone()==true)
			{
				calls.setText(GlobalVars.context.getResources().getString(R.string.mainCalls) + " (" + String.valueOf(GlobalVars.getCallsMissedCount()) + ")");
			}
			else
			{
				calls.setText(GlobalVars.context.getResources().getString(R.string.mainCalls) + " (0)");
			}
		}
		catch(NullPointerException e)
		{
		}
		catch(IllegalStateException e)
		{
		}
		catch(Exception e)
		{
		}
	}


	public void onInit(int status)
	{
		if (status == TextToSpeech.SUCCESS)
		{
			GlobalVars.talk(getResources().getString(R.string.mainWelcome));
			GlobalVars.tts.setOnUtteranceCompletedListener(new OnUtteranceCompletedListener()
			{
				@Override public void onUtteranceCompleted(String utteranceId)
				{
					try
					{
						GlobalVars.musicPlayer.setVolume(1.5f,1.5f);
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

	public void shutdownEverything()
	{
		try
		{
			GlobalVars.tts.shutdown();
		}
		catch(Exception e)
		{
		}
		try
		{
			if (GlobalVars.musicPlayer!=null)
			{
				GlobalVars.musicPlayer.stop();
				GlobalVars.musicPlayer.reset();
				GlobalVars.musicPlayer.release();
				GlobalVars.musicPlayer = null;
			}
		}
		catch(Exception e)
		{
		}
		GlobalVars.musicPlayerPlayingSongIndex = -1;
		try
		{
			GlobalVars.stopService(Drishti.class);
		}
		catch(Exception e)
		{
		}
	}


	public void select()
	{
		switch (GlobalVars.activityItemLocation)
		{
			case 1: //MESSAGES
				if (GlobalVars.deviceIsAPhone()==true)
				{
					int smsUnread = GlobalVars.getMessagesUnreadCount();
					if (smsUnread==0)
					{
						GlobalVars.talk(getResources().getString(R.string.mainMessagesNoNew));
					}
					else if (smsUnread==1)
					{
						GlobalVars.talk(getResources().getString(R.string.mainMessagesOneNew));
					}
					else
					{
						GlobalVars.talk(getResources().getString(R.string.mainMessages) + ". " + smsUnread + " " + getResources().getString(R.string.mainMessagesNew));
					}
				//	GlobalVars.talk(getResources().getString(R.string.SlideHearToEachOne));
				}
				else
				{
					GlobalVars.talk(getResources().getString(R.string.mainMessagesNotAvailable));
				}
				GlobalVars.selectTextView(messages,true);
				GlobalVars.selectTextView(calls,false);
				GlobalVars.selectTextView(language,false);
				break;

			case 2: //CALLS
				if (GlobalVars.deviceIsAPhone()==true)
				{
					int missedCalls = GlobalVars.getCallsMissedCount();
					if (missedCalls == 0)
					{
						GlobalVars.talk(getResources().getString(R.string.mainCallsNoMissed));
					}
					else if (missedCalls==1)
					{
						GlobalVars.talk(getResources().getString(R.string.mainCallsOneMissed));
					}
					else
					{
						GlobalVars.talk(getResources().getString(R.string.mainCalls) + ". " + missedCalls + " " + getResources().getString(R.string.mainCallsMissed));
					}

				//	GlobalVars.talk(getResources().getString(R.string.SlideHearToEachOne));
				}
				else
				{
					GlobalVars.talk(getResources().getString(R.string.mainCallsNotAvailable));
				}
				GlobalVars.selectTextView(calls, true);
				GlobalVars.selectTextView(messages,false);
				GlobalVars.selectTextView(contacts,false);
				Log.e("Langggg",LocaleHelper.getLanguage(this));
				break;

			case 3: //CONTACTS
				GlobalVars.selectTextView(contacts,true);
				GlobalVars.selectTextView(calls,false);
				GlobalVars.selectTextView(music,false);
				GlobalVars.talk(getResources().getString(R.string.mainContacts));
				//GlobalVars.talk(getResources().getString(R.string.SlideHearToEachOne));
				break;

			case 4: //MUSIC
				GlobalVars.selectTextView(music,true);
				GlobalVars.selectTextView(contacts,false);
				GlobalVars.selectTextView(internet,false);
				GlobalVars.talk(getResources().getString(R.string.mainMusicPlayer));
				//GlobalVars.talk(getResources().getString(R.string.SlideHearToEachOne));
				break;

			case 5: //INTERNET
				GlobalVars.selectTextView(internet,true);
				GlobalVars.selectTextView(status,false);
				GlobalVars.selectTextView(music,false);

				GlobalVars.talk(getResources().getString(R.string.youtube));
				//GlobalVars.talk(getResources().getString(R.string.SlideHearToEachOne));
				break;
			case 6: //STATUS
				GlobalVars.selectTextView(status,true);
				GlobalVars.selectTextView(internet,false);
				GlobalVars.selectTextView(language,false);
				GlobalVars.talk(getResources().getString(R.string.mainStatus));
				break;


			case 7:
				GlobalVars.selectTextView(language,true);
				GlobalVars.selectTextView(status,false);
				GlobalVars.selectTextView(messages,false);
				GlobalVars.talk(getResources().getString(R.string.languagess));
				//GlobalVars.talk(getResources().getString(R.string.SlideHearToEachOne));
				break;
		}

	}

	public void execute()
	{
		switch (GlobalVars.activityItemLocation)
		{
			case 1: //MESSAGES
				if (GlobalVars.deviceIsAPhone()==true)
				{
					GlobalVars.startActivity(Messages.class);
				}
				else
				{
					GlobalVars.talk(getResources().getString(R.string.mainNotAvailable));
				}
				break;

			case 2: //CALLS
				if (GlobalVars.deviceIsAPhone()==true)
				{
					GlobalVars.startActivity(Calls.class);
				}
				else
				{
					GlobalVars.talk(getResources().getString(R.string.mainNotAvailable));
				}
				break;

			case 3: //CONTACTS
				GlobalVars.startActivity(Contacts.class);
				break;

			case 4: //MUSIC
				if (GlobalVars.musicPlayerDatabaseReady==true)
				{
					GlobalVars.startActivity(MusicPlayer.class);
				}
				else
				{
					GlobalVars.talk(getResources().getString(R.string.mainMusicPlayerPleaseTryAgain));
				}
				break;


			case 5:
				//case 5: //INTERNET
				if(GlobalVars.isOnline(this)){
				GlobalVars.startActivity(yo.class);}
				else{
					GlobalVars.talk(getResources().getString(R.string.nonet));
				}
				break;
			case 6: //STATUS
				GlobalVars.talk(getDeviceStatus());
				break;

			case 7:
				GlobalVars.startActivity(language.class);
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

		if (keyCode== KeyEvent.KEYCODE_BACK)
		{
			if (GlobalVars.isBCTheDefaultLauncher()==false)
			{
				if (okToFinish==false)
				{
					okToFinish=true;
					Handler handler = new Handler();
					handler.postDelayed(new Runnable()
					{
						@Override public void run()
						{
							okToFinish = false;
						}
					}, 3000);
					GlobalVars.talk(getResources().getString(R.string.mainPressBack));
					return false;
				}
				else
				{
					shutdownEverything();
					this.finish();
				}
			}
			else
			{
				return false;
			}
		}
		return super.onKeyUp(keyCode, event);
	}


	private String getDeviceStatus()
	{
		String year = GlobalVars.getYear();
		String month = GlobalVars.getMonthName(Integer.valueOf(GlobalVars.getMonth()));
		String day = Integer.toString(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
		String dayname = GlobalVars.getDayName(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
		String hour = Integer.toString(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
		String minutes = Integer.toString(Calendar.getInstance().get(Calendar.MINUTE));

		String textStatus = "";

		textStatus = textStatus +
				getResources().getString(R.string.mainBatteryChargedAt) +
				String.valueOf(batteryLevel() +
						getResources().getString(R.string.mainPercentAndTime) +
						hour + getResources().getString(R.string.mainHours) +
						minutes + getResources().getString(R.string.mainMinutesAndDate) +
						dayname + " " + day + getResources().getString(R.string.mainOf) +
						month + getResources().getString(R.string.mainOf) + year);

		if (GlobalVars.batteryAt100==true)
		{
			textStatus = textStatus + getResources().getString(R.string.deviceChargedStatus);
		}
		else if (GlobalVars.batteryIsCharging==true)
		{
			textStatus = textStatus + getResources().getString(R.string.deviceChargingStatus);
		}

		if (GlobalVars.deviceIsAPhone()==true)
		{
			if (GlobalVars.deviceIsConnectedToMobileNetwork()==true)
			{
				textStatus = textStatus + getResources().getString(R.string.mainCarrierIs) + getCarrier();
			}
			else
			{
				textStatus = textStatus + getResources().getString(R.string.mainNoSignal);
			}
		}

		AudioManager audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
		switch(audioManager.getRingerMode())
		{
			case AudioManager.RINGER_MODE_NORMAL:
				textStatus = textStatus + getResources().getString(R.string.mainProfileIsNormal);
				break;

			case AudioManager.RINGER_MODE_SILENT:
				textStatus = textStatus + getResources().getString(R.string.mainProfileIsSilent);
				break;

			case AudioManager.RINGER_MODE_VIBRATE:
				textStatus = textStatus + getResources().getString(R.string.mainProfileIsVibrate);
				break;
		}

		if (GlobalVars.isWifiEnabled())
		{
			String name = GlobalVars.getWifiSSID();
			if (name=="")
			{
				textStatus = textStatus + getResources().getString(R.string.mainWifiOnWithoutNetwork);
			}
			else
			{
				textStatus = textStatus + getResources().getString(R.string.mainWifiOnWithNetwork) + name + ".";
			}
		}
		else
		{
			textStatus = textStatus + getResources().getString(R.string.mainWifiOff);
		}
		return textStatus;
	}

	private int batteryLevel()
	{
		Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		if(level == -1 || scale == -1)
		{
			return (int)50.0f;
		}
		return (int)(((float)level / (float)scale) * 100.0f);
	}

	private String getCarrier()
	{
		try
		{
			TelephonyManager telephonyManager = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE));
			String carrier;
			carrier = telephonyManager.getSimOperatorName();
			if (carrier==null | carrier=="")
			{
				return getResources().getString(R.string.mainCarrierNotAvailable);
			}
			else
			{
				return carrier;
			}
		}
		catch(Exception e)
		{
			return getResources().getString(R.string.mainCarrierNotAvailable);
		}
	}


}
