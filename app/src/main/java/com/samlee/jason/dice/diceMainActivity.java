package com.samlee.jason.dice;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

public class diceMainActivity extends AppCompatActivity {

    Random rand = new Random();
    int currentImageNum;
    int totalNum = 0 ;
    int rollTime = 1;
    int locator = 0;
    ArrayList recentNum = new ArrayList();

    // this onCreateOptionsMenu is  used to show the element on action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dices_main_activity_actions, menu);
        return true;
    }

//    Handler handler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dice_main);

        //this is the initial dice image, show this first before starting
        final ImageView diceinitImage = (ImageView) findViewById(R.id.diceOne);

        // this is for background , set it as invisable first
//        final ImageView background = (ImageView) findViewById(R.id.background);
//        background.setVisibility(View.INVISIBLE);
        final VideoView firework = (VideoView) findViewById(R.id.firework);
        firework.setVisibility(View.INVISIBLE);

        // this is the bar for point and roll times shown
        final TextView point = (TextView) findViewById(R.id.totalPoint);

        // button to clear the point and roll times
        final Button clearButton = (Button) findViewById(R.id.clearButton);
        final Button diceButton = (Button) findViewById(R.id.moreDice);

        //cleant the arraylist
        recentNum.clear();
        //register the dice action

        // create a sound for dice roll
        final SoundPool diceSoundPool;
        final SoundPool bonusSoundPool;
        final SoundPool clearSoundPool;

        // Create a sound pool
        if (Build.VERSION.SDK_INT <= 15) {
            //API level 15
            diceSoundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
            bonusSoundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
            clearSoundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);

        } else {
            //API level 21
            diceSoundPool = new SoundPool.Builder().setMaxStreams(10).build();
            bonusSoundPool = new SoundPool.Builder().setMaxStreams(10).build();
            clearSoundPool = new SoundPool.Builder().setMaxStreams(10).build();
        }

        final int diceSound;
        final int bonusSound;
        final int clearSound;

        diceSound = diceSoundPool.load(this , R.raw.coin,1);
        bonusSound = bonusSoundPool.load(this , R.raw.lync_videocall,1);
        clearSound = clearSoundPool.load(this , R.raw.windows_notify,1);

        diceinitImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            // catch the touch event here , only down event will be caught.
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        //play the dice roll music
                        diceSoundPool.play(diceSound,1,1,0,0,1);
                        // this is used for frame action
                        AnimationDrawable anim = new AnimationDrawable();
                        for (int i = 1; i <= 3 ; i++) {
                            try {

                                // pick up 6 dices randomly
                                currentImageNum = rand.nextInt(6) + 1;
                                InputStream stream = getResources().getAssets().open("dice" + currentImageNum + ".png");
                                Drawable d = Drawable.createFromStream(stream, null);
                                anim.addFrame(d, 90);

                            }catch (IOException e){
                                e.printStackTrace();
                            }
                        }

                        anim.setOneShot(true);
                        diceinitImage.setImageDrawable(anim);
                        anim.start();

                        // this is for roate action
                        RotateAnimation rotate = new RotateAnimation(0f, 360f, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                        rotate.setDuration(280);
                        rotate.setFillAfter(false);
                        diceinitImage.startAnimation(rotate);

                        // catch the point and store into the array
                        totalNum = totalNum + currentImageNum;
                        recentNum.add(currentImageNum);


                        // if 3 dices were rolled at continue 3 times, 10 additional bonus will be added
                        if(locator >= 3 && (recentNum.get(locator - 2).equals(recentNum.get(locator - 1)) && recentNum.get(locator - 2).equals(recentNum.get(locator)) )){
                            Toast showCheer = Toast.makeText(getApplicationContext(), "Cheer! , 10 bonus additional ", Toast.LENGTH_LONG);
                            showCheer.show();
                            totalNum = totalNum +10;
                            bonusSoundPool.play(diceSound,1,1,0,0,1);

                            firework.setVisibility(View.VISIBLE);

                            Uri rawUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.fireworks1c);
                            firework.setVideoURI(rawUri);
                            firework.start();
                            firework.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    Toast Cheer = Toast.makeText(getApplicationContext(), "Cheer! , 10 bonus additional ", Toast.LENGTH_LONG);
                                    Cheer.show();

                                    firework.setVisibility(View.GONE);
                                }
                            });
                        }
                        locator++;
//                        Log.i("locator:", ""+locator);

                        point.setText("Total bonus : " + totalNum + "\n\nRolled times: " + rollTime++);
                        break;
                    // We can add more action event here
                    case MotionEvent.ACTION_UP:
//                        diceinitImage.setBackgroundColor(0);
                        break;
                }

                return false;
            }
        });

        // clear button; clear the bonus and initial dice start screen
        clearButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        totalNum = 0;
                        rollTime = 1;
                        locator = 0;
                        recentNum.clear();
                        try {
                            // show the initial image
                            InputStream initStream = getAssets().open("diceinit.png");
                            Drawable dInit = Drawable.createFromStream(initStream,null);
                            diceinitImage.setImageDrawable(dInit);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // play the clear button sound
                        clearSoundPool.play(clearSound,1,1,0,0,1);
                        point.setText("Total points : 0" + "\n\nRolled times: 0" );

                        // add the motion for clear button
                        clearButton.setBackgroundColor(0x99000);
                        break;

                    // add the motion for clear button with above codes
                    case MotionEvent.ACTION_UP:
                        clearButton.setBackgroundResource(R.drawable.dice0);
                        break;
                }

                return false;
            }
        });

        // handler for more dice button
        diceButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        totalNum = 0;
                        rollTime = 1;
                        locator = 0;
                        recentNum.clear();
                        try {
                            // show the initial image
                            InputStream initStream = getAssets().open("diceinit.png");
                            Drawable dInit = Drawable.createFromStream(initStream,null);
                            diceinitImage.setImageDrawable(dInit);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // TODO  we will replace this button sound
                        clearSoundPool.play(clearSound,1,1,0,0,1);
                        diceButton.setBackgroundColor(0x99000);

                        // open the new activity for more dice

                        Intent moreDiceIntent = new Intent(diceMainActivity.this, dicesActivity.class);
                        startActivity(moreDiceIntent);

                        break;

                    // add the motion for more dice button with above codes
                    case MotionEvent.ACTION_UP:
                        diceButton.setBackgroundResource(R.drawable.dice0);
                        break;
                }

                return false;
            }
        });

    }


    public void diceClickHandler(MenuItem item) {

//         open about activity
        Intent infoIntent = new Intent(diceMainActivity.this, infoActivity.class);
        startActivity(infoIntent);

    }

}

