package com.project.ovi.liceenta.view.main;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.project.ovi.liceenta.MainActivity;
import com.project.ovi.liceenta.R;
import com.project.ovi.liceenta.model.DriveFile;
import com.project.ovi.liceenta.model.DriveFolder;
import com.project.ovi.liceenta.model.DriveItem;
import com.project.ovi.liceenta.service.activities.TagItemActivity;
import com.project.ovi.liceenta.util.ProjectConstants;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * Created by Ovi
 */
public class DriveItemsViewAdapter extends RecyclerView.Adapter<DriveItemViewHolder> implements Serializable {

    List<DriveItem> list = Collections.emptyList();
    MainActivity mainActivity;

    public DriveItemsViewAdapter(MainActivity mainActivity) {
        this(Collections.<DriveItem>emptyList(), mainActivity);
    }

    public DriveItemsViewAdapter(List<DriveItem> driveItems, MainActivity mainActivity) {
        this.list = driveItems;
        this.mainActivity = mainActivity;
    }

    public void updateItemsView(List<DriveItem> items) {
        this.list = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        if (list.get(position).getId() != null) {
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public DriveItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //Inflate the layout, initialize the View Holder
        DriveItemViewHolder holder;
        switch (viewType) {
            case 1:
                CardView folderView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_layout, parent, false);
                holder = new DriveItemViewHolder(folderView);
                break;
            default:
                CardView messageView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.message_row_layout, parent, false);
                holder = new DriveItemViewHolder(messageView);
                break;
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(final DriveItemViewHolder holder, final int position) {
        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        holder.bindObject(list.get(position));

        setClickItemListener(holder, position);

        setPopupMenuListener(holder, position);

        setTagListener(holder, position);
    }

    private void setTagListener(DriveItemViewHolder holder, final int position) {
        if(holder.cardView.findViewById(R.id.itemTagView) != null) {
            holder.cardView.findViewById(R.id.itemTagView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DriveItem item = list.get(position);
                    Intent tagIntent = new Intent(mainActivity, TagItemActivity.class);
                    tagIntent.putExtra(ProjectConstants.ITEM_ID_TAG, item.getId());
                    tagIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mainActivity.startActivityForResult(tagIntent, ProjectConstants.REQUEST_PROCESS_ITEM);
                }
            });
        }
    }

    private void setPopupMenuListener(DriveItemViewHolder holder, final int position) {
        if(holder.cardView.findViewById(R.id.itemMenuView) != null) {
            holder.cardView.findViewById(R.id.itemMenuView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupMenu(v, list.get(position));
                }
            });
        }
    }

    private void setClickItemListener(DriveItemViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DriveItem item = list.get(position);
                if (item instanceof DriveFolder) {
                    String folderId = item.getId();
                    mainActivity.populateContent(folderId);
                }
                if (item instanceof DriveFile) {
                    String fileId = item.getId();
                    mainActivity.openFile(fileId);
                }
            }
        });
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view, DriveItem driveItem) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mainActivity, view);
        forceShowIcons(popup);
        MenuInflater inflater = popup.getMenuInflater();

        if(driveItem.isBookmarked()) {
            inflater.inflate(R.menu.item_menu_bookmarked, popup.getMenu());
        } else {
            inflater.inflate(R.menu.item_menu_unbookmarked, popup.getMenu());
        }
        popup.setOnMenuItemClickListener(new ItemMenuClickListener(mainActivity, driveItem));
        popup.show();
    }

    private void forceShowIcons(PopupMenu popup) {
        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
