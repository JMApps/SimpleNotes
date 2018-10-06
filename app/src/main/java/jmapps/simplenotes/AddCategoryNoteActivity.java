package jmapps.simplenotes;

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

public class AddCategoryNoteActivity extends AppCompatActivity implements TextWatcher {

    private DatabaseManager databaseManager;

    private EditText etAddCategoryItemTitle;
    private EditText etAddCategoryItemContent;

    private String strCategoryItemTitle;
    private String strCategoryItemContent;

    private ActionBar actionBar;
    private MenuItem addChangeNote;

    private boolean questionDialogState = false;

    private int categoryId;
    private String strCategoryTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category_note);

        Toolbar toolbar = findViewById(R.id.toolbar_add_category_note);
        setSupportActionBar(toolbar);

        categoryId = getIntent().getIntExtra("category_position", 1);
        strCategoryTitle = getIntent().getStringExtra("category_title");

        // Получаем объект DatabaseManager и открываем базу данных
        databaseManager = new DatabaseManager(this);
        databaseManager.databaseOpen();

        // Получаем доступ к actionBar
        actionBar = getSupportActionBar();

        // Инициализируем View элементы
        etAddCategoryItemTitle = findViewById(R.id.et_add_category_item_title);
        etAddCategoryItemContent = findViewById(R.id.et_add_category_item_content);

        // Слушатель на изменение текста
        etAddCategoryItemTitle.addTextChangedListener(this);
        etAddCategoryItemContent.addTextChangedListener(this);

        requestFocus(etAddCategoryItemContent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_category_note, menu);
        addChangeNote = menu.findItem(R.id.action_add_change_category_item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_change_category_item:
                if (etAddCategoryItemTitle.isFocused()) {
                    saveNote(etAddCategoryItemTitle);
                } else if (etAddCategoryItemContent.isFocused()) {
                    saveNote(etAddCategoryItemContent);
                }
                break;
            case R.id.action_delete_category_item:
                getTextFromEditTexts();
                if (!strCategoryItemTitle.isEmpty() || !strCategoryItemContent.isEmpty()) {
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
        if (etAddCategoryItemTitle.getText().hashCode() == s.hashCode()) {
            ifTextChanged(etAddCategoryItemTitle, strCategoryItemTitle);
        } else if (etAddCategoryItemContent.getText().hashCode() == s.hashCode()) {
            ifTextChanged(etAddCategoryItemContent, strCategoryItemContent);
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
        if (!strCategoryItemTitle.isEmpty() || !strCategoryItemContent.isEmpty()) {
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
        if (!strCategoryItemTitle.isEmpty() || !strCategoryItemContent.isEmpty()) {
            databaseManager.insertCategoryItem(categoryId, strCategoryItemTitle, strCategoryItemContent);
            Toast.makeText(this, R.string.note_added, Toast.LENGTH_SHORT).show();
            returnMainActivity();
        } else {
            finish();
        }
    }

    private void returnMainActivity() {
        Intent toCategoryContentList = new Intent(AddCategoryNoteActivity.this, CategoryContentActivity.class)
                .putExtra("category_position", categoryId)
                .putExtra("category_title", strCategoryTitle)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(toCategoryContentList);
    }

    private void checkState() {
        getTextFromEditTexts();
        if (!strCategoryItemTitle.isEmpty() || !strCategoryItemContent.isEmpty()) {
            questionSaveChangesDialog();
        } else {
            finish();
        }
    }

    private void getTextFromEditTexts() {
        strCategoryItemTitle = etAddCategoryItemTitle.getText().toString();
        strCategoryItemContent = etAddCategoryItemContent.getText().toString();
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
                Toast.makeText(AddCategoryNoteActivity.this, R.string.note_deleted, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(AddCategoryNoteActivity.this, R.string.note_deleted, Toast.LENGTH_SHORT).show();
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
