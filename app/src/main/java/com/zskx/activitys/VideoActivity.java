package com.zskx.activitys;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;
import com.example.hrv.startpack.R;
import com.zskx.util.Hrv;
import com.zskx.util.Config;

import static com.zskx.util.TimeUtils.formatTime;
import static com.zskx.util.TimeUtils.formatTurnSecond;

/**
 * 视频播放页面
 */
public class VideoActivity extends AppCompatActivity {

    private VideoView videoView;//显示视频的控件
    private AudioManager audioManager = null;//视频播放管理器
    private AlertDialog alertDialog1;//弹窗
    private Hrv hrv;//hrv监听类
    private Hrv hrvs;//hrv监听类
    private boolean lean = true;//数据是否已经上传
    private boolean leans = true;//数据是否已经上传
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_video);
        //业务实现
        init();
    }

    /**
     * 业务实现
     */
    private void init() {
        //得到视频管理器对象
        audioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
        //初始化组件
        videoView = findViewById(R.id.videos);
        //设置视频播放控制器
        videoView.setMediaController(new MediaController(this));
        //视频播放路径
        videoView.setVideoPath(Config.VIDEO_PATH);
        //videoView.setVideoPath("http://"+Config.IP+"/video/smmx.mp4");
        //延时加载视频,缓解页面跳转卡顿问题
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                    videoView.requestFocus();
                    videoView.start();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        //播放错误监听
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                videoView.stopPlayback();
                return true;
            }
        });
        //视频播放完成监听
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //视频播放完成后,弹窗提示
                alertDialog1 = new AlertDialog.Builder(VideoActivity.this)
                        .setMessage(R.string.testnext)
                        .create();
                alertDialog1.show();
                //启动定时器,3秒后跳转
                countDownTimer.start();
            }
        });
        //视频装载完成监听
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                initHrv();
                initHrv2();
            }
        });

    }
    /**
     * hrv监测1段
     */
    private void initHrv() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    hrv = new Hrv(VideoActivity.this,0);
                    hrv.startHrv();
                    for (int i = 0; i <= Config.TIME; i++) {
                        int currents = formatTurnSecond(formatTime(videoView.getCurrentPosition()));
                        Thread.sleep(1000);
                        if (lean) {
                            if(currents >= Config.TIME || !lean){
                                hrv.stopHrv();
                                lean = false;
                                break;
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    /**
     * hrv监测2段
     */
    public void initHrv2(){
        String hrvTime = formatTime(videoView.getDuration());
        final int hrvTimes = Integer.parseInt(String.valueOf(formatTurnSecond(hrvTime)))-Config.TIME;
        final int allTime = Integer.parseInt(String.valueOf(formatTurnSecond(hrvTime)));

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean toggle = false;
                try {
                    hrvs = new Hrv(VideoActivity.this,1);
                    for (int i = 0; i < allTime; i++) {
                        int current = formatTurnSecond(formatTime(videoView.getCurrentPosition()));
                        Thread.sleep(1000);
                        if (current >= hrvTimes && !toggle) {
                            hrvs.startHrv();
                            toggle= true;
                        }
                        //alltime-10是提前10秒提交数据
                        if(current >= allTime-10 || !leans){
                            hrvs.stopHrv();
                            break;
                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    /**
     * 定时器
     */
    private CountDownTimer countDownTimer = new CountDownTimer(3000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            if (alertDialog1 != null) {
                alertDialog1.dismiss();
            }
            Intent intent = new Intent(VideoActivity.this, WebActivity.class);
            VideoActivity.this.startActivity(intent);
            finish();
        }
    };

    /**
     * 返回键
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void finish() {
        if (videoView != null) {
            videoView.suspend();
        }
        if (alertDialog1 != null) {
            alertDialog1.dismiss();
        }
        lean = false;
        leans = false;
        super.finish();
    }
    @Override
    public void onPause() {
        if (videoView != null) {
            videoView.suspend();
        }
        if (alertDialog1 != null) {
            alertDialog1.dismiss();
        }
        lean = false;
        leans = false;
        super.onPause();
    }
}
