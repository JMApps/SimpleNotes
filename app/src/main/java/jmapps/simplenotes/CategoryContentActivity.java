package jmapps.simplenotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import jmapps.simplenotes.Adapter.CategoryContentAdapter;
import jmapps.simplenotes.Database.DatabaseManager;
import jmapps.simplenotes.Model.CategoryContentModel;

public class CategoryContentActivity extends AppCompatActivity {

    private int categoryPosition;
    private String strCategoryTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        Toolbar toolbar = findViewById(R.id.toolbar_category_list);
        setSupportActionBar(toolbar);

        categoryPosition = getIntent().getIntExtra("category_position", 0);
        strCategoryTitle = getIntent().getStringExtra("category_title");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(strCategoryTitle);
        }

        DatabaseManager databaseManager = new DatabaseManager(this);
        databaseManager.databaseOpen();

        TextView tvIsCategoryItemListEmpty = findViewById(R.id.tv_is_category_list_empty);
        RecyclerView rvContentCategoryList = findViewById(R.id.rv_content_category_list);

        List<CategoryContentModel> categoryContentModel = databaseManager.getCategoryContentList(categoryPosition);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvContentCategoryList.setLayoutManager(linearLayoutManager);
        rvContentCategoryList.setHasFixedSize(true);

        CategoryContentAdapter categoryContentAdapter = new CategoryContentAdapter(this, categoryContentModel, this);

        if (categoryContentAdapter.getItemCount() == 0) {
            tvIsCategoryItemListEmpty.setVisibility(View.VISIBLE);
            rvContentCategoryList.setVisibility(View.GONE);
        } else {
            tvIsCategoryItemListEmpty.setVisibility(View.GONE);
            rvContentCategoryList.setVisibility(View.VISIBLE);
        }

        categoryContentAdapter.notifyDataSetChanged();
        rvContentCategoryList.setAdapter(categoryContentAdapter);

        final FloatingActionButton fabCategoryItemNote = findViewById(R.id.fab_category_item_note);
        fabCategoryItemNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addCategoryNoteIntent = new Intent(CategoryContentActivity.this, AddCategoryNoteActivity.class);
                addCategoryNoteIntent.putExtra("category_position", categoryPosition);
                addCategoryNoteIntent.putExtra("category_title", strCategoryTitle);
                startActivity(addCategoryNoteIntent);
            }
        });

        rvContentCategoryList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) fabCategoryItemNote.hide();
                else fabCategoryItemNote.show();
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    public void setExtraToModifyCategoryItem(String strId,
                                             String strCategoryItemId,
                                             String strCategoryItemTitle,
                                             String strCategoryItemContent) {
        Intent toModifyNote = new Intent(CategoryContentActivity.this, ModifyCategoryNoteActivity.class);
        toModifyNote.putExtra("category_item_id", strId);
        toModifyNote.putExtra("category_item_position", strCategoryItemId);
        toModifyNote.putExtra("category_item_title", strCategoryItemTitle);
        toModifyNote.putExtra("category_item_content", strCategoryItemContent);
        toModifyNote.putExtra("category_position", categoryPosition);
        toModifyNote.putExtra("category_title", strCategoryTitle);
        startActivity(toModifyNote);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
