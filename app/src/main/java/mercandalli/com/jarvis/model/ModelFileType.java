/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis.model;

public class ModelFileType {

    private String title;
	private String[] extensions;

    public ModelFileType(String value) {
        this.extensions = new String[] {value};
        for(ModelFileTypeENUM t : ModelFileTypeENUM.values())
            for(String ext : t.type.getExtensions())
                if(value.equals(ext))
                    this.title = t.type.title;
    }

	public ModelFileType(String title, String value) {
        this.title = title;
        this.extensions = new String[] {value};
	}
	
	public ModelFileType(String title, String[] extensions) {
        this.title = title;
        this.extensions = extensions;
	}

	@Override
	public boolean equals(Object o) {		
		if(this == o) return true;		
		if(!(o  instanceof ModelFileType))
			return false;
		String[] objextensions = ((ModelFileType) o).extensions;
		if(this.extensions == null || objextensions == null)
			return false;
		for(String myext : this.extensions)
			for(String objext : objextensions)
				if(myext.equals(objext))
					return true;
		return false;
	}

    @Override
    public String toString() {
        return getFirstExtension();
    }

    public String[] getExtensions() {
        return extensions;
    }

    public String getFirstExtension() {
        return (extensions.length>0) ? extensions[0] : "";
    }

    public String getTitle() {
        return this.title != null ? this.title : "";
    }
}
