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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import jmapps.simplenotes.Database.DatabaseManager;

public class AddNoteActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    private DatabaseManager databaseManager;

    private EditText etAddChapterTitle;
    private EditText etAddChapterContent;

    private String strChapterTitle;
    private String strChapterContent;

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

        etAddChapterTitle = findViewById(R.id.et_add_chapter_title);
        etAddChapterContent = findViewById(R.id.et_add_chapter_content);

        // Слушатель на получение фокуса EditText
        etAddChapterTitle.setOnFocusChangeListener(this);
        etAddChapterContent.setOnFocusChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_note, menu);
        addChangeNote = menu.findItem(R.id.action_add_change);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Состояние isCheckedAddChange противположно текущему состоянию addChangeNote
        boolean isCheckedAddChange = !addChangeNote.isChecked();
        // Задаем для addChangeNote состояние isCheckedAddChange

        switch (item.getItemId()) {
            case R.id.action_add_change:
                if (isCheckedAddChange) {
                    // Добавить заметку
                    saveNote();
                } else {
                    // Изменить заметку
                    modifyItem(etAddChapterContent);
                }
                break;
            case R.id.action_delete:
                getTextFromEditTexts();
                if (!strChapterTitle.isEmpty() || !strChapterContent.isEmpty()) {
                    questionDeleteDialog();
                } else {
                    finish();
                }
                break;
            case android.R.id.home:
                // Добавить заметку
                addNote();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Если questionDialogState не равен true, проверяем поля на наличие текста
        // Если нет текста, закрываем активити, если есть текст, предлагаем сохранить или удалить его
        if (!questionDialogState) {
            getTextFromEditTexts();
            if (!strChapterTitle.isEmpty() || !strChapterContent.isEmpty()) {
                questionSaveChangesDialog();
            } else {
                super.onBackPressed();
            }
            // Если же questionDialogState равен true, проверяем поля на наличие текста
            // Если есть текст, добавляем его в базу данных, если его нет, закрываем активити
        } else {
            addNote();
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.et_add_chapter_title:
                if (hasFocus) {
                    // Изменить заметку
                    modifyItem(etAddChapterTitle);
                }
                break;
            case R.id.et_add_chapter_content:
                if (hasFocus) {
                    // Изменить заметку
                    modifyItem(etAddChapterContent);
                }
                break;
        }
    }

    // Добавить заметку
    private void saveNote() {
        getTextFromEditTexts();
        // Если поля обоих или одного EditText не пусты
        if (!strChapterTitle.isEmpty() || !strChapterContent.isEmpty()) {
            // Убираем фокус
            clearFocusEditTexts();
            // Задаем состояние для addChangeNote
            addChangeNote.setChecked(true);
            // Меняем картинку
            addChangeNote.setIcon(R.drawable.ic_edit);
            // Меняем заговок на "Изменить"
            addChangeNote.setTitle(R.string.modify_note);
            // Скрываем клавиатуру
            closeKeyboard();
            // Отображаем кнопку "Назад"
            if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
            // Делаем не нужным вызов диалога
            questionDialogState = true;
        } else {
            // Если поля обоих или одного EditText пусты уничтожаем активити
            finish();
        }
    }

    // Изменить заметку
    private void modifyItem(EditText editText) {
        getTextFromEditTexts();
        // Если поля обоих или одного EditText не пусты
        if (!strChapterTitle.isEmpty() || !strChapterContent.isEmpty()) {
            // Получаем фокус текущего EditText
            editText.requestFocus();
            // Задаем состояние для addChangeNote
            addChangeNote.setChecked(false);
            // Меняем картинку
            addChangeNote.setIcon(R.drawable.ic_save);
            // Меняем заговок на "Сохранить"
            addChangeNote.setTitle(R.string.save_note);
            // Переходим в конец текста
            editText.setSelection(editText.getText().length());
            // Отображаем клавиатуру с фокусом на текущий EditText
            openKeyboard(editText);
            // Скрываем кнопку "Назад"
            if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(false);
            // При попытке выйти будет вызвано диалоговое окно
            questionDialogState = false;
        }
    }

    // Добавляем конечный вариант в базу данных
    @SuppressLint("SimpleDateFormat")
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

    // Диалоговое окно с предложением сохранить или удалить данные
    private void questionSaveChangesDialog() {
        final AlertDialog.Builder questionDialog = new AlertDialog.Builder(this);

        questionDialog.setIcon(R.drawable.ic_warning);
        questionDialog.setTitle(R.string.warning);
        questionDialog.setMessage(R.string.question_save_note);
        questionDialog.setCancelable(false);

        // Обработчик варианта "Сохранить"
        questionDialog.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addNote();
            }
        });

        // Обработчик варианта "Удалить"
        questionDialog.setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                Toast.makeText(AddNoteActivity.this, R.string.note_deleted, Toast.LENGTH_SHORT).show();
            }
        });

        // Обработчик варианта "Отмена"
        questionDialog.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        questionDialog.create().show();
    }

    // Диалоговое окно с предложением сохранить или удалить данные
    private void questionDeleteDialog() {
        final AlertDialog.Builder questionDialog = new AlertDialog.Builder(this);

        questionDialog.setIcon(R.drawable.ic_warning);
        questionDialog.setTitle(R.string.warning);
        questionDialog.setMessage(R.string.question_delete_note);
        questionDialog.setCancelable(false);

        // Обработчик варианта "Сохранить"
        questionDialog.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                Toast.makeText(AddNoteActivity.this, R.string.note_deleted, Toast.LENGTH_SHORT).show();
            }
        });

        // Обработчик варианта "Удалить"
        questionDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        questionDialog.create().show();
    }

    // Отображаем клаиатуру с фокусом на текущий EditText
    private void openKeyboard(final EditText editText) {
        editText.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager keyboard = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                if (keyboard != null) {
                    keyboard.showSoftInput(editText, 0);
                }
            }
        }, 200);
    }

    // Скрываем клавиатуру
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

    // Метод возвращения к главной активти
    private void returnMainActivity() {
        Intent toMainList = new Intent(AddNoteActivity.this, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(toMainList);
    }

    // Получаем введенные в EditText данные
    private void getTextFromEditTexts() {
        strChapterTitle = etAddChapterTitle.getText().toString();
        strChapterContent = etAddChapterContent.getText().toString();
    }

    // Переходим к началу текста и убираем курсор
    private void clearFocusEditTexts() {
        // Переходим в начало текста
        etAddChapterTitle.setSelection(0);
        etAddChapterContent.setSelection(0);
        // Очищаем фокус
        etAddChapterTitle.clearFocus();
        etAddChapterContent.clearFocus();
    }
}