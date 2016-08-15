package com.project.ovi.liceenta.util;

import java.util.HashMap;
import java.util.Map;
import com.project.ovi.liceenta.R;

/**
 * Created by Ovi on 15/08/16.
 */
public class GoogleTypesUtil {

    private static Map<String, Long> googleMimeTypesMapping;

    static {
        googleMimeTypesMapping = new HashMap<>();
        googleMimeTypesMapping.put("application/vnd.google-apps.audio", new Long(R.drawable.audio_icon));
        googleMimeTypesMapping.put("application/vnd.google-apps.document", new Long(R.drawable.document_icon));
        googleMimeTypesMapping.put("application/vnd.google-apps.drawing", new Long(R.drawable.drawing_icon));
        googleMimeTypesMapping.put("application/vnd.google-apps.file", new Long(R.drawable.file_icon));
        googleMimeTypesMapping.put("application/vnd.google-apps.folder", new Long(R.drawable.folder2));
        googleMimeTypesMapping.put("application/vnd.google-apps.form", new Long(R.drawable.forms_icon));
        googleMimeTypesMapping.put("application/vnd.google-apps.fusiontable", new Long(R.drawable.fusion_tables_icon));
        googleMimeTypesMapping.put("application/vnd.google-apps.map", new Long(R.drawable.map_icon));
        googleMimeTypesMapping.put("application/vnd.google-apps.photo", new Long(R.drawable.photo_icon));
        googleMimeTypesMapping.put("application/vnd.google-apps.presentation", new Long(R.drawable.presentation_icon));
        googleMimeTypesMapping.put("application/vnd.google-apps.script", new Long(R.drawable.script_icon));
        googleMimeTypesMapping.put("application/vnd.google-apps.sites", new Long(R.drawable.sites_icon));
        googleMimeTypesMapping.put("application/vnd.google-apps.spreadsheet", new Long(R.drawable.sheets_icon));
        googleMimeTypesMapping.put("application/vnd.google-apps.unknown", new Long(R.drawable.unknow_icon));
        googleMimeTypesMapping.put("application/vnd.google-apps.video", new Long(R.drawable.video_icon));
    }


    public static Long getIconId(String googleMimeType){
        if(googleMimeTypesMapping.get(googleMimeType) != null) {
            return googleMimeTypesMapping.get(googleMimeType);
        } else {
            return new Long(R.drawable.unknow_icon);
        }
    }




}
