package com.pcamarounds.utils;

import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class InfiniteScroller extends RecyclerView.OnScrollListener {
    private static final String TAG = "InfiniteScroller";
    /**
     * The total number of items in the dataset after the last load
     */
    private int mPreviousTotal = 0;
    /**
     * True if we are still waiting for the last set of data to load.
     */
    private boolean mLoading = true;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = recyclerView.getChildCount();
        int totalItemCount = recyclerView.getLayoutManager().getItemCount();
        int firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

        if (mLoading) {
            if (totalItemCount > mPreviousTotal) {
                mLoading = false;
                mPreviousTotal = totalItemCount;
            }
        }
       /* int visibleThreshold = 5;
        if (!mLoading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            // End has been reached
            onLoadMore();
            mLoading = true;
        }*/


        if (dy > 0) {
            if ((visibleItemCount + firstVisibleItem) >= totalItemCount) {
                if (!mLoading){
                    Log.e(TAG, "onLoadMore: ----------------------" );
                    onLoadMore();
                    mLoading = true;
                }
            }
        }

    }

    public abstract void onLoadMore();

}