/**
 * This file is part of Jarvis for Android, an app for managing your server (files, talks...).
 *
 * Copyright (c) 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 *
 * LICENSE:
 *
 * Jarvis for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 *
 * Jarvis for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 */
package mercandalli.com.filespace.ui.views;

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