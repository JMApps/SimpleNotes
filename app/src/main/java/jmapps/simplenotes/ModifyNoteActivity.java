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
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import jmapps.simplenotes.Database.DatabaseManager;
import jmapps.simplenotes.Observer.UpdateLists;

public class ModifyNoteActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    private DatabaseManager databaseManager;

    private MenuItem changeSaveNote;
    private MenuItem addDeleteBookmarkNote;

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private EditText etModifyChapterTitle;
    private EditText etModifyChapterContent;

    private String strGetChapterTitle;
    private String strGetChapterContent;

    private String strChapterTitle;
    private String strChapterContent;

    private int _id;
    private boolean savedState = false;

    @SuppressLint({"CommitPrefEdits", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_note);

        Toolbar toolbarAddNote = findViewById(R.id.toolbar_modify_note);
        setSupportActionBar(toolbarAddNote);

        // Создаем объект DatabaseManager и открываем базу данных
        databaseManager = new DatabaseManager(this);
        databaseManager.databaseOpen();

        // Добавляем в Toolbar кнопку "Назад"
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }

        // Получаем содержимое переданное через Intent
        Intent intent = getIntent();
        String strId = intent.getStringExtra("_id");
        strGetChapterTitle = intent.getStringExtra("chapter_title");
        strGetChapterContent = intent.getStringExtra("chapter_content");
        _id = Integer.parseInt(strId);

        // Реализовываем PreferenceManager и SharedPreferences.Editor
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();

        etModifyChapterTitle = findViewById(R.id.et_modify_chapter_title);
        etModifyChapterContent = findViewById(R.id.et_modify_chapter_content);

        // Задаем это содержимое нашим EditText
        etModifyChapterTitle.setText(strGetChapterTitle);
        etModifyChapterContent.setText(strGetChapterContent);

        // Слушатель на касание по полям EditText
        etModifyChapterTitle.setOnFocusChangeListener(this);
        etModifyChapterContent.setOnFocusChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_modify_note, menu);
        addDeleteBookmarkNote = menu.findItem(R.id.action_add_bookmark_modify);
        changeSaveNote = menu.findItem(R.id.action_change_save);

        // Задаем для addDeleteBookmarkNote состояние полученное из mPreferences по умолчанию false
        boolean bookmarkState = mPreferences.getBoolean("bookmark_modify " + _id, false);
        addDeleteBookmarkNote.setChecked(bookmarkState);

        // По состоянию полученному из mPreferences отображаем одну или другую иконку
        if (bookmarkState) {
            addDeleteBookmarkNote.setIcon(R.drawable.ic_bookmark_white);
        } else {
            addDeleteBookmarkNote.setIcon(R.drawable.ic_bookmark_border_white);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean isCheckedBookmark = !addDeleteBookmarkNote.isChecked();
        boolean isCheckedChangeNote = !changeSaveNote.isChecked();

        switch (item.getItemId()) {
            case R.id.action_change_save:
                if (isCheckedChangeNote) {
                    // Изменить заметку. Фокус на содержимое
                    modifyItem(etModifyChapterContent);
                } else {
                    // Сохранить заметку
                    saveNote();
                }
                break;

            case R.id.action_add_bookmark_modify:

                // Получаем инстанцию UpdateLists
                UpdateLists updateAdapterChapters = UpdateLists.getInstance();

                if (isCheckedBookmark) {
                    // Закладка активна
                    addDeleteBookmarkNote.setIcon(R.drawable.ic_bookmark_white);
                    databaseManager.addRemoveBookmark(true, _id);

                    // Вносим изменения в метод setUpdateAdapterLists в UpdateLists и обновляем Observer
                    updateAdapterChapters.setUpdateAdapterLists(true);
                    updateAdapterChapters.notifyObservers();
                } else {
                    // Закладка неактивна
                    addDeleteBookmarkNote.setIcon(R.drawable.ic_bookmark_border_white);
                    databaseManager.addRemoveBookmark(false, _id);

                    // Вносим изменения в метод setUpdateAdapterLists в UpdateLists и обновляем Observer
                    updateAdapterChapters.setUpdateAdapterLists(false);
                    updateAdapterChapters.notifyObservers();
                }

                // Сохраняем состояние
                mEditor.putBoolean("bookmark_modify " + _id, isCheckedBookmark).apply();
                break;

            case R.id.action_share_note:
                shareANote();
                break;

            case R.id.action_modify_note_delete:
                // Удаляем пункт
                questionDeleteDialog();
                break;

            case android.R.id.home:
                if (!savedState) {
                    getTextFromEditTexts();
                    // Если поля обоих или одного EditText не пусты
                    if (!strChapterTitle.isEmpty() || !strChapterContent.isEmpty()) {
                        // Проверяем их совпадение на тот текст, который получили через Intent
                        if (!strChapterTitle.equals(strGetChapterTitle)
                                || !strChapterContent.equals(strGetChapterContent)) {
                            // Если не равны, вызываем диалоговое окно
                            questionSaveChangesDialog();
                        } else {
                            // Если равны (то есть не было изменений) закрываем активити
                            finish();
                        }
                    } else {
                        // Если поля обоих или одного EditText пусты, удаляем текущий пункт
                        deleteItem();
                    }
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
            getTextFromEditTexts();
            // Если поля обоих или одного EditText не пусты
            if (!strChapterTitle.isEmpty() || !strChapterContent.isEmpty()) {
                // Проверяем их совпадение на тот текст, который получили через Intent
                if (!strChapterTitle.equals(strGetChapterTitle)
                        || !strChapterContent.equals(strGetChapterContent)) {
                    // Если не равны, вызываем диалоговое окно
                    questionSaveChangesDialog();
                } else {
                    // Если равны (то есть не было изменений) закрываем активити
                    super.onBackPressed();
                }
            } else {
                // Если поля обоих или одного EditText пусты, удаляем текущий пункт
                deleteItem();
            }
        } else {
            addNote();
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.et_modify_chapter_title:
                if (hasFocus) {
                    // Изменить название заметки
                    modifyItem(etModifyChapterTitle);
                }
                break;
            case R.id.et_modify_chapter_content:
                if (hasFocus) {
                    // Изменить содержимое заметки
                    modifyItem(etModifyChapterContent);
                }
                break;
        }
    }

    // Изменить заметку
    private void modifyItem(EditText editText) {
        // Получаем фокус текущего EditText
        editText.requestFocus();
        // Задаем состояние для addChangeNote
        changeSaveNote.setChecked(true);
        // Меняем картинку
        changeSaveNote.setIcon(R.drawable.ic_save);
        // Меняем заговок на "Сохранить"
        changeSaveNote.setTitle(R.string.save_note);
        // Переходим в конец текста
        editText.setSelection(editText.getText().length());
        // Отображаем клавиатуру с фокусом на текущий EditText
        openKeyboard(editText);
        // При попытке выйти будет вызвано диалоговое окно
        savedState = false;
    }

    // Добавить заметку
    private void saveNote() {
        getTextFromEditTexts();
        // Если поля обоих или одного EditText не пусты
        if (!strChapterTitle.isEmpty() || !strChapterContent.isEmpty()) {
            // Удаляем фокус с обоих EditText
            clearFocusEditTexts();
            // Задаем состояние для addChangeNote
            changeSaveNote.setChecked(false);
            // Меняем картинку
            changeSaveNote.setIcon(R.drawable.ic_edit);
            // Меняем заговок на "Изменить"
            changeSaveNote.setTitle(R.string.modify_note);
            // Скрываем клавиатуру
            closeKeyboard();
            // Присваиваем новые значения старым
            strChapterTitle = strGetChapterTitle;
            strChapterContent = strGetChapterContent;
            // Делаем не нужным вызов диалога
            savedState = true;
        } else {
            // Если поля обоих или одного EditText пусты удаляем текущий пункт
            deleteItem();
        }
    }

    // Получаем конечный вариант и обновляем данный пункт в базе данных
    @SuppressLint("SimpleDateFormat")
    private void addNote() {
        // Получаем текущие введенные данные
        getTextFromEditTexts();
        // Вызываем метод databaseUpdateItem передаем id и новые данные
        databaseManager.databaseUpdateItem(_id, strChapterTitle, strChapterContent);
        // Вызываем тост сообщение
        Toast.makeText(this, R.string.note_change, Toast.LENGTH_SHORT).show();
        // Возвращаемся в главное активити
        returnMainActivity();
    }

    private void deleteItem() {
        // Удаляем текущий пункт
        databaseManager.databaseDeleteItem(_id);
        // Вызываем тост сообщение
        Toast.makeText(this, R.string.note_deleted, Toast.LENGTH_SHORT).show();
        // Возвращаемся в главное активити
        returnMainActivity();
    }

    // Диалоговое окно с предложением сохранить или удалить данные
    private void questionSaveChangesDialog() {
        final AlertDialog.Builder questionDialog = new AlertDialog.Builder(this);

        questionDialog.setIcon(R.drawable.ic_warning);
        questionDialog.setTitle(R.string.warning);
        questionDialog.setMessage(R.string.question_save_changes);
        questionDialog.setCancelable(false);

        // Обработчик варианта "Сохранить"
        questionDialog.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addNote();
            }
        });

        // Обработчик варианта "Не сохранять"
        questionDialog.setNegativeButton(R.string.not_save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
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
                deleteItem();
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

    // Отображаем клаиатуру с фокусом на etAddChapterContent
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
        etModifyChapterContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager keyboard = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                if (keyboard != null) {
                    keyboard.hideSoftInputFromWindow(etModifyChapterContent.
                            getWindowToken(), 0);
                }
            }
        }, 200);
    }

    // Метод возвращения к главной активти
    private void returnMainActivity() {
        Intent toMainList = new Intent(ModifyNoteActivity.this, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(toMainList);
    }

    // Получаем введенные в EditText данные
    private void getTextFromEditTexts() {
        strChapterTitle = etModifyChapterTitle.getText().toString();
        strChapterContent = etModifyChapterContent.getText().toString();
    }

    // Убираем фокус
    private void clearFocusEditTexts() {
        // Переходим в начало текста
        etModifyChapterTitle.setSelection(0);
        etModifyChapterContent.setSelection(0);
        // Очищаем фокус
        etModifyChapterTitle.clearFocus();
        etModifyChapterContent.clearFocus();
    }

    // Метод "Поделиться"
    private void shareANote() {
        getTextFromEditTexts();
        String contentForSharing = strChapterTitle + "<p/>" + strChapterContent;
        Intent shareText = new Intent(Intent.ACTION_SEND);
        shareText.setType("text/plain");
        shareText.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(contentForSharing));
        shareText.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareText = Intent.createChooser(shareText, "Поделиться через");
        startActivity(shareText);
    }
}