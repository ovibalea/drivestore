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
public class SelectTagDialogActivity extends BaseActivity{

    private Button selectButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tag_item_dialog);
        setButtonListener();

    }

    private void setButtonListener(){
        selectButton = (Button) findViewById(R.id.setTagBtn);
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroup radioGroup = (RadioGroup) findViewById(R.id.tagRadioGroup);
                int tagId = radioGroup.getCheckedRadioButtonId();
                String tagName = ProjectConstants.tagIdNameMapping.get(tagId);
                Intent intent = new Intent();
                intent.putExtra(ProjectConstants.ITEM_TAG, tagName);
                setResult(ProjectConstants.REQUEST_TAG, intent);
                finish();
            }
        });
    }



    @Override
    public void onBackPressed() {
        finish();
    }

}
