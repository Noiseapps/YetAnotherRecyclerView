package pl.noiseapps.yetanotherrecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class YetAnotherRecyclerView extends FrameLayout {

    private int mainViewId = R.layout.main_view;
    private int emptyId;
    private int errorId;
    private int progressId;

    private RecyclerView recycler;
    private ViewStub loadingStub;

    private NestedScrollView emptyView;
    private NestedScrollView errorView;

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    public YetAnotherRecyclerView(Context context) {
        super(context);
        initAttrs(null);
        initViews();
    }

    public YetAnotherRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initViews();
    }

    public YetAnotherRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        initViews();
    }

    private void initViews() {
        LayoutInflater.from(getContext()).inflate(mainViewId, this);

        recycler = (RecyclerView) findViewById(R.id.recycler);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        emptyView = (NestedScrollView) findViewById(R.id.emptyView);
        errorView = (NestedScrollView) findViewById(R.id.errorView);

        ViewStub emptyStub = (ViewStub) findViewById(R.id.emptyViewContent);
        emptyStub.setLayoutResource(emptyId);
        emptyStub.inflate();

        ViewStub errorStub = (ViewStub) findViewById(R.id.errorViewContent);
        errorStub.setLayoutResource(errorId);
        errorStub.inflate();

        loadingStub = (ViewStub) findViewById(R.id.loadingViewContent);
        loadingStub.setLayoutResource(progressId);
        loadingStub.inflate();

        recycler.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        loadingStub.setVisibility(View.VISIBLE);
    }

    private void initAttrs(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.YetAnotherRecyclerView);
            try {
                emptyId = a.getResourceId(R.styleable.YetAnotherRecyclerView_emptyViewLayout, R.layout.layout_empty);
                errorId = a.getResourceId(R.styleable.YetAnotherRecyclerView_emptyViewLayout, R.layout.layout_error);
                progressId = a.getResourceId(R.styleable.YetAnotherRecyclerView_progressViewLayout, R.layout.layout_loading);
            } finally {
                a.recycle();
            }
        } else {
            emptyId = R.layout.layout_empty;
            errorId = R.layout.layout_error;
            progressId = R.layout.layout_loading;

        }
    }

    public void initRecyclerView(RecyclerView.LayoutManager layoutManager, RecyclerView.Adapter adapter) {
        setLayoutManager(layoutManager);
        setAdapter(adapter);
    }

    public RecyclerView getRecycler() {
        return recycler;
    }

    public RecyclerView.Adapter getAdapter() {
        return adapter;
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        this.setAdapter(adapter, false, false);
    }

    public void swapAdapter(RecyclerView.Adapter adapter, boolean recycleViews) {
        this.setAdapter(adapter, true, recycleViews);
    }

    public void setRefreshing(boolean refreshing) {
        swipeRefresh.setRefreshing(refreshing);
    }

    private void hideAll() {
        hideList();
        hideError();
        hideEmpty();
        hideProgress();
    }

    public void showError() {
        errorView.setVisibility(View.VISIBLE);
    }

    public void showList() {
        hideAll();
        recycler.setVisibility(View.VISIBLE);
    }

    public void showEmpty() {
        emptyView.setVisibility(View.VISIBLE);
    }

    public void showProgress() {
        loadingStub.setVisibility(View.VISIBLE);
    }

    public void hideError() {
        errorView.setVisibility(View.GONE);
    }

    public void hideList() {
        recycler.setVisibility(View.GONE);
    }

    public void hideEmpty() {
        emptyView.setVisibility(View.GONE);
    }

    public void hideProgress() {
        swipeRefresh.setRefreshing(false);
        loadingStub.setVisibility(View.GONE);
    }

    public void toggleErrorViewState(boolean show) {
        hideAll();
        if (show) {
            errorView.setVisibility(View.VISIBLE);
        } else {
            recycler.setVisibility(View.VISIBLE);
        }
    }

    public void toggleEmptyViewState(boolean show) {
        hideAll();
        if (show) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recycler.setVisibility(View.VISIBLE);
        }
    }

    public void toggleLoadingViewState(boolean show) {
        hideAll();
        if (show) {
            errorView.setVisibility(View.VISIBLE);
        } else {
            swipeRefresh.setRefreshing(false);
            recycler.setVisibility(View.VISIBLE);
        }
    }

    private void setAdapter(RecyclerView.Adapter adapter, boolean swap, boolean recycleViews) {
        if (swap && this.adapter != null) {
            recycler.setAdapter(adapter);
        } else {
            recycler.swapAdapter(adapter, recycleViews);
        }
        this.adapter = adapter;
        if (adapter.getItemCount() == 0) {
            toggleEmptyViewState(true);
        } else {
            showList();
        }
        swipeRefresh.setRefreshing(false);
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return layoutManager;
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
        recycler.setLayoutManager(layoutManager);
    }

    public SwipeRefreshLayout getSwipeRefresh() {
        return swipeRefresh;
    }

    public void setRefreshListener(@Nullable SwipeRefreshLayout.OnRefreshListener refreshListener) {
        if (refreshListener != null) {
            swipeRefresh.setOnRefreshListener(refreshListener);
        }
    }

    public void setErrorMessage(@NonNull String message) {
        ((TextView) findViewById(R.id.recyclerErrorText)).setText(message);
    }

    public void setErrorImage(@NonNull Drawable message) {
        ((ImageView) findViewById(R.id.recyclerErrorImage)).setImageDrawable(message);
    }

    public void setEmptyMessage(@NonNull String message) {
        ((TextView) findViewById(R.id.recyclerErrorText)).setText(message);
    }

    public void setEmptyImage(@NonNull Drawable message) {
        ((ImageView) findViewById(R.id.recyclerEmptyImage)).setImageDrawable(message);
    }

    public void setLoadingMessage(@NonNull String message) {
        ((TextView) findViewById(R.id.recyclerProgressText)).setText(message);
    }
}
