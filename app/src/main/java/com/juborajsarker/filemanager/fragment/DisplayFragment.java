package com.juborajsarker.filemanager.fragment;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcel;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.juborajsarker.filemanager.R;
import com.juborajsarker.filemanager.activity.MainActivity;
import com.juborajsarker.filemanager.adapter.DisplayFragmentAdapter;
import com.juborajsarker.filemanager.dialog.OnLongPressDialog;
import com.juborajsarker.filemanager.manager.EventManager;

import java.io.File;
import java.util.ArrayList;


public class DisplayFragment extends android.app.Fragment {

    public Handler mHandler = new Handler();

    LinearLayout customCardView;

    View view;


    private final String PREF_IS_CASE_SENSITIVE = "IS_CASE_SENSITIVE";
    private final String PREF_SHOW_HIDDEN_FILES = "SHOW_HIDDEN_FILES";
    private final String PREF_SORT_ORDER = "SORT_ORDER";
    private final String PREF_SORT_BY = "SORT_BY";


    private RecyclerView recyclerView;
    private RecyclerView recyclerView2;

    private File path;
    private File path2;

    private ArrayList<File> filesAndFolders;
    private ArrayList<File> filesAndFolders2;

    private Toolbar toolbar;
    private Toolbar toolbar2;

    private DisplayFragmentAdapter adapter;
    private DisplayFragmentAdapter adapter2;

    private ActionMode actionMode;

    private DialogFragment longPressDialog;

    private SharedPreferences prefs;
    private SharedPreferences prefs2;

    private boolean clickAllowed;
    private boolean clickAllowed2;

    public Context context;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String temp;
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)

            temp = "/";

        else temp = Environment.getExternalStorageDirectory().toString();

        path = new File(temp);

    }

    @Override
    public void onResume() {
        super.onResume();
        setPrefs();
        EventManager.getInstance().refreshCurrentDirectory();
        // mHandler.postDelayed(mRunnable, 1000);
    }



    @Override
    public void onPause() {

        // mHandler.removeCallbacks(mRunnable);
        super.onPause();

    }



//    public final Runnable mRunnable = new Runnable() {
//        public void run() {
//
//            int number = 0;
//
//            MainActivity activity = (MainActivity) getActivity();
//            String myDataFromActivity = activity.getMyData();
//
//            number = Integer.parseInt(myDataFromActivity);
//
//            recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
//            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),number);
//            gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
//
//            toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
//            toolbar.setTitle(path.getName());
//
//            filesAndFolders = new ArrayList<>();
//
//            adapter = new DisplayFragmentAdapter(filesAndFolders,onItemClickListenerCallback,getActivity());
//            EventManager.getInstance().init(getActivity(), DisplayFragment.this, filesAndFolders, adapter);
//
//            prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
//
//            setPrefs();
//            EventManager.getInstance().getFileManager().initialisePathStackWithAbsolutePath(true,path);
//
//            recyclerView.setLayoutManager(gridLayoutManager);
//            EventManager.getInstance().open(path);
//            recyclerView.setAdapter(adapter);
//
//            clickAllowed = true;
//
//
//
//            mHandler.postDelayed(mRunnable, 1000);
//
//        }
//    };








    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_display,container,false);

        setRetainInstance(true);




        gridView();
        return view;
    }


    private void gridView() {


        int number = 0;

        MainActivity activity = (MainActivity) getActivity();
        String myDataFromActivity = activity.getMyData();

        number = Integer.parseInt(myDataFromActivity);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),number);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(path.getName());

        filesAndFolders = new ArrayList<>();

        adapter = new DisplayFragmentAdapter(filesAndFolders,onItemClickListenerCallback,getActivity());
        EventManager.getInstance().init(getActivity(), this, filesAndFolders, adapter);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        setPrefs();
        EventManager.getInstance().getFileManager().initialisePathStackWithAbsolutePath(true,path);

        recyclerView.setLayoutManager(gridLayoutManager);
        EventManager.getInstance().open(path);
        recyclerView.setAdapter(adapter);

        clickAllowed = true;



    }






    private void setPrefs() {

        EventManager
                .getInstance()
                .getFileManager()
                .setShowHiddenFiles(
                        prefs.getBoolean(PREF_IS_CASE_SENSITIVE, false)
                );

        EventManager
                .getInstance()
                .getFileManager()
                .setSortingStyle(
                        prefs.getString(PREF_SORT_ORDER, EventManager.SORT_ORDER_ASC),
                        prefs.getString(PREF_SORT_BY, EventManager.SORT_BY_NAME),
                        prefs.getBoolean(PREF_SHOW_HIDDEN_FILES, false)
                );

    }

    private DisplayFragmentAdapter.OnItemClickListener onItemClickListenerCallback =
            new DisplayFragmentAdapter.OnItemClickListener() {

                @Override
                public void onItemClick(View view, int position) {
                    File singleItem = filesAndFolders.get(position);

                    if(clickAllowed)
                        EventManager.getInstance().open(singleItem);

                }

                @Override
                public void onItemLongClick(View view, int position) {

                    if(clickAllowed) {
                        longPressDialog = OnLongPressDialog.newInstance(onLongPressListenerCallback,position);
                        longPressDialog.show(getFragmentManager(), "onLongPressDialog");
                    }
                }

                @Override
                public void onIconClick(View view, int position) {

                    clickAllowed = false;

                    if (actionMode != null) {

                        adapter.toggleSelection(position);
                        actionMode.setTitle(adapter.getSelectedItemsCount() + "  " + getString(R.string.info_items_selected));

                        if (adapter.getSelectedItemsCount() <= 0)
                            actionMode.finish();

                        return;
                    }

                    actionMode = getActivity().startActionMode(actionModeCallback);
                    adapter.toggleSelection(position);
                    actionMode.setTitle(adapter.getSelectedItemsCount() + "  " + getString(R.string.info_items_selected));
                }
            };



    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater menuInflater = mode.getMenuInflater();
            menuInflater.inflate(R.menu.menu_action_mode, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, final MenuItem item) {

            switch (item.getItemId()) {

                case R.id.shareButton1 :
                    Toast.makeText(getActivity(), "Share Button Clicked", Toast.LENGTH_SHORT).show();
                    mode.finish();
                    return true;

                case R.id.deleteButton1 :
                    EventManager.getInstance().delete(adapter.getSelectedItems());
                    mode.finish();
                    return true;

                case R.id.moveButton1 :
                case R.id.copyButton1 :

                    selectTargetAndPerformOperation(adapter.getSelectedItems(), item.getItemId());
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }



        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
            adapter.clearSelection();
            clickAllowed = true;
        }

    };


    private OnLongPressDialog.OnLongPressListener  onLongPressListenerCallback = new  OnLongPressDialog.OnLongPressListener() {

        @Override
        public void onOpenButtonClicked(int position) {

            EventManager
                    .getInstance()
                    .open(filesAndFolders.get(position));
            longPressDialog.dismiss();
        }

        @Override
        public void onShareButtonClicked(int position) {

            ArrayList<File> files = new ArrayList<File>();
            files.add(filesAndFolders.get(position));
            EventManager.getInstance().share(files);
            longPressDialog.dismiss();
        }

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onDeleteButtonClicked(final int position) {

            Context mContext = getContext();

            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Light_Dialog);
            } else {
                builder = new AlertDialog.Builder(mContext);
            }
            builder.setTitle("Delete entry")
                    .setMessage("Are you sure you want to really delete this entry?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete

                            dialog.cancel();
                            ArrayList<File> files = new ArrayList<>();
                            files.add(filesAndFolders.get(position));
                            EventManager.getInstance().delete(files);
                            longPressDialog.dismiss();

                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing

                            dialog.cancel();
                            longPressDialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        }

        @Override
        public void onRenameButtonClicked(int position) {

            promptUserForRenameInput(filesAndFolders.get(position));
            longPressDialog.dismiss();
        }

        @Override
        public void onCopyButtonClicked(int position) {

            ArrayList<File> list = new ArrayList<>();
            list.add(filesAndFolders.get(position));
            selectTargetAndPerformOperation(list, R.id.copyButton1);
            longPressDialog.dismiss();
        }

        @Override
        public void onMoveButtonClicked(int position) {

            ArrayList<File> list = new ArrayList<>();
            list.add(filesAndFolders.get(position));
            selectTargetAndPerformOperation(list, R.id.moveButton1);
            longPressDialog.dismiss();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {

        }

        @Override
        public int describeContents() {
            return 0;
        }
    };


    private void selectTargetAndPerformOperation(final ArrayList<File> list,final int id) {

        Toast.makeText(getActivity(), getString(R.string.prompt_select_destination), Toast.LENGTH_SHORT).show();
        toolbar.inflateMenu(R.menu.menu_copy_move);

        toolbar.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                adapter.clearSelection();
                toolbar
                        .getMenu()
                        .clear();

                toolbar.inflateMenu(R.menu.menu_main);
            }
        });

        toolbar.findViewById(R.id.selectButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                File target = EventManager.getInstance()
                        .getFileManager()
                        .getCurrentDirectory();

                switch (id) {

                    case R.id.copyButton1:
                        EventManager
                                .getInstance()
                                .copy(list, target);
                        break;

                    case R.id.moveButton1:
                        EventManager
                                .getInstance()
                                .move(list, target);
                        break;
                }

                adapter.clearSelection();
                toolbar
                        .getMenu()
                        .clear();

                toolbar.inflateMenu(R.menu.menu_main);

            }
        });

    }

    private void promptUserForRenameInput(final File file) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final EditText editText = new EditText(getActivity());
        editText.setText(file.getName());

        builder.setMessage(getString(R.string.prompt_rename_newName))
                .setView(editText)
                .setPositiveButton(getString(R.string.rename), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(EventManager.getInstance().getFileManager().renameFileTo(file,editText.getText().toString())) {
                            Toast.makeText(getActivity(), getString(R.string.success_rename), Toast.LENGTH_SHORT).show();
                            EventManager.getInstance().populateList(EventManager.getInstance().getFileManager().getCurrentDirectory());
                        }
                        else Toast.makeText(getActivity(),getString(R.string.error_rename),Toast.LENGTH_SHORT).show();

                    }
                })
                .setNegativeButton( getString(R.string.cancel), null)
                .create()
                .show();
    }


    public Toolbar getToolbar() {
        return toolbar;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }





}