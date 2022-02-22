package com.peng.plant.deangjun.listener;

import com.peng.plant.deangjun.adapter.PicHolder;
import com.peng.plant.deangjun.data.pictureFacer;

import java.util.ArrayList;

/**
 * Author CodeBoy722
 */
public interface itemClickListener {

    /**
     * Called when a picture is clicked
     * @param holder The ViewHolder for the clicked picture
     * @param position The position in the grid of the picture that was clicked
     */
    void onPicClicked(PicHolder holder, int position, ArrayList<pictureFacer> pics);
    void onPicClicked(String pictureFolderPath,String folderName);
}
