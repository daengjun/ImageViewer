package com.peng.plant.deangjun.activity;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;


import com.peng.plant.deangjun.listener.itemClickListener;
import com.peng.plant.deangjun.listener.recyclerView_ScrollController;
import com.peng.plant.daengjun.R;
import com.peng.plant.deangjun.utils.ScrollZoomLayoutManager;
import com.peng.plant.deangjun.adapter.PicHolder;
import com.peng.plant.deangjun.data.imageFolder;
import com.peng.plant.deangjun.data.pictureFacer;
import com.peng.plant.deangjun.adapter.pictureFolderAdapter;

import java.util.ArrayList;

/**
 * Author CodeBoy722
 *
 * The main Activity start and loads all folders containing images in a RecyclerView
 * this folders are gotten from the MediaStore by the Method getPicturePaths()
 */
public class Gallary_activity extends AppCompatActivity implements itemClickListener, recyclerView_ScrollController.ScrollListener {

    private RecyclerView folderRecycler;
    private TextView empty;
    private ScrollZoomLayoutManager scrollZoomLayoutManager;
    private recyclerView_ScrollController mScrollController;
    private SnapHelper snapHelper;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private RecyclerView.Adapter folderAdapter;
    private pictureFolderAdapter folderadapter;
    private int sensor_statistic;


    /**
     * Request the user for permission to access media files and read images on the device
     * this will be useful as from api 21 and above, if this check is not done the Activity will crash
     *
     * Setting up the RecyclerView and getting all folders that contain pictures from the device
     * the getPicturePaths() returns an ArrayList of imageFolder objects that is then used to
     * create a RecyclerView Adapter that is set to the RecyclerView
     *
     * @param savedInstanceState saving the activity state
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ContextCompat.checkSelfPermission(Gallary_activity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(Gallary_activity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        //____________________________________________________________________________________

        empty =findViewById(R.id.empty);


//        imageRecycler.addItemDecoration(new MarginDecoration(this)); 간격조절

        mScrollController = new recyclerView_ScrollController(getApplicationContext(), this);



        folderRecycler = findViewById(R.id.folderRecycler);
//        folderRecycler.addItemDecoration(new MarginDecoration(this));
        scrollZoomLayoutManager = new ScrollZoomLayoutManager(this, Dp2px(10));
        folderRecycler.setLayoutManager(scrollZoomLayoutManager);
        folderRecycler.hasFixedSize();
        snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(folderRecycler);
        ArrayList<imageFolder> folds = getPicturePaths();

        if(folds.isEmpty()){
            empty.setVisibility(View.VISIBLE);
        }else{
            folderadapter = new pictureFolderAdapter(folds,Gallary_activity.this,this);
            folderAdapter = folderadapter;
            folderRecycler.setAdapter(folderAdapter);
        }

        changeStatusBarColor();
    }

    /**1
     * @return
     * gets all folders with pictures on the device and loads each of them in a custom object imageFolder
     * the returns an ArrayList of these custom objects
     */


    @Override
    public void onTilt(float x, int y, float deltaX) {
        Log.d("dsadsadas", "onTilt: deltax값"+deltaX);
        folderRecycler.smoothScrollBy((int) (x * (scrollZoomLayoutManager.getEachItemWidth())/2),0);
        folderadapter.choice_position(scrollZoomLayoutManager.getCurrentPosition());

//        Log.d("aaaa", "onCreate: 너의값 "+ scrollZoomLayoutManager.getCurrentPosition());

//        if(deltaX<1.0){
//            imageRecycler.smoothScrollBy((int) (x * (scrollZoomLayoutManager.getEachItemWidth()))/10000,0);
//        }
//        else if(deltaX>1.0){
//
//        }
        sensor_statistic = (int) (x * (scrollZoomLayoutManager.getEachItemWidth())/2);

    }

    public int Dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScrollController.requestAllSensors();
        folderRecycler.smoothScrollBy(sensor_statistic, 0);
    }
    @Override
    public void onPause() {
        super.onPause();
        mScrollController.releaseAllSensors();
    }

    private ArrayList<imageFolder> getPicturePaths(){
        ArrayList<imageFolder> picFolders = new ArrayList<>();
        ArrayList<String> picPaths = new ArrayList<>();
        Uri allImagesuri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.Images.ImageColumns.DATA ,MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,MediaStore.Images.Media.BUCKET_ID};
        Cursor cursor = this.getContentResolver().query(allImagesuri, projection, null, null, null);
        try {
            if (cursor != null) {
                cursor.moveToFirst();
            }
            do{
                imageFolder folds = new imageFolder();
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                String folder = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                String datapath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));

                //String folderpaths =  datapath.replace(name,"");
                String folderpaths = datapath.substring(0, datapath.lastIndexOf(folder+"/"));
                folderpaths = folderpaths+folder+"/";
                if (!picPaths.contains(folderpaths)) {
                    picPaths.add(folderpaths);

                    folds.setPath(folderpaths);
                    folds.setFolderName(folder);
                    folds.setFirstPic(getUriFromPath(datapath));//if the folder has only one picture this line helps to set it as first so as to avoid blank image in itemview
                    folds.addpics();
                    picFolders.add(folds);
                }else{
                    for(int i = 0;i<picFolders.size();i++){
                        if(picFolders.get(i).getPath().equals(folderpaths)){
                            picFolders.get(i).setFirstPic(getUriFromPath(datapath));
                            picFolders.get(i).addpics();
                        }
                    }
                }
            }while(cursor.moveToNext());
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(int i = 0;i < picFolders.size();i++){
            Log.d("picture folders",picFolders.get(i).getFolderName()+" and path = "+picFolders.get(i).getPath()+" "+picFolders.get(i).getNumberOfPics());
        }

        //reverse order ArrayList
       /* ArrayList<imageFolder> reverseFolders = new ArrayList<>();
        for(int i = picFolders.size()-1;i > reverseFolders.size()-1;i--){
            reverseFolders.add(picFolders.get(i));
        }*/

        return picFolders;
    }


    @Override
    public void onPicClicked(PicHolder holder, int position, ArrayList<pictureFacer> pics) {

    }

    /**
     * Each time an item in the RecyclerView is clicked this method from the implementation of the transitListerner
     * in this activity is executed, this is possible because this class is passed as a parameter in the creation
     * of the RecyclerView's Adapter, see the adapter class to understand better what is happening here
     * @param pictureFolderPath a String corresponding to a folder path on the device external storage
     */
    @Override
    public void onPicClicked(String pictureFolderPath,String folderName) {
        Intent move = new Intent(Gallary_activity.this, ImageDisplay.class);
        move.putExtra("folderPath",pictureFolderPath);
        move.putExtra("folderName",folderName);

        //move.putExtra("recyclerItemSize",getCardsOptimalWidth(4));
        startActivity(move);
    }


    /**
     * Default status bar height 24dp,with code API level 24
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void changeStatusBarColor()
    {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

    }

    public Uri getUriFromPath(String path){

        String fileName= path;

        Uri fileUri = Uri.parse( fileName );

        String filePath = fileUri.getPath();

        Cursor c = getContentResolver().query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,

                null, "_data = '" + filePath + "'", null, null );

        c.moveToNext();

        int id = c.getInt( c.getColumnIndex( "_id" ) );

        Uri uri = ContentUris.withAppendedId( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id );


        return uri;
    }



}