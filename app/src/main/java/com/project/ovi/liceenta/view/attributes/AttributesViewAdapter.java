package com.project.ovi.liceenta.view.attributes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.project.ovi.liceenta.R;
import com.project.ovi.liceenta.model.ItemAttribute;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ovi on 23/08/16.
 */
public class AttributesViewAdapter extends ExpandableRecyclerAdapter<AttributeViewHolder, AttributeValueViewHolder> {

    private List<ItemAttribute> attributesList;

    public AttributesViewAdapter(List<ItemAttribute> attributesList) {
        super(attributesList);
    }

    public AttributesViewAdapter() {
        super(new ArrayList<ParentListItem>());
    }

    public void updateAttributesView(List<ItemAttribute> attributes){
        this.attributesList = attributes;
        notifyDataSetChanged();
    }

    @Override
    public AttributeViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View attrNameView = LayoutInflater.from(parentViewGroup.getContext()).inflate(R.layout.item_attribute_row, parentViewGroup, false);
        return new AttributeViewHolder(attrNameView);
    }

    @Override
    public AttributeValueViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View attrValueView = LayoutInflater.from(childViewGroup.getContext()).inflate(R.layout.item_attribute_value_row, childViewGroup, false);
        return new AttributeValueViewHolder(attrValueView);
    }

    @Override
    public void onBindParentViewHolder(AttributeViewHolder parentViewHolder, int position, ParentListItem parentListItem) {
        ItemAttribute attribute = (ItemAttribute) parentListItem;
        parentViewHolder.bind(attribute);
    }

    @Override
    public void onBindChildViewHolder(AttributeValueViewHolder childViewHolder, int position, Object childListItem) {
        String value = (String) childListItem;
        childViewHolder.bind(value);
    }


}
