package com.peng.plant.deangjun.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.peng.plant.deangjun.listener.TiltScrollController;
import com.peng.plant.deangjun.utils.ZoomControll;
import com.peng.plant.daengjun.R;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;


public class imageView extends AppCompatActivity implements TiltScrollController.ScrollListener {

    private static final String TAG = "daengjundl";
    private ImageView happy_img;
    private RelativeLayout container;
    private Uri uri;
    private Bitmap bitmap;
    private String urladress;
    private Animation fadeInAnim_in, fadeInAnim_out;
    private TiltScrollController mTiltScrollController;
    private Button zoomlevel_1, zoomlevel_2, zoomlevel_3, zoomlevel_4, zoomlevel_5, display_stop_img;
    private Boolean display_move_control = true;
    private TextView display_move_on, display_move_off, zoom_control1, zoom_control2, zoom_control3, zoom_control4, zoom_control5, next_btn, before_btn;
    private ZoomControll zoomControll;
    private float zoomlevel_choice;
    private String image_choice;
    private int img_order, iv_position;
    private ArrayList<Uri> pics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //전체화면으로 나오게만들기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_image_view);


        mTiltScrollController = new TiltScrollController(getApplicationContext(), this);

        image_choice = getIntent().getStringExtra("uridata");

        if (image_choice == null) {
            image_choice = getIntent().getStringExtra("urlchoce");
        }
        pics = (ArrayList<Uri>) getIntent().getSerializableExtra("imglist");
        iv_position = getIntent().getIntExtra("img_position", 0);
        img_order = iv_position;
        uri = getIntent().getParcelableExtra("imageUri");
        urladress = getIntent().getStringExtra("urlImage");
        initView();

        View v = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.zoom_item, null, false);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        happy_img = v.findViewById(R.id.image_test);

        if (image_choice.equals("one")) {
            setImage();
        }
        imageload();

        zoomControll = new ZoomControll(this);
        zoomControll.addView(v);
        zoomControll.setLayoutParams(layoutParams);
        zoomControll.setMiniMapEnabled(true); //좌측 상단에 미니맵 설정
        zoomControll.setMaxZoom(7f);// 줌 Max 배율 설정  1f 로 설정하면 줌 안됩니다.
        zoomControll.setMiniMapHeight(200); //미니맵 크기지정
        container.addView(zoomControll);

        display_move_on.setOnClickListener(Tiltcontroll_voice_input);
        display_move_off.setOnClickListener(Tiltcontroll_voice_input);
        zoom_control1.setOnClickListener(Tiltcontroll_voice_input);
        zoom_control2.setOnClickListener(Tiltcontroll_voice_input);
        zoom_control3.setOnClickListener(Tiltcontroll_voice_input);
        zoom_control4.setOnClickListener(Tiltcontroll_voice_input);
        zoom_control5.setOnClickListener(Tiltcontroll_voice_input);
        next_btn.setOnClickListener(Tiltcontroll_voice_input);
        before_btn.setOnClickListener(Tiltcontroll_voice_input);

    }

    private View.OnClickListener Tiltcontroll_voice_input = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.display_move:
                    display_move_control = true;
                    display_stop_img.startAnimation(fadeInAnim_out);
                    display_stop_img.setVisibility(View.GONE);
                    break;

                case R.id.display_stop:
                    display_move_control = false;
                    display_stop_img.setVisibility(View.VISIBLE);
                    display_stop_img.startAnimation(fadeInAnim_in);
                    break;

                case R.id.zoomlevel_voice1:
                    zoomlevelcheck(1f);
                    break;
                case R.id.zoomlevel_voice2:
                    zoomlevelcheck(2f);
                    break;
                case R.id.zoomlevel_voice3:
                    zoomlevelcheck(3f);
                    break;
                case R.id.zoomlevel_voice4:
                    zoomlevelcheck(4f);
                    break;
                case R.id.zoomlevel_voice5:
                    zoomlevelcheck(5f);
                    break;
                case R.id.next_img:
                    if (image_choice.equals("one")) {
                        Log.d(TAG, "onClick: pics.size() : "+pics.size());


                            img_order = img_order + 1;

                        if (img_order < pics.size()) {
                            uri = pics.get(img_order);
                            setImage();
                            imageload();
                            if (zoomlevel_choice != 1) {
                                zoomlevelcheck(1);
                            }
                            if (display_move_control == false) {
                                display_stop_img.startAnimation(fadeInAnim_out);
                                display_stop_img.setVisibility(View.GONE);
                                display_move_control = true;
                            }
                        } else {
                            img_order = pics.size();
                        }
                    }
                    break;
                case R.id.before_img:

                    if (image_choice.equals("one")) {
                        if(img_order==pics.size()){
                            img_order = img_order - 2;
                        }else {
                            img_order = img_order - 1;
                        }
                        if (img_order > -1) {
                            uri = pics.get(img_order);
                            setImage();
                            imageload();
                            if (zoomlevel_choice != 1) {
                                zoomlevelcheck(1);
                            }
                            if (display_move_control == false) {
                                display_stop_img.startAnimation(fadeInAnim_out);
                                display_stop_img.setVisibility(View.GONE);
                                display_move_control = true;
                            }
                        } else {
                            img_order = 0;
                        }
                    }
                    break;

            }
        }
    };

    private void initView() {
        container = (RelativeLayout) findViewById(R.id.container);
        zoomlevel_1 = (Button) findViewById(R.id.zoomlevel_1btn);
        zoomlevel_2 = (Button) findViewById(R.id.zoomlevel_2btn);
        zoomlevel_3 = (Button) findViewById(R.id.zoomlevel_3btn);
        zoomlevel_4 = (Button) findViewById(R.id.zoomlevel_4btn);
        zoomlevel_5 = (Button) findViewById(R.id.zoomlevel_5btn);

        display_stop_img = (Button) findViewById(R.id.display_lock_img);
        display_move_on = (TextView) findViewById(R.id.display_move);
        display_move_off = (TextView) findViewById(R.id.display_stop);

        next_btn = (TextView) findViewById(R.id.next_img);
        before_btn = (TextView) findViewById(R.id.before_img);


        zoom_control1 = (TextView) findViewById(R.id.zoomlevel_voice1);
        zoom_control2 = (TextView) findViewById(R.id.zoomlevel_voice2);
        zoom_control3 = (TextView) findViewById(R.id.zoomlevel_voice3);
        zoom_control4 = (TextView) findViewById(R.id.zoomlevel_voice4);
        zoom_control5 = (TextView) findViewById(R.id.zoomlevel_voice5);

        fadeInAnim_in = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeInAnim_out = AnimationUtils.loadAnimation(this, R.anim.fade_out);
    }

    private void imageload() {

        if (image_choice.equals("one")) {
            Glide.with(getApplicationContext())
                    .load(uri)
                    .into(happy_img);
        } else {
            Glide.with(getApplicationContext())
                    .load(urladress)
                    .into(happy_img);
        }
    }

    private void setImage() {
        try {
            InputStream in = getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(in);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTilt(float x, float y) {
        if (display_move_control) {
            zoomControll.Move_Sensor(-x, -y);
            Log.d(TAG, "onClick: img_order : "+img_order);

        }
    }


    private void zoomlevelcheck(float check) {

        re_zoomcheck();
        zoomlevel_choice = check;
        zoomcheck();
        zoomControll.zoomlevel_choice(check);
    }

    private void zoomcheck() {

        switch ((int) zoomlevel_choice) {
            case 1:
                zoomlevel_1.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.circle_border_check));
                break;
            case 2:
                zoomlevel_2.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.circle_border_check));
                break;
            case 3:
                zoomlevel_3.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.circle_border_check));
                break;
            case 4:
                zoomlevel_4.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.circle_border_check));
                break;
            case 5:
                zoomlevel_5.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.circle_border_check));
                break;
        }
    }

    private void re_zoomcheck() {

        switch ((int) zoomlevel_choice) {
            case 1:
                zoomlevel_1.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.circle_border));
                break;
            case 2:
                zoomlevel_2.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.circle_border));
                break;
            case 3:
                zoomlevel_3.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.circle_border));
                break;
            case 4:
                zoomlevel_4.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.circle_border));
                break;
            case 5:
                zoomlevel_5.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.circle_border));
                break;
        }
    }
}