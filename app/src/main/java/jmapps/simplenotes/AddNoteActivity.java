package jmapps.simplenotes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import jmapps.simplenotes.Database.DatabaseManager;

public class AddNoteActivity extends AppCompatActivity implements TextView.OnEditorActionListener {

    private DatabaseManager databaseManager;

    private EditText etAddChapterTitle;
    private EditText etAddChapterContent;

    private String strChapterTitle;
    private String strChapterContent;

    private ActionBar actionBar;
    private MenuItem addChangeNote;

    private boolean questionDialogState = false;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        // Получаем объект DatabaseManager и открываем базу данных
        databaseManager = new DatabaseManager(this);
        databaseManager.databaseOpen();

        actionBar = getSupportActionBar();

        etAddChapterTitle = findViewById(R.id.et_add_chapter_title);
        etAddChapterContent = findViewById(R.id.et_add_chapter_content);

        etAddChapterTitle.setOnEditorActionListener(this);

        openKeyboard();
    }

    @Override
    public void onBackPressed() {
        if (!questionDialogState) {
            getTextFromEditTexts();
            if (!strChapterTitle.isEmpty() || !strChapterContent.isEmpty()) {
                questionDialog();
            } else {
                finish();
            }
        } else {
            super.onBackPressed();
            addNote();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_note, menu);
        addChangeNote = menu.findItem(R.id.action_add_change);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean isChecked;
        item.setChecked(isChecked = !item.isChecked());

        switch (item.getItemId()) {
            case R.id.action_add_change:
                if (isChecked) {
                    // Сохранить заметку
                    saveNote();
                    questionDialogState = true;
                } else {
                    // Изменить заметку
                    modifyItem();
                    questionDialogState = false;
                }
                break;
            case android.R.id.home:

                addNote();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        boolean handled = false;
        switch (v.getId()) {
            case R.id.et_add_chapter_title:
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    etAddChapterTitle.clearFocus();
                    etAddChapterContent.requestFocus();
                    handled = true;
                }
        }
        return handled;
    }

    private void saveNote() {
        getTextFromEditTexts();
        if (!strChapterTitle.isEmpty() || !strChapterContent.isEmpty()) {
            clearFocusEditTexts();
            addChangeNote.setTitle(R.string.modify_note);
            if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
            closeKeyboard();
        } else {
            finish();
        }
    }

    private void modifyItem() {
        getTextFromEditTexts();
        if (!strChapterTitle.isEmpty() || !strChapterContent.isEmpty()) {
            addChangeNote.setTitle(R.string.save_note);
            if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(false);
            openKeyboard();
        } else {
            addChangeNote.setTitle(R.string.save_note);
            if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(false);
            openKeyboard();
        }
    }

    private void addNote() {
        getTextFromEditTexts();
        if (!strChapterTitle.isEmpty() || !strChapterContent.isEmpty()) {
            databaseManager.databaseInsertItem(strChapterTitle, strChapterContent);
            Toast.makeText(this, R.string.note_added, Toast.LENGTH_SHORT).show();
            returnMainActivity();
        } else {
            finish();
        }
    }

    private void questionDialog() {
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
                Toast.makeText(AddNoteActivity.this, R.string.note_deleted, Toast.LENGTH_SHORT).show();
            }
        });
        questionDialog.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        questionDialog.create().show();
    }

    private void openKeyboard() {
        etAddChapterContent.requestFocus();
        etAddChapterContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager keyboard = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                if (keyboard != null) {
                    keyboard.showSoftInput(etAddChapterContent, 0);
                }
            }
        }, 200);
    }

    private void closeKeyboard() {
        etAddChapterContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager keyboard = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                if (keyboard != null) {
                    keyboard.hideSoftInputFromWindow(etAddChapterContent.
                            getWindowToken(), 0);
                }
            }
        }, 200);
    }

    private void returnMainActivity() {
        Intent toMainList = new Intent(AddNoteActivity.this, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(toMainList);
    }

    private void getTextFromEditTexts() {
        strChapterTitle = etAddChapterTitle.getText().toString();
        strChapterContent = etAddChapterContent.getText().toString();
    }

    private void clearFocusEditTexts() {
        etAddChapterTitle.clearFocus();
        etAddChapterContent.clearFocus();
    }
}