/*
 * Copyright (C) 2015 Pedro Vicente Gomez Sanchez.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.nodeepshit;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.CheckResult;

import com.github.nodeepshit.model.AndroidMainThread;
import com.github.nodeepshit.model.Logdog;
import com.github.nodeepshit.model.Logcat;
import com.github.nodeepshit.model.Lynx;
import com.github.nodeepshit.model.TimeProvider;
import com.github.nodeepshit.model.Trace;
import com.github.nodeepshit.presenter.LynxPresenter;
import com.github.nodeepshit.renderer.TraceRendererBuilder;
import com.pedrogomez.renderers.RendererAdapter;
import com.pedrogomez.renderers.RendererBuilder;

import java.util.List;

/**
 * Main library view. Custom view based on a RelativeLayout used to show all the information
 * printed by the Android Logcat. Add this view to your layouts if you want to show your Logcat
 * traces and configure it using styleable attributes.
 *
 * @author Pedro Vicente Gomez Sanchez.
 */
public class LynxView extends RelativeLayout implements LynxPresenter.View {

  private static final String LOGTAG = "LynxView";
  private static final String SHARE_INTENT_TYPE = "text/plain";
  private static final CharSequence SHARE_INTENT_TITLE = "Application Logcat";
  private static final int DEFAULT_POSITION = 0;

  private LynxPresenter presenter;
  private LynxConfig lynxConfig;

  private ListView lv_traces;

  private RendererAdapter<Trace> adapter;
  private int lastScrollPosition;
  private Listener mListener;
  private int mLastSelectedPosition = -1;

  public LynxView(Context context) {
    this(context, null);
  }

  public LynxView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public LynxView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initializeConfiguration(attrs);
    initializePresenter();
    initializeView();
  }

  /**
   * Initializes LynxPresenter if LynxView is visible when is attached to the window.
   */
  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    if (isVisible()) {
      resumePresenter();
    }
  }

  /**
   * Stops LynxPresenter when LynxView is detached from the window.
   */
  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    pausePresenter();
  }

  /**
   * Initializes or stops LynxPresenter based on visibility changes. Doing this Lynx is not going
   * to
   * read your application Logcat if LynxView is not visible or attached.
   */
  @Override protected void onVisibilityChanged(View changedView, int visibility) {
    super.onVisibilityChanged(changedView, visibility);
    if (changedView != this) {
      return;
    }

    if (visibility == View.VISIBLE) {
      resumePresenter();
    } else {
      pausePresenter();
    }
  }

  /**
   * Given a valid LynxConfig object update all the dependencies to apply this new configuration.
   *
   * @param lynxConfig the lynx configuration
   */
  public void setLynxConfig(LynxConfig lynxConfig) {
    validateLynxConfig(lynxConfig);
    boolean hasChangedLynxConfig = !this.lynxConfig.equals(lynxConfig);
    if (hasChangedLynxConfig) {
      this.lynxConfig = (LynxConfig) lynxConfig.clone();
      updateAdapter();
      presenter.setLynxConfig(lynxConfig);
    }
  }

  /**
   * Returns the current LynxConfig object used.
   *
   * @return the lynx configuration
   */
  public LynxConfig getLynxConfig() {
    return lynxConfig;
  }

  /**
   * Given a {@code List<Trace>} updates the ListView adapter with this information and keeps the
   * scroll position if needed.
   */
  @Override public void showTraces(List<Trace> traces, int removedTraces) {
    if (lastScrollPosition == 0) {
      lastScrollPosition = lv_traces.getFirstVisiblePosition();
    }
    adapter.clear();
    adapter.addAll(traces);
    adapter.notifyDataSetChanged();
    updateScrollPosition(removedTraces);
  }

  /**
   * Removes all the traces rendered in the ListView.
   */
  @Override public void clear() {
    adapter.clear();
    adapter.notifyDataSetChanged();
  }

  /**
   * Uses an intent to share content and given one String with all the information related to the
   * List of traces shares this information with other applications.
   */
  @CheckResult
  @Override public boolean shareTraces(String fullTraces) {
    try {
      shareTracesInternal(fullTraces);
      return true;
    } catch (RuntimeException exception1) { // Likely cause is a TransactionTooLargeException on API levels 15+.
      try {
        /*
         * Limit trace size to between 100kB and 400kB, since Unicode characters can be 1-4 bytes each.
         */
        int fullTracesLength = fullTraces.length();
        String truncatedTraces = fullTraces.substring(Math.max(0, fullTracesLength - 100000), fullTracesLength);
        shareTracesInternal(truncatedTraces);
        return true;
      } catch (RuntimeException exception2) { // Likely cause is a TransactionTooLargeException on API levels 15+.
        return false;
      }
    }
  }

  @Override public void notifyShareTracesFailed() {
    Toast.makeText(getContext(), "Share failed", Toast.LENGTH_SHORT).show();
  }

  @Override public void disableAutoScroll() {
    lv_traces.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
  }

  @Override public void enableAutoScroll() {
    lv_traces.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
  }

  private boolean isPresenterReady() {
    return presenter != null;
  }

  private void resumePresenter() {
    if (isPresenterReady()) {
      presenter.resume();
      int lastPosition = adapter.getCount() - 1;
      lv_traces.setSelection(lastPosition);
    }
  }

  private void pausePresenter() {
    if (isPresenterReady()) {
      presenter.pause();
    }
  }

  private boolean isVisible() {
    return getVisibility() == View.VISIBLE;
  }

  private void initializeConfiguration(AttributeSet attrs) {
    lynxConfig = new LynxConfig();
    if (attrs != null) {
      TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.lynx);

      int maxTracesToShow = attributes.getInteger(R.styleable.lynx_max_traces_to_show,
          lynxConfig.getMaxNumberOfTracesToShow());
      String filter = attributes.getString(R.styleable.lynx_filter);
      float fontSizeInPx = attributes.getDimension(R.styleable.lynx_text_size, -1);
      if (fontSizeInPx != -1) {
        fontSizeInPx = pixelsToSp(fontSizeInPx);
        lynxConfig.setTextSizeInPx(fontSizeInPx);
      }
      int samplingRate =
          attributes.getInteger(R.styleable.lynx_sampling_rate, lynxConfig.getSamplingRate());

      lynxConfig.setMaxNumberOfTracesToShow(maxTracesToShow)
          .setFilter(TextUtils.isEmpty(filter) ? "" : filter)
          .setSamplingRate(samplingRate);

      boolean focusable = attributes.getBoolean(R.styleable.lynx_focusable, lynxConfig.isFocusable());
      lynxConfig.setFocusable(focusable);

      attributes.recycle();
    }
  }

  private void initializeView() {
    Context context = getContext();
    LayoutInflater layoutInflater = LayoutInflater.from(context);
    layoutInflater.inflate(R.layout.lynx_view, this);
    mapGui();
    initializeRenderers();
    hookListeners();
  }

  private void mapGui() {
    lv_traces = (ListView) findViewById(R.id.lv_traces);
    lv_traces.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
    lv_traces.setFocusable(lynxConfig.isFocusable());
  }

  private void initializeRenderers() {
    RendererBuilder<Trace> tracesRendererBuilder = new TraceRendererBuilder(lynxConfig);
    adapter = new RendererAdapter<>(tracesRendererBuilder);
    adapter.addAll(presenter.getCurrentTraces());
    if (adapter.getCount() > 0) {
      adapter.notifyDataSetChanged();
    }
    lv_traces.setAdapter(adapter);
  }

  private void hookListeners() {
    lv_traces.setOnScrollListener(new AbsListView.OnScrollListener() {
      @Override public void onScrollStateChanged(AbsListView view, int scrollState) {
        //Empty
      }

      @Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
          int totalItemCount) {
        //Hack to avoid problems with the scroll position when auto scroll is disabled. This hack
        // is needed because Android notify a firstVisibleItem one position before it should be.
        if (lastScrollPosition - firstVisibleItem != 1) {
          lastScrollPosition = firstVisibleItem;
        }
        int lastVisiblePositionInTheList = firstVisibleItem + visibleItemCount;
        presenter.onScrollToPosition(lastVisiblePositionInTheList);
      }
    });
  }

  private void initializePresenter() {
    Lynx lynx = new Lynx(new Logcat(), new Logdog(),new AndroidMainThread(), new TimeProvider());
    lynx.setConfig(lynxConfig);
    lynx.startReading();
    presenter = new LynxPresenter(lynx, this, lynxConfig.getMaxNumberOfTracesToShow());
  }

  private void validateLynxConfig(LynxConfig lynxConfig) {
    if (lynxConfig == null) {
      throw new IllegalArgumentException(
          "You can't configure Lynx with a null LynxConfig instance.");
    }
  }

  private void updateAdapter() {
    if (lynxConfig.hasTextSizeInPx()
        && this.lynxConfig.getTextSizeInPx() != lynxConfig.getTextSizeInPx()) {
      initializeRenderers();
    }
  }

  private float pixelsToSp(float px) {
    float scaledDensity = getContext().getResources().getDisplayMetrics().scaledDensity;
    return px / scaledDensity;
  }

  private void updateScrollPosition(int removedTraces) {
    boolean shouldUpdateScrollPosition = removedTraces > 0;
    if (shouldUpdateScrollPosition) {
      int newScrollPosition = lastScrollPosition - removedTraces;
      lastScrollPosition = newScrollPosition;
      lv_traces.setSelectionFromTop(newScrollPosition, 0);
    }
  }

  private void shareTracesInternal(final String plainTraces) {
    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
    sharingIntent.setType(SHARE_INTENT_TYPE);
    sharingIntent.putExtra(Intent.EXTRA_TEXT, plainTraces);
    getContext().startActivity(Intent.createChooser(sharingIntent, SHARE_INTENT_TITLE));
  }

  /**
   * Backdoor used to replace the presenter used in this view. This method should be used just for
   * testing purposes.
   */
  void setPresenter(LynxPresenter presenter) {
    this.presenter = presenter;
  }

  @Override
  public boolean dispatchKeyEvent(KeyEvent event) {
    if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
      if (mLastSelectedPosition == lv_traces.getAdapter().getCount() - 1) {
        if (mListener != null) {
          mListener.yieldFocus();
        }
      }
    }
    mLastSelectedPosition = lv_traces.getSelectedItemPosition();
    return super.dispatchKeyEvent(event);
  }

  public void setListener(Listener listener) {
    mListener = listener;
  }

  public interface Listener {

    void yieldFocus();
  }
}
