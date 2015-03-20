package com.fjl.test.tools.files;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.slf4j.Logger;

public class ExtensionFileFilter extends FileFilter {
    /**
     * Extensions file filter
     */
    String[] extensions;

    /**
     * Description file filter
     */
    String description;

    /**
     * 
     * Constructor of ExtensionFileFilter.
     * 
     * @param description
     * @param extensions
     */
    public ExtensionFileFilter(String description, String[] extensions) {
        this.description = description;
        this.extensions = extensions;
    }

    /**
     * 
     * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
     */
    @Override
    public boolean accept(File file) {
        String fileName = file.getName();
        if (file.isDirectory())
            return true;
        if (extensions != null) {
            for (int i = 0; i < extensions.length; i++) {
                if (fileName.endsWith(extensions[i])) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    @Override
    public String getDescription() {
        return description;
    }
}
