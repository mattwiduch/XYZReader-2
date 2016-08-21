package com.example.xyzreader.ui;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.graphics.Palette;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;

/**
 * A fragment representing a single Article detail screen. This fragment is
 * either contained in a {@link ArticleListActivity} in two-pane mode (on
 * tablets) or a {@link ArticleDetailActivity} on handsets.
 */
public class ArticleDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "ArticleDetailFragment";

    public static final String ARG_ITEM_ID = "item_id";

    private Cursor mCursor;
    private long mItemId;
    private View mRootView;
    private ImageView mPhotoView;
    private CollapsingToolbarLayout mToolbarLayout;

    private int mColor;

    public ArticleDetailFragment() {
        /**
         * Mandatory empty constructor for the fragment manager to instantiate the
         * fragment (e.g. upon screen orientation changes).
         */
    }

    public static ArticleDetailFragment newInstance(long itemId) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARG_ITEM_ID, itemId);
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItemId = getArguments().getLong(ARG_ITEM_ID);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // In support library r8, calling initLoader for a fragment in a FragmentPagerAdapter in
        // the fragment's onCreate may cause the same LoaderManager to be dealt to multiple
        // fragments because their mIndex is -1 (haven't been added to the activity yet). Thus,
        // we do this in onActivityCreated.
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);
        mPhotoView = (ImageView) mRootView.findViewById(R.id.article_photo);
        mRootView.findViewById(R.id.app_bar_layout).setBackgroundColor(
                ContextCompat.getColor(getActivity(), android.R.color.transparent));
        mColor = ContextCompat.getColor(getActivity(), R.color.theme_primary_dark);

        final NestedScrollView scrollView = (NestedScrollView) mRootView.findViewById(R.id.scroll_view);
        final Bundle state = savedInstanceState;
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (isVisible()) {
                    // Landscape mode scroll adjustment
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        int height = scrollView.getHeight();
                        if (height > 0) {
                            int bottomPadding = Math.round(24 *
                                    (getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
                            int topPadding = scrollView.getHeight() - bottomPadding;
                            scrollView.setPadding(0, topPadding, 0, bottomPadding);
                            // If there's no saved state
                            if (state == null) {
                                // calculate correct padding and scroll to 1/3 of the view
                                scrollView.smoothScrollTo(0, topPadding / 3);
                            } else {
                                int toScroll = state.getInt(getString(R.string.key_article_state)) > topPadding / 3
                                        ? state.getInt(getString(R.string.key_article_state)) : topPadding / 3;
                                scrollView.smoothScrollTo(0, toScroll);
                            }
                            // remove listener so it's called only once
                            scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                        // Portrait mode scroll adjustment
                    } else if (getResources().getConfiguration().orientation ==
                            Configuration.ORIENTATION_PORTRAIT && state != null) {
                        int scrollPosition = state.getInt(getString(R.string.key_article_state));
                        final AppBarLayout appBarLayout =
                                (AppBarLayout) mRootView.findViewById(R.id.app_bar_layout);

                        if (scrollPosition > 0) {
                            // Calculate toolbar collapse offset
                            int toCollapse = scrollPosition > appBarLayout.getBottom()
                                    ? appBarLayout.getBottom() : scrollPosition;
                            // Calculate new scroll position
                            int toScroll = scrollPosition - appBarLayout.getBottom() > 0
                                    ? scrollPosition - appBarLayout.getBottom() : 0;

                            // Collapse toolbar by calculated offset before scrolling
                            CoordinatorLayout.LayoutParams params =
                                    (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
                            final AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
                            if (behavior != null) {
                                ValueAnimator valueAnimator = ValueAnimator.ofInt();
                                valueAnimator.setInterpolator(new DecelerateInterpolator());
                                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        behavior.setTopAndBottomOffset((Integer) animation.getAnimatedValue());
                                        appBarLayout.requestLayout();
                                    }
                                });

                                valueAnimator.setIntValues(0, -toCollapse);
                                valueAnimator.setDuration(400);
                                valueAnimator.start();
                            }
                            scrollView.smoothScrollTo(0, toScroll);
                        } else {
                            scrollView.smoothScrollTo(0, 0);
                        }
                        // remove listener so it's called only once
                        scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            }
        });

        // Add OnClickListener to FAB
        mRootView.findViewById(R.id.share_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(getString(R.string.share_sample_text))
                        .getIntent(), getString(R.string.action_share)));
            }
        });

        bindViews();
        return mRootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int scrollPosition = mRootView.findViewById(R.id.scroll_view).getScrollY();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Calculate correct scroll position in portrait mode by including offset of collapsed
            // toolbar
            AppBarLayout appBarLayout = (AppBarLayout) mRootView.findViewById(R.id.app_bar_layout);
            scrollPosition += appBarLayout.getHeight() - appBarLayout.getBottom();
        } else {
            NestedScrollView scrollView = (NestedScrollView) mRootView.findViewById(R.id.scroll_view);
            // Do not scroll if scroll view was at or below default position in landscape mode
            if (scrollPosition <= (scrollView.getPaddingTop() / 3)) {
                scrollPosition = 0;
            }
        }
        outState.putInt(getString(R.string.key_article_state), scrollPosition);
    }

    /**
     * Binds data to the views.
     */
    private void bindViews() {
        if (mRootView == null) {
            return;
        }

        mToolbarLayout = (CollapsingToolbarLayout) mRootView.findViewById(R.id.collapsing_toolbar_layout);
        final TextView titleView = (TextView) mRootView.findViewById(R.id.article_title);
        TextView bylineView = (TextView) mRootView.findViewById(R.id.article_byline);
        bylineView.setMovementMethod(new LinkMovementMethod());
        TextView bodyView = (TextView) mRootView.findViewById(R.id.article_body);

        if (mCursor != null) {
            mRootView.setAlpha(0);
            mRootView.setVisibility(View.VISIBLE);
            mRootView.animate().alpha(1);
            titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
            bylineView.setText(Html.fromHtml(
                    DateUtils.getRelativeTimeSpanString(
                            mCursor.getLong(ArticleLoader.Query.PUBLISHED_DATE),
                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_ALL).toString()
                            + " by <b>"
                            + mCursor.getString(ArticleLoader.Query.AUTHOR)
                            + "</b>"));
            bodyView.setText(Html.fromHtml(mCursor.getString(ArticleLoader.Query.BODY)));

            AppBarLayout appBarLayout = (AppBarLayout) mRootView.findViewById(R.id.app_bar_layout);
            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                boolean showTitle = false;
                int scrollRange = -1;

                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    // Show title only when toolbar is collapsed
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if (scrollRange + verticalOffset == 0) {
                        mToolbarLayout.setTitle(titleView.getText());
                        showTitle = true;
                    } else if (showTitle) {
                        mToolbarLayout.setTitle(" ");
                        showTitle = false;
                    }

                    // Show/Hide fab (only in portrait)
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        if (verticalOffset < -1.95 * (mToolbarLayout.getHeight() - mPhotoView.getHeight())) {
                            ((FloatingActionButton) mRootView.findViewById(R.id.share_fab)).hide();
                        } else {
                            ((FloatingActionButton) mRootView.findViewById(R.id.share_fab)).show();
                        }
                    }
                }
            });

            ImageLoaderHelper.getInstance(getActivity()).getImageLoader()
                    .get(mCursor.getString(ArticleLoader.Query.PHOTO_URL), new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                            Bitmap bitmap = imageContainer.getBitmap();
                            if (bitmap != null) {
                                mPhotoView.setImageBitmap(bitmap);

                                final int imageSampleHeight = (int) TypedValue.applyDimension(
                                        TypedValue.COMPLEX_UNIT_DIP,
                                        36,
                                        getActivity().getResources().getDisplayMetrics());

                                Palette p = Palette.from(bitmap)
                                        .maximumColorCount(5)
                                        .setRegion(0, 0, bitmap.getWidth() - 1, imageSampleHeight)
                                        .generate();

                                mColor = p.getDarkVibrantColor(p.getDarkMutedColor(p.getVibrantColor(
                                        p.getMutedColor(p.getLightVibrantColor(p.getLightMutedColor(
                                                Color.BLACK))))));

                                if (getUserVisibleHint()) {
                                    applyColors();
                                }
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                                Bitmap logo = BitmapFactory.decodeResource(getActivity().getResources(),
                                        R.drawable.empty_detail);
                                mColor = ContextCompat.getColor(getActivity(),
                                        R.color.theme_background_dark);

                                // Combine logo and background color in new bitmap
                                // and show it as article picture
                                Bitmap errorImage = Bitmap.createBitmap(logo.getWidth(),
                                        logo.getHeight(), logo.getConfig());
                                Canvas canvas = new Canvas(errorImage);
                                canvas.drawColor(ContextCompat.getColor(getActivity(),
                                        R.color.theme_background));
                                canvas.drawBitmap(logo, 0, 0, null);
                                mPhotoView.setImageBitmap(errorImage);

                                if (getUserVisibleHint()) {
                                    applyColors();
                                    // Show Toast with error message
                                    Toast.makeText(getActivity(), R.string.error_loading_image,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                    });
        } else {
            mRootView.setVisibility(View.GONE);
            titleView.setText("N/A");
            bylineView.setText("N/A");
            bodyView.setText("N/A");
        }
    }

    /**
     * Applies colours derived from article photo to UI elements.
     **/
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void applyColors() {
        // Collapsed toolbar colour
        mToolbarLayout.setContentScrimColor(lightenColor(mColor));

        //Status bar colour change animation
        //Credit to Nick Butcher, creator of Plaid
        //https://github.com/nickbutcher/plaid
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Window window = getActivity().getWindow();

            if (mColor != window.getStatusBarColor()) {
                ValueAnimator statusBarColorAnim = ValueAnimator.ofArgb(
                        window.getStatusBarColor(), mColor);
                statusBarColorAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        window.setStatusBarColor((int) animation.getAnimatedValue());
                    }
                });
                statusBarColorAnim.setDuration(600L);
                statusBarColorAnim.setInterpolator(new AccelerateDecelerateInterpolator());
                statusBarColorAnim.start();
            }
        }
    }

    /**
     * Creates lighter shade of a given colour.
     *
     * @param color int value of base colour
     * @return new colour value
     */
    private int lightenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[1] = hsv[1] - 0.1f;
        hsv[2] = hsv[2] + 0.05f;
        return Color.HSVToColor(hsv);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newInstanceForItemId(getActivity(), mItemId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (!isAdded()) {
            if (cursor != null) {
                cursor.close();
            }
            return;
        }

        mCursor = cursor;
        if (mCursor != null && !mCursor.moveToFirst()) {
            Log.e(TAG, "Error reading item detail cursor");
            mCursor.close();
            mCursor = null;
        }

        bindViews();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
        bindViews();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisible() && isResumed()) {
            applyColors();
        }
    }
}