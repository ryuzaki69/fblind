package org.project.harsh.fblind;

import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import android.app.Activity;
import android.content.ContentValues;

public class MessagesInbox extends Activity
	{
	public static TextView inbox;
	private TextView reply;
	private TextView delete;
	private TextView callto;
	private TextView addtonewcontact;
	private TextView deleteall;
	private TextView goback;
	public static int selectedMessage = -1;
	
    @Override protected void onCreate(Bundle savedInstanceState)
    	{
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.messagesinbox);
		GlobalVars.lastActivity = MessagesInbox.class;
		inbox = (TextView) findViewById(R.id.messagesinbox);
		reply = (TextView) findViewById(R.id.messagesreply);
		delete = (TextView) findViewById(R.id.messagesdelete);
		callto = (TextView) findViewById(R.id.messagescallto);
		addtonewcontact = (TextView) findViewById(R.id.addtonewcontact);
		deleteall = (TextView) findViewById(R.id.messagesdeleteall);
		goback = (TextView) findViewById(R.id.goback);
		GlobalVars.activityItemLocation=0;
		GlobalVars.activityItemLimit=7;
		selectedMessage = -1;
		new MessagesCheckThread(GlobalVars.TYPE_INBOX).execute();
    	}
    
	@Override public void onResume()
		{
		super.onResume();
		try{GlobalVars.alarmVibrator.cancel();}catch(NullPointerException e){}catch(Exception e){}
		GlobalVars.lastActivity = MessagesInbox.class;
		GlobalVars.activityItemLocation=0;
		GlobalVars.activityItemLimit=2;
		GlobalVars.selectTextView(inbox,false);
		GlobalVars.selectTextView(reply,false);
		GlobalVars.selectTextView(delete,false);
		GlobalVars.selectTextView(callto, false);
		GlobalVars.selectTextView(addtonewcontact, false);
		GlobalVars.selectTextView(deleteall,false);
		GlobalVars.selectTextView(goback,false);
		if (GlobalVars.messagesWasSent == true)
			{
			GlobalVars.messagesWasSent = false;
			GlobalVars.talk(getResources().getString(R.string.layoutMessagesInboxOnResume2));
			}
			else
			{
			if (GlobalVars.messagesWasDeleted==true)
				{
				new MessagesCheckThread(GlobalVars.TYPE_INBOX).execute();
				try
					{
					selectedMessage = -1;
					if (GlobalVars.activityItemLocation==1)
						{
						GlobalVars.setText(MessagesInbox.inbox, true, GlobalVars.context.getResources().getString(R.string.layoutMessagesInboxInbox));
						}
						else
						{
						GlobalVars.setText(MessagesInbox.inbox, false, GlobalVars.context.getResources().getString(R.string.layoutMessagesInboxInbox));
						}
					}
					catch(Exception e)
					{
					}
				GlobalVars.talk(getResources().getString(R.string.layoutMessagesInboxOnResume3));
				GlobalVars.messagesWasDeleted=false;
				}
			else if (GlobalVars.messagesInboxWereDeleted==true)
				{
				new MessagesCheckThread(GlobalVars.TYPE_INBOX).execute();
				try
					{
					selectedMessage = -1;
					if (GlobalVars.activityItemLocation==1)
						{
						GlobalVars.setText(MessagesInbox.inbox, true, GlobalVars.context.getResources().getString(R.string.layoutMessagesInboxInbox));
						}
						else
						{
						GlobalVars.setText(MessagesInbox.inbox, false, GlobalVars.context.getResources().getString(R.string.layoutMessagesInboxInbox));
						}
					}
					catch(Exception e)
					{
					}
				GlobalVars.talk(getResources().getString(R.string.layoutMessagesInboxOnResume4));
				GlobalVars.messagesInboxWereDeleted=false;
				}
			else
				{
				GlobalVars.talk(getResources().getString(R.string.layoutMessagesInboxOnResume));
				}
			}
		}
		
	public void select()
		{
		switch (GlobalVars.activityItemLocation) {
			case 1: //INBOX
				GlobalVars.selectTextView(inbox, true);
				GlobalVars.selectTextView(reply, false);
				GlobalVars.selectTextView(goback, false);
				if (selectedMessage == -1) {
					GlobalVars.talk(getResources().getString(R.string.layoutMessagesInboxInbox2));
				} else {
					if (GlobalVars.messagesInboxDatabaseReady == false) {
						GlobalVars.talk(getResources().getString(R.string.layoutMessagesInboxTryAgain));
					} else {
						GlobalVars.talk(getResources().getString(R.string.layoutMessagesInboxMessageItem) +
								String.valueOf(selectedMessage + 1) +
								getResources().getString(R.string.layoutMessagesInboxMessageOf) +
								String.valueOf(GlobalVars.messagesInboxDataBase.size()) + ". " +
								getResources().getString(R.string.layoutMessagesInboxMessageReceived) +
								GlobalVars.getMessageContactName(GlobalVars.messagesInboxDataBase.get(selectedMessage)) +
								GlobalVars.getMessageDateTime(GlobalVars.messagesInboxDataBase.get(selectedMessage)) +
								getResources().getString(R.string.layoutMessagesInboxMessageMessageBody) +
								GlobalVars.getMessageBody(GlobalVars.messagesInboxDataBase.get(selectedMessage)));
					}
				}
				break;

			case 2: //REPLY

				GlobalVars.selectTextView(goback, true);

				GlobalVars.selectTextView(inbox, false);
				GlobalVars.talk(getResources().getString(R.string.backToPreviousMenu));
				break;

		}
		}
		
	public void execute()
		{
		switch (GlobalVars.activityItemLocation)
			{
			case 1: //INBOX
			if (GlobalVars.messagesInboxDatabaseReady==false)
				{
				GlobalVars.talk(getResources().getString(R.string.layoutMessagesInboxTryAgain));
				}
				else
				{
				if (GlobalVars.messagesInboxDataBase.size()>0)
					{
					if (selectedMessage+1==GlobalVars.messagesInboxDataBase.size())
						{
						selectedMessage = -1;
						}
					selectedMessage = selectedMessage + 1;
					GlobalVars.setText(inbox, true, getResources().getString(R.string.layoutMessagesInboxMessageItem) +
													"(" + String.valueOf(selectedMessage + 1) + "/" + 
													String.valueOf(GlobalVars.messagesInboxDataBase.size()) + ")\n" +
													GlobalVars.getMessageContactName(GlobalVars.messagesInboxDataBase.get(selectedMessage)));
					GlobalVars.talk(getResources().getString(R.string.layoutMessagesInboxMessageItem) +
									String.valueOf(selectedMessage + 1) + 
									getResources().getString(R.string.layoutMessagesInboxMessageOf) +
									String.valueOf(GlobalVars.messagesInboxDataBase.size()) + ". " +
									getResources().getString(R.string.layoutMessagesInboxMessageReceived) +
									GlobalVars.getMessageContactName(GlobalVars.messagesInboxDataBase.get(selectedMessage)) +
									GlobalVars.getMessageDateTime(GlobalVars.messagesInboxDataBase.get(selectedMessage)) +
									getResources().getString(R.string.layoutMessagesInboxMessageMessageBody) +
									GlobalVars.getMessageBody(GlobalVars.messagesInboxDataBase.get(selectedMessage)));
					markMessageRead(GlobalVars.getMessageID(GlobalVars.messagesInboxDataBase.get(selectedMessage)));
					}
					else
					{
					GlobalVars.talk(getResources().getString(R.string.layoutMessagesInboxNoMessages));
					}
				}
			break;


			
			case 2: //GO BACK TO THE PREVIOUS MENU
			this.finish();
			break;
			}
		}
	
	private void previousItem()
		{
		switch (GlobalVars.activityItemLocation)
			{
			case 1: //INBOX
			if (GlobalVars.messagesInboxDatabaseReady==false)
				{
				GlobalVars.talk(getResources().getString(R.string.layoutMessagesInboxTryAgain));
				}
				else
				{
				if (GlobalVars.messagesInboxDataBase.size()>0)
					{
					if (selectedMessage-1<0)
						{
						selectedMessage = GlobalVars.messagesInboxDataBase.size();
						}
					selectedMessage = selectedMessage - 1;
					GlobalVars.setText(inbox, true, getResources().getString(R.string.layoutMessagesInboxMessageItem) +
												"(" + String.valueOf(selectedMessage + 1) + "/" + 
												String.valueOf(GlobalVars.messagesInboxDataBase.size()) + ")\n" +
												GlobalVars.getMessageContactName(GlobalVars.messagesInboxDataBase.get(selectedMessage)));
					GlobalVars.talk(getResources().getString(R.string.layoutMessagesInboxMessageItem) +
									String.valueOf(selectedMessage + 1) + 
									getResources().getString(R.string.layoutMessagesInboxMessageOf) +
									String.valueOf(GlobalVars.messagesInboxDataBase.size()) + ". " +
									getResources().getString(R.string.layoutMessagesInboxMessageReceived) +
									GlobalVars.getMessageContactName(GlobalVars.messagesInboxDataBase.get(selectedMessage)) +
									GlobalVars.getMessageDateTime(GlobalVars.messagesInboxDataBase.get(selectedMessage)) +
									getResources().getString(R.string.layoutMessagesInboxMessageMessageBody) +
									GlobalVars.getMessageBody(GlobalVars.messagesInboxDataBase.get(selectedMessage)));
					markMessageRead(GlobalVars.getMessageID(GlobalVars.messagesInboxDataBase.get(selectedMessage)));
					}
					else
					{
					GlobalVars.talk(getResources().getString(R.string.layoutMessagesInboxNoMessages));
					}
				}
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

			case GlobalVars.ACTION_SELECT_PREVIOUS:
			previousItem();
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
	
	private void markMessageRead(String messageID)
		{
        try
        	{
        	ContentValues values = new ContentValues();
        	values.put("read", true);
        	getContentResolver().update(Uri.parse("content://sms/inbox"), values, "_id=" + messageID, null);
        	}
        	catch(Exception e)
        	{
        	}
		}
	}
