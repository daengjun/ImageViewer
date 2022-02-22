package com.peng.plant.deangjun.activity;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;


import com.peng.plant.deangjun.listener.itemClickListener;
import com.peng.plant.deangjun.listener.recyclerView_ScrollController;
import com.peng.plant.daengjun.R;
import com.peng.plant.deangjun.adapter.PicHolder;
import com.peng.plant.deangjun.data.pictureFacer;
import com.peng.plant.deangjun.adapter.picture_Adapter;
import com.peng.plant.deangjun.utils.ScrollZoomLayoutManager;
import com.peng.plant.deangjun.view.imageView;

import java.util.ArrayList;

/**
 * Author CodeBoy722
 * <p>
 * This Activity get a path to a folder that contains images from the MainActivity Intent and displays
 * all the images in the folder inside a RecyclerView
 */

public class ImageDisplay extends AppCompatActivity implements itemClickListener, recyclerView_ScrollController.ScrollListener {

    private RecyclerView imageRecycler;
    private ArrayList<pictureFacer> allpictures;
    private ProgressBar load;
    private String foldePath;
    private TextView folderName;
    private ScrollZoomLayoutManager scrollZoomLayoutManager;
    private recyclerView_ScrollController mScrollController;
    private SnapHelper snapHelper;
    private int sensor_statistic;
    private picture_Adapter pictures_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);

        folderName = findViewById(R.id.foldername);
        folderName.setText(getIntent().getStringExtra("folderName"));
        foldePath = getIntent().getStringExtra("folderPath");
        allpictures = new ArrayList<>();
        imageRecycler = findViewById(R.id.recycler);

        scrollZoomLayoutManager = new ScrollZoomLayoutManager(this, Dp2px(10));
        imageRecycler.setLayoutManager(scrollZoomLayoutManager);

//        imageRecycler.addItemDecoration(new MarginDecoration(this)); 간격조절
        imageRecycler.hasFixedSize();
        load = findViewById(R.id.loader);

        mScrollController = new recyclerView_ScrollController(getApplicationContext(), this);
        snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(imageRecycler);


        if (allpictures.isEmpty()) {
            load.setVisibility(View.VISIBLE);
            allpictures = getAllImagesByFolder(foldePath);
            pictures_adapter = new picture_Adapter(allpictures, ImageDisplay.this, this);
            imageRecycler.setAdapter(pictures_adapter);
            load.setVisibility(View.GONE);
        } else {

        }
    }

    /**
     * @param holder   The ViewHolder for the clicked picture
     * @param position The position in the grid of the picture that was clicked
     * @param pics     An ArrayList of all the items in the Adapter
     */
    @Override
    public void onPicClicked(PicHolder holder, int position, ArrayList<pictureFacer> pics) {

        pictureFacer pic = pics.get(position);
        Log.d("ddd", "onPicClicked: 0번쨰값 : "+ pics.get(position));

        ArrayList<pictureFacer> a = new ArrayList();

        for (int i=0; i<pics.size(); i++){
            a.add(pics.get(i));

        }

        Log.d("ddd", "onPicClicked: a" + a.get(0));

        ArrayList<Uri> uris = new ArrayList();

        for (int i=0; i<pics.size(); i++){
            uris.add(a.get(i).getPicturePath());
        }

        Log.d("ddd", "onPicClicked: uris" + uris.get(0));

        Uri path = pic.getPicturePath();

        Intent intent = new Intent(getApplicationContext(), imageView.class);

        intent.putExtra("imglist",uris);
        intent.putExtra("uridata","one");
        intent.putExtra("img_position",position);
        intent.putExtra("imageUri", path);
        startActivity(intent);

    }

    //(int) (x * (scrollZoomLayoutManager.getEachItemWidth()) / 3)
    @Override
    public void onTilt(float x, int y, float deltaX) {
        imageRecycler.smoothScrollBy((int) (x * (scrollZoomLayoutManager.getEachItemWidth())/2),0);
        pictures_adapter.choice_position(scrollZoomLayoutManager.getCurrentPosition());
        sensor_statistic = (int) (x * (scrollZoomLayoutManager.getEachItemWidth())/2);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScrollController.requestAllSensors();
        imageRecycler.smoothScrollBy(sensor_statistic, 0);
    }
    @Override
    public void onPause() {
        super.onPause();
        mScrollController.releaseAllSensors();
    }

    @Override
    public void onPicClicked(String pictureFolderPath, String folderName) {

    }

    /**
     * This Method gets all the images in the folder paths passed as a String to the method and returns
     * and ArrayList of pictureFacer a custom object that holds data of a given image
     *
     * @param path a String corresponding to a folder path on the device external storage
     */
    public ArrayList<pictureFacer> getAllImagesByFolder(String path) {
        ArrayList<pictureFacer> images = new ArrayList<>();
        Uri allVideosuri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE};
        Cursor cursor = ImageDisplay.this.getContentResolver().query(allVideosuri, projection, MediaStore.Images.Media.DATA + " like ? ", new String[]{"%" + path + "%"}, null);
        try {
            cursor.moveToFirst();
            do {
                pictureFacer pic = new pictureFacer();

                pic.setPicturName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)));

                pic.setPicturePath(getUriFromPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))));
                pic.setPictureSize(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)));

                images.add(pic);
            } while (cursor.moveToNext());
            cursor.close();
            ArrayList<pictureFacer> reSelection = new ArrayList<>();
            for (int i = images.size() - 1; i > -1; i--) {
                reSelection.add(images.get(i));
            }
            images = reSelection;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return images;
    }


    public int Dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public Uri getUriFromPath(String path) {

        String fileName = path;

        Uri fileUri = Uri.parse(fileName);

        String filePath = fileUri.getPath();

        Cursor c = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,

                null, "_data = '" + filePath + "'", null, null);

        c.moveToNext();

        int id = c.getInt(c.getColumnIndex("_id"));

        Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);


        return uri;
    }


}