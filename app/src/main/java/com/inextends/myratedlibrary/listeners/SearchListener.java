package com.inextends.myratedlibrary.listeners;

import android.widget.CursorAdapter;
import android.widget.SearchView;

public class SearchListener implements SearchView.OnQueryTextListener {
    private CursorAdapter mAdapter;

    public SearchListener(CursorAdapter adapter) {
        this.mAdapter = adapter;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        mAdapter.getFilter().filter(s.toString());
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        mAdapter.getFilter().filter(s.toString());
        return true;
    }
}
