package com.mercandalli.jarvis.model;


public class ModelFileType {	
	
	String[] extentions;
	
	public ModelFileType(String value) {
		extentions = new String[] {value};
	}
	
	public ModelFileType(String[] extentions) {
		this.extentions = extentions;
	}

	@Override
	public boolean equals(Object o) {
		if(!(o  instanceof ModelFileType))
			return false;
		String[] objextentions = ((ModelFileType) o).extentions;
		if(this.extentions == null || objextentions == null)
			return false;
		for(String myext : this.extentions)
			for(String objext : objextentions)
				if(myext.equals(objext))
					return true;
		return false;
	}
}
