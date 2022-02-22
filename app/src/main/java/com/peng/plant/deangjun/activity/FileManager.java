package com.peng.plant.deangjun.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.peng.plant.daengjun.R;
import com.peng.plant.deangjun.view.imageView;
import com.peng.plant.deangjun.view.pdfView;


/****************************************************************************************************
<미구현 기능>
1.pdf 경로 읽어와서 실행하기
<버그>
1.이미지 비트맵으로 변환해서 줌확대시 이미지 해상도 낮아지는 부분 -> 변환없이 uri사용하면 해당문제 해결 / 수정하기
<최적화>
1.onpause , onresum 등등 사용해서 어플 버벅임없게 만들기
 ***************************************************************************************************/

public class FileManager extends AppCompatActivity {

    private Button file_read_btn, exit_btn,pdf_view,image_view,url_img_view;
    private View file_read_dialog,url_read_dialog;
    private Animation fadeInAnim_in, fadeInAnim_out,leftAnim_In;
    private long backKeyPressedTime = 0;
    private Toast toast;
    private Boolean file_back,url_back,back_delay;
    private EditText url_adress_In;
    private String http_adress;
    private ImageView preview_img;
    private TextView pre_show_btn,img_open_btn,pre_text_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filemanager_activity);
        initview();

        pdf_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FileManager.this, pdfView.class);
                startActivity(intent);
            }
        });


        file_read_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (file_read_dialog.getVisibility() != View.VISIBLE) {
                    show_dialog(1);
                }

            }
        });

        url_img_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_dialog(2);
                show_dialog(3);

            }
        });

        exit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toast = Toast.makeText(getApplicationContext(), "이용해주셔서 감사합니다", Toast.LENGTH_SHORT);
                showDialog();
            }
        });


        image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FileManager.this, Gallary_activity.class);
                startActivity(intent);
            }
        });

        pre_show_btn.setOnClickListener(Url_voice_input);
        img_open_btn.setOnClickListener(Url_voice_input);

    }
    private View.OnClickListener Url_voice_input = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.preview_show:

                    if(pre_text_btn.getVisibility() == View.VISIBLE) {
                        pre_text_btn.setVisibility(View.GONE);
                        show_dialog(5);
                        http_adress = url_adress_In.getText().toString();
                        Glide.with(getApplicationContext())
                                .load(http_adress)
                                .into(preview_img);
                    }
                    break;

                case R.id.img_opne:
                    http_adress = url_adress_In.getText().toString();
                    Intent intent = new Intent(FileManager.this, imageView.class);
                    intent.putExtra("urlImage",http_adress);
                    intent.putExtra("urlchoce","two");
                    startActivity(intent);
                    break;
            }
        }
    };


    private void show_dialog(int dialog_num) {

        switch (dialog_num) {
            case 1:
                file_read_dialog.setVisibility(View.VISIBLE);
                file_read_dialog.startAnimation(fadeInAnim_in);
                file_back = false;
                break;
            case 2:
                file_read_dialog.startAnimation(fadeInAnim_out);
                file_read_dialog.setVisibility(View.GONE);
                break;
            case 3:
                url_read_dialog.setVisibility(View.VISIBLE);
                url_read_dialog.startAnimation(leftAnim_In);
                url_back = false;
                back_delay = false;
                break;
            case 4:
                url_read_dialog.startAnimation(fadeInAnim_out);
                url_read_dialog.setVisibility(View.GONE);
                preview_img.startAnimation(fadeInAnim_out);
                if(preview_img.getVisibility() == View.VISIBLE) {
                    preview_img.setVisibility(View.GONE);
                    pre_text_btn.setVisibility(View.VISIBLE);
                }
                break;
            case 5:
                preview_img.setVisibility(View.VISIBLE);
                preview_img.startAnimation(fadeInAnim_in);
                break;

        }
    }


    private void initview() {
        file_back = true;
        url_back = true;
        back_delay = true;
        file_read_btn = (Button) findViewById(R.id.file_read);
        url_img_view = (Button) findViewById(R.id.Url_image_viewer);
        exit_btn = (Button) findViewById(R.id.view_exit);
        pdf_view = (Button) findViewById(R.id.pdf_viewer);
        image_view = (Button) findViewById(R.id.image_viewer);
        pre_show_btn =(TextView) findViewById(R.id.preview_show);
        img_open_btn =(TextView) findViewById(R.id.img_opne);
        pre_text_btn =(TextView) findViewById(R.id.preview_text);

        preview_img = (ImageView) findViewById(R.id.preview_img);
        file_read_dialog = (View) findViewById(R.id.file_read_open);
        url_read_dialog = (View) findViewById(R.id.url_read_open);
        url_adress_In = (EditText) findViewById(R.id.url_adress);
//        mlayouts = (RelativeLayout) findViewById(R.id.url_dialog_layout);
        fadeInAnim_in = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeInAnim_in = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeInAnim_out = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        leftAnim_In =AnimationUtils.loadAnimation(this, R.anim.left_in);
    }

    private void exitProgram() {
        if (Build.VERSION.SDK_INT >= 21) {
            // 액티비티 종료 + 태스크 리스트에서 지우기
            finishAndRemoveTask();
        } else {
            // 액티비티 종료
            finish();
        }
        System.exit(0);
    }

    private void showDialog() {
        AlertDialog.Builder msgBuilder = new AlertDialog.Builder(FileManager.this)
                .setTitle("종료하시겠습니까?").setMessage("버그 및 불편사항 문의는 WATT 공식 홈페이지 참조")
                .setPositiveButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setNegativeButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        toast.show();
                        exitProgram();

                    }
                });
        AlertDialog msgDlg = msgBuilder.create();
        msgDlg.show();
    }

    @Override
    public void onBackPressed() {
        Log.d("ㅇㄴㅇㄴ", "onBackPressed: 뒤로?");

        if (url_read_dialog.getVisibility() == View.VISIBLE) {
            show_dialog(4);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    url_back = true;
                    back_delay = true;
                }
            }, 500);
        }


        if (back_delay&&file_read_dialog.getVisibility() == View.VISIBLE) {
            show_dialog(2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    file_back = true;
                }
            }, 500);
        }




        if (url_back&&file_back && System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
            toast.cancel();
        }


    }
}