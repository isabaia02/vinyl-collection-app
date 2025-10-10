package baia.isadora.vinylcollection;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

import baia.isadora.vinylcollection.model.Disc;
import baia.isadora.vinylcollection.model.DiscSpeed;
import baia.isadora.vinylcollection.utils.UtilsAlert;

public class NewVinylActivity extends AppCompatActivity {

    public static final String KEY_NOME = "KEY_NOME";
    public static final String KEY_ARTIST = "KEY_ARTIST";
    public static final String KEY_RELEASE_YEAR = "KEY_RELEASE_YEAR";
    public static final String KEY_HAS_VINYL = "KEY_HAS_VINYL";
    public static final String KEY_VINYL_SPEED = "KEY_VINYL_SPEED";
    public static final String KEY_CONDITION = "KEY_CONDITION";
    public static final String KEY_GENRE = "KEY_GENRE";
    public static final String KEY_MODE = "MODO";
    public static final String KEY_SUGGEST_CONDITION = "SUGGEST_CONDITION";
    public static final String KEY_LAST_CONDITION = "LAST_CONDITION";
    public static final int NEW_MODE = 0;
    public static final int EDIT_MODE = 1;
    private EditText editTextName, editTextArtist, editTextReleaseYear, editTextGenre;
    private CheckBox checkBoxHasVinyl;
    private RadioGroup radioGroupRPM;
    private Spinner spinnerCondition;
    private RadioButton radioButton33, radioButton45;
    private int mode;
    private Disc originalDisc;
    private boolean suggestCondition = false;
    private int lastCondition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextName = findViewById(R.id.editTextName);
        editTextArtist = findViewById(R.id.editTextArtist);
        editTextReleaseYear = findViewById(R.id.editTextYear);
        editTextGenre = findViewById(R.id.editTextGenre);
        checkBoxHasVinyl = findViewById(R.id.checkBoxHasVinyl);
        radioGroupRPM = findViewById(R.id.radioGroupRPM);
        spinnerCondition = findViewById(R.id.spinnerCondition);
        radioButton33 = findViewById(R.id.radioButton33rpm);
        radioButton45 = findViewById(R.id.radioButton45rpm);

        readPreferences();

        Intent intentOpening = getIntent();
        Bundle bundle = intentOpening.getExtras();
        if(bundle != null) {
            mode = bundle.getInt(KEY_MODE);

            if(mode == NEW_MODE){
                setTitle(getString(R.string.new_disc));
                if(suggestCondition){
                    spinnerCondition.setSelection(lastCondition);
                }
            } else {
                setTitle(getString(R.string.edit_disc));

                String name = bundle.getString(NewVinylActivity.KEY_NOME);
                String artist = bundle.getString(NewVinylActivity.KEY_ARTIST);
                int releaseYear = bundle.getInt(NewVinylActivity.KEY_RELEASE_YEAR);
                String genre = bundle.getString(NewVinylActivity.KEY_GENRE);
                boolean alreadyHave = bundle.getBoolean(NewVinylActivity.KEY_HAS_VINYL);
                int condition = bundle.getInt(NewVinylActivity.KEY_CONDITION);
                String discSpeedText = bundle.getString(NewVinylActivity.KEY_VINYL_SPEED);

                DiscSpeed discSpeed = DiscSpeed.valueOf(discSpeedText);

                originalDisc = new Disc(name, artist, releaseYear, genre, alreadyHave, condition, discSpeed);
                editTextName.setText(name);
                editTextArtist.setText(artist);
                editTextReleaseYear.setText(String.valueOf(releaseYear));
                editTextGenre.setText(genre);
                checkBoxHasVinyl.setChecked(alreadyHave);
                radioGroupRPM.clearCheck();
                spinnerCondition.setSelection(condition);

                if(discSpeed == DiscSpeed.RPM_33){
                    radioButton33.setChecked(true);
                } else if(discSpeed == DiscSpeed.RPM_45){
                    radioButton45.setChecked(true);
                }
            }
        }

    }

    public void clearForm(){
        final String name = editTextName.getText().toString();
        final String artist = editTextArtist.getText().toString();
        final String releaseYear = editTextReleaseYear.getText().toString();
        final String genre = editTextGenre.getText().toString();
        final boolean hasVinyl = checkBoxHasVinyl.isChecked();
        final int radioButtonRPM = radioGroupRPM.getCheckedRadioButtonId();
        final int condition = spinnerCondition.getSelectedItemPosition();
        final ScrollView scrollView = findViewById(R.id.main);
        final View focusedView = scrollView.findFocus();

        editTextName.setText(null);
        editTextArtist.setText(null);
        editTextReleaseYear.setText(null);
        editTextGenre.setText(null);
        checkBoxHasVinyl.setChecked(false);
        radioGroupRPM.clearCheck();
        spinnerCondition.setSelection(0);

        editTextName.requestFocus();

        Snackbar snackbar = Snackbar.make(scrollView, R.string.campos_apagados_com_sucesso, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextName.setText(name);
                editTextArtist.setText(artist);
                editTextReleaseYear.setText(releaseYear);
                editTextGenre.setText(genre);
                checkBoxHasVinyl.setChecked(hasVinyl);
                if(radioButtonRPM == R.id.radioButton33rpm){
                    radioButton33.setChecked(true);
                } else if(radioButtonRPM == R.id.radioButton45rpm){
                    radioButton45.setChecked(true);
                }
                spinnerCondition.setSelection(condition);
                if (focusedView != null){
                    focusedView.requestFocus();
                }

            }
        });

        snackbar.show();
    }

    public void saveData() {
        String name = editTextName.getText().toString();
        if (name.trim().isEmpty()) {
            UtilsAlert.showWarning(this, R.string.insira_um_nome);

            editTextName.requestFocus();
            editTextName.setSelection(0);
            return;
        }

        String artist = editTextArtist.getText().toString();
        if (artist.trim().isEmpty()) {
            UtilsAlert.showWarning(this, R.string.insira_um_artista);

            editTextArtist.requestFocus();
            editTextArtist.setSelection(0);
            return;
        }

        String releaseYearString = editTextReleaseYear.getText().toString();
        int releaseYear;
        try {
            releaseYear = Integer.parseInt(releaseYearString);
        } catch (NumberFormatException e) {
            UtilsAlert.showWarning(this, R.string.releaseYearHasToBeInteger);

            editTextReleaseYear.requestFocus();
            editTextReleaseYear.setSelection(0, editTextReleaseYear.getText().toString().length());
            return;
        }

        int condition = spinnerCondition.getSelectedItemPosition();
        if(condition == AdapterView.INVALID_POSITION){
            UtilsAlert.showWarning(this,R.string. errorSpinnerEmpty);
            return;
        }

        int radioButtonId = radioGroupRPM.getCheckedRadioButtonId();
        DiscSpeed vinylSpeed;
        if(radioButtonId == R.id.radioButton33rpm){
            vinylSpeed = DiscSpeed.RPM_33;
        } else if (radioButtonId == R.id.radioButton45rpm){
            vinylSpeed = DiscSpeed.RPM_45;
        } else {
            UtilsAlert.showWarning(this, R.string.selectSpeed);
            return;
        }

        String genre = editTextGenre.getText().toString();
        if (genre.trim().isEmpty()) {
            UtilsAlert.showWarning(this, R.string.insira_o_genero);

            editTextGenre.requestFocus();
            editTextGenre.setSelection(0);
            return;
        }

        boolean hasVinyl = checkBoxHasVinyl.isChecked();

        if(mode == EDIT_MODE &&
            name.equalsIgnoreCase(originalDisc.getName()) &&
            artist.equalsIgnoreCase(originalDisc.getArtist()) &&
            releaseYear == originalDisc.getReleaseYear() &&
            hasVinyl == originalDisc.isAlreadyHave() &&
            vinylSpeed == originalDisc.getDiscSpeed() &&
            condition == originalDisc.getCondition() &&
            genre.equalsIgnoreCase(originalDisc.getGenre())) {

            setResult(NewVinylActivity.RESULT_CANCELED);
            finish();
            return;
        }

        saveLastCondition(condition);

        Intent intentResponse = new Intent();
        intentResponse.putExtra(KEY_NOME, name);
        intentResponse.putExtra(KEY_ARTIST, artist);
        intentResponse.putExtra(KEY_RELEASE_YEAR, releaseYear);
        intentResponse.putExtra(KEY_HAS_VINYL, hasVinyl);
        intentResponse.putExtra(KEY_VINYL_SPEED, vinylSpeed.toString());
        intentResponse.putExtra(KEY_CONDITION, condition);
        intentResponse.putExtra(KEY_GENRE, genre);

        setResult(NewVinylActivity.RESULT_OK, intentResponse);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.disc_options, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.menuItemSuggest);
        item.setChecked(suggestCondition);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int idMenuItem = item.getItemId();
        if(idMenuItem == R.id.menuItemSave){
            saveData();
            return true;
        } else if (idMenuItem == R.id.menuItemClear){
            clearForm();
            return true;
        } else if (idMenuItem == R.id.menuItemSuggest){
            boolean value = !item.isChecked();
            saveSuggestCondition(value);
            item.setChecked(value);
            if(suggestCondition){
                spinnerCondition.setSelection(lastCondition);
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void readPreferences(){

        SharedPreferences shared = getSharedPreferences(ListDiscsActivity.PREFERENCES_FILE, Context.MODE_PRIVATE);

        suggestCondition = shared.getBoolean(KEY_SUGGEST_CONDITION, suggestCondition);
        lastCondition = shared.getInt(KEY_LAST_CONDITION, lastCondition);
    }

    private void saveSuggestCondition(boolean newValue){
        SharedPreferences shared = getSharedPreferences(ListDiscsActivity.PREFERENCES_FILE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = shared.edit();
        editor.putBoolean(KEY_SUGGEST_CONDITION, newValue);

        editor.commit();

        suggestCondition = newValue;
    }

    private void saveLastCondition(int newValue){
        SharedPreferences shared = getSharedPreferences(ListDiscsActivity.PREFERENCES_FILE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = shared.edit();
        editor.putInt(KEY_LAST_CONDITION, newValue);

        editor.commit();

        lastCondition = newValue;
    }
}

