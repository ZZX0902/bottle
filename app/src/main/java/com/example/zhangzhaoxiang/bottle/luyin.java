package com.example.zhangzhaoxiang.bottle;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class luyin extends Fragment {
    private TextView textView;
    private Button button;
    private ImageButton btn_control;
    private boolean isStart = false;
    private MediaRecorder mr = null;
    private Chronometer timer;


    private String getMyTime() {
        //存储格式化后的时间
        String time;
        //存储上午下午
        String ampTime = "";
        //判断上午下午，am上午，值为 0 ； pm下午，值为 1
        int apm = Calendar.getInstance().get(Calendar.AM_PM);
        if (apm == 0) {
            ampTime = "上午";
        } else {
            ampTime = "下午";
        }
        //设置格式化格式
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd E " + ampTime + " kk:mm:ss");
        time = format.format(new Date());

        return time;
    }
    private  void showBottomDialog2() {
        //1、使用Dialog、设置style
        final Dialog dialog = new Dialog(getActivity(),R.style.DialogTheme);
        //2、设置布局
        View view = View.inflate(getActivity(),R.layout.dialog_custom_layout2,null);
        dialog.setContentView(view);

        Window window = dialog.getWindow();
        //设置弹出位置
        window.setGravity(Gravity.BOTTOM);
        //设置弹出动画
        window.setWindowAnimations(R.style.main_menu_animStyle);
        //设置对话框大小
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        dialog.findViewById(R.id.use_voice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), choosetime.class);
                startActivity(intent);
            }
        });

        dialog.findViewById(R.id.listen_voice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playaudio();
            }
        });

        dialog.findViewById(R.id.tv_cancel2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });
    }


    private void  startRecord() {
        if (mr == null) {
            File dir = new File(Environment.getExternalStorageDirectory(), "memorybottal");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File soundFile = new File(dir, "memorybottal"+getMyTime()+ ".amr");
            if (!soundFile.exists()) {
                try {
                    soundFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mr = new MediaRecorder();
            mr.setAudioSource(MediaRecorder.AudioSource.MIC);  //音频输入源
            mr.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);   //设置输出格式
            mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);   //设置编码格式
            mr.setOutputFile(soundFile.getAbsolutePath());
            try {
                mr.prepare();
                mr.start();  //开始录制
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void stopRecord() {
        if (mr != null) {
            mr.stop();
            mr.release();
            mr = null;
        }
    }
    private  void playaudio() {
        MediaPlayer player=new MediaPlayer();
        File dir = new File(Environment.getExternalStorageDirectory(), "memorybottal");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            File file = new File(dir, "memorybottal"+getMyTime()+ ".amr");
            FileInputStream fis = new FileInputStream(file);
            player.setDataSource(fis.getFD());
            player.prepare();
            player.start();
        } catch (IllegalArgumentException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //不同的Activity对应不同的布局
        View view = inflater.inflate(R.layout.fragment_luyin, container, false);
        super.onActivityCreated(savedInstanceState);
        timer = (Chronometer) view.findViewById(R.id.timer);
        btn_control = (ImageButton) view.findViewById(R.id.btn_control);
        final TextView tv = (TextView) view.findViewById(R.id.tv_1);
        tv.setText("开始录音");
        btn_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isStart) {
                    startRecord();
                    btn_control.setImageDrawable(getResources().getDrawable(R.drawable.stop));
                    tv.setText("停止录音");
                    timer.setBase(SystemClock.elapsedRealtime());//计时器清零
                    int hour = (int) ((SystemClock.elapsedRealtime() - timer.getBase()) / 1000 / 60);
                    timer.setFormat("0" + String.valueOf(hour) + ":%s");
                    timer.start();

                    isStart = true;
                } else {
                    stopRecord();
                    btn_control.setImageDrawable(getResources().getDrawable(R.drawable.start));
                    tv.setText("开始录音");
                    timer.stop();
                    isStart = false;
                    showBottomDialog2();


                }
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /**
         * TODO 实现底部菜单对应布局控件事件
         * */
    }



}

