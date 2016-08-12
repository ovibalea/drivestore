package com.project.ovi.liceenta.view;

import android.content.Context;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.project.ovi.liceenta.R;

/**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private Context mContext;

        public MyMenuItemClickListener(Context mContext) {
            this.mContext = mContext;
        }
 
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_info:
                    Toast.makeText(mContext, "Show info action", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_download:
                    Toast.makeText(mContext, "Download action", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_delete:
                    Toast.makeText(mContext, "Delete action", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }
