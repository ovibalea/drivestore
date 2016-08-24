package com.project.ovi.liceenta.view.attributes;

import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.project.ovi.liceenta.R;
import com.project.ovi.liceenta.model.ItemAttribute;

/**
 * Created by Ovi on 24/08/16.
 */
public class AttributeViewHolder extends ParentViewHolder {

    private ImageView expandImageView;

    private TextView attrNameTV;

    public AttributeViewHolder(View view) {
        super(view);
        CardView cardView = (CardView) view;

        attrNameTV = (TextView) cardView.findViewById(R.id.attrName);

        expandImageView = (ImageView) cardView.findViewById(R.id.parent_list_item_expand_arrow);
        expandImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded()) {
                    expandImageView.setImageResource(android.R.drawable.arrow_down_float);
                    collapseView();
                } else {
                    expandView();
                    expandImageView.setImageResource(android.R.drawable.arrow_up_float);
                }
            }
        });
    }

    public void bind(ItemAttribute attribute){
        attrNameTV.setText(attribute.getName());
    }

    @Override
    public boolean shouldItemViewClickToggleExpansion() {
        return false;
    }


}
