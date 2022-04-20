package com.ltzk.mbsf.utils;

import android.view.MotionEvent;
import android.view.View;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.tencent.smtt.export.external.extension.proxy.ProxyWebViewClientExtension;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewCallbackClient;

/**
 * Created by JinJie on 2022/4/7
 */
public class WebViewHelper {
  private WebView                       mWebView;
  private SmartRefreshLayout            mSmartRefreshLayout;
  private MyWebViewCallbackClient       mWebViewCallbackClient;
  private MyProxyWebViewClientExtension mProxyWebViewClientExtension;

  public MyWebViewCallbackClient getWebViewCallbackClient() {
    return mWebViewCallbackClient;
  }

  public MyProxyWebViewClientExtension getProxyWebViewClientExtension() {
    return mProxyWebViewClientExtension;
  }

  private WebViewHelper() {
    mWebViewCallbackClient = new MyWebViewCallbackClient();
    mProxyWebViewClientExtension = new MyProxyWebViewClientExtension();
  }

  public static class Builder {
    private WebView            webView;
    private SmartRefreshLayout smartRefreshLayout;

    public Builder() {

    }

    public Builder setWebView(WebView webView) {
      this.webView = webView;
      return this;
    }

    public Builder setRefreshLayout(SmartRefreshLayout refreshLayout) {
      this.smartRefreshLayout = refreshLayout;
      return this;
    }

    public WebViewHelper build() {
      final WebViewHelper build = new WebViewHelper();
      build.mWebView = webView;
      build.mSmartRefreshLayout = smartRefreshLayout;
      return build;
    }
  }

  private class MyWebViewCallbackClient implements WebViewCallbackClient {
    @Override public void invalidate() {
    }

    @Override public boolean onTouchEvent(MotionEvent motionEvent, View view) {
      return mWebView.super_onTouchEvent(motionEvent);
    }

    @Override
    public boolean overScrollBy(int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7,
        boolean b, View view) {
      return mWebView.super_overScrollBy(i, i1, i2, i3, i4, i5, i6, i7, b);
    }

    @Override public void computeScroll(View view) {
      mWebView.super_computeScroll();
    }

    @Override public void onOverScrolled(int i, int i1, boolean b, boolean b1, View view) {
      mWebView.super_onOverScrolled(i, i1, b, b1);
    }

    @Override public void onScrollChanged(int i, int i1, int i2, int i3, View view) {
      mWebView.super_onScrollChanged(i, i1, i2, i3);
    }

    @Override public boolean dispatchTouchEvent(MotionEvent motionEvent, View view) {
      return mWebView.super_dispatchTouchEvent(motionEvent);
    }

    @Override public boolean onInterceptTouchEvent(MotionEvent motionEvent, View view) {
      return mWebView.super_onInterceptTouchEvent(motionEvent);
    }
  }

  private class MyProxyWebViewClientExtension extends ProxyWebViewClientExtension {
    @Override public boolean onTouchEvent(MotionEvent event, View view) {
      if (event.getAction() == MotionEvent.ACTION_DOWN) {
        mSmartRefreshLayout.setEnableRefresh(false);
      }
      return mWebViewCallbackClient.onTouchEvent(event, view);
    }

    @Override public void onOverScrolled(int i, int i1, boolean b, boolean b1, View view) {
      super.onOverScrolled(i, i1, b, b1, view);
      mWebViewCallbackClient.onOverScrolled(i, i1, b, b1, view);
      mSmartRefreshLayout.setEnableRefresh(b1);
    }

    @Override public boolean onInterceptTouchEvent(MotionEvent motionEvent, View view) {
      return mWebViewCallbackClient.onInterceptTouchEvent(motionEvent, view);
    }

    @Override public boolean dispatchTouchEvent(MotionEvent motionEvent, View view) {
      return mWebViewCallbackClient.dispatchTouchEvent(motionEvent, view);
    }

    @Override
    public boolean overScrollBy(int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7,
        boolean b, View view) {
      return mWebViewCallbackClient.overScrollBy(i, i1, i2, i3, i4, i5, i6, i7, b, view);
    }

    @Override public void onScrollChanged(int i, int i1, int i2, int i3, View view) {
      mWebViewCallbackClient.onScrollChanged(i, i1, i2, i3, view);
    }

    @Override public void computeScroll(View view) {
      mWebViewCallbackClient.computeScroll(view);
    }
  }
}