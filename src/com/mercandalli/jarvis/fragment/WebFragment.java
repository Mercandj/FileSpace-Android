/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package com.mercandalli.jarvis.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.mercandalli.jarvis.Application;
import com.mercandalli.jarvis.R;
import com.mercandalli.jarvis.net.MyWebViewClient;

/**
 * Fragment show web page
 * @author Jonathan
 *
 */
public class WebFragment extends Fragment {

	Application app;
	private ProgressBar progress_web;
	private WebView webView;
	private View rootView;    
	private String initURL;
    
    public WebFragment(Application app, String initURL) {
    	this.app = app;
    	this.initURL = initURL;
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_web, container, false);
		
		progress_web = (ProgressBar) rootView.findViewById(R.id.progressBar_webView);
		progress_web.setMax(100);
		
		try {
			webView = (WebView) rootView.findViewById(R.id.webView1);
			webView.getSettings().setJavaScriptEnabled(true);
			
			webView.setWebChromeClient(new WebChromeClient() {
	            public void onProgressChanged(WebView view, int progress) {
	            	progress_web.setProgress(progress);
	            }
			});
			
			webView.setWebViewClient(new MyWebViewClient(progress_web));
			webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
			webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
			webView.getSettings().setDisplayZoomControls(false);
			/*
			String ua = "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0";
			webView.getSettings().setUserAgentString(ua);
 			*/
			webView.getSettings().setLoadWithOverviewMode(true);			
			webView.getSettings().setUseWideViewPort(false);			
			webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
			webView.setScrollbarFadingEnabled(true);			
			webView.setInitialScale(150);			
			webView.loadUrl(initURL);			
			
		} catch (Exception e) {
			Log.e(getClass().getSimpleName(),"Browser: " + e.getMessage());
		}
		return rootView;
    }
    
    public boolean home() {
    	webView.loadUrl(initURL);
    	return true;
    }	
	
	public void load_url(String string) {
		try {
			webView.loadUrl(string);
		} catch (Exception e) {
			
		}
	}
}