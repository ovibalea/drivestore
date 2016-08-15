package com.project.ovi.liceenta.model;

import com.google.api.client.util.DateTime;

/**
 * Created by Ovi on 12/08/16.
 */
public class MessageItem extends DriveItem {

    public MessageItem() {
        super(null, "No Items!", null, null);
    }

    @Override
    public String getInfo(){
        return "";
    }
}
