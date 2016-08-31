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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.project.ovi.liceenta.model.DriveItem;
import com.project.ovi.liceenta.service.AuthenticateActivity;
import com.project.ovi.liceenta.service.DriveServiceManager;
import com.project.ovi.liceenta.service.activities.CreateFileActivity;
import com.project.ovi.liceenta.service.activities.CreateFolderActivity;
import com.project.ovi.liceenta.service.activities.OpenFileActivity;
import com.project.ovi.liceenta.service.activities.UploadFileActivity;
import com.project.ovi.liceenta.service.queries.FilterActivity;
import com.project.ovi.liceenta.service.queries.OrderActivity;
import com.project.ovi.liceenta.service.queries.QueryItemsByFolderIdActivity;
import com.project.ovi.liceenta.service.sms.SmsBackupActivity;
import com.project.ovi.liceenta.util.ProjectConstants;
import com.project.ovi.liceenta.view.main.DriveItemsViewAdapter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Stack;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    boolean isAuthenticated;

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

        requestAuthentication();

        populateContent("root");

    }

    private void requestAuthentication() {
        Intent authentication = new Intent(MainActivity.this, AuthenticateActivity.class);
        authentication.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(authentication, ProjectConstants.AUTHENTICATION_REQUEST);
    }

    public void populateContent(String folderId) {
        if (DriveServiceManager.getInstance() != null) {
            this.folderId = folderId;
            this.foldersVisited.push(folderId);
            Intent intent = new Intent(MainActivity.this, QueryItemsByFolderIdActivity.class);
            intent.putExtra(QueryItemsByFolderIdActivity.FOLDER_ID, folderId);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, ProjectConstants.REQUEST_CONTENT);
        }
    }

    public void sortingContent(String folderId) {
        if (DriveServiceManager.getInstance() != null) {
            this.folderId = folderId;
            this.foldersVisited.push(folderId);
            Intent intent = new Intent(MainActivity.this, OrderActivity.class);
            intent.putExtra(ProjectConstants.FOLDER_ID, folderId);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, ProjectConstants.REQUEST_CONTENT);
        }
    }

    public void filterContent() {
        if (DriveServiceManager.getInstance() != null) {
            this.foldersVisited.push(folderId);
            Intent intent = new Intent(MainActivity.this, FilterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, ProjectConstants.REQUEST_CONTENT);
        }
    }

    public void openFile(String fileId){
        if(DriveServiceManager.getInstance() != null){
            Intent intent = new Intent(MainActivity.this, OpenFileActivity.class);
            intent.putExtra(ProjectConstants.ITEM_ID_TAG, fileId);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
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
                intent.putExtra(ProjectConstants.PARENT_FOLDER_ID_TAG, folderId);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, ProjectConstants.REQUEST_CREATE_ITEM);

            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), CreateFileActivity.class);
                intent.putExtra(ProjectConstants.PARENT_FOLDER_ID_TAG, folderId);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, ProjectConstants.REQUEST_CREATE_ITEM);


            }
        });
    }


    private void expandFAB() {

        //Floating Action Button 1
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab1.getLayoutParams();
        layoutParams.bottomMargin += (int) (fab1.getHeight() * 1.5);
        fab1.setLayoutParams(layoutParams);
        fab1.startAnimation(show_fab_1);
        fab1.setClickable(true);

        //Floating Action Button 2
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) fab2.getLayoutParams();
        layoutParams2.bottomMargin += (int) (fab2.getHeight() * 3.0);
        fab2.setLayoutParams(layoutParams2);
        fab2.startAnimation(show_fab_2);
        fab2.setClickable(true);

    }


    private void hideFAB() {

        //Floating Action Button 1
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab1.getLayoutParams();
        layoutParams.bottomMargin -= (int) (fab1.getHeight() * 1.5);
        fab1.setLayoutParams(layoutParams);
        fab1.startAnimation(hide_fab_1);
        fab1.setClickable(false);

        //Floating Action Button 2
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) fab2.getLayoutParams();
        layoutParams2.bottomMargin -= (int) (fab2.getHeight() * 3.0);
        fab2.setLayoutParams(layoutParams2);
        fab2.startAnimation(hide_fab_2);
        fab2.setClickable(false);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ProjectConstants.AUTHENTICATION_REQUEST:
                isAuthenticated = data.getBooleanExtra(ProjectConstants.IS_AUTHENTICATED, false);
                if (isAuthenticated) {
                    populateContent(ProjectConstants.ROOT);
                } else {
                    requestAuthentication();
                }
                break;
            case ProjectConstants.REQUEST_CONTENT:
                if(data != null) {
                    ArrayList<DriveItem> output = (ArrayList<DriveItem>) data.getSerializableExtra(ProjectConstants.VIEW_ADAPTER_ITEMS);

                    if (output != null) {
                        driveItemsViewAdapter.updateItemsView(output);
                    }
                    recyclerView.setAdapter(driveItemsViewAdapter);
                }
                break;
            case ProjectConstants.REQUEST_PROCESS_ITEM:
                if(data != null) {
                    boolean isItemProcessed = data.getBooleanExtra(ProjectConstants.IS_ITEM_PROCESSED, true);
                    if (isItemProcessed) {
                        populateContent(folderId);
                    }
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
        MenuInflater menuInflater = getMenuInflater();
        forceShowIcons(menu);
        menuInflater.inflate(R.menu.main, menu);
        return true;
    }

    private void forceShowIcons(Menu menu) {
        if(menu.getClass().getSimpleName().equals("MenuBuilder")){
            try{
                Method m = menu.getClass().getDeclaredMethod(
                        "setOptionalIconsVisible", Boolean.TYPE);
                m.setAccessible(true);
                m.invoke(menu, true);
            }
            catch(NoSuchMethodException e){
                Log.e(TAG, "onMenuOpened", e);
            }
            catch(Exception e){
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_search:
                return true;

            case R.id.action_sort:
                sortingContent(folderId);
                return true;

            case R.id.action_filter:
                filterContent();
                return true;

            case R.id.action_reload:
                populateContent(ProjectConstants.ROOT);
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
                Intent intentUpload = new Intent(MainActivity.this, UploadFileActivity.class);
                intentUpload.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intentUpload, ProjectConstants.REQUEST_PROCESS_ITEM);
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
