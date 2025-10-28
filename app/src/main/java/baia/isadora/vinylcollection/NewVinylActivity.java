package baia.isadora.vinylcollection;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
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

import java.time.LocalDate;
import java.util.List;

import baia.isadora.vinylcollection.model.Artist;
import baia.isadora.vinylcollection.model.Disc;
import baia.isadora.vinylcollection.model.DiscSpeed;
import baia.isadora.vinylcollection.persistency.DiscsDatabase;
import baia.isadora.vinylcollection.utils.UtilsAlert;
import baia.isadora.vinylcollection.utils.UtilsLocalDate;

public class NewVinylActivity extends AppCompatActivity {
    public static final String KEY_ID = "ID";
    public static final String KEY_MODE = "MODO";
    public static final String KEY_SUGGEST_CONDITION = "SUGGEST_CONDITION";
    public static final String KEY_LAST_CONDITION = "LAST_CONDITION";
    public static final int NEW_MODE = 0;
    public static final int EDIT_MODE = 1;
    private EditText editTextName, editTextArtist, editTextReleaseYear, editTextGenre, editTextAcquiredDate;
    private CheckBox checkBoxHasVinyl;
    private RadioGroup radioGroupRPM;
    private Spinner spinnerCondition, spinnerArtist;
    private RadioButton radioButton33, radioButton45;
    private LocalDate acquiredDate;
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
        spinnerArtist = findViewById(R.id.spinnerArtist);
        DiscsDatabase database = DiscsDatabase.getInstance(this);
        List<Artist> artists = database.getArtistDao().queryAllAscending();
        if (artists.isEmpty()) {
            UtilsAlert.showWarning(this, R.string.no_artists_registered);
            spinnerArtist.setEnabled(false);
        } else {
            ArrayAdapter<Artist> artistAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    artists
            );
            artistAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerArtist.setAdapter(artistAdapter);
        }
        editTextReleaseYear = findViewById(R.id.editTextYear);
        editTextReleaseYear.setFilters(new InputFilter[] {
                new InputFilter.LengthFilter(4)
        });
        editTextReleaseYear.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTextGenre = findViewById(R.id.editTextNationality);
        editTextAcquiredDate = findViewById(R.id.editTextDate);
        checkBoxHasVinyl = findViewById(R.id.checkBoxHasVinyl);
        checkBoxHasVinyl.setOnCheckedChangeListener((buttonView, isChecked) -> {
            editTextAcquiredDate.setEnabled(isChecked);
            if (!isChecked) {
                editTextAcquiredDate.setText(null);
            }
        });
        radioGroupRPM = findViewById(R.id.radioGroupRPM);
        spinnerCondition = findViewById(R.id.spinnerCondition);
        radioButton33 = findViewById(R.id.radioButton33rpm);
        radioButton45 = findViewById(R.id.radioButton45rpm);

        editTextAcquiredDate.setFocusable(false);
        editTextAcquiredDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBoxHasVinyl.isChecked()) {
                    showDatePickerDialog();
                } else {
                    UtilsAlert.showWarning(NewVinylActivity.this, R.string.check_already_own_the_record);
                }
            }
        });

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
                acquiredDate = LocalDate.now();
            } else {
                setTitle(getString(R.string.edit_disc));

                long id = bundle.getLong(KEY_ID);

                originalDisc = database.getDiscDao().queryForId(id);

                editTextName.setText(originalDisc.getName());

                long artistId = originalDisc.getArtistId();
                for (int i = 0; i < spinnerArtist.getCount(); i++) {
                    Artist artist = (Artist) spinnerArtist.getItemAtPosition(i);
                    if (artist.getId() == artistId) {
                        spinnerArtist.setSelection(i);
                        break;
                    }
                }

                editTextReleaseYear.setText(String.valueOf(originalDisc.getReleaseYear()));
                editTextGenre.setText(originalDisc.getGenre());

                if (originalDisc.getAcquiredDate() != null){
                    acquiredDate = originalDisc.getAcquiredDate();
                }

                checkBoxHasVinyl.setChecked(originalDisc.isAlreadyHave());
                spinnerCondition.setSelection(originalDisc.getCondition());

                DiscSpeed discSpeed = originalDisc.getDiscSpeed();
                if(discSpeed == DiscSpeed.RPM_33){
                    radioButton33.setChecked(true);
                } else if(discSpeed == DiscSpeed.RPM_45){
                    radioButton45.setChecked(true);
                }

                editTextName.requestFocus();
                editTextName.setSelection(editTextName.getText().length());
            }
        }

    }
    private void showDatePickerDialog(){
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                acquiredDate = LocalDate.of(year, month + 1, dayOfMonth);
                editTextAcquiredDate.setText(UtilsLocalDate.formatLocalDate(acquiredDate));
            }
        };

        DatePickerDialog picker = new DatePickerDialog(this, R.style.SpinnerDatePicker, listener, acquiredDate.getYear(), acquiredDate.getMonthValue() -1, acquiredDate.getDayOfMonth());
        long maxDateMillis = UtilsLocalDate.toMiliseconds(LocalDate.now());
        picker.getDatePicker().setMaxDate(maxDateMillis);
        picker.show();
    }
    public void clearForm(){
        final String name = editTextName.getText().toString();
        final String artist = editTextArtist.getText().toString();
        final String releaseYear = editTextReleaseYear.getText().toString();
        final String genre = editTextGenre.getText().toString();
        final LocalDate oldAcquiredDate = acquiredDate;
        final boolean hasVinyl = checkBoxHasVinyl.isChecked();
        final int radioButtonRPM = radioGroupRPM.getCheckedRadioButtonId();
        final int condition = spinnerCondition.getSelectedItemPosition();
        final ScrollView scrollView = findViewById(R.id.main);
        final View focusedView = scrollView.findFocus();

        editTextName.setText(null);
        editTextArtist.setText(null);
        editTextReleaseYear.setText(null);
        editTextGenre.setText(null);
        editTextAcquiredDate.setText(null);
        acquiredDate = LocalDate.now();
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
                acquiredDate = oldAcquiredDate;
                editTextAcquiredDate.setText(UtilsLocalDate.formatLocalDate(acquiredDate));
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

        Artist selectedArtist = (Artist) spinnerArtist.getSelectedItem();
        long artistId = selectedArtist.getId();

        String releaseYearString = editTextReleaseYear.getText().toString().trim();
        int releaseYear;
        if (releaseYearString.isEmpty()) {
            UtilsAlert.showWarning(this, R.string.releaseYearRequired);
            editTextReleaseYear.requestFocus();
            return;
        }
        if (releaseYearString.length() > 4) {
            UtilsAlert.showWarning(this, R.string.releaseYearTooLong);
            editTextReleaseYear.requestFocus();
            editTextReleaseYear.setSelection(releaseYearString.length());
            return;
        }
        try {
            releaseYear = Integer.parseInt(releaseYearString);
        } catch (NumberFormatException e) {
            UtilsAlert.showWarning(this, R.string.releaseYearHasToBeInteger);
            editTextReleaseYear.requestFocus();
            editTextReleaseYear.setSelection(releaseYearString.length());
            return;
        }
        int currentYear = LocalDate.now().getYear();
        if (releaseYear < 1800 || releaseYear > currentYear) {
            UtilsAlert.showWarning(this, R.string.releaseYearOutOfRange);
            editTextReleaseYear.requestFocus();
            editTextReleaseYear.setSelection(releaseYearString.length());
            return;
        }

        boolean hasVinyl = checkBoxHasVinyl.isChecked();
        if (hasVinyl) {
            String acquiredDateString = editTextAcquiredDate.getText().toString();
            if (acquiredDateString == null || acquiredDateString.trim().isEmpty()) {
                UtilsAlert.showWarning(this, R.string.acquired_date_cannot_be_empty);
                editTextAcquiredDate.requestFocus();
                return;
            }
        } else {
            acquiredDate = null;
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

        Disc disc = new Disc(name, artistId, releaseYear, genre, hasVinyl, condition, vinylSpeed, acquiredDate);

        if(disc.equals(originalDisc)){
            setResult(NewVinylActivity.RESULT_CANCELED);
            finish();
            return;
        }

        Intent intentResponse = new Intent();

        DiscsDatabase database = DiscsDatabase.getInstance(this);

        if(mode == NEW_MODE){
            long newId = database.getDiscDao().insert(disc);
            if(newId <= 0){
                UtilsAlert.showWarning(this, R.string.error_trying_to_insert);
                return;
            }
            disc.setId(newId);
        } else {
            disc.setId(originalDisc.getId());
            int alteredQuant = database.getDiscDao().update(disc);

            if(alteredQuant != 1){
                UtilsAlert.showWarning(this, R.string.error_trying_to_update);
                return;
            }
        }
        saveLastCondition(condition);

        intentResponse.putExtra(KEY_ID, disc.getId());
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
        } else if (idMenuItem == R.id.menuItemClearArtist){
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

