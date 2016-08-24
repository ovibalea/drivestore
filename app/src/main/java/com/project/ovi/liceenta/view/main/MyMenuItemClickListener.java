package com.project.ovi.liceenta.view.main;

import android.content.Intent;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.project.ovi.liceenta.MainActivity;
import com.project.ovi.liceenta.R;
import com.project.ovi.liceenta.model.DriveFile;
import com.project.ovi.liceenta.model.DriveItem;
import com.project.ovi.liceenta.service.activities.DeleteItemActivity;
import com.project.ovi.liceenta.service.activities.DownloadItemActivity;
import com.project.ovi.liceenta.service.activities.RenameActivity;
import com.project.ovi.liceenta.service.activities.ShowItemInfoActivity;
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
                Intent infoIntent = new Intent(mainActivity, ShowItemInfoActivity.class);
                infoIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                infoIntent.putExtra(ProjectConstants.ITEM_ID_TAG, driveItem.getId());
                mainActivity.startActivity(infoIntent);
                return true;
            case R.id.action_download:
                Intent intent = new Intent(mainActivity, DownloadItemActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(ProjectConstants.DOWNLOAD_ITEM_ID_TAG, driveItem);
                mainActivity.startActivity(intent);
                return true;
            case R.id.action_delete:
                if(driveItem instanceof DriveFile) {
                    Intent deleteItent = new Intent(mainActivity, DeleteItemActivity.class);
                    deleteItent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    deleteItent.putExtra(ProjectConstants.DELETE_ITEM_ID_TAG, driveItem.getId());
                    mainActivity.startActivityForResult(deleteItent, ProjectConstants.REQUEST_PROCESS_ITEM);
                } else {
                    Toast.makeText(mainActivity, "Folder is not downloadable.", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_rename:
                Intent renameIntent = new Intent(mainActivity, RenameActivity.class);
                renameIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                renameIntent.putExtra(ProjectConstants.ITEM_ID_TAG, driveItem.getId());
                mainActivity.startActivityForResult(renameIntent, ProjectConstants.REQUEST_PROCESS_ITEM);
                return true;
            default:
        }
        return false;
    }
}
