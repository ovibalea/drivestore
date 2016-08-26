package com.project.ovi.liceenta.util;

import com.project.ovi.liceenta.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ovi on 11/08/16.
 */
public class ProjectConstants {

    public static final int REQUEST_AUTHORIZATION = 1001;

    public static final int REQUEST_CONTENT = 10;

    public static final int REQUEST_CREATE_ITEM = 20;

    public static final int AUTHENTICATION_REQUEST = 30;

    public static final int REQUEST_PROCESS_ITEM = 40;

    public static final String PARENT_FOLDER_ID_TAG = "parentFolderIdTag";

    public static final String DOWNLOAD_ITEM_ID_TAG = "downloadItemIdTag";

    public static final String ITEM_ID_TAG = "itemIdTag";

    public static final String DELETE_ITEM_ID_TAG = "downloadItemIdTag";

    public static final String IS_ITEM_CREATED = "isItemCreated";
    public static final String IS_ITEM_PROCESSED = "isItemProcessed";

    public static final String IS_AUTHENTICATED = "isAuthenticated";

    public static final String VIEW_ADAPTER_ITEMS = "driveItemsViewAdapter";

    public static final String MIMETYPE_FOLDER = "application/vnd.google-apps.folder";

    public static final String ITEM_FIELDS = "id, name, fullFileExtension, trashed, createdTime, size, mimeType, fileExtension, properties, webContentLink, webViewLink, starred";

    public static final String ITEM_TAG = "itemTag";
    public static final String FOLDER_ID = "folderId";

    public static final String ROOT = "root";

    public static final String ITEM_ATTRIBUTES = "id,name,description,kind,mimeType,createdTime,modifiedTime,modifiedByMeTime,viewedByMeTime,lastModifyingUser,owners,parents,shared,size,version,properties";

    public static final String NAME = "name";
    public static final String CREATED_DATE = "createdTime";
    public static final String MODIFIED_DATE = "modifiedTime";
    public static final String MODIFIED_BY_ME_DATE = "modifiedByMeTime";
    public static final String SHARED_WITH_ME_TIME = "sharedWithMeTime";
    public static final String RECENCY = "recency";
    public static final String TYPE = "folder";

    public static Map<Integer, String> orderByMapping;

    static {
        orderByMapping = new HashMap<>();

        orderByMapping.put(R.id.radioOrderName, NAME);
        orderByMapping.put(R.id.radioOrderCrDate, CREATED_DATE);
        orderByMapping.put(R.id.radioOrderModDate, MODIFIED_DATE);
        orderByMapping.put(R.id.radioOrderModMeDate, MODIFIED_BY_ME_DATE);
        orderByMapping.put(R.id.radioOrderSharedMeDate, SHARED_WITH_ME_TIME);
        orderByMapping.put(R.id.radioOrderRecency, RECENCY);
        orderByMapping.put(R.id.radioOrderType, TYPE);

    }

    public static Map<String, Integer> tagNameIconMapping;

    static {
        tagNameIconMapping = new HashMap<>();
        tagNameIconMapping.put("blueTag", R.drawable.tag_blue_icon);
        tagNameIconMapping.put("redTag", R.drawable.tag_red_icon);
        tagNameIconMapping.put("greenTag", R.drawable.tag_green_icon);
        tagNameIconMapping.put("orangeTag", R.drawable.tag_orange_icon);
        tagNameIconMapping.put("yellowTag", R.drawable.tag_yellow_icon);
        tagNameIconMapping.put("noTag", R.drawable.tag_white_icon);
    }

    public static Map<Integer, String> tagIdNameMapping;
    static {
        tagIdNameMapping = new HashMap<>();
        tagIdNameMapping.put(R.id.blueTag, "blueTag");
        tagIdNameMapping.put(R.id.redTag, "redTag");
        tagIdNameMapping.put(R.id.greenTag, "greenTag");
        tagIdNameMapping.put(R.id.orangeTag, "orangeTag");
        tagIdNameMapping.put(R.id.yellowTag, "yellowTag");
        tagIdNameMapping.put(R.id.noTag, "noTag");
    }

}
