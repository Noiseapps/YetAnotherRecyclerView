package pl.noiseapps.yetanotherrecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@SuppressWarnings("unused")
public class YetAnotherRecyclerView extends FrameLayout {
    public static final int STATE_NORMAL = 1;
    public static final int STATE_LOADING = 2;
    public static final int STATE_EMPTY = 3;
    public static final int STATE_ERROR = 4;
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

        final ViewStub emptyStub = (ViewStub) findViewById(R.id.emptyViewContent);
        emptyStub.setLayoutResource(emptyId);
        emptyStub.inflate();

        final ViewStub errorStub = (ViewStub) findViewById(R.id.errorViewContent);
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

    public void setState(@ViewStates int viewState) {
        switch (viewState) {
            case STATE_EMPTY:
                hideAll();
                showEmpty();
                break;
            case STATE_ERROR:
                hideAll();
                showError();
                break;
            case STATE_LOADING:
                hideAll();
                showProgress();
                break;
            case STATE_NORMAL:
            default:
                hideAll();
                showList();
                break;
        }
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

    /**
     * Sets message when an error state is showing
     *
     * @param message {@link String message}
     * @throws android.content.res.Resources.NotFoundException if custom error view is used that
     *                                                         does not contain {@link TextView} with id <code>@id/recyclerErrorText</code> NOTE: there is no @+id
     */
    public void setErrorMessage(@NonNull String message) {
        ((TextView) findViewById(R.id.recyclerErrorText)).setText(message);
    }

    /**
     * Sets message when an error state ({@link ViewStates}) is showing
     *
     * @param message {@link Spanned} message
     * @throws android.content.res.Resources.NotFoundException if custom error view is used that
     *                                                         does not contain {@link TextView} with id <code>@id/recyclerErrorText</code> NOTE: there is no @+id
     */
    public void setErrorMessage(@NonNull Spanned message) {
        ((TextView) findViewById(R.id.recyclerErrorText)).setText(message);
    }

    /**
     * Sets image when an empty state ({@link ViewStates}) is showing
     *
     * @param image {@link Drawable image}
     * @throws android.content.res.Resources.NotFoundException if custom error view is used that
     *                                                         does not contain {@link ImageView} with id <code>@id/recyclerErrorImage</code> NOTE: there is no @+id
     */
    public void setErrorImage(@NonNull Drawable image) {
        ((ImageView) findViewById(R.id.recyclerErrorImage)).setImageDrawable(image);
    }

    /**
     * Sets message when an empty state ({@link ViewStates}) is showing
     *
     * @param message {@link String message}
     * @throws android.content.res.Resources.NotFoundException if custom error view is used that
     *                                                         does not contain {@link TextView} with id <code>@id/recyclerEmptyText</code> NOTE: there is no @+id
     */
    public void setEmptyMessage(@NonNull String message) {
        ((TextView) findViewById(R.id.recyclerEmptyText)).setText(message);
    }

    /**
     * Sets message when an empty state ({@link ViewStates}) is showing
     *
     * @param message {@link Spanned message}
     * @throws android.content.res.Resources.NotFoundException if custom error view is used that
     *                                                         does not contain {@link TextView} with id <code>@id/recyclerEmptyText</code> NOTE: there is no @+id
     */
    public void setEmptyMessage(@NonNull Spanned message) {
        ((TextView) findViewById(R.id.recyclerEmptyText)).setText(message);
    }

    /**
     * Sets image when an empty state ({@link ViewStates}) is showing
     *
     * @param image {@link Drawable image}
     * @throws android.content.res.Resources.NotFoundException if custom error view is used that
     *                                                         does not contain {@link ImageView} with id <code>@id/recyclerEmptyImage</code> NOTE: there is no @+id
     */
    public void setEmptyImage(@NonNull Drawable image) {
        ((ImageView) findViewById(R.id.recyclerEmptyImage)).setImageDrawable(image);
    }

    /**
     * Sets message when an loading state ({@link ViewStates}) is showing
     *
     * @param message {@link String message}
     * @throws android.content.res.Resources.NotFoundException if custom error view is used that
     *                                                         does not contain {@link TextView} with id <code>@id/recyclerEmptyText</code> NOTE: there is no @+id
     */
    public void setLoadingMessage(@NonNull String message) {
        ((TextView) findViewById(R.id.recyclerProgressText)).setText(message);
    }

    /**
     * Sets message when an loading state ({@link ViewStates}) is showing
     *
     * @param message {@link Spanned message}
     * @throws android.content.res.Resources.NotFoundException if custom error view is used that
     *                                                         does not contain {@link TextView} with id <code>@id/recyclerEmptyText</code> NOTE: there is no @+id
     */
    public void setLoadingMessage(@NonNull Spanned message) {
        ((TextView) findViewById(R.id.recyclerProgressText)).setText(message);
    }

    private void hideAll() {
        hideList();
        hideError();
        hideEmpty();
        hideProgress();
    }

    private void showError() {
        errorView.setVisibility(View.VISIBLE);
    }

    private void showList() {
        hideAll();
        recycler.setVisibility(View.VISIBLE);
    }

    private void showEmpty() {
        emptyView.setVisibility(View.VISIBLE);
    }

    private void showProgress() {
        loadingStub.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        errorView.setVisibility(View.GONE);
    }

    private void hideList() {
        recycler.setVisibility(View.GONE);
    }

    private void hideEmpty() {
        emptyView.setVisibility(View.GONE);
    }

    private void hideProgress() {
        swipeRefresh.setRefreshing(false);
        loadingStub.setVisibility(View.GONE);
    }

    private void setAdapter(RecyclerView.Adapter adapter, boolean swap, boolean recycleViews) {
        if (swap && this.adapter != null) {
            recycler.setAdapter(adapter);
        } else {
            recycler.swapAdapter(adapter, recycleViews);
        }
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                update();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                update();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
                super.onItemRangeChanged(positionStart, itemCount, payload);
                update();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                update();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                update();
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                update();
            }
        });
        this.adapter = adapter;
        update();
        swipeRefresh.setRefreshing(false);
    }


    private void update() {
        if (recycler.getAdapter().getItemCount() == 0) {
            setState(STATE_EMPTY);
        } else {
            setState(STATE_NORMAL);
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATE_NORMAL, STATE_LOADING, STATE_EMPTY, STATE_ERROR})
    public @interface ViewStates {

    }
}
