package com.peng.plant.deangjun.data;

import android.net.Uri;

/**
 * author CodeBoy722
 * <p>
 * Custom Class that holds information of a folder containing images
 * on the device external storage, used to populate our RecyclerView of
 * picture folders
 */
public class imageFolder {

    private String path;
    private String FolderName;
    private String image_name;
    private int numberOfPics = 0;
    private Uri firstPic;

    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }


    public imageFolder() {

    }

    public imageFolder(String path, String folderName) {
        this.path = path;
        FolderName = folderName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFolderName() {
        return FolderName;
    }

    public void setFolderName(String folderName) {
        FolderName = folderName;
    }

    public int getNumberOfPics() {
        return numberOfPics;
    }

    public void setNumberOfPics(int numberOfPics) {
        this.numberOfPics = numberOfPics;
    }

    public void addpics() {
        this.numberOfPics++;
    }

    public Uri getFirstPic() {
        return firstPic;
    }

    public void setFirstPic(Uri firstPic) {
        this.firstPic = firstPic;
    }
}
