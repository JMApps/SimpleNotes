package jmapps.simplenotes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import jmapps.simplenotes.Database.DatabaseManager;

public class AddMainNoteActivity extends AppCompatActivity implements TextWatcher {

    private DatabaseManager databaseManager;

    private EditText etAddMainItemTitle;
    private EditText etAddMainItemContent;

    private String strMainItemTitle;
    private String strMainItemContent;

    private ActionBar actionBar;
    private MenuItem addChangeNote;

    private boolean questionDialogState = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        Toolbar toolbarAddNote = findViewById(R.id.toolbar_add_note);
        setSupportActionBar(toolbarAddNote);

        // Получаем объект DatabaseManager и открываем базу данных
        databaseManager = new DatabaseManager(this);
        databaseManager.databaseOpen();

        // Получаем доступ к actionBar
        actionBar = getSupportActionBar();

        // Инициализируем View элементы
        etAddMainItemTitle = findViewById(R.id.et_add_main_item_title);
        etAddMainItemContent = findViewById(R.id.et_add_main_item_content);

        // Слушатель на изменение текста
        etAddMainItemTitle.addTextChangedListener(this);
        etAddMainItemContent.addTextChangedListener(this);

        requestFocus(etAddMainItemContent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_main_note, menu);
        addChangeNote = menu.findItem(R.id.action_add_change_main_item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_change_main_item:
                if (etAddMainItemTitle.isFocused()) {
                    saveNote(etAddMainItemTitle);
                } else if (etAddMainItemContent.isFocused()) {
                    saveNote(etAddMainItemContent);
                }
                break;
            case R.id.action_delete_main_item:
                getTextFromEditTexts();
                if (!strMainItemTitle.isEmpty() || !strMainItemContent.isEmpty()) {
                    questionDeleteDialog();
                } else {
                    finish();
                }
                break;
            case android.R.id.home:
                addNote();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!questionDialogState) {
            checkState();
        } else {
            addNote();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (etAddMainItemTitle.getText().hashCode() == s.hashCode()) {
            ifTextChanged(etAddMainItemTitle, strMainItemTitle);
        } else if (etAddMainItemContent.getText().hashCode() == s.hashCode()) {
            ifTextChanged(etAddMainItemContent, strMainItemContent);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void requestFocus(EditText currentEditText) {
        currentEditText.requestFocus();
        openKeyboard(currentEditText);
    }

    private void saveNote(EditText currentEditText) {
        getTextFromEditTexts();
        if (!strMainItemTitle.isEmpty() || !strMainItemContent.isEmpty()) {
            addChangeNote.setVisible(false);
            closeKeyboard(currentEditText);
            clearFocusEditText(currentEditText);
            if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
            questionDialogState = true;
        } else {
            finish();
        }
    }

    private void clearFocusEditText(EditText currentEditText) {
        currentEditText.setSelection(0);
        currentEditText.clearFocus();
    }

    private void ifTextChanged(EditText currentEditText, String strSavedText) {

        String strTextChanged = currentEditText.getText().toString();

        if (!strTextChanged.equals(strSavedText)) {
            if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(false);
            addChangeNote.setVisible(true);
            questionDialogState = false;
        } else {
            if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
            addChangeNote.setVisible(false);
            questionDialogState = true;
        }
    }

    private void openKeyboard(final EditText currentEditText) {
        currentEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager keyboard = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                if (keyboard != null) {
                    keyboard.showSoftInput(currentEditText, 0);
                }
            }
        }, 200);
    }

    private void closeKeyboard(final EditText currentEditText) {
        currentEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager keyboard = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                if (keyboard != null) {
                    keyboard.hideSoftInputFromWindow(currentEditText.
                            getWindowToken(), 0);
                }
            }
        }, 200);
    }

    private void addNote() {
        getTextFromEditTexts();
        if (!strMainItemTitle.isEmpty() || !strMainItemContent.isEmpty()) {
            databaseManager.insertMainItem(strMainItemTitle, strMainItemContent);
            Toast.makeText(this, R.string.note_added, Toast.LENGTH_SHORT).show();
            returnMainActivity();
        } else {
            finish();
        }
    }

    private void returnMainActivity() {
        Intent toMainList = new Intent(AddMainNoteActivity.this, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(toMainList);
    }

    private void checkState() {
        getTextFromEditTexts();
        if (!strMainItemTitle.isEmpty() || !strMainItemContent.isEmpty()) {
            questionSaveChangesDialog();
        } else {
            finish();
        }
    }

    private void getTextFromEditTexts() {
        strMainItemTitle = etAddMainItemTitle.getText().toString();
        strMainItemContent = etAddMainItemContent.getText().toString();
    }

    private void questionSaveChangesDialog() {
        final AlertDialog.Builder questionDialog = new AlertDialog.Builder(this);

        questionDialog.setIcon(R.drawable.ic_warning);
        questionDialog.setTitle(R.string.warning);
        questionDialog.setMessage(R.string.question_save_note);
        questionDialog.setCancelable(false);

        questionDialog.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addNote();
            }
        });

        questionDialog.setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                Toast.makeText(AddMainNoteActivity.this, R.string.note_deleted, Toast.LENGTH_SHORT).show();
            }
        });

        questionDialog.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        questionDialog.create().show();
    }

    private void questionDeleteDialog() {
        final AlertDialog.Builder questionDialog = new AlertDialog.Builder(this);

        questionDialog.setIcon(R.drawable.ic_warning);
        questionDialog.setTitle(R.string.warning);
        questionDialog.setMessage(R.string.question_delete_note);
        questionDialog.setCancelable(false);

        questionDialog.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                Toast.makeText(AddMainNoteActivity.this, R.string.note_deleted, Toast.LENGTH_SHORT).show();
            }
        });

        questionDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        questionDialog.create().show();
    }
}