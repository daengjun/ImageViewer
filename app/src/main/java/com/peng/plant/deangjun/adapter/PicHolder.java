package com.peng.plant.deangjun.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.peng.plant.daengjun.R;


/**
 *Author CodeBoy722
 *
 * picture_Adapter's ViewHolder
 */

public class PicHolder extends RecyclerView.ViewHolder{

    public ImageView picture;
    public TextView image_label,image_position_btn,position_select,select_Item_btn,selecet_name;
    PicHolder(@NonNull View itemView) {
        super(itemView);
        picture = itemView.findViewById(R.id.image);
        image_position_btn = itemView.findViewById(R.id.img_position);
        image_label = itemView.findViewById(R.id.image_Name);
        position_select = itemView.findViewById(R.id.img_choice);
        select_Item_btn = itemView.findViewById(R.id.select_img);
        selecet_name =itemView.findViewById(R.id.img_name);

    }
}
