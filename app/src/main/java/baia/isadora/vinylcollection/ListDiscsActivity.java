package baia.isadora.vinylcollection;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import baia.isadora.vinylcollection.model.Disc;
import baia.isadora.vinylcollection.model.DiscSpeed;
import baia.isadora.vinylcollection.persistency.DiscsDatabase;
import baia.isadora.vinylcollection.utils.UtilsAlert;

public class ListDiscsActivity extends AppCompatActivity {
    private ListView listViewDiscs;
    private List<Disc> listDiscs;
    private DiscAdapter adapterDisc;
    private int selectedPosition = -1;
    private ActionMode actionMode;
    private View selectedView;
    private Drawable backgroundDrawable;
    public static final String PREFERENCES_FILE = "baia.isadora.vinylcollection.PREFERENCES";
    public static final String KEY_GROWING_SORT = "GROWING_SORT";
    public static final boolean DEFAULT_PREFERENCES_SORT = true;
    private boolean sortGrowing = DEFAULT_PREFERENCES_SORT;
    private MenuItem menuItemSort;
    private ActionMode.Callback actionCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflate = mode.getMenuInflater();
            inflate.inflate(R.menu.listdiscs_itemselected, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int idMenuItem = item.getItemId();

            if(idMenuItem == R.id.menuItemEdit){
                editDisc();
                return true;
            } else if (idMenuItem == R.id.menuItemDelete){
                deleteDisc();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if(selectedView != null){
                selectedView.setBackground(backgroundDrawable);
            }

            actionMode = null;
            selectedView = null;
            backgroundDrawable = null;

            listViewDiscs.setEnabled(true);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_discs);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setTitle(getString(R.string.my_discs_title));

        listViewDiscs = findViewById(R.id.listViewDiscs);

        readPreferences();

        populateListDiscs();
    }

    private void populateListDiscs(){
        DiscsDatabase database = DiscsDatabase.getInstance(this);

        if(sortGrowing){
            listDiscs = database.getDiscDao().queryAllAscending();
        } else {
            listDiscs = database.getDiscDao().queryAllDownward();
        }

        adapterDisc = new DiscAdapter(listDiscs, this);

        listViewDiscs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = position;
                editDisc();
            }
        });

        listViewDiscs.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (actionMode != null){
                    return false;
                }

                selectedPosition = position;
                selectedView = view;
                backgroundDrawable = view.getBackground();

                view.setBackgroundColor(getColor(R.color.selectedColor));
                listViewDiscs.setEnabled(false);
                actionMode = startSupportActionMode(actionCallback);

                return true;
            }
        });

        listViewDiscs.setAdapter(adapterDisc);
    }

    public void openAbout (){
        Intent intentOpening = new Intent(this, AboutActivity.class);

        startActivity(intentOpening);
    }

    ActivityResultLauncher<Intent> launcherNewDisc = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == ListDiscsActivity.RESULT_OK){
                        Intent intent = result.getData();

                        Bundle bundle = intent.getExtras();
                        if (bundle != null){
                            long id = bundle.getLong(NewVinylActivity.KEY_ID);

                            DiscsDatabase database = DiscsDatabase.getInstance(ListDiscsActivity.this);
                            Disc disc = database.getDiscDao().queryForId(id);

                            listDiscs.add(disc);

                            sortList();
                        }
                    }
                }
            });

    public void openNewDisc () {
        Intent intentOpening = new Intent(this, NewVinylActivity.class);
        intentOpening.putExtra(NewVinylActivity.KEY_MODE, NewVinylActivity.NEW_MODE);
        launcherNewDisc.launch(intentOpening);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.listdiscs_options, menu);
        menuItemSort = menu.findItem(R.id.menuItemSort);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        updateIconSort();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int idMenuItem = item.getItemId();
        if(idMenuItem == R.id.menuItemAdd){
            openNewDisc();
            return true;
        } else if (idMenuItem == R.id.menuItemAbout){
            openAbout();
            return true;
        } else if (idMenuItem == R.id.menuItemSort){
            saveSortPreferences(!sortGrowing);
            updateIconSort();
            sortList();
            return true;
        } else if(idMenuItem == R.id.menuItemRestore){
            confirmRestorePreferences();
            return true;
        } else if (idMenuItem == R.id.menuItemAddArtist) {
            openNewArtist();
            return true;
        } else if (idMenuItem == R.id.menuItemViewArtists) {
            openListArtists();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void deleteDisc(){
        final Disc disc = listDiscs.get(selectedPosition);
        String message = getString(R.string.do_you_want_to_delete, disc.getName());
        DialogInterface.OnClickListener listenerYes = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DiscsDatabase database = DiscsDatabase.getInstance(ListDiscsActivity.this);
                int alteredQuant = database.getDiscDao().delete(disc);

                if(alteredQuant != 1){
                    UtilsAlert.showWarning(ListDiscsActivity.this, R.string.error_trying_to_delete);
                    return;
                }

                listDiscs.remove(selectedPosition);
                adapterDisc.notifyDataSetChanged();
                actionMode.finish();
            }
        };
        UtilsAlert.confirmAction(this, message, listenerYes, null);
    }

    ActivityResultLauncher<Intent> launcherEditDisc = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == ListDiscsActivity.RESULT_OK){
                        Intent intent = result.getData();

                        Bundle bundle = intent.getExtras();
                        if (bundle != null){
                            final Disc originalDisc = listDiscs.get(selectedPosition);

                            long id = bundle.getLong(NewVinylActivity.KEY_ID);
                            final DiscsDatabase database = DiscsDatabase.getInstance(ListDiscsActivity.this);
                            final Disc editedDisc = database.getDiscDao().queryForId(id);

                            listDiscs.set(selectedPosition, editedDisc);

                            sortList();

                            final ConstraintLayout constraintLayout = findViewById(R.id.main);

                            Snackbar snackBar = Snackbar.make(constraintLayout, R.string.data_updated_successfully, Snackbar.LENGTH_LONG);

                            snackBar.setAction(R.string.undo, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int alteredQuant = database.getDiscDao().update(originalDisc);
                                    if(alteredQuant != 1){
                                        UtilsAlert.showWarning(ListDiscsActivity.this, R.string.error_trying_to_update);
                                        return;
                                    }

                                    listDiscs.remove(editedDisc);
                                    listDiscs.add(originalDisc);

                                    sortList();
                                }
                            });

                            snackBar.show();
                        }
                    }
                    selectedPosition = -1;
                    if(actionMode != null){
                        actionMode.finish();
                    }
                }
            });

    private void editDisc(){
        Disc disc = listDiscs.get(selectedPosition);
        Intent intentOpening = new Intent(this, NewVinylActivity.class);
        intentOpening.putExtra(NewVinylActivity.KEY_MODE, NewVinylActivity.EDIT_MODE);

        intentOpening.putExtra(NewVinylActivity.KEY_ID, disc.getId());

        launcherEditDisc.launch(intentOpening);
    }

    private void sortList(){
        if(sortGrowing) {
            Collections.sort(listDiscs, Disc.growingOrder);
        } else {
            Collections.sort(listDiscs, Disc.decreasingOrder);
        }
        adapterDisc.notifyDataSetChanged();
    }

    private void updateIconSort(){
        if(sortGrowing){
            menuItemSort.setIcon(R.drawable.ic_action_growingorder);
        } else {
            menuItemSort.setIcon(R.drawable.ic_action_decreasingorder);
        }
    }
    private void readPreferences(){
        SharedPreferences shared = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);

        sortGrowing = shared.getBoolean(KEY_GROWING_SORT, sortGrowing);
    }

    private void saveSortPreferences(boolean newValue){
        SharedPreferences shared = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = shared.edit();
        editor.putBoolean(KEY_GROWING_SORT, newValue);

        editor.commit();
        sortGrowing = newValue;
    }

    private void confirmRestorePreferences(){
        DialogInterface.OnClickListener listenerYes = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                restorePreferences();
                updateIconSort();
                sortList();

                Toast.makeText(ListDiscsActivity.this, R.string.config_back_to_default, Toast.LENGTH_LONG).show();
            }
        };
        UtilsAlert.confirmAction(this, getString(R.string.do_you_want_to_restore_preferences), listenerYes, null);
    }
    private void restorePreferences(){
        SharedPreferences shared = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = shared.edit();

        editor.clear();
        editor.commit();

        sortGrowing = DEFAULT_PREFERENCES_SORT;
    }
    private void openNewArtist() {
        Intent intentOpening = new Intent(this, NewArtistActivity.class);
        intentOpening.putExtra(NewArtistActivity.KEY_MODE, NewArtistActivity.NEW_MODE);
        startActivity(intentOpening);
    }
    private void openListArtists() {
        Intent intentOpening = new Intent(this, ListArtistsActivity.class);
        startActivity(intentOpening);
    }
}