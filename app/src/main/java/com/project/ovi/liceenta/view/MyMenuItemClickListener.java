package com.project.ovi.liceenta.view;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.project.ovi.liceenta.MainActivity;
import com.project.ovi.liceenta.R;
import com.project.ovi.liceenta.model.DriveItem;
import com.project.ovi.liceenta.service.create.CreateFolderActivity;
import com.project.ovi.liceenta.service.create.DownloadItemActivity;
import com.project.ovi.liceenta.util.ProjectConstants;

/**
 * Click listener for popup menu items
 */
class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

    private MainActivity mainActivity;

    private DriveItem driveItem;

    public MyMenuItemClickListener(MainActivity activity, DriveItem selectedDriveItem) {
        this.mainActivity = activity;
        this.driveItem = selectedDriveItem;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_info:
                Toast.makeText(mainActivity, "Show info action", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_download:
                Intent intent = new Intent(mainActivity, DownloadItemActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(ProjectConstants.DOWNLOAD_ITEM_ID_TAG, driveItem);
                mainActivity.startActivity(intent);
                return true;
            case R.id.action_delete:
                Toast.makeText(mainActivity, "Delete action", Toast.LENGTH_SHORT).show();
                return true;
            default:
        }
        return false;
    }
}
