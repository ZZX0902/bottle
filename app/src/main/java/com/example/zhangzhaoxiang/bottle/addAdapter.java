package com.example.zhangzhaoxiang.bottle;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
        import android.content.Intent;
        import android.view.View;
        import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleAdapter;


        import java.util.List;
        import java.util.Map;

public class addAdapter extends SimpleAdapter {
    Dialog dia;
    Context context;

    public addAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.context = context;
        // TODO Auto-generated constructor stub
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View view = super.getView(position, convertView, parent);
        ImageButton btn = (ImageButton) view.findViewById(R.id.btn_add);
        btn.setTag(position);
        btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dia.show();

            }
        });
        dia = new Dialog(context, R.style.edit_AlertDialog_style);
        dia.setContentView(R.layout.add_dialog);
        ImageView imageView = (ImageView) dia.findViewById(R.id.ok);
        //选择true的话点击其他地方可以使dialog消失，为false的话不会消失
        dia.setCanceledOnTouchOutside(true); // Sets whether this dialog is
        Window w = dia.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        lp.y = 20;
        dia.onWindowAttributesChanged(lp);
        imageView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dia.dismiss();
                    }
                });
        return view;
    }
}