package jmapps.simplenotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import jmapps.simplenotes.Database.DatabaseManager;

public class AddNoteActivity extends AppCompatActivity {

    private EditText etAddChapterTitle;
    private EditText etAddChapterContent;
    private DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.add_note));
        setContentView(R.layout.activity_add_note);

        databaseManager = new DatabaseManager(this);
        databaseManager.databaseOpen();

        etAddChapterTitle = findViewById(R.id.et_add_chapter_title);
        etAddChapterContent = findViewById(R.id.et_add_chapter_content);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_note) {

            addNote();

        } else if (id == android.R.id.home) {

            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addNote() {
        final String strChapterTitle = etAddChapterTitle.getText().toString();
        final String strChapterContent = etAddChapterContent.getText().toString();

        databaseManager.databaseInsert(strChapterTitle, strChapterContent);

        Intent returnMainActivityIntent = new Intent(AddNoteActivity.this, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(returnMainActivityIntent);
    }
}
