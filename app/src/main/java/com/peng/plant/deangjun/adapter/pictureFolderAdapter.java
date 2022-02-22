package com.peng.plant.deangjun.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.peng.plant.deangjun.listener.itemClickListener;
import com.peng.plant.daengjun.R;
import com.peng.plant.deangjun.data.imageFolder;

import java.util.ArrayList;

/**
 * Author CodeBoy722
 * <p>
 * An adapter for populating RecyclerView with items representing folders that contain images
 */
public class pictureFolderAdapter extends RecyclerView.Adapter<pictureFolderAdapter.FolderHolder> {

    private static final String TAG = "happyDaengjun";
    private ArrayList<imageFolder> folders;
    private Context folderContx;
    private itemClickListener listenToClick;
    private int position_check;

    /**
     * @param folders     An ArrayList of String that represents paths to folders on the external storage that contain pictures
     * @param folderContx The Activity or fragment Context
     * @param listen      interFace for communication between adapter and fragment or activity
     */
    public pictureFolderAdapter(ArrayList<imageFolder> folders, Context folderContx, itemClickListener listen) {
        this.folders = folders;
        this.folderContx = folderContx;
        this.listenToClick = listen;
    }

    @NonNull
    @Override
    public FolderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View cell = inflater.inflate(R.layout.picture_folder_item, parent, false);
        return new FolderHolder(cell);

    }

    @Override
    public void onBindViewHolder(@NonNull FolderHolder holder, int position) {
        final imageFolder folder = folders.get(position);

        Log.d(TAG, "onBindViewHolder: folder.getFirstPic()+" + folder.getFirstPic());

        Glide.with(folderContx)
                .load(folder.getFirstPic())
                .apply(new RequestOptions().centerCrop())
                .error(R.drawable.cat)
                .into(holder.folderPic);

        //setting the number of images
        String position_num = "" + (position + 1);
        holder.folder_fosition_btn.setText(position_num);
        String text = "" + folder.getFolderName();
        String folderSizeString = "" + folder.getNumberOfPics() + " Picture";
        holder.folderSize.setText(folderSizeString);
        holder.folderName.setText(text);
        holder.select_Item_btn.setText("항목 " + position_num + " 선택");

        holder.select_Item_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listenToClick.onPicClicked(folder.getPath(), folder.getFolderName());
            }
        });

        holder.folder_fosition_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listenToClick.onPicClicked(folder.getPath(), folder.getFolderName());
            }
        });

        holder.folderName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listenToClick.onPicClicked(folder.getPath(), folder.getFolderName());
            }
        });

        holder.folderPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listenToClick.onPicClicked(folder.getPath(), folder.getFolderName());
            }
        });
        holder.img_position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final imageFolder folder = folders.get(position_check);
                listenToClick.onPicClicked(folder.getPath(), folder.getFolderName());

            }
        });

    }

    @Override
    public int getItemCount() {
        return folders.size();
    }


    public class FolderHolder extends RecyclerView.ViewHolder {
        ImageView folderPic;
        TextView folderName;
        TextView folderSize, folder_fosition_btn, img_position, select_Item_btn;

        CardView folderCard;

        public FolderHolder(@NonNull View itemView) {
            super(itemView);
            folderPic = itemView.findViewById(R.id.folderPic);
            folderName = itemView.findViewById(R.id.folderName);
            folderSize = itemView.findViewById(R.id.folderSize);
            folder_fosition_btn = itemView.findViewById(R.id.folder_position);
            folderCard = itemView.findViewById(R.id.folderCard);
            img_position = itemView.findViewById(R.id.img_choice);
            select_Item_btn = itemView.findViewById(R.id.select_img);
        }
    }

    public void choice_position(int position) {
        position_check = position;
    }
}
