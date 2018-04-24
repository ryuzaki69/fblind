package org.project.harsh.fblind;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.provider.*;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.telephony.*;
import android.util.Log;
import android.util.Pair;
import android.view.*;
import android.widget.*;

import com.google.api.services.youtube.YouTube;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL;

public class yo extends Activity
{
    public static String video=null;
    private TextView body;
    private SpeechRecognizer sr;
    private int selectedValue = -1;
    private TextView send;
    private TextView goback;
    private TextView in;
    private int selectedContact = -1;
    private boolean sending = false;
    private TextView resultsTextview;
    List<VideoItem> hash;
    List<VideoItem> hash2;
    ArrayList<Pair<String,String>> ans;

    String messageBody="";
    String mm=null;
    private List<String> stringResults = new ArrayList<String>();
    int j=0;
    int j1=0;
    private TextView oh;
    @Override protected void onCreate(Bundle savedInstanceState)
    {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.yo);

        in = (TextView) findViewById(R.id.start);
        oh = (TextView) findViewById(R.id.ok) ;
        resultsTextview = (TextView) findViewById(R.id.possibleresults);
        body = (TextView) findViewById(R.id.results);
        send = (TextView) findViewById(R.id.enter);
        goback = (TextView) findViewById(R.id.goback);
        GlobalVars.activityItemLocation=0;
        GlobalVars.activityItemLimit=5;
        GlobalVars.messagesWasSent = false;

        stringResults.clear();
        GlobalVars.setText(in, false, getResources().getString(R.string.layoutInputVoicePossibleResults) + stringResults.size() + ")");

    }

    @Override public void onResume()
    {
        super.onResume();
        //try{GlobalVars.alarmVibrator.cancel();}catch(NullPointerException e){}catch(Exception e){}
       /*
        if (GlobalVars.inputModeResult != null)
        {
                mm=     GlobalVars.inputModeResult;
            messageBody = GlobalVars.inputModeResult;
            GlobalVars.setText(body, false, messageBody);
            GlobalVars.inputModeResult = null;
            new Thread(){
                @Override
                public void run() {
                    YoutubeConnector yc = new YoutubeConnector(yo.this);
                    hash = yc.search(mm);
                    Log.e("fhgv",hash.get(0).getDescription());


                    //GlobalVars.startActivity(carry.class);
                    //
                }
            }.start();

        }
        */
        //GlobalVars.lastActivity = MessagesCompose.class;
        GlobalVars.activityItemLocation=0;
        GlobalVars.activityItemLimit = 6;
        GlobalVars.selectTextView(in,false);
        GlobalVars.selectTextView(resultsTextview,false);
        GlobalVars.selectTextView(oh,false);
        GlobalVars.selectTextView(body,false);
        GlobalVars.selectTextView(send,false);
        GlobalVars.selectTextView(goback,false);
        GlobalVars.talk(getResources().getString(R.string.welcometoyoutube));
       //GlobalVars.talk(getResources().getString(R.string.layoutMessagesComposeOnResume));
    }

    @Override protected void onDestroy()
    {
        super.onDestroy();
        try
        {
            if(sr != null)
            {
                sr.destroy();
            }

        }
        catch(NullPointerException e)
        {

        }
        catch(Exception e)
        {
        }
    }


    class listener implements RecognitionListener
    {
        public void onReadyForSpeech(Bundle params)
        {
        }

        public void onBeginningOfSpeech()
        {
        }

        public void onRmsChanged(float rmsdB)
        {
        }

        public void onBufferReceived(byte[] buffer)
        {
        }

        public void onEndOfSpeech()
        {
        }

        public void onError(int error)
        {
            stringResults.clear();
            selectedValue = -1;
            if (GlobalVars.activityItemLocation==2)
            {
                GlobalVars.setText(resultsTextview, true, getResources().getString(R.string.layoutInputVoicePossibleResults) + stringResults.size() + ")");
            }
            else
            {
                GlobalVars.setText(resultsTextview, false, getResources().getString(R.string.layoutInputVoicePossibleResults) + stringResults.size() + ")");
            }
            GlobalVars.talk(getResources().getString(R.string.layoutInputVoiceNoRecognition));
        }

        public void onResults(Bundle results)
        {
            try
            {
                stringResults.clear();
                if (GlobalVars.activityItemLocation==2)
                {
                    GlobalVars.setText(resultsTextview, true, getResources().getString(R.string.layoutInputVoicePossibleResults) + stringResults.size() + ")");
                }
                else
                {
                    GlobalVars.setText(resultsTextview, false, getResources().getString(R.string.layoutInputVoicePossibleResults) + stringResults.size() + ")");
                }
                ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                for (int i=0;i<data.size();i++)
                {
                    stringResults.add(data.get(i));
                }
                if (GlobalVars.activityItemLocation==2)
                {
                    GlobalVars.setText(resultsTextview, true, getResources().getString(R.string.layoutInputVoicePossibleResults) + stringResults.size() + ")");
                }
                else
                {
                    GlobalVars.setText(resultsTextview, false, getResources().getString(R.string.layoutInputVoicePossibleResults) + stringResults.size() + ")");
                }
                GlobalVars.talk(getResources().getString(R.string.layoutInputVoicePossibleResults2) + stringResults.size());
            }
            catch(NullPointerException e)
            {
                GlobalVars.talk(getResources().getString(R.string.layoutInputVoiceSystemError));
            }
            catch(Exception e)
            {
                GlobalVars.talk(getResources().getString(R.string.layoutInputVoiceSystemError));
            }
        }

        public void onPartialResults(Bundle partialResults)
        {
            stringResults.clear();
            selectedValue = -1;
            if (GlobalVars.activityItemLocation==2)
            {
                GlobalVars.setText(resultsTextview, true, getResources().getString(R.string.layoutInputVoicePossibleResults) + stringResults.size() + ")");
            }
            else
            {
                GlobalVars.setText(resultsTextview, false, getResources().getString(R.string.layoutInputVoicePossibleResults) + stringResults.size() + ")");
            }
            GlobalVars.talk(getResources().getString(R.string.layoutInputVoiceNoRecognition));
        }

        public void onEvent(int eventType, Bundle params)
        {
        }
    }

    public void select()
    {
        switch (GlobalVars.activityItemLocation)
        {
            case 1: // LINK ENRTY
                GlobalVars.selectTextView(in,true);
                GlobalVars.selectTextView(resultsTextview,false);
                GlobalVars.selectTextView(oh,false);
                GlobalVars.selectTextView(body,false);

                GlobalVars.selectTextView(goback,false);
                //GlobalVars.selectTextView(goback,false);

                GlobalVars.talk(getResources().getString(R.string.searchyo));
                break;

            case 2: // result on text view

                GlobalVars.selectTextView(in, false);
                GlobalVars.selectTextView(resultsTextview,true );
                GlobalVars.selectTextView(oh,false);
                GlobalVars.selectTextView(body,false);
                if (selectedValue==-1)
                {
                    GlobalVars.talk(getResources().getString(R.string.layoutInputVoicePossibleResults2) + stringResults.size() +  getResources().getString(R.string.layoutInputVoicePossibleResults3));
                }
                else
                {
                    GlobalVars.talk(getResources().getString(R.string.layoutInputVoicePossibleResult) + (selectedValue + 1) + ". " + stringResults.get(selectedValue));
                }

                GlobalVars.talk(getResources().getString(R.string.selectyo));

                break;

            case 3: //SEND
                GlobalVars.selectTextView(in,false);
                GlobalVars.selectTextView(resultsTextview, false);
                GlobalVars.selectTextView(oh,true);
                GlobalVars.selectTextView(body,false);
                GlobalVars.selectTextView(send,false);
                GlobalVars.talk(getResources().getString(R.string.okyo));
                break;

            case 4: //GO BACK TO THE PREVIOUS MENU
                GlobalVars.selectTextView(oh,false);

                GlobalVars.selectTextView(send,false);
                GlobalVars.selectTextView(goback,false);GlobalVars.selectTextView(body,true);
                GlobalVars.talk(getResources().getString(R.string.selectyo));
                break;

            case 5 :

                GlobalVars.selectTextView(oh,false);
                GlobalVars.selectTextView(body,false);
                GlobalVars.selectTextView(send,true);
                GlobalVars.selectTextView(goback,false);
                GlobalVars.talk(getResources().getString(R.string.playyo));
                break;

            case 6 :
                GlobalVars.selectTextView(in,false);
                GlobalVars.selectTextView(resultsTextview,false);
                GlobalVars.selectTextView(body,false);
                GlobalVars.selectTextView(send,false);
                GlobalVars.selectTextView(goback,true);
                GlobalVars.talk(getResources().getString(R.string.backToMainMenu));


        }
    }

    public void execute()
    {   int count = 0;
        switch (GlobalVars.activityItemLocation)
        {
            case 1: // LINK

                try
                {
                    stringResults.clear();
                    selectedValue = -1;
                    sr = SpeechRecognizer.createSpeechRecognizer(this);
                    sr.setRecognitionListener(new listener());
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Voice Recognition...");
                    intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,5000000);
                    sr.startListening(intent);
                }
                catch(NullPointerException e)
                {
                    GlobalVars.talk(getResources().getString(R.string.layoutInputVoiceSystemError));
                }
                catch(Exception e)
                {
                    GlobalVars.talk(getResources().getString(R.string.layoutInputVoiceSystemError));
                }
                /*
                try
                {
                    stringResults.clear();
                    selectedValue = -1;
                    sr = SpeechRecognizer.createSpeechRecognizer(this);
                    sr.setRecognitionListener(new yo.listener());
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Voice Recognition...");
                    intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,5000000);
                    sr.startListening(intent);

                    Log.e("yha STRING VALUE", GlobalVars.inputModeResult);
                }
                catch(Exception e)
                {
                    GlobalVars.talk(getResources().getString(R.string.layoutInputVoiceSystemError));
                }

                if (stringResults.size()==0)
                {
                    GlobalVars.talk(getResources().getString(R.string.layoutInputVoiceNoRecognition));
                }

                else
                {
                    Log.e("ha ha bhai","232");
                    if (selectedValue+1==stringResults.size())
                    {
                        selectedValue=-1;
                    }
                    selectedValue = selectedValue + 1;
                    GlobalVars.setText(start, true, getResources().getString(R.string.layoutInputVoicePossibleResults) + stringResults.size() + ")\n" + (selectedValue + 1) + " - " + stringResults.get(selectedValue));
                    GlobalVars.talk(getResources().getString(R.string.layoutInputVoicePossibleResult) + (selectedValue + 1) + ". " + stringResults.get(selectedValue));
                }
                if (selectedValue==-1)
                {
                    GlobalVars.talk(getResources().getString(R.string.layoutInputVoiceNoResultToSelect));
                }
                else
                {

                    GlobalVars.inputModeResult = stringResults.get(selectedValue);
                    //this.finish();
                    //GlobalVars.startActivity(yo.class);
                }

                GlobalVars.inputModeResult = stringResults.get(selectedValue);
                Log.e("VALUE dekho string ki ",GlobalVars.inputModeResult);
                */
                break;

            case 2: //BODY
                if (stringResults.size()==0)
                {
                    GlobalVars.talk(getResources().getString(R.string.layoutInputVoiceNoRecognition));
                }
                else
                {
                    if (selectedValue+1==stringResults.size())
                    {
                        selectedValue=-1;
                    }
                    selectedValue = selectedValue + 1;
                    GlobalVars.setText(resultsTextview, true, getResources().getString(R.string.layoutInputVoicePossibleResults) + stringResults.size() + ")\n" + (selectedValue + 1) + " - " + stringResults.get(selectedValue));
                    GlobalVars.talk(getResources().getString(R.string.layoutInputVoicePossibleResult) + (selectedValue + 1) + ". " + stringResults.get(selectedValue));
                }


                break;

            case 3: //SEND
                if (selectedValue==-1)
                {
                    GlobalVars.talk(getResources().getString(R.string.layoutInputVoiceNoResultToSelect));
                }
                else
                {

                    video = stringResults.get(selectedValue);
                 //   this.finish();
                    //GlobalVars.startActivity(yo.class);

                    if (video != null)
                    {
                        mm=     video;
                        messageBody = GlobalVars.inputModeResult;
                        GlobalVars.setText(body, false, video);
                        GlobalVars.inputModeResult = null;
                        new Thread(){
                            @Override
                            public void run() {
                                YoutubeConnector yc = new YoutubeConnector(yo.this);
                                hash = yc.search(mm);
                                Log.e("fhgv",hash.get(0).getDescription());


                                //GlobalVars.startActivity(carry.class);
                                //
                            }
                        }.start();

                    }
                }
                //GlobalVars.talk(getResources().getString(R.string.selectedyo));
                String s=getResources().getString(R.string.selectedyo);
                GlobalVars.talk(s+mm);
            break;

            case 4:
                /*
                if(count == 0){


                }
                count++;

                */

                if(mm == null)
                {
                    GlobalVars.talk(getResources().getString(R.string.younosel));
                    break;
                }
                // if(hash.size()-1 < 0){
                //   GlobalVars.talk("select again.");
                //       break;
                //  }
                if(j+1>hash.size()-1){
                    j=0;
                }
                else

                {
                    body.setText(hash.get(j).getTitle());
                    GlobalVars.talk(hash.get(j).getTitle());
                    Log.e("ID:-", hash.get(j).getId());
                    j = j + 1;
                }

                break;

            case 5:

                if(j==0){break;}
                if( mm == null )
                {
                    GlobalVars.talk(getResources().getString(R.string.noselection));
                    break;
                }
                new Thread(){
                    @Override
                    public void run() {
                        //YoutubeConnector yc = new YoutubeConnector(yo.this);
                        //hash = yc.search(mm);
                        Log.e("fhgv",hash.get(0).getDescription());
                        GlobalVars.h=hash.get(j-1).getId();

                        GlobalVars.startActivity(carry.class);



                    }
                }.start();

                break;

            case 6:
                this.finish();
                break;
                    //break;
            }
        }

        private void previousItem()
        {
            switch (GlobalVars.activityItemLocation)
            {
                case 2://
                    if(selectedValue - 1 < 0){
                        selectedValue = stringResults.size();
                    }else{
                        selectedValue = selectedValue - 1 ;
                        GlobalVars.setText(resultsTextview, true, getResources().getString(R.string.layoutInputVoicePossibleResults) + stringResults.size() + ")\n" + (selectedValue + 1) + " - " + stringResults.get(selectedValue));
                        GlobalVars.talk(getResources().getString(R.string.layoutInputVoicePossibleResult) + (selectedValue + 1) + ". " + stringResults.get(selectedValue));

                    }
                    break;
                    case 4: //result playlist
                        if(j - 1 < 0){
                            j = hash.size()-1;
                        }else if(j>0){
                            j=j-1;
                            body.setText(hash.get(j).getTitle());
                        }else{
                            j = hash.size() - 1;
                            body.setText(hash.get(j).getTitle());
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

}
