package com.project.ovi.liceenta.util;

import com.google.api.services.drive.model.File;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ovi on 11/08/16.
 */
public class ProjectConstants {

    public static final int REQUEST_AUTHORIZATION = 1001;

    public static final int REQUEST_CONTENT = 10;

    public static final int REQUEST_CREATE_ITEM = 20;

    public static final int REQUEST_PROCESS_ITEM = 40;

    public static final String PARENT_FOLDER_ID_TAG = "parentFolderIdTag";

    public static final String DOWNLOAD_ITEM_ID_TAG = "downloadItemIdTag";

    public static final String ITEM_ID_TAG = "itemIdTag";

    public static final String DELETE_ITEM_ID_TAG = "downloadItemIdTag";

    public static final String IS_ITEM_CREATED = "isItemCreated";
    public static final String IS_ITEM_PROCESSED = "isItemProcessed";

    public static final String IS_AUTHENTICATED = "isAuthenticated";

    public static final int AUTHENTICATION_REQUEST = 30;

    public static final int SELECT_FILE_REQUEST = 50;

    public static final String MIME_TEXT_PLAIN = "text/plain";

    public static final String MIMETYPE_FOLDER = "application/vnd.google-apps.folder";

    public static final String MIME_TYPE_TAG = "mimeTypeTag";

    public static final String ITEM_FIELDS = "id, name, fullFileExtension, trashed, createdTime, size, mimeType, fileExtension, properties, webContentLink, webViewLink";

    public static final String IS_BOOKMARKED = "isBookmarked";

    public static final String ITEM_NAME_TAG = "itemNameTag";

    public static final String ITEM_ATTRIBUTES = "id,name,description,kind,mimeType,createdTime,modifiedTime,modifiedByMeTime,viewedByMeTime,lastModifyingUser,owners,parents,shared,size,version,properties";


    public static Map<String, Method> attributeNamesMapping;

    static {
        attributeNamesMapping = new HashMap<>();
        try {
            attributeNamesMapping.put("ID", File.class.getMethod("getId", null));
            attributeNamesMapping.put("Name", File.class.getMethod("getName", null));
            attributeNamesMapping.put("Description", File.class.getMethod("getDescription", null));
            attributeNamesMapping.put("Item type", File.class.getMethod("getKind", null));
            attributeNamesMapping.put("MimeType", File.class.getMethod("getMimeType", null));
            attributeNamesMapping.put("Creation Date", File.class.getMethod("getCreatedTime", null));
            attributeNamesMapping.put("Modified Date", File.class.getMethod("getModifiedTime", null));
            attributeNamesMapping.put("Last modified by Me", File.class.getMethod("getModifiedByMeTime", null));
            attributeNamesMapping.put("Last viewed by Me", File.class.getMethod("getViewedByMeTime", null));
            attributeNamesMapping.put("Last modifying user", File.class.getMethod("getLastModifyingUser", null));
            attributeNamesMapping.put("Owners", File.class.getMethod("getId", null));
            attributeNamesMapping.put("Links", File.class.getMethod("getId", null));
            attributeNamesMapping.put("Is shared", File.class.getMethod("getId", null));
            attributeNamesMapping.put("Size", File.class.getMethod("getId", null));
            attributeNamesMapping.put("Version", File.class.getMethod("getId", null));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }


    }

}
