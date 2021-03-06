package com.inextends.myratedlibrary;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SearchView;

import com.inextends.myratedlibrary.data.AuthorContract;
import com.inextends.myratedlibrary.listeners.SearchListener;

public class AuthorListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "AuthorListFragment";
    private AuthorCursorAdapter mAuthorCursorAdapter;
    private static final int LOADER_ID = 2;
    private SearchView mSearchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView authorsListView = (ListView) view.findViewById(R.id.list_authors);
        mAuthorCursorAdapter = new AuthorCursorAdapter(getContext(), null);
        authorsListView.setAdapter(mAuthorCursorAdapter);
        authorsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(getContext(), AuthorDetailsActivity.class);
                Uri currentAuthorUri = ContentUris.withAppendedId(AuthorContract.AuthorEntry.CONTENT_URI, id);
                intent.setData(currentAuthorUri);
                startActivity(intent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AuthorEditorActivity.class);
                startActivity(intent);
            }
        });

        setUpSearchFeature(view);
    }

    private void setUpSearchFeature(View view) {
        mSearchView = (SearchView) view.findViewById(R.id.search);
        mSearchView.setOnQueryTextListener(new SearchListener(mAuthorCursorAdapter));

        mAuthorCursorAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence charSequence) {
                String select = "(" + AuthorContract.AuthorEntry.COLUMN_NAME + " LIKE ?) ";
                String[] selectArgs = {"%" + charSequence + "%"};
                String[] contactsProjection = new String[]{
                        AuthorContract.AuthorEntry._ID,
                        AuthorContract.AuthorEntry.COLUMN_NAME
                };

                return getActivity().getContentResolver().query(AuthorContract.AuthorEntry.CONTENT_URI, contactsProjection, select, selectArgs, null);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.author_list_fragment, container, false);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri.Builder builder = AuthorContract.AuthorEntry.CONTENT_URI.buildUpon();
        builder.appendPath("ratings");
        Uri uri = builder.build();
        return new CursorLoader(getContext(), uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAuthorCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAuthorCursorAdapter.swapCursor(null);
    }
}
