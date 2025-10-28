package baia.isadora.vinylcollection;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
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

import java.util.Collections;
import java.util.List;

import baia.isadora.vinylcollection.model.Artist;
import baia.isadora.vinylcollection.persistency.DiscsDatabase;
import baia.isadora.vinylcollection.utils.UtilsAlert;

public class ListArtistsActivity extends AppCompatActivity {
    private ListView listViewArtists;
    private List<Artist> listArtists;
    private ArtistAdapter adapterArtist;
    private int selectedPosition = -1;
    private ActionMode actionMode;
    private View selectedView;
    private Drawable backgroundDrawable;
    private boolean sortGrowing = true;
    private MenuItem menuItemSort;
    private final ActionMode.Callback actionCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.listdiscs_itemselected, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int idMenuItem = item.getItemId();
            if (idMenuItem == R.id.menuItemEdit) {
                editArtist();
                return true;
            } else if (idMenuItem == R.id.menuItemDelete) {
                deleteArtist();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if (selectedView != null) {
                selectedView.setBackground(backgroundDrawable);
            }

            actionMode = null;
            selectedView = null;
            backgroundDrawable = null;

            listViewArtists.setEnabled(true);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_artists);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setTitle(getString(R.string.my_artists));
        listViewArtists = findViewById(R.id.listViewArtists);
        populateListArtists();
    }

    private void populateListArtists() {
        DiscsDatabase database = DiscsDatabase.getInstance(this);
        if(sortGrowing){
            listArtists = database.getArtistDao().queryAllAscending();
        } else {
            listArtists = database.getArtistDao().queryAllDownward();
        }

        adapterArtist = new ArtistAdapter(listArtists, this);
        listViewArtists.setAdapter(adapterArtist);

        listViewArtists.setOnItemClickListener((parent, view, position, id) -> {
            selectedPosition = position;
            editArtist();
        });

        listViewArtists.setOnItemLongClickListener((parent, view, position, id) -> {
            if (actionMode != null) return false;

            selectedPosition = position;
            selectedView = view;
            backgroundDrawable = view.getBackground();

            view.setBackgroundColor(getColor(R.color.selectedColor));
            listViewArtists.setEnabled(false);
            actionMode = startSupportActionMode(actionCallback);

            return true;
        });
    }
    ActivityResultLauncher<Intent> launcherNewArtist = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null && intent.getExtras() != null) {
                        long id = intent.getExtras().getLong(NewArtistActivity.KEY_ID);
                        DiscsDatabase database = DiscsDatabase.getInstance(this);
                        Artist artist = database.getArtistDao().queryForId(id);

                        listArtists.add(artist);
                        sortList();
                    }
                }
            });
    ActivityResultLauncher<Intent> launcherEditArtist = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null && intent.getExtras() != null) {
                        final Artist originalArtist = listArtists.get(selectedPosition);
                        long id = intent.getExtras().getLong(NewArtistActivity.KEY_ID);
                        final DiscsDatabase database = DiscsDatabase.getInstance(this);
                        final Artist editedArtist = database.getArtistDao().queryForId(id);

                        listArtists.set(selectedPosition, editedArtist);
                        sortList();

                        final ConstraintLayout layout = findViewById(R.id.main);

                        Snackbar snackbar = Snackbar.make(layout, R.string.data_updated_successfully, Snackbar.LENGTH_LONG);
                        snackbar.setAction(R.string.undo, v -> {
                            int altered = database.getArtistDao().update(originalArtist);
                            if (altered != 1) {
                                UtilsAlert.showWarning(ListArtistsActivity.this, R.string.error_trying_to_update);
                                return;
                            }
                            listArtists.remove(editedArtist);
                            listArtists.add(originalArtist);
                            sortList();
                        });
                        snackbar.show();
                    }
                    selectedPosition = -1;
                    if (actionMode != null) actionMode.finish();
                }
            });
    private void editArtist() {
        Artist artist = listArtists.get(selectedPosition);
        Intent intent = new Intent(this, NewArtistActivity.class);
        intent.putExtra(NewArtistActivity.KEY_MODE, NewArtistActivity.EDIT_MODE);
        intent.putExtra(NewArtistActivity.KEY_ID, artist.getId());
        launcherEditArtist.launch(intent);
    }

    private void deleteArtist() {
        final Artist artist = listArtists.get(selectedPosition);
        String message = getString(R.string.do_you_want_to_delete, artist.getName());
        DialogInterface.OnClickListener listenerYes = (dialog, which) -> {
            DiscsDatabase database = DiscsDatabase.getInstance(this);
            int alteredQuant = database.getArtistDao().delete(artist);

            if (alteredQuant != 1) {
                UtilsAlert.showWarning(this, R.string.error_trying_to_delete);
                return;
            }

            listArtists.remove(selectedPosition);
            adapterArtist.notifyDataSetChanged();
            actionMode.finish();
        };
        UtilsAlert.confirmAction(this, message, listenerYes, null);
    }
    private void sortList() {
        if (sortGrowing) {
            Collections.sort(listArtists, Artist.growingOrder);
        } else {
            Collections.sort(listArtists, Artist.decreasingOrder);
        }
        if (adapterArtist != null) adapterArtist.notifyDataSetChanged();
    }

    private void updateIconSort() {
        if (sortGrowing) {
            menuItemSort.setIcon(R.drawable.ic_action_growingorder);
        } else {
            menuItemSort.setIcon(R.drawable.ic_action_decreasingorder);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.listartists_options, menu);
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
        if (idMenuItem == R.id.menuItemAdd) {
            openNewArtist();
            return true;
        } else if (idMenuItem == R.id.menuItemSort) {
            sortGrowing = !sortGrowing;
            updateIconSort();
            sortList();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
    private void openNewArtist() {
        Intent intent = new Intent(this, NewArtistActivity.class);
        intent.putExtra(NewArtistActivity.KEY_MODE, NewArtistActivity.NEW_MODE);
        launcherNewArtist.launch(intent);
    }
}