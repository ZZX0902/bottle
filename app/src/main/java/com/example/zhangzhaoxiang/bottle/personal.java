package com.example.zhangzhaoxiang.bottle;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

public class personal extends AppCompatActivity {
    private Boolean b_sub_square = false;
private ImageButton back;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        back = (ImageButton) findViewById(R.id.back_personal);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent show=new Intent(personal.this,home.class);
                show.putExtra("id",1);
                startActivity(show);
                finish();
            }
        });

        final Button btn_out= (Button) findViewById(R.id.out);
        btn_out.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!b_sub_square) {
                    b_sub_square = true;
                    //设置是否被激活状态，true表示被激活
                    btn_out.setActivated(b_sub_square);
                } else {
                    b_sub_square = false;
                    //设置是否被激活状态，false表示未激活
                    btn_out.setActivated(b_sub_square);
                }
                AlertDialog alertDialog = new AlertDialog.Builder(personal.this)
                        .setTitle("确认登出")
                        .setMessage("登出后不会删除任何历史数据，下次登陆依然可以使用本账号。")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(personal.this, MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                return;
                            }
                        }).create();
                alertDialog.show();
            }
        });
    }
}
