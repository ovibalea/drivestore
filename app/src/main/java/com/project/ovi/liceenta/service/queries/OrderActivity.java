package com.project.ovi.liceenta.service.queries;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Switch;

import com.project.ovi.liceenta.R;
import com.project.ovi.liceenta.service.BaseActivity;
import com.project.ovi.liceenta.util.ProjectConstants;

/**
 * Created by Ovi on 25/08/16.
 */
public class OrderActivity extends BaseActivity {


    public static final String FOLDER_ID = "folderId";
    private String folderId;
    private Button closeButton;
    private Button orderButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_dialog);
        setButtonsListeners();
    }

    private void setButtonsListeners(){
        closeButton = (Button) findViewById(R.id.buttonCancelOrder);
        orderButton = (Button) findViewById(R.id.orderBtn);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroup radioGroup = (RadioGroup) findViewById(R.id.orderRadioGroup);
                int criteriaId = radioGroup.getCheckedRadioButtonId();
                String criteriaString = ProjectConstants.orderByMapping.get(criteriaId);
                Switch descendentSwitch = (Switch) findViewById(R.id.descendentSwitch);
                boolean isdescendent = descendentSwitch.isChecked();
                if(isdescendent){
                    criteriaString += " desc";
                }
                launchProcessing(criteriaString);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void launchProcessing(String criteria) {

        folderId = getIntent().getStringExtra(ProjectConstants.FOLDER_ID);
        new RequestItemsTask(this, "'" + folderId + "' in parents and trashed != true", criteria).execute();
    }


}
