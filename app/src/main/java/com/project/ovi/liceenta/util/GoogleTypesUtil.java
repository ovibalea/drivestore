package com.project.ovi.liceenta.util;

import com.project.ovi.liceenta.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ovi on 23/08/16.
 */
public class GoogleTypesUtil {

    private static final String DEFAULT_EXTENSION = "";

    private static Map<String, String> googleMimeTypesMapping;

    private static Map<String, String> googleMimeTypesExtensionsMapping;

    private static Map<String, Long> googleMimeTypesIconsMapping;

    static {
        googleMimeTypesMapping = new HashMap<>();
        googleMimeTypesMapping.put("application/vnd.google-apps.audio", "audio/mpeg3");
        googleMimeTypesMapping.put("application/vnd.google-apps.document", "application/pdf");
        googleMimeTypesMapping.put("application/vnd.google-apps.drawing", "application/vnd.google-apps.drawing");
        googleMimeTypesMapping.put("application/vnd.google-apps.file", "text/plain");
        googleMimeTypesMapping.put("application/vnd.google-apps.folder", "");
        googleMimeTypesMapping.put("application/vnd.google-apps.form", "text/csv");
        googleMimeTypesMapping.put("application/vnd.google-apps.fusiontable", "text/csv");
        googleMimeTypesMapping.put("application/vnd.google-apps.map", "application/vnd.google-earth.kmz");
        googleMimeTypesMapping.put("application/vnd.google-apps.photo", "image/jpeg" );
        googleMimeTypesMapping.put("application/vnd.google-apps.presentation", "application/pdf");
        googleMimeTypesMapping.put("application/vnd.google-apps.script", "gscript");
        googleMimeTypesMapping.put("application/vnd.google-apps.sites", "application/pdf");
        googleMimeTypesMapping.put("application/vnd.google-apps.spreadsheet", "application/excel");
        googleMimeTypesMapping.put("application/vnd.google-apps.unknown", "application/pdf");
        googleMimeTypesMapping.put("application/vnd.google-apps.video", "video/gvi");

    }

    static {
        googleMimeTypesExtensionsMapping = new HashMap<>();
        googleMimeTypesExtensionsMapping.put("application/vnd.google-apps.audio", "mp3");
        googleMimeTypesExtensionsMapping.put("application/vnd.google-apps.document", "pdf");
        googleMimeTypesExtensionsMapping.put("application/vnd.google-apps.drawing", "gdraw");
        googleMimeTypesExtensionsMapping.put("application/vnd.google-apps.file", "txt");
        googleMimeTypesExtensionsMapping.put("application/vnd.google-apps.folder", "");
        googleMimeTypesExtensionsMapping.put("application/vnd.google-apps.form", "csv");
        googleMimeTypesExtensionsMapping.put("application/vnd.google-apps.fusiontable", "csv");
        googleMimeTypesExtensionsMapping.put("application/vnd.google-apps.map", "kml");
        googleMimeTypesExtensionsMapping.put("application/vnd.google-apps.photo", "jpg" );
        googleMimeTypesExtensionsMapping.put("application/vnd.google-apps.presentation", "pdf");
        googleMimeTypesExtensionsMapping.put("application/vnd.google-apps.script", "gscript");
        googleMimeTypesExtensionsMapping.put("application/vnd.google-apps.sites", "pdf");
        googleMimeTypesExtensionsMapping.put("application/vnd.google-apps.spreadsheet", "xls");
        googleMimeTypesExtensionsMapping.put("application/vnd.google-apps.unknown", "pdf");
        googleMimeTypesExtensionsMapping.put("application/vnd.google-apps.video", "gvi");

    }

    static {
        googleMimeTypesIconsMapping = new HashMap<>();
        googleMimeTypesIconsMapping.put("application/vnd.google-apps.audio", new Long(R.drawable.audio_icon));
        googleMimeTypesIconsMapping.put("application/vnd.google-apps.document", new Long(R.drawable.document_icon));
        googleMimeTypesIconsMapping.put("application/vnd.google-apps.drawing", new Long(R.drawable.drawing_icon));
        googleMimeTypesIconsMapping.put("application/vnd.google-apps.file", new Long(R.drawable.file_icon));
        googleMimeTypesIconsMapping.put("application/vnd.google-apps.folder", new Long(R.drawable.folder2));
        googleMimeTypesIconsMapping.put("application/vnd.google-apps.form", new Long(R.drawable.forms_icon));
        googleMimeTypesIconsMapping.put("application/vnd.google-apps.fusiontable", new Long(R.drawable.fusion_tables_icon));
        googleMimeTypesIconsMapping.put("application/vnd.google-apps.map", new Long(R.drawable.map_icon));
        googleMimeTypesIconsMapping.put("application/vnd.google-apps.photo", new Long(R.drawable.photo_icon));
        googleMimeTypesIconsMapping.put("application/vnd.google-apps.presentation", new Long(R.drawable.presentation_icon));
        googleMimeTypesIconsMapping.put("application/vnd.google-apps.script", new Long(R.drawable.script_icon));
        googleMimeTypesIconsMapping.put("application/vnd.google-apps.sites", new Long(R.drawable.sites_icon));
        googleMimeTypesIconsMapping.put("application/vnd.google-apps.spreadsheet", new Long(R.drawable.sheets_icon));
        googleMimeTypesIconsMapping.put("application/vnd.google-apps.unknown", new Long(R.drawable.unknow_icon));
        googleMimeTypesIconsMapping.put("application/vnd.google-apps.video", new Long(R.drawable.video_icon));
        googleMimeTypesIconsMapping.put("text/html", new Long(R.drawable.document_icon));
        googleMimeTypesIconsMapping.put("text/plain", new Long(R.drawable.document_icon));
        googleMimeTypesIconsMapping.put("application/rtf", new Long(R.drawable.document_icon));
        googleMimeTypesIconsMapping.put("application/vnd.oasis.opendocument.text", new Long(R.drawable.document_icon));
        googleMimeTypesIconsMapping.put("application/pdf", new Long(R.drawable.document_icon));
        googleMimeTypesIconsMapping.put("text/csv", new Long(R.drawable.sheets_icon));
        googleMimeTypesIconsMapping.put("image/jpeg", new Long(R.drawable.photo_icon));
        googleMimeTypesIconsMapping.put("image/png", new Long(R.drawable.photo_icon));
        googleMimeTypesIconsMapping.put("image/jpg", new Long(R.drawable.photo_icon));

    }


    public static String getExtensionByGoogleMimeType(String googleMimeType){
        if(googleMimeTypesExtensionsMapping.get(googleMimeType) != null) {
            return googleMimeTypesExtensionsMapping.get(googleMimeType);
        } else {
            return DEFAULT_EXTENSION;
        }
    }

    public static String getMimeTypeByGoogleMimeType(String googleMimetype){
        return googleMimeTypesMapping.get(googleMimetype);
    }

    public static Long getIconId(String googleMimeType){
        if(googleMimeTypesIconsMapping.get(googleMimeType) != null) {
            return googleMimeTypesIconsMapping.get(googleMimeType);
        } else {
            return new Long(R.drawable.unknow_icon);
        }
    }

    public static boolean isGoogleType(String mimeType){
        return googleMimeTypesExtensionsMapping.get(mimeType) != null;
    }

}
