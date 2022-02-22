package com.peng.plant.deangjun.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.peng.plant.deangjun.listener.TiltScrollController;
import com.peng.plant.deangjun.utils.ZoomControll;
import com.peng.plant.daengjun.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class pdfView extends AppCompatActivity implements TiltScrollController.ScrollListener {

    //    @BindView(R.id.pdf_image)
//    ImageView imageViewPdf;
    @BindView(R.id.button_pre_doc)
    FloatingActionButton prePageButton;
    @BindView(R.id.button_next_doc)
    FloatingActionButton nextPageButton;

    private static final String FILENAME = "report.pdf";

    private int pageIndex;
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPage;
    private ParcelFileDescriptor parcelFileDescriptor;
    private RelativeLayout container;
    private Button display_stop_img, zoomlevel_1, zoomlevel_2, zoomlevel_3, zoomlevel_4, zoomlevel_5;
    private TextView display_move_on, display_move_off, zoom_control1, zoom_control2, zoom_control3, zoom_control4, zoom_control5, next_btn, before_btn;
    private ZoomControll zoomControll;
    private TiltScrollController mTiltScrollController;
    private ImageView imageView_test;
    private float zoomlevel_choice;
    private Boolean display_move_control = true;
    private Animation fadeInAnim_in, fadeInAnim_out;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdf_activity);
        ButterKnife.bind(this);
        pageIndex = 0;
        mTiltScrollController = new TiltScrollController(getApplicationContext(), this);

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

        View v = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.pdf_zoom_item, null, false);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageView_test = v.findViewById(R.id.pdf_image);
        container = (RelativeLayout) findViewById(R.id.container);
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onStart() {
        super.onStart();
        try {
            openRenderer(getApplicationContext());
            showPage(pageIndex);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onStop() {
        try {
            closeRenderer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.button_pre_doc)
    public void onPreviousDocClick() {
        showPage(currentPage.getIndex() - 1);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.button_next_doc)
    public void onNextDocClick() {
        showPage(currentPage.getIndex() + 1);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void openRenderer(Context context) throws IOException {
        // In this sample, we read a PDF from the assets directory.
        File file = new File(context.getCacheDir(), FILENAME);

        if (!file.exists()) {
            // Since PdfRenderer cannot handle the compressed asset file directly, we copy it into
            // the cache directory.
            InputStream asset = context.getAssets().open(FILENAME);


            FileOutputStream output = new FileOutputStream(file);
            final byte[] buffer = new byte[1024];
            int size;
            while ((size = asset.read(buffer)) != -1) {
                output.write(buffer, 0, size);
            }
            asset.close();
            output.close();
        }
        parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);

//         This is the PdfRenderer we use to render the PDF.
        if (parcelFileDescriptor != null) {
            pdfRenderer = new PdfRenderer(parcelFileDescriptor);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void closeRenderer() throws IOException {
        if (null != currentPage) {
            currentPage.close();
        }
        pdfRenderer.close();
        parcelFileDescriptor.close();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showPage(int index) {
        if (pdfRenderer.getPageCount() <= index) {
            return;
        }
        // Make sure to close the current page before opening another one.
        if (null != currentPage) {
            currentPage.close();
        }
        // Use `openPage` to open a specific page in PDF.
        currentPage = pdfRenderer.openPage(index);
        // Important: the destination bitmap must be ARGB (not RGB).
        Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(),
                Bitmap.Config.ARGB_8888);
        // Here, we render the page onto the Bitmap.
        // To render a portion of the page, use the second and third parameter. Pass nulls to get
        // the default result.
        // Pass either RENDER_MODE_FOR_DISPLAY or RENDER_MODE_FOR_PRINT for the last parameter.
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

        // We are ready to show the Bitmap to user.
//        imageView_test.setImageBitmap(bitmap);
                Glide.with(getApplicationContext())
                .load(bitmap)
                .into(imageView_test);
        updateUi();
    }

    /**
     * Updates the state of 2 control buttons in response to the current page index.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void updateUi() {
        int index = currentPage.getIndex();
        int pageCount = pdfRenderer.getPageCount();
        prePageButton.setEnabled(0 != index);
        nextPageButton.setEnabled(index + 1 < pageCount);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public int getPageCount() {
        return pdfRenderer.getPageCount();
    }


//    private void pdf_search() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("application/pdf");
//        startActivityForResult(intent, 1);
//
//    }
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1) {
//            if (resultCode == RESULT_OK) {
//                try {
//
//                } catch (Exception e) {
//
//                }
//            } else if (resultCode == RESULT_CANCELED) {
//                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
//            }
//        }
//
//    }

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
                    showPage(currentPage.getIndex() + 1);
                    if (zoomlevel_choice != 1) {
                        zoomlevelcheck(1);
                    }
                    if (display_move_control == false) {
                        display_stop_img.startAnimation(fadeInAnim_out);
                        display_stop_img.setVisibility(View.GONE);
                        display_move_control = true;
                    }
                    break;
                case R.id.before_img:
                    showPage(currentPage.getIndex() - 1);
                    if (zoomlevel_choice != 1) {
                        zoomlevelcheck(1);
                    }
                    if (display_move_control == false) {
                        display_stop_img.startAnimation(fadeInAnim_out);
                        display_stop_img.setVisibility(View.GONE);
                        display_move_control = true;
                    }
                    break;

            }
        }
    };


    @Override
    public void onTilt(float x, float y) {
        if (display_move_control) {
            zoomControll.Move_Sensor(-x, -y);
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

