package baia.isadora.vinylcollection;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

import baia.isadora.vinylcollection.model.Artist;
import baia.isadora.vinylcollection.persistency.ArtistDao;
import baia.isadora.vinylcollection.persistency.DiscsDatabase;
import baia.isadora.vinylcollection.utils.UtilsAlert;

public class NewArtistActivity extends AppCompatActivity {
    public static final String KEY_ID = "ID";
    public static final String KEY_MODE = "MODO";
    public static final int NEW_MODE = 0;
    public static final int EDIT_MODE = 1;

    private EditText editTextArtistName, editTextNationality;
    private int mode;
    private Artist originalArtist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_artist);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextArtistName = findViewById(R.id.editTextName);
        editTextNationality = findViewById(R.id.editTextNationality);

        Intent intentOpening = getIntent();
        Bundle bundle = intentOpening.getExtras();

        if(bundle != null){
            mode = bundle.getInt(KEY_MODE);

            if(mode == NEW_MODE) {
                setTitle(getString(R.string.new_artist));
            } else {
                setTitle(getString(R.string.edit_artist));
                long id = bundle.getLong(KEY_ID);
                DiscsDatabase database = DiscsDatabase.getInstance(this);
                originalArtist = database.getArtistDao().queryForId(id);

                editTextArtistName.setText(originalArtist.getName());
                editTextNationality.setText(originalArtist.getNationality());
                editTextArtistName.requestFocus();
                editTextArtistName.setSelection(editTextArtistName.getText().length());
            }
        }

    }

    public void clearForm() {
        final String oldName = editTextArtistName.getText().toString();
        final String oldNationality = editTextNationality.getText().toString();
        final View mainView = findViewById(R.id.main);
        final View focusedView = getCurrentFocus();

        editTextArtistName.setText(null);
        editTextNationality.setText(null);
        editTextArtistName.requestFocus();

        Snackbar snackbar = Snackbar.make(mainView, R.string.campos_apagados_com_sucesso, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextArtistName.setText(oldName);
                editTextNationality.setText(oldNationality);
                if (focusedView != null) focusedView.requestFocus();
            }
        });
        snackbar.show();
    }

    public void saveData() {
        String name = editTextArtistName.getText().toString().trim();
        String nationality = editTextNationality.getText().toString().trim();

        if (name.isEmpty()) {
            UtilsAlert.showWarning(this, R.string.insira_um_nome);
            editTextArtistName.requestFocus();
            return;
        }

        if (nationality.isEmpty()) {
            UtilsAlert.showWarning(this, R.string.insert_a_nationality);
            editTextNationality.requestFocus();
            return;
        }

        DiscsDatabase database = DiscsDatabase.getInstance(this);
        ArtistDao artistDao = database.getArtistDao();

        Artist artist = new Artist(name, nationality);

        if (mode == EDIT_MODE && originalArtist != null) {
            artist.setId(originalArtist.getId());
            int updated = artistDao.update(artist);
            if (updated != 1) {
                UtilsAlert.showWarning(this, R.string.error_trying_to_update);
                return;
            }
        } else {
            long newId = artistDao.insert(artist);
            if (newId <= 0) {
                UtilsAlert.showWarning(this, R.string.error_trying_to_insert);
                return;
            }
            artist.setId(newId);
        }

        Intent intent = new Intent(this, ListArtistsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.artist_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int idMenuItem = item.getItemId();
        if (idMenuItem == R.id.menuItemSave) {
            saveData();
            return true;
        } else if (idMenuItem == R.id.menuItemClearArtist) {
            clearForm();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}