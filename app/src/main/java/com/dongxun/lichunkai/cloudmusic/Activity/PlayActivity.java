package com.dongxun.lichunkai.cloudmusic.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dongxun.lichunkai.cloudmusic.Common.Common;
import com.dongxun.lichunkai.cloudmusic.R;
import com.gyf.immersionbar.ImmersionBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private TextView textView_name;
    private TextView textView_author;
    private ImageView imageView_close;
    private ImageView imageView_coverImg;
    private SeekBar seekBar;
    private ImageView imageView_lastSong;
    private ImageView imageView_playOrPause;
    private ImageView imageView_nextSong;
    private ImageView imageView_like;
    private ImageView imageView_loop;
    private ImageView imageView_comments;
    private ImageView imageView_list;
    private TextView textView_nowTime;
    private TextView textView_sumTime;

    //其他
    private LocalBroadcastManager localBroadcastManager;
    private TimeReceiver timeReceiver;
    private IntentFilter intentFilter;

    private String TAG = "PlayActivity";

    private Boolean updateSeekbar = true;//是否更新进度条，用户自行调整进度时使用


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        initStateBar();
        initView();
        initReceiver();
    }

    /**
     * 初始化状态栏
     */
    private void initStateBar() {
        ImmersionBar.with(this).init();
        getSupportActionBar().hide();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    /**
     * 更新UI
     */
    private void refreshUI() {
        textView_name.setText(Common.song_playing.getName());
        textView_author.setText(Common.song_playing.getArtist());
        if (Common.song_playing.getCover() != null){
            //显示封面
            imageView_coverImg.setImageBitmap(Common.song_playing.getCover());
        }else {
            //加载封面
            if (!(Common.song_playing.getCoverURL() == null))getCoverImage(Common.song_playing.getCoverURL());
        }
        //加载歌词
        if (!(Common.song_playing.getId() == null))getLyric(Common.song_playing.getId());

        if (Common.state_playing) imageView_playOrPause.setImageResource(R.drawable.logo_pause);
        else imageView_playOrPause.setImageResource(R.drawable.logo_play);
        textView_sumTime.setText(generateTime(Common.song_playing.getSunTime()));
        textView_nowTime.setText(generateTime(Common.song_playing.getNowTime()));
    }


    /**
     * 将毫秒转时分秒
     * @param time
     * @return
     */
    public static String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onResume() {
        refreshUI();
        super.onResume();
    }

    /**
     * 加载网络图片，获取网络图片的bitmap
     * @param url：网络图片的地址
     * @return
     */
    public void getCoverImage(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bitmap = null;
                    URL myurl = new URL(url);
                    // 获得连接
                    HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
                    conn.setConnectTimeout(6000);//设置超时
                    conn.setDoInput(true);
                    conn.setUseCaches(false);//不缓存
                    conn.connect();
                    InputStream is = conn.getInputStream();//获得图片的数据流
                    bitmap = BitmapFactory.decodeStream(is);
                    //修改公共变量和更新UI
                    Common.song_playing.setCover(bitmap);
                    //切换主线程更新UI
                    imageView_coverImg.post(new Runnable() {
                        @Override
                        public void run() {
                            imageView_coverImg.setImageBitmap(Common.song_playing.getCover());
                        }
                    });
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 加载歌词
     * @param id：歌曲ID
     */
    public void getLyric(final String id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();//新建一个OKHttp的对象
                    //和风请求方式
                    Request request = new Request.Builder()
                            .url("https://api.imjad.cn/cloudmusic/?type=lyric&id="+ id +"")
                            .build();//创建一个Request对象
                    //第三步构建Call对象
                    Call call = client.newCall(request);
                    //第四步:异步get请求
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String responseData = response.body().string();//处理返回的数据
                            //解析歌词
                            try {
                                String lycir = new JSONObject(responseData).getJSONObject("lrc").getString("lyric");
                                Common.song_playing.setLyric(lycir);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //显示歌词
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 初始化组件
     */
    private void initView() {
        textView_name = findViewById(R.id.textView_name);
        textView_author = findViewById(R.id.textView_author);
        imageView_close = findViewById(R.id.imageView_close);
        imageView_close.setOnClickListener(this);
        imageView_coverImg = findViewById(R.id.imageView_coverImg);
        imageView_coverImg.setOnClickListener(this);
        textView_nowTime = findViewById(R.id.textView_nowTime);
        textView_sumTime = findViewById(R.id.textView_sumTime);
        seekBar = findViewById(R.id.seekBar);
        seekBar.setMax(Common.song_playing.getSunTime());
        seekBar.setProgress(Common.song_playing.getNowTime());
        seekBar.setOnSeekBarChangeListener(this);
        imageView_lastSong = findViewById(R.id.imageView_lastSong);
        imageView_lastSong.setOnClickListener(this);
        imageView_playOrPause = findViewById(R.id.imageView_playOrPause);
        imageView_playOrPause.setOnClickListener(this);
        imageView_nextSong = findViewById(R.id.imageView_nextSong);
        imageView_nextSong.setOnClickListener(this);
        imageView_like = findViewById(R.id.imageView_like);
        imageView_like.setOnClickListener(this);
        imageView_loop = findViewById(R.id.imageView_loop);
        imageView_loop.setOnClickListener(this);
        imageView_comments = findViewById(R.id.imageView_comments);
        imageView_comments.setOnClickListener(this);
        imageView_list = findViewById(R.id.imageView_list);
        imageView_list.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imageView_close:
                finish();
                break;
            case R.id.imageView_coverImg:
                Toast.makeText(this,"封面",Toast.LENGTH_SHORT).show();
                break;
            case R.id.imageView_lastSong:
                Toast.makeText(this,"上一曲",Toast.LENGTH_SHORT).show();
                break;
            case R.id.imageView_playOrPause:
                Toast.makeText(this,"播放/暂停",Toast.LENGTH_SHORT).show();
                //发送本地广播播放
                Intent intent_broadcast = new Intent("com.dongxun.lichunkai.cloudmusic.MUSIC_BROADCAST");
                intent_broadcast.putExtra("ACTION","PLAY_PAUSE");
                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
                localBroadcastManager.sendBroadcast(intent_broadcast);
                //更新UI
                if (Common.state_playing){
                    //暂停
                    imageView_playOrPause.setImageResource(R.drawable.logo_play);
                }else {
                    //播放
                    imageView_playOrPause.setImageResource(R.drawable.logo_pause);
                }
                break;
            case R.id.imageView_nextSong:
                Toast.makeText(this,"下一曲",Toast.LENGTH_SHORT).show();
                break;
            case R.id.imageView_like:
                Toast.makeText(this,"收藏",Toast.LENGTH_SHORT).show();
                break;
            case R.id.imageView_loop:
                Toast.makeText(this,"循环",Toast.LENGTH_SHORT).show();
                break;
            case R.id.imageView_comments:
                Toast.makeText(this,"评论",Toast.LENGTH_SHORT).show();
                break;
            case R.id.imageView_list:
                Toast.makeText(this,"歌单",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * 初始化本地广播接收器
     */
    private void initReceiver() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.dongxun.lichunkai.cloudmusic.TIME_BROADCAST");
        timeReceiver = new TimeReceiver();
        localBroadcastManager.registerReceiver(timeReceiver,intentFilter);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch (seekBar.getId()){
            case R.id.seekBar:
                if (!updateSeekbar)textView_nowTime.setText(generateTime(Long.valueOf(seekBar.getProgress())));
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        switch (seekBar.getId()){
            case R.id.seekBar:
                Log.d(TAG, "onStartTrackingTouch: 开始");
                updateSeekbar = false;
                break;
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        switch (seekBar.getId()){
            case R.id.seekBar:
                Log.d(TAG, "onStartTrackingTouch: 结束");
                updateSeekbar = true;
                //调整歌曲进度
                Common.changeProgress = Integer.valueOf(seekBar.getProgress());
                //发送广播
                Intent intent_broadcast = new Intent("com.dongxun.lichunkai.cloudmusic.MUSIC_BROADCAST");
                intent_broadcast.putExtra("ACTION","CHANGEPROGRESS");
                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
                localBroadcastManager.sendBroadcast(intent_broadcast);
                break;
        }
    }

    /**
     * 刷新时间接收器
     */
    public class TimeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra("ACTION");
            switch (action){
                case "REFRESH":
                    //更新UI
                    textView_sumTime.setText(generateTime(Common.song_playing.getSunTime()));
                    if (updateSeekbar) textView_nowTime.setText(generateTime(Common.song_playing.getNowTime()));
                    if (Common.state_playing) imageView_playOrPause.setImageResource(R.drawable.logo_pause);
                    else imageView_playOrPause.setImageResource(R.drawable.logo_play);
                    seekBar.setMax(Common.song_playing.getSunTime());
                    if (updateSeekbar) seekBar.setProgress(Common.song_playing.getNowTime());
                    break;
                case "COMPLETE":
                    imageView_playOrPause.setImageResource(R.drawable.logo_play);
                    if (updateSeekbar) textView_nowTime.setText(generateTime(Common.song_playing.getNowTime()));
                    if (updateSeekbar) seekBar.setProgress(Common.song_playing.getNowTime());
                    break;
            }
        }
    }

}
