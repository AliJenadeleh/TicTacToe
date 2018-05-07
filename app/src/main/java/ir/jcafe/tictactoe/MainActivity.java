package ir.jcafe.tictactoe;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;

import ir.jcafe.tictactoe.TicTacToe.TicTacToeProvider;

public class MainActivity extends AppCompatActivity {

    private int hours = 0,minutes = 0,seconds = 0;
    private boolean keepTimer = false;

    private Toolbar tbMain;

    private TableLayout tableLayout;

    private Switch switchHard;

    private TextView lblWinner,lblTimer;

    private TicTacToeProvider ticTacToe;

    private TicTacToeProvider.IMyRunnable runnable;

    private Handler timehandler;

    private MediaPlayer mediaPlayer;

    private int[][] idArray = {
             {R.id.btn1,R.id.btn2,R.id.btn3}
            ,{R.id.btn4,R.id.btn5,R.id.btn6}
            ,{R.id.btn7,R.id.btn8,R.id.btn9}};

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         boolean tst = super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.menuMain) {

            try {
                Bitmap bmp = getBitmap();
                File file = saveBitmap(bmp);
                SendCaste(file);
            }
            catch (Exception e){
                Toast.makeText(this,"Error",Toast.LENGTH_LONG).show();
            }
            return true;
        }

        return tst;
    }

    private Bitmap getBitmap(){
        tableLayout.invalidate();
        tableLayout.buildDrawingCache();
        return tableLayout.getDrawingCache();
    }

    private File saveBitmap(Bitmap bmp)throws IOException{

        File dir = this.getExternalFilesDir(null);
        File of = new File(dir,"image.jpg");

        if(of.exists())
            of.delete();

        FileOutputStream fo = new FileOutputStream(of);
        bmp.compress(Bitmap.CompressFormat.JPEG,100,fo);
        fo.flush();
        fo.close();

        return of;
    }

    private void SendCaste(File resource){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("Image/jpg");
        Uri uri = Uri.fromFile(resource);
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        startActivity(Intent.createChooser(intent,"نتیجه بازی"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menumain,menu);
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int subColor = getColorByResource(R.color.colorAccent);

        tbMain = (Toolbar) findViewById(R.id.tbMain);
            tbMain.setTitle(R.string.title);
            tbMain.setSubtitle(R.string.subtitle);

            tbMain.setSubtitleTextColor(subColor);

        setSupportActionBar(tbMain);

        tableLayout = (TableLayout) findViewById(R.id.tablePlay);

        switchHard = (Switch) findViewById(R.id.switchHard);

        lblWinner = (TextView) findViewById(R.id.lblWiner);

        lblTimer = (TextView) findViewById(R.id.lblTimer);

        mediaPlayer = MediaPlayer.create(this,R.raw.bgsound);

        setOnGameEnd();

        resetAll();

        PlayMusic();

        StartTimer();
    }

    private int getColorByResource(int resource){
        if(Build.VERSION.SDK_INT >= 21){
            return ContextCompat.getColor(this,resource);
        }
         return getResources().getColor(resource);
    }

    private void setOnGameEnd(){
        runnable = new TicTacToeProvider.IMyRunnable() {
            @Override
            public void Run(String playerName) {
                lblWinner.setVisibility(View.VISIBLE);
                lblWinner.setText(playerName);
                tableLayout.animate().setDuration(300).alpha(0.3f);
                StopMusic();
                StopTimer();
            }
        };
    }

    public void btnExit_OnClick(View view) {
        System.exit(0);
    }

    public void btnReset_OnClick(View view) {
        reset();
    }

    private void reset(){

        lblWinner.setVisibility(View.GONE);
        tableLayout.setRotation(-360);
        tableLayout.animate().setDuration(800).rotation(360);
        tableLayout.animate().alpha(1f);
        resetAll();

        PlayMusic();

        StartTimer();
    }

    private void resetAll(){

        boolean ishard = switchHard.isChecked();

        if(ticTacToe == null)
         ticTacToe = new TicTacToeProvider(this,idArray,runnable,ishard);
        else
            ticTacToe.reset(this,ishard);
    }

    public void SwitchHard_OnClick(View view) {
        boolean ishard = switchHard.isChecked();
        String message ;
        if(ishard)
            message = "حالت سخت فعال شده";
        else
        message = "حالت سخت غیر فعال شد";

        Snackbar.make(view,message,Snackbar.LENGTH_SHORT).show();
        reset();
    }

    private void StopMusic(){
        if(mediaPlayer != null && mediaPlayer.isPlaying())
            mediaPlayer.stop();
    }

    private void PlayMusic(){
        if(mediaPlayer == null || !mediaPlayer.isPlaying()) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bgsound);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    PlayMusic();
                }
            });
            mediaPlayer.start();
        }
    }

    private void StartTimer(){

        seconds = 0;
        minutes = 0;
        hours = 0;
        keepTimer = true;
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(keepTimer) {
                    timeInc();
                    timehandler.postDelayed(this, 1000);
                }
            }
        };

        timehandler = new Handler();
        timehandler.post(runnable);
    }

    private void StopTimer(){
        keepTimer = false;
    }

    private void timeInc() {
     seconds++;
        if(seconds >= 60) {
            seconds = 0;
            minutes++;
        }
        if(minutes >= 60)
        {
            minutes = 0;
            seconds++;
        }


        String time = String.format("%1s:%2s:%3s",hours,minutes,seconds);
        lblTimer.setText(time);
        // display time
    }
}
