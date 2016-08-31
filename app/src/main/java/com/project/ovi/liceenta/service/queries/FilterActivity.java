package com.project.ovi.liceenta.service.queries;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.project.ovi.liceenta.R;
import com.project.ovi.liceenta.service.BaseActivity;
import com.project.ovi.liceenta.util.ProjectConstants;

/**
 * Created by Ovi on 29/08/16.
 */
public class FilterActivity extends BaseActivity {

    public static final String FOLDER_ID = "folderId";
    private String folderId;
    private Button closeButton;
    private Button filterButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_dialog);
        setButtonsListeners();
    }

    private void setButtonsListeners(){
        closeButton = (Button) findViewById(R.id.buttonCancelFilter);
        filterButton = (Button) findViewById(R.id.filterBtn);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroup radioGroup = (RadioGroup) findViewById(R.id.filterRadioGroup);
                int criteriaId = radioGroup.getCheckedRadioButtonId();
                String condition = "";
                switch (criteriaId) {
                    case R.id.radioFilterSharedWithMe:
                        launchProcessing("sharedWithMe = true");
                        break;
                    case R.id.radioFilterOwnedByMe:
                        launchProcessing("'me' in owners and trashed = false");
                        break;
                    case R.id.radioFilterBookmarked:
                        launchProcessing("starred = true");
                        break;
                    case R.id.radioFilterTagged:
                        Intent selectTag = new Intent(FilterActivity.this, SelectTagDialogActivity.class);
                        selectTag.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivityForResult(selectTag, ProjectConstants.REQUEST_TAG);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ProjectConstants.REQUEST_TAG){
            String tagName = data.getStringExtra(ProjectConstants.ITEM_TAG);
            launchProcessing("properties has { key='"+ProjectConstants.ITEM_TAG+"' and value='"+tagName+"' }");
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void launchProcessing(String condition) {

        folderId = getIntent().getStringExtra(ProjectConstants.FOLDER_ID);
        new RequestItemsTask(this, condition, "modifiedTime").execute();
    }


}
