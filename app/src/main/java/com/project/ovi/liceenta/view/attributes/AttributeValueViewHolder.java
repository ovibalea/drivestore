package com.project.ovi.liceenta.view.attributes;

import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.project.ovi.liceenta.R;

/**
 * Created by Ovi on 24/08/16.
 */
public class AttributeValueViewHolder extends ChildViewHolder {

    private TextView attrValueTV;

    public AttributeValueViewHolder(View itemView) {
        super(itemView);

        attrValueTV = (TextView) itemView.findViewById(R.id.attrvalue);
    }

    public void bind(String value){
        attrValueTV.setText(value);
    }

}
