package jmapps.simplenotes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import jmapps.simplenotes.Database.DatabaseManager;
import jmapps.simplenotes.Observer.UpdateLists;

public class ModifyCategoryNoteActivity extends AppCompatActivity implements TextWatcher {

    private DatabaseManager databaseManager;

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private EditText etModifyCategoryItemTitle;
    private EditText etModifyCategoryItemContent;

    private String strGetCategoryItemTitle;
    private String strGetCategoryItemContent;

    private String strCurrentCategoryItemTitle;
    private String strCurrentCategoryItemContent;

    private int _id;
    private boolean savedState = false;

    private MenuItem addDeleteBookmarkNote;
    private MenuItem changeSaveNote;

    private int categoryPosition;
    private String strCategoryTitle;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_category_note);

        Toolbar toolbar = findViewById(R.id.toolbar_modify_category_note);
        setSupportActionBar(toolbar);

        // Реализовываем PreferenceManager и SharedPreferences.Editor
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();

        // Создаем объект DatabaseManager и открываем базу данных
        databaseManager = new DatabaseManager(this);
        databaseManager.databaseOpen();

        // Добавляем в Toolbar кнопку "Назад"
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Получаем содержимое переданное через Intent
        Intent intent = getIntent();
        String strItemId = intent.getStringExtra("category_item_id");
        strGetCategoryItemTitle = intent.getStringExtra("category_item_title");
        strGetCategoryItemContent = intent.getStringExtra("category_item_content");
        categoryPosition = getIntent().getIntExtra("category_position", 0);
        strCategoryTitle = getIntent().getStringExtra("category_title");
        _id = Integer.parseInt(strItemId);

        // Инициализируем View элементы
        etModifyCategoryItemTitle = findViewById(R.id.et_modify_category_item_title);
        etModifyCategoryItemContent = findViewById(R.id.et_modify_category_item_content);

        // Задаем это содержимое нашим EditText
        etModifyCategoryItemTitle.setText(strGetCategoryItemTitle);
        etModifyCategoryItemContent.setText(strGetCategoryItemContent);

        // Слушатель на изменение текста
        etModifyCategoryItemTitle.addTextChangedListener(this);
        etModifyCategoryItemContent.addTextChangedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_modify_category_note, menu);
        changeSaveNote = menu.findItem(R.id.action_change_save);
        addDeleteBookmarkNote = menu.findItem(R.id.action_add_bookmark_modify);

        boolean bookmarkState = mPreferences.getBoolean("sample_favorite " + _id, false);
        addDeleteBookmarkNote.setChecked(bookmarkState);

        if (bookmarkState) {
            addDeleteBookmarkNote.setIcon(R.drawable.ic_favorite_white);
        } else {
            addDeleteBookmarkNote.setIcon(R.drawable.ic_favorite_border_white);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean isCheckedBookmark = !addDeleteBookmarkNote.isChecked();

        switch (item.getItemId()) {
            case R.id.action_change_save:
                if (etModifyCategoryItemTitle.isFocused()) {
                    saveNote(etModifyCategoryItemTitle);
                } else if (etModifyCategoryItemContent.isFocused()) {
                    saveNote(etModifyCategoryItemContent);
                }
                break;
            case R.id.action_add_bookmark_modify:

                UpdateLists updateAdapterChapters = UpdateLists.getInstance();

                if (isCheckedBookmark) {
                    addDeleteBookmarkNote.setIcon(R.drawable.ic_favorite_white);
                    databaseManager.addRemoveBookmark(true, _id);
                    addDeleteBookmarkNote.setChecked(true);

                    updateAdapterChapters.setUpdateAdapterLists(true);
                    updateAdapterChapters.notifyObservers();
                } else {
                    addDeleteBookmarkNote.setIcon(R.drawable.ic_favorite_border_white);
                    databaseManager.addRemoveBookmark(false, _id);
                    addDeleteBookmarkNote.setChecked(false);

                    updateAdapterChapters.setUpdateAdapterLists(false);
                    updateAdapterChapters.notifyObservers();
                }

                mEditor.putBoolean("sample_favorite " + _id, isCheckedBookmark).apply();
                break;

            case R.id.action_share_note:
                shareANote();
                break;

            case R.id.action_note_delete:
                questionDeleteDialog();
                break;

            case android.R.id.home:
                if (!savedState) {
                    checkState();
                } else {
                    addNote();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!savedState) {
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
        if (etModifyCategoryItemTitle.getText().hashCode() == s.hashCode()) {
            ifTextChanged(etModifyCategoryItemTitle, strGetCategoryItemTitle);
        } else if (etModifyCategoryItemContent.getText().hashCode() == s.hashCode()) {
            ifTextChanged(etModifyCategoryItemContent, strGetCategoryItemContent);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void checkState() {
        getTextFromEditTexts();
        if (!strCurrentCategoryItemTitle.isEmpty() || !strCurrentCategoryItemContent.isEmpty()) {
            if (!strCurrentCategoryItemTitle.equals(strGetCategoryItemTitle)
                    || !strCurrentCategoryItemContent.equals(strGetCategoryItemContent)) {
                questionSaveChangesDialog();
            } else {
                finish();
            }
        } else {
            deleteItem();
        }
    }

    private void ifTextChanged(EditText currentEditText, String strSavedText) {

        String strTextChanged = currentEditText.getText().toString();

        if (!strTextChanged.equals(strSavedText)) {
            changeSaveNote.setVisible(true);
            savedState = false;
        } else {
            changeSaveNote.setVisible(false);
        }
    }

    private void saveNote(EditText currentEditText) {
        getTextFromEditTexts();
        if (!strCurrentCategoryItemTitle.isEmpty() || !strCurrentCategoryItemContent.isEmpty()) {
            changeSaveNote.setVisible(false);
            closeKeyboard(currentEditText);
            clearFocusEditText(currentEditText);
            savedState = true;

            strCurrentCategoryItemTitle = strGetCategoryItemTitle;
            strCurrentCategoryItemContent = strGetCategoryItemContent;
        } else {
            finish();
        }
    }

    private void addNote() {
        getTextFromEditTexts();
        databaseManager.updateMainItem(_id, strCurrentCategoryItemTitle, strCurrentCategoryItemContent);
        Toast.makeText(this, R.string.note_change, Toast.LENGTH_SHORT).show();
        returnMainActivity();
    }

    private void deleteItem() {
        databaseManager.deleteMainItem(_id);
        Toast.makeText(this, R.string.note_deleted, Toast.LENGTH_SHORT).show();
        returnMainActivity();
    }

    private void questionSaveChangesDialog() {
        final AlertDialog.Builder questionDialog = new AlertDialog.Builder(this);

        questionDialog.setIcon(R.drawable.ic_warning);
        questionDialog.setTitle(R.string.warning);
        questionDialog.setMessage(R.string.question_save_changes);
        questionDialog.setCancelable(false);

        questionDialog.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addNote();
            }
        });

        questionDialog.setNegativeButton(R.string.not_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
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
                deleteItem();
            }
        });

        questionDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        questionDialog.create().show();
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

    private void returnMainActivity() {
        Intent toCategoryContent = new Intent(ModifyCategoryNoteActivity.this, CategoryContentActivity.class)
                .putExtra("category_position", categoryPosition)
                .putExtra("category_title", strCategoryTitle)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(toCategoryContent);
    }

    private void getTextFromEditTexts() {
        strCurrentCategoryItemTitle = etModifyCategoryItemTitle.getText().toString();
        strCurrentCategoryItemContent = etModifyCategoryItemContent.getText().toString();
    }

    private void clearFocusEditText(EditText currentEditText) {
        currentEditText.clearFocus();
        currentEditText.setSelection(0);
    }

    private void shareANote() {
        getTextFromEditTexts();
        String contentForSharing = strCurrentCategoryItemTitle + "<p/>" + strCurrentCategoryItemContent;
        Intent shareText = new Intent(Intent.ACTION_SEND);
        shareText.setType("text/plain");
        shareText.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(contentForSharing));
        shareText.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareText = Intent.createChooser(shareText, "Поделиться через");
        startActivity(shareText);
    }
}
