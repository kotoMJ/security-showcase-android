package cz.koto.securityshowcase.utility;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;


public class LazyLoadingManager<T> {

	private final boolean mProgressOnAllPages;
	private int mStartPage = 0;// init start page to 0 by default
	private List<T> mResults = new ArrayList<T>();
	private boolean mProgress;
	private RecyclerView mRecyclerView;
	private boolean mFetching = false;
	private int mCurrentPageToFetch = mStartPage;
	private ResultsCallProvider<T> mResultsCallProvider;
	private OnResultsUpdatedCallback<T> mOnResultsUpdatedCallback;
	private OnProgressChangedCallback mOnProgressChangedCallback;
	private OnNetworkErrorCallback mOnNetworkErrorCallback;


	public interface OnNetworkErrorCallback {
		void onNetworkError(Throwable throwable);
	}


	public interface OnResultsUpdatedCallback<S> {
		void onResultsUpdated(List<S> newResults);
	}


	public interface OnProgressChangedCallback {
		void onProgressChanged(boolean progress);
	}


	public interface ResultsCallProvider<S> {
		Maybe<List<S>> getResultsCall(int page);
	}


	private interface OnResultsFetchedCallback {
		void onResultsFetched();
	}


	public LazyLoadingManager(ResultsCallProvider<T> resultsCallProvider) {
		this(resultsCallProvider, false);
	}


	/**
	 * @param resultsCallProvider
	 * @param startPage
	 */
	public LazyLoadingManager(ResultsCallProvider<T> resultsCallProvider, int startPage) {
		this(resultsCallProvider, false, startPage);
	}


	public LazyLoadingManager(ResultsCallProvider<T> resultsCallProvider, boolean progressOnAllPages) {
		mResultsCallProvider = resultsCallProvider;
		mProgressOnAllPages = progressOnAllPages;
	}


	/**
	 * @param resultsCallProvider
	 * @param progressOnAllPages
	 * @param startPage
	 */
	public LazyLoadingManager(ResultsCallProvider<T> resultsCallProvider, boolean progressOnAllPages, int startPage) {
		mStartPage = startPage;
		mCurrentPageToFetch = mStartPage;//re-init start page default
		mResultsCallProvider = resultsCallProvider;
		mProgressOnAllPages = progressOnAllPages;
	}


	public void setOnNetworkErrorCallback(OnNetworkErrorCallback onNetworkErrorCallback) {
		mOnNetworkErrorCallback = onNetworkErrorCallback;
	}


	public RecyclerView getRecyclerView() {
		return mRecyclerView;
	}


	public void setRecyclerView(RecyclerView recyclerView) {
		mRecyclerView = recyclerView;
		Logcat.INSTANCE.d("Setting recycler %s for %s", recyclerView, this);

		mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				if(dy > 0) //check for scroll down
				{
//					Logcat.INSTANCE.w("Scrolled recycler %s on %s", recyclerView, this);
					int visibleItemCount = getRecyclerView().getLayoutManager().getChildCount();
					int totalItemCount = getRecyclerView().getLayoutManager().getItemCount();
					int pastVisiblesItems = getLayoutManager().findFirstVisibleItemPosition();
					Logcat.INSTANCE.d("VisibleCount %s, TotalCount %s, pastVisibleItems %s, fetching:%s", visibleItemCount, totalItemCount, pastVisiblesItems, mFetching);
					if(!mFetching) {
						//Logcat.INSTANCE.d("visibleItemCount + pastVisiblesItems: %s >= totalItemCount: %s", (visibleItemCount + pastVisiblesItems), totalItemCount);
						if((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
							mFetching = true;
							fetchNextResults();
						}
					}
				}
			}
		});
	}


	public List<T> getResults() {
		return mResults;
	}


	public boolean isProgress() {
		return mProgress;
	}


	private void setProgress(boolean progress) {
		mProgress = progress;
		onProgressUpdated();
	}


	public void clearItems() {
		mCurrentPageToFetch = mStartPage;
		mResults.clear();
		onResultsUpdated();
	}


	public void refresh() {
		int fetchUntilPage = mCurrentPageToFetch == mStartPage ? mStartPage : mCurrentPageToFetch - 1;
		//Logcat.INSTANCE.d("fetchUntilPage %s", fetchUntilPage);
		mCurrentPageToFetch = mStartPage;
		mResults.clear();
		fetchResultsUntilPage(mStartPage, fetchUntilPage);
	}


	public void fetchNextResults() {
		fetchNextResults(null);
	}


	public void fetchNextResults(final OnResultsFetchedCallback callback) {
		setProgress(mCurrentPageToFetch == mStartPage || mProgressOnAllPages);
		Logcat.INSTANCE.w("mCurrentPageToFetch: %s", mCurrentPageToFetch);
		mResultsCallProvider.getResultsCall(mCurrentPageToFetch)
				.subscribe(new Consumer<List<T>>() {
					@Override
					public void accept(List<T> searchItemViewModels) throws Exception {
						mResults.addAll(searchItemViewModels);
						LazyLoadingManager.this.onResultsUpdated();
						mCurrentPageToFetch++;
						mFetching = false;
						LazyLoadingManager.this.setProgress(false);
						if(callback != null)
							callback.onResultsFetched();
					}
				}, new Consumer<Throwable>() {
					@Override
					public void accept(Throwable throwable) throws Exception {
//					Logcat.e(throwable, "Error fetching search results");
						LazyLoadingManager.this.onNetworkError(throwable);
						mFetching = false;
						LazyLoadingManager.this.setProgress(false);
					}
				}, new Action() {
					@Override
					public void run() throws Exception {//ensure cleanup even in case of empty result
						mFetching = false;
						LazyLoadingManager.this.setProgress(false);
					}
				});
	}


	public void setOnProgressChangedCallback(OnProgressChangedCallback onProgressChangedCallback) {
		mOnProgressChangedCallback = onProgressChangedCallback;
	}


	public void setOnResultsUpdatedCallback(OnResultsUpdatedCallback<T> onResultsUpdatedCallback) {
		mOnResultsUpdatedCallback = onResultsUpdatedCallback;
	}


	private void fetchResultsUntilPage(final int page, final int untilPage) {
		if(page <= untilPage) {
			fetchNextResults(new OnResultsFetchedCallback() {
				@Override
				public void onResultsFetched() {
					LazyLoadingManager.this.fetchResultsUntilPage(page + 1, untilPage);
				}
			});
		}
	}


	private void onNetworkError(Throwable throwable) {
		if(mOnNetworkErrorCallback != null)
			mOnNetworkErrorCallback.onNetworkError(throwable);
	}


	private void onResultsUpdated() {
		if(mOnResultsUpdatedCallback != null) mOnResultsUpdatedCallback.onResultsUpdated(new ArrayList<T>(mResults));
	}


	private void onProgressUpdated() {
		if(mOnProgressChangedCallback != null) mOnProgressChangedCallback.onProgressChanged(mProgress);
	}


	private LinearLayoutManager getLayoutManager() {
		return ((LinearLayoutManager) getRecyclerView().getLayoutManager());
	}
}
