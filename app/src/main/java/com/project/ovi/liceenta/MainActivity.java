package com.project.ovi.liceenta;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.project.ovi.liceenta.model.DriveItem;
import com.project.ovi.liceenta.service.create.CreateFileActivity;
import com.project.ovi.liceenta.service.queries.QueryItemsByFolderIdActivity;
import com.project.ovi.liceenta.service.sms.SmsBackupActivity;
import com.project.ovi.liceenta.view.DriveItemsViewAdapter;

import java.util.ArrayList;
import java.util.Stack;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    //Animations
    Animation show_fab_1;
    Animation hide_fab_1;
    Animation show_fab_2;
    Animation hide_fab_2;

    FloatingActionButton fab;
    FloatingActionButton fab1;
    FloatingActionButton fab2;
    CoordinatorLayout rootLayout;
    RecyclerView recyclerView;
    DriveItemsViewAdapter driveItemsViewAdapter;

    //Save the FAB's active status
    //false -> fab = close
    //true -> fab = open
    private boolean FAB_Status = false;

    private String folderId;
    private Stack<String> foldersVisited;


    public static final int REQUEST_AUTHORIZATION = 1001;

    public static final int REQUEST_CONTENT = 1;

    public static final int REQUEST_CREATE_FILE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        driveItemsViewAdapter = new DriveItemsViewAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        foldersVisited = new Stack<String>();

        setSupportActionBar(toolbar);

        SetNavigationMenu(toolbar);

        setFabMenuActions();

        setHideFabOnReciclerViewTouch();

        populateContent("root");

    }

    public void populateContent(String folderId) {
        this.folderId = folderId;
        this.foldersVisited.push(folderId);
        Intent intent = new Intent(MainActivity.this, QueryItemsByFolderIdActivity.class);
        intent.putExtra(QueryItemsByFolderIdActivity.FOLDER_ID, folderId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, REQUEST_CONTENT);
    }


    ////SET Screen Stuff
    private void SetNavigationMenu(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setHideFabOnReciclerViewTouch() {


        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (FAB_Status) {
                    hideFAB();
                    FAB_Status = false;
                }
                return false;
            }
        });
    }

    private void setFabMenuActions() {
        rootLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        //Floating Action Buttons
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab_1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab_2);

        //Animations
        show_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.animator.fab1_show);
        hide_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.animator.fab1_hide);
        show_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.animator.fab2_show);
        hide_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.animator.fab2_hide);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (FAB_Status == false) {
                    //Display FAB menu
                    expandFAB();
                    FAB_Status = true;
                } else {
                    //Close FAB menu
                    hideFAB();
                    FAB_Status = false;
                }
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), CreateFolderActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), CreateFileActivity.class);
                intent.putExtra(CreateFileActivity.FOLDER_ID_TAG, folderId);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, REQUEST_CREATE_FILE);


            }
        });
    }


    private void expandFAB() {

        //Floating Action Button 1
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab1.getLayoutParams();
        layoutParams.bottomMargin += (int) (fab1.getHeight() * 0.75);
        fab1.setLayoutParams(layoutParams);
        fab1.startAnimation(show_fab_1);
        fab1.setClickable(true);

        //Floating Action Button 2
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) fab2.getLayoutParams();
        layoutParams2.bottomMargin += (int) (fab2.getHeight() * 1.5);
        fab2.setLayoutParams(layoutParams2);
        fab2.startAnimation(show_fab_2);
        fab2.setClickable(true);

    }


    private void hideFAB() {

        //Floating Action Button 1
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab1.getLayoutParams();
        layoutParams.bottomMargin -= (int) (fab1.getHeight() * 0.75);
        fab1.setLayoutParams(layoutParams);
        fab1.startAnimation(hide_fab_1);
        fab1.setClickable(false);

        //Floating Action Button 2
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) fab2.getLayoutParams();
        layoutParams2.bottomMargin -= (int) (fab2.getHeight() * 1.5);
        fab2.setLayoutParams(layoutParams2);
        fab2.startAnimation(hide_fab_2);
        fab2.setClickable(false);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CONTENT:
                ArrayList<DriveItem> output = (ArrayList<DriveItem>) data.getSerializableExtra(QueryItemsByFolderIdActivity.VIEW_ADAPTER_ITEMS);
                driveItemsViewAdapter.updateItemsView(output);

                recyclerView.setAdapter(driveItemsViewAdapter);
                break;
            case REQUEST_CREATE_FILE:
                boolean isFileCreated = data.getBooleanExtra(CreateFileActivity.IS_FILE_CREATED, true);
                if (isFileCreated) {
                    populateContent(folderId);
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     *
     * @param requestCode  The request code passed in
     *                     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            foldersVisited.pop();
            if (foldersVisited.size() > 0) {
                String forewardId = foldersVisited.pop();
                populateContent(forewardId);
            } else {
                moveTaskToBack(true);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_sms_backup:
                Intent intent = new Intent(getBaseContext(), SmsBackupActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.nav_sms_restore:
                break;
            case R.id.nav_slideshow:
                break;
            case R.id.nav_manage:
                break;
            case R.id.nav_share:
                break;
            case R.id.nav_send:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showToastMessage(String message) {
        Toast.makeText(this, message,
                Toast.LENGTH_LONG).show();
    }


}
