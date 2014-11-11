/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package com.mercandalli.jarvis.net;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class MyWebViewClient extends WebViewClient  {
	
	ProgressBar progress_web;
	
	public MyWebViewClient(ProgressBar progress_web) {
		super();
		this.progress_web = progress_web;
	}

	@Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
		progress_web.setVisibility(View.VISIBLE);
		progress_web.setProgress(0);
        super.onPageStarted(view, url, favicon);
    }
	
	@Override
    public void onPageFinished(WebView view, String url) {
		progress_web.setVisibility(View.INVISIBLE);
        progress_web.setProgress(100);
        super.onPageFinished(view, url);
    }

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		if (url.endsWith(".mp4")) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.parse(url), "video/*");

			view.getContext().startActivity(intent);
			return true;
		} else {
			return super.shouldOverrideUrlLoading(view, url);
		}
	}
}