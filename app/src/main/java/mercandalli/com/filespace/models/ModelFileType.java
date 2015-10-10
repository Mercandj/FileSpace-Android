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
package mercandalli.com.filespace.models;

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
