package com.videokenplayer;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.videokenplayer.adapter.ListAdapter;
import com.videokenplayer.database.Database;
import com.videokenplayer.database.VideoModel;
import com.videokenplayer.listerners.RecyclersOnItemClickListener;
import com.videokenplayer.utils.Config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements YouTubePlayer.OnInitializedListener, RecyclersOnItemClickListener {


    YouTubePlayer youTubePlayer;

    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;

    Button startBtn;
    //    private AudioRecorder mAudioRecorder;
    private File mAudioFile;
    private Button stopBtn;

    private static final int REQUEST_AUDIO = 0;
    private static final int SPEECH_REQUEST_CODE = 123;

    private static String[] PERMISSIONS_CONTACT = {Manifest.permission.RECORD_AUDIO};
    private View mLayout;
    private String audioSavePathInDevice;

    Random random;
    String randomAudioFileName = "ABCDEFGHIJKLMNOP";

    MediaRecorder mediaRecorder;
    private TextView speech_output;
    private Button nextBtn;

    private Toolbar mToolbar;

    private MenuItem mSearchAction;
    private boolean isSearchOpened = false;
    private EditText edtSeach;

    private MyPlayerStateChangeListener myPlayerStateChangeListener;
    private MyPlaybackEventListener myPlaybackEventListener;
    private int seekTime = 0;
    boolean firsttime = false;

    private RecyclerView mRecyclerView;
    private ListAdapter adapter;

    List<VideoModel> videoList = new ArrayList<>();
    private String videoName = "";
    private MenuItem mMicAction;

    String pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
    private Database db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white));
        setSupportActionBar(mToolbar);

        mLayout = findViewById(R.id.mainlayout);

        db = new Database(MainActivity.this);

//        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
//        youTubeView.initialize(Config.YOUTUBE_API_KEY, this);

        YouTubePlayerSupportFragment frag =
                (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.youtube_fragment);
        frag.initialize(Config.YOUTUBE_API_KEY, this);

        startBtn = (Button) findViewById(R.id.start_btn);
        stopBtn = (Button) findViewById(R.id.stop_btn);
        nextBtn = (Button) findViewById(R.id.next_btn);

        speech_output = (TextView) findViewById(R.id.speachtotext);


        myPlayerStateChangeListener = new MyPlayerStateChangeListener();
        myPlaybackEventListener = new MyPlaybackEventListener();

//        startBtn.setOnClickListener(v -> {
//
//
//            if (ActivityCompat.checkSelfPermission(MainActivity.this,
//                    Manifest.permission.RECORD_AUDIO)
//                    != PackageManager.PERMISSION_GRANTED) {
//
//                // Should we show an explanation?
//                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
//                        Manifest.permission.RECORD_AUDIO)) {
//
//                    // Show an explanation to the user *asynchronously* -- don't block
//                    // this thread waiting for the user's response! After the user
//                    // sees the explanation, try again to request the permission.
//
//                    Snackbar.make(mLayout, R.string.permission_audio_record_rationale,
//                            Snackbar.LENGTH_INDEFINITE)
//                            .setAction("OK", view -> ActivityCompat.requestPermissions(MainActivity.this,
//                                    new String[]{Manifest.permission.RECORD_AUDIO},
//                                    REQUEST_AUDIO))
//                            .show();
//
//                } else {
//
//                    // No explanation needed, we can request the permission.
//
//                    ActivityCompat.requestPermissions(MainActivity.this,
//                            new String[]{Manifest.permission.RECORD_AUDIO},
//                            REQUEST_AUDIO);
//
//                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                    // app-defined int constant. The callback method gets the
//                    // result of the request.
//                }
//            } else {
//                recordAudio();
//            }
//
//        });
//
//        stopBtn.setOnClickListener(v -> {
//
//            mediaRecorder.stop();
//
//            Toast.makeText(MainActivity.this, "Recording Completed", Toast.LENGTH_LONG).show();
//
//
//        });

//        speech_output.setOnClickListener(v1 -> youTubePlayer.loadVideo("fhWaJi1Hsfo", seekTime));

//        nextBtn.setOnClickListener(v -> showGoogleInputDialog());

        if (db.getVideoCount() > 0) {
            videoList.clear();
            videoList.addAll(db.getAllVideos());
        }
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new ListAdapter(this, this, videoList);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(adapter);
    }

    public void showGoogleInputDialog() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Your device is not supported!",
                    Toast.LENGTH_SHORT).show();
        }
    }

//    private void recordAudio() {
//
////        audioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + createRandomAudioFileName(5) + "AudioRecording.3gp";
//
//
//        audioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath() + "/www" + "AudioRecording.mp4";
//
//        mediaRecorderReady();
//
//        try {
//            mediaRecorder.prepare();
//            mediaRecorder.start();
//        } catch (IllegalStateException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//
//        Toast.makeText(MainActivity.this, "Recording started", Toast.LENGTH_LONG).show();
//
//
//    }
//
//
//    public String createRandomAudioFileName(int string) {
//
//        StringBuilder stringBuilder = new StringBuilder(string);
//
//        int i = 0;
//        while (i < string) {
//
//            stringBuilder.append(randomAudioFileName.charAt(random.nextInt(randomAudioFileName.length())));
//
//            i++;
//        }
//        return stringBuilder.toString();
//
//    }
//
//
//    public void mediaRecorderReady() {
//
//        mediaRecorder = new MediaRecorder();
//
//        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//
//        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//
//        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
//
//        mediaRecorder.setOutputFile(audioSavePathInDevice);
//
//    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        if (!wasRestored) {

            youTubePlayer.setPlayerStateChangeListener(myPlayerStateChangeListener);
            youTubePlayer.setPlaybackEventListener(myPlaybackEventListener);

            youTubePlayer.loadVideo("fhWaJi1Hsfo"); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
            this.youTubePlayer = youTubePlayer;
        }


    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            String error = String.format(getString(R.string.player_error), errorReason.toString());
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }


    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return youTubeView;
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case REQUEST_AUDIO: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    // permission was granted, yay! Do the
//                    // audio-related task you need to do.
//                    recordAudio();
//
//                } else {
//
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                    Snackbar.make(mLayout, R.string.permissions_not_granted,
//                            Snackbar.LENGTH_SHORT).show();
//                }
//                return;
//            }
//
//            // other 'case' lines to check for other
//            // permissions this app might request
//        }
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SPEECH_REQUEST_CODE: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//                    speech_output.setText(result.get(0));


                    db.addLocation(new VideoModel(videoName, String.valueOf(seekTime), result.get(0)));

                    if (db.getVideoCount() > 0) {
                        videoList.clear();
                        videoList.addAll(db.getAllVideos());
                        adapter.notifyDataSetChanged();
                    }
                }
                break;
            }

            case RECOVERY_REQUEST: {
                if (resultCode == RESULT_OK && null != data) {

                    getYouTubePlayerProvider().initialize(Config.YOUTUBE_API_KEY, this);
                }
                break;
            }

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(R.id.action_search);
//        mMicAction = menu.findItem(R.id.action_mic);
        return super.onPrepareOptionsMenu(menu);
//        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_mic:
                showGoogleInputDialog();
                return true;
            case R.id.action_search:
                handleMenuSearch();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void handleMenuSearch() {
        ActionBar action = getSupportActionBar(); //get the actionbar

        if (isSearchOpened) { //test if the search is open

            action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
            action.setDisplayShowTitleEnabled(true); //show the title in the action bar

            //hides the keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtSeach.getWindowToken(), 0);

            //add the search icon in the action bar
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_search));

            isSearchOpened = false;
        } else { //open the search entry

            action.setDisplayShowCustomEnabled(true); //enable it to display a
            // custom view in the action bar.
            action.setCustomView(R.layout.search_bar);//add the custom view
            action.setDisplayShowTitleEnabled(false); //hide the title

            edtSeach = (EditText) action.getCustomView().findViewById(R.id.edtSearch); //the text editor

            //this is a listener to do a search when the user clicks on search button
            edtSeach.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    doSearch(v.getText().toString());
                    return true;
                }
                return false;
            });


            edtSeach.requestFocus();

            //open the keyboard focused in the edtSearch
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edtSeach, InputMethodManager.SHOW_IMPLICIT);


            //add the close icon
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_close));

            isSearchOpened = true;
        }
    }

    private void doSearch(String url) {

        Toast.makeText(this, ""+url, Toast.LENGTH_SHORT).show();

        if (url.contains("https://") || url.contains("http://")) {
            Pattern compiledPattern = Pattern.compile(pattern);
            Matcher matcher = compiledPattern.matcher(url);

            if (matcher.find()) {
            //        return matcher.group();
                Log.d("Tag","----video from url ----"+matcher.group());

                youTubePlayer.loadVideo(matcher.group());
            }
        } else {
            youTubePlayer.loadVideo(url); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
        }


    }

    @Override
    public void onItemClick(int position, View v) {


        youTubePlayer.loadVideo(videoList.get(position).get_video(), Integer.parseInt(videoList.get(position).get_time()));
    }


    private final class MyPlaybackEventListener implements YouTubePlayer.PlaybackEventListener {

        String log = "";


        private void updateLog(String prompt) {
            log += "MyPlaybackEventListener" + "\n-" +
                    prompt + "\n\n=====";
            Log.d("Tag", log);
        }

        @Override
        public void onBuffering(boolean arg0) {
            updateLog("onBuffering(): " + String.valueOf(arg0));
        }

        @Override
        public void onPaused() {

            updateLog("onPaused()");

            Log.d("Tag", "-------wwww " + youTubePlayer.getCurrentTimeMillis());

            seekTime = youTubePlayer.getCurrentTimeMillis();

            Toast.makeText(MainActivity.this, "Click Mic icon to record audio", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onPlaying() {
            updateLog("onPlaying()");
        }

        @Override
        public void onSeekTo(int arg0) {
            updateLog("onSeekTo(): " + String.valueOf(arg0));
        }

        @Override
        public void onStopped() {
            updateLog("onStopped()");
        }

    }


    private final class MyPlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener {

        String log = "";

        private void updateLog(String prompt) {
            log += "MyPlayerStateChangeListener" + "\n" +
                    prompt + "\n\n=====";
//            textVideoLog.setText(log);

            Log.d("Tag", log);
        }

        @Override
        public void onAdStarted() {
            updateLog("onAdStarted()");
        }

        @Override
        public void onError(
                com.google.android.youtube.player.YouTubePlayer.ErrorReason arg0) {
            updateLog("onError(): " + arg0.toString());
        }

        @Override
        public void onLoaded(String arg0) {
            videoName = arg0;
            updateLog("onLoaded(): " + arg0);
        }

        @Override
        public void onLoading() {
            updateLog("onLoading()");
        }

        @Override
        public void onVideoEnded() {
            updateLog("onVideoEnded()");
        }

        @Override
        public void onVideoStarted() {
            updateLog("onVideoStarted()");
        }

    }


}
