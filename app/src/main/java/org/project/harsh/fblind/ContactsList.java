package org.project.harsh.fblind;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TextView;

public class ContactsList extends Activity {
    private TextView callto;
    private TextView contacts;
    private TextView delete;
    private TextView goback;
    public int selectedContact = -1;
    private TextView writeto;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contactslist);
        GlobalVars.lastActivity = ContactsList.class;
        this.contacts = (TextView) findViewById(R.id.contactslist);
        this.callto = (TextView) findViewById(R.id.contactscall);
        this.writeto = (TextView) findViewById(R.id.contactssms);
        this.delete = (TextView) findViewById(R.id.contactsdelete);
        this.goback = (TextView) findViewById(R.id.goback);
        GlobalVars.activityItemLocation = 0;
        GlobalVars.activityItemLimit = 3;
        this.selectedContact = -1;
        new ContactsListThread().execute(new String[]{""});
    }

    protected void onResume() {
        super.onResume();
        try {
            GlobalVars.alarmVibrator.cancel();
        } catch (NullPointerException e) {
        } catch (Exception e2) {
        }
        GlobalVars.lastActivity = ContactsList.class;
        GlobalVars.activityItemLocation = 0;
        GlobalVars.activityItemLimit = 3;
        GlobalVars.selectTextView(this.contacts, false);
        GlobalVars.selectTextView(this.callto, false);
        GlobalVars.selectTextView(this.writeto, false);
        GlobalVars.selectTextView(this.delete, false);
        GlobalVars.selectTextView(this.goback, false);
        GlobalVars.contactToDeleteName = "";
        GlobalVars.contactToDeletePhone = "";
        if (GlobalVars.contactWasDeleted) {
            GlobalVars.contactWasDeleted = false;
            GlobalVars.setText(this.contacts, false, getResources().getString(R.string.layoutContactsListContactsList));
            GlobalVars.talk(getResources().getString(R.string.layoutContactsListOnResumeContactDeleted));
            this.selectedContact = -1;
            new ContactsListThread().execute(new String[]{""});
        } else if (GlobalVars.messagesWasSent) {
            GlobalVars.messagesWasSent = false;
            GlobalVars.talk(getResources().getString(R.string.layoutContactsListOnResume2));
        } else {
            GlobalVars.talk(getResources().getString(R.string.layoutContactsListOnResume));
        }
    }

    public void select() {
        switch (GlobalVars.activityItemLocation) {
            case 1:
                GlobalVars.selectTextView(this.contacts, true);
                GlobalVars.selectTextView(this.callto, false);
                GlobalVars.selectTextView(this.goback, false);
                if (this.selectedContact == -1) {
                    GlobalVars.talk(getResources().getString(R.string.layoutContactsListContactsList2));
                    return;
                } else if (!GlobalVars.contactListReady) {
                    GlobalVars.talk(getResources().getString(R.string.layoutContactsListPleaseWait));
                    return;
                } else if (GlobalVars.contactDataBase.size() > 0) {
                    try {
                        GlobalVars.talk(new StringBuilder(String.valueOf(GlobalVars.contactsGetNameFromListValue((String) GlobalVars.contactDataBase.get(this.selectedContact)))).append(getResources().getString(R.string.layoutContactsListWithThePhoneNumber)).append(GlobalVars.divideNumbersWithBlanks(GlobalVars.contactsGetPhoneNumberFromListValue((String) GlobalVars.contactDataBase.get(this.selectedContact)))).toString());
                        return;
                    } catch (NullPointerException e) {
                        GlobalVars.talk(getResources().getString(R.string.layoutContactsListPleaseWait));
                        return;
                    } catch (Exception e2) {
                        GlobalVars.talk(getResources().getString(R.string.layoutContactsListPleaseWait));
                        return;
                    }
                } else {
                    GlobalVars.talk(getResources().getString(R.string.layoutContactsListPleaseWait));
                    return;
                }
            case 2:
                GlobalVars.selectTextView(this.callto, true);
                GlobalVars.selectTextView(this.contacts, false);
                GlobalVars.selectTextView(this.goback, false);
                GlobalVars.talk(getResources().getString(R.string.layoutContactsListCall2));
                return;
            case 3:
                GlobalVars.selectTextView(this.goback, true);
                GlobalVars.selectTextView(this.contacts, false);
                GlobalVars.selectTextView(this.callto, false);
                GlobalVars.talk(getResources().getString(R.string.backToPreviousMenu));
                return;

            default:
                return;
        }
    }

    public void execute() {
        switch (GlobalVars.activityItemLocation) {
            case 1:
                if (!GlobalVars.contactListReady) {
                    GlobalVars.talk(getResources().getString(R.string.layoutContactsListPleaseWait));
                    return;
                } else if (GlobalVars.contactDataBase.size() > 0) {
                    if (this.selectedContact + 1 == GlobalVars.contactDataBase.size()) {
                        this.selectedContact = -1;
                    }
                    this.selectedContact++;
                    GlobalVars.setText(this.contacts, true, new StringBuilder(String.valueOf(GlobalVars.contactsGetNameFromListValue((String) GlobalVars.contactDataBase.get(this.selectedContact)))).append("\n").append(GlobalVars.contactsGetPhoneNumberFromListValue((String) GlobalVars.contactDataBase.get(this.selectedContact))).toString());
                    GlobalVars.talk(new StringBuilder(String.valueOf(GlobalVars.contactsGetNameFromListValue((String) GlobalVars.contactDataBase.get(this.selectedContact)))).append(getResources().getString(R.string.layoutContactsListWithThePhoneNumber)).append(GlobalVars.divideNumbersWithBlanks(GlobalVars.contactsGetPhoneNumberFromListValue((String) GlobalVars.contactDataBase.get(this.selectedContact)))).toString());
                    return;
                } else {
                    GlobalVars.talk(getResources().getString(R.string.layoutContactsListNoContacts));
                    return;
                }
            case 2:
                if (!GlobalVars.contactListReady) {
                    GlobalVars.talk(getResources().getString(R.string.layoutContactsListPleaseWait));
                    return;
                } else if (this.selectedContact == -1) {
                    GlobalVars.talk(getResources().getString(R.string.layoutContactsListError));
                    return;
                } else if (GlobalVars.deviceIsAPhone()) {
                    GlobalVars.callTo("tel:" + ((String) GlobalVars.contactDataBase.get(this.selectedContact)).substring(((String) GlobalVars.contactDataBase.get(this.selectedContact)).lastIndexOf("|") + 1, ((String) GlobalVars.contactDataBase.get(this.selectedContact)).length()));
                    return;
                } else {
                    GlobalVars.talk(getResources().getString(R.string.mainNotAvailable));
                    return;
                }
            case 3:
                finish();
                return;

            default:
                return;
        }
    }

    private void previousItem() {
        switch (GlobalVars.activityItemLocation) {
            case 1:
                if (!GlobalVars.contactListReady) {
                    GlobalVars.talk(getResources().getString(R.string.layoutContactsListPleaseWait));
                    return;
                } else if (GlobalVars.contactDataBase.size() > 0) {
                    if (this.selectedContact - 1 < 0) {
                        this.selectedContact = GlobalVars.contactDataBase.size();
                    }
                    this.selectedContact--;
                    GlobalVars.setText(this.contacts, true, new StringBuilder(String.valueOf(GlobalVars.contactsGetNameFromListValue((String) GlobalVars.contactDataBase.get(this.selectedContact)))).append("\n").append(GlobalVars.contactsGetPhoneNumberFromListValue((String) GlobalVars.contactDataBase.get(this.selectedContact))).toString());
                    GlobalVars.talk(new StringBuilder(String.valueOf(GlobalVars.contactsGetNameFromListValue((String) GlobalVars.contactDataBase.get(this.selectedContact)))).append(getResources().getString(R.string.layoutContactsListWithThePhoneNumber)).append(GlobalVars.divideNumbersWithBlanks(GlobalVars.contactsGetPhoneNumberFromListValue((String) GlobalVars.contactDataBase.get(this.selectedContact)))).toString());
                    return;
                } else {
                    GlobalVars.talk(getResources().getString(R.string.layoutContactsListNoContacts));
                    return;
                }
            default:
                return;
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (GlobalVars.detectMovement(event)) {
            case 1:
                select();
                break;
            case 2:
                previousItem();
                break;
            case 3:
                execute();
                break;
        }
        return super.onTouchEvent(event);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (GlobalVars.detectKeyUp(keyCode)) {
            case 1:
                select();
                break;
            case 2:
                previousItem();
                break;
            case 3:
                execute();
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return GlobalVars.detectKeyDown(keyCode);
    }
}
