package com.juborajsarker.filemanager.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.juborajsarker.filemanager.R;
import com.juborajsarker.filemanager.fragment.DisplayFragment;
import com.juborajsarker.filemanager.manager.EventManager;
import com.juborajsarker.filemanager.manager.FileManager;

import java.io.File;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    final String DISPLAY_FRAGMENT_TAG = "displayFragment";

    public  String viewValue = "";
    public int mState = 0;
    public int count = 0;


    Bundle bundle;
    DisplayFragment displayFragment;

    DrawerLayout drawerLayout;
    ListView drawerListView;
    FragmentManager fm;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;

    EventManager eventManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer);


        viewValue = "1";







        requestForPermission();

        init();

        StrictMode.setThreadPolicy(
                new StrictMode.ThreadPolicy.Builder().detectAll()
                        .penaltyLog().build());

        if(fm.findFragmentById(R.id.RelativeLayoutMain) == null) {
            DisplayFragment displayFragment = new DisplayFragment();
            setSupportActionBar(toolbar);

            fm.beginTransaction()
                    .add(R.id.RelativeLayoutMain, displayFragment,DISPLAY_FRAGMENT_TAG)
                    .addToBackStack(DISPLAY_FRAGMENT_TAG)
                    .commit();
        }
    }


    public String getMyData() {

        String data = viewValue;

        return data;
    }


    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }


    private void init() {

        fm = getFragmentManager();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //drawerListView = (ListView) findViewById(R.id.drawerListView);

        toggle = new ActionBarDrawerToggle(MainActivity.this,
                drawerLayout,
                toolbar,
                R.string.open,
                R.string.cancel);

        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void requestForPermission() {

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                promptForPermissionsDialog(getString(R.string.error_request_permission), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                100);
                    }
                });

            } else {

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        100);
            }
        }
    }

    private void promptForPermissionsDialog(String message, DialogInterface.OnClickListener onClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setMessage(message)
                .setPositiveButton(getString(R.string.ok), onClickListener)
                .setNegativeButton(getString(R.string.cancel), null)
                .create()
                .show();

    }

    @Override
    public void onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return;
        }

        count = EventManager.getInstance().getFileManager().getPathStackItemsCount();

        if (count == 1){

            eventManager.refreshCurrentDirectory();
            super.onBackPressed();

        }


        else {

            EventManager.getInstance().moveUpDirectory();



        }

    }










    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);




        //configureSearchView(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }


        switch (item.getItemId()) {


            case R.id.action_back:{


                if (count == 1){

                    eventManager.refreshCurrentDirectory();
                    super.onBackPressed();
                }

                else {

                    EventManager.getInstance().moveUpDirectory();

                }

                return true;
            }
            case R.id.searchView :
                Toast.makeText(MainActivity.this,"search clicked",Toast.LENGTH_SHORT).show();
                onSearchRequested();
                return true;

            case R.id.newFolder:
                createNewFolderInCurrDirectory();
                return true;

            case R.id.action_settings:
                Intent intent = new Intent(this,PrefsActivity.class);
                // bypassing the single header
                intent.putExtra( PrefsActivity.EXTRA_SHOW_FRAGMENT, PrefsActivity.Header1.class.getName() );
                intent.putExtra( PrefsActivity.EXTRA_NO_HEADERS, true );
                startActivity(intent);
                return true;

            case R.id.list_view:{


                viewValue = "1";
                getMyData();


                if(fm.findFragmentById(R.id.RelativeLayoutMain) != null) {

                    DisplayFragment displayFragment = new DisplayFragment();
                    setSupportActionBar(toolbar);

                    fm.beginTransaction()
                            .replace(R.id.RelativeLayoutMain, displayFragment,DISPLAY_FRAGMENT_TAG)
                            .addToBackStack(DISPLAY_FRAGMENT_TAG)
                            .commit();
                }


                return true;

            }


            case R.id.grid_view: {

                viewValue = "2";
                getMyData();




                if(fm.findFragmentById(R.id.RelativeLayoutMain) != null) {


                    DisplayFragment displayFragment = new DisplayFragment();
                    setSupportActionBar(toolbar);

                    fm.beginTransaction()
                            .replace(R.id.RelativeLayoutMain, displayFragment,DISPLAY_FRAGMENT_TAG)
                            .addToBackStack(DISPLAY_FRAGMENT_TAG)
                            .commit();



                }





                return true;
            }







        }

        return super.onOptionsItemSelected(item);
    }



    /*
        private void configureSearchView(Menu menu) {

            // Associate searchable configuration with the SearchView
            SearchManager searchManager =
                    (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView =
                    (SearchView) menu.findItem(R.id.searchView).getActionView();
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getComponentName()));


        }

        protected SearchView.OnQueryTextListener searchViewListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        };


        protected SearchView.OnCloseListener searchViewCloseListener = new SearchView.OnCloseListener() {

            @Override
            public boolean onClose() {
                return false;
            }
        };

    */
    private void createNewFolderInCurrDirectory() {

        final FileManager fileManager = EventManager.getInstance().getFileManager();

        final File dir = fileManager.getCurrentDirectory();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText editText = new EditText(this);

        builder.setMessage(getString(R.string.prompt_create_folder))
                .setView(editText)
                .setPositiveButton(getString(R.string.create), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(fileManager.newFolder(dir,editText.getText().toString())) {
                            Toast.makeText(MainActivity.this, getString(R.string.success_create_folder), Toast.LENGTH_SHORT).show();
                            EventManager
                                    .getInstance()
                                    .populateList(fileManager.getCurrentDirectory());
                        }
                        else Toast.makeText(MainActivity.this,getString(R.string.error_create_folder),Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(getString(R.string.cancel),null)
                .create()
                .show();

    }



    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

            Toast.makeText(this, "Gallery", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}