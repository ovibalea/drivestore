package com.project.ovi.liceenta.view;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.ovi.liceenta.R;
import com.project.ovi.liceenta.model.DriveFile;
import com.project.ovi.liceenta.model.DriveFolder;
import com.project.ovi.liceenta.model.DriveItem;

/**
 * Created by Ovi on 12/08/16.
 */
public class DriveItemViewHolder extends RecyclerView.ViewHolder {

    public CardView cardView;

    DriveItemViewHolder(View rowLayout) {
        super(rowLayout);
        this.cardView = (CardView) rowLayout;
    }

    public void bindObject(DriveItem contentObject) {
        setCardTitle(contentObject);

        setInfos(contentObject);

        setCardImage(contentObject);

    }

    private void setInfos(DriveItem contentObject) {
        if (contentObject instanceof DriveFolder) {
            TextView infosTextView = (TextView) cardView.findViewById(R.id.infosTextView);
            DriveFolder folder = (DriveFolder) contentObject;
            infosTextView.setText(folder.getInfo());
        } else if (contentObject instanceof DriveFile) {
            TextView infosTextView = (TextView) cardView.findViewById(R.id.infosTextView);
            DriveFile file = (DriveFile) contentObject;
            infosTextView.setText(file.getInfo());
        }
    }

    private void setCardTitle(DriveItem contentObject) {
        TextView folderTextView = (TextView) cardView.findViewById(R.id.titleTextView);
        String objectName = contentObject.getName();
        if (objectName.length() > 15) {
            objectName = objectName.substring(0, 15);
            objectName.concat("...");
        }
        folderTextView.setText(objectName);
    }

    private void setCardImage(DriveItem contentObject) {
        ImageView imageView = (ImageView) cardView.findViewById(R.id.imageView);
        if (contentObject instanceof DriveFolder || contentObject instanceof DriveFile) {
            imageView.setImageResource(contentObject.getIconId());
        } else {
            imageView.setImageResource(R.drawable.warning2);
        }
    }
}
