package jmapps.simplenotes.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jmapps.simplenotes.R;

public class BookmarkListFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootBookmarkList = inflater.inflate(R.layout.fragment_bookmark, container, false);

        return rootBookmarkList;
    }
}
