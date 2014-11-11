package com.mercandalli.jarvis.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

public class ModelFile {
	public String url;
	public String name;
	public String size;
	public boolean isDirectory;
	
	public List<BasicNameValuePair> getForUpload() {
		List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
		/*if(url!=null)
			parameters.add(new BasicNameValuePair("name", name));*/
		return parameters;
	}
}
