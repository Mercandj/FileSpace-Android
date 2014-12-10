/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis_android.model;

public class ModelFileType {	
	
	private String[] extentions;
	
	public ModelFileType(String value) {
		extentions = new String[] {value};
	}
	
	public ModelFileType(String[] extentions) {
		this.extentions = extentions;
	}

	@Override
	public boolean equals(Object o) {		
		if(this == o) return true;		
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
