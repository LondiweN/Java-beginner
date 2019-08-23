package com.oracle.stcurr.ide.util;

import java.net.URI;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;

/**
 * The source code for this class is from 
 * http://docs.oracle.com/javase/7/docs/api/javax/tools/JavaCompiler.html
 * with a little change to better handle file extensions
 * A file object used to represent source coming from a string.
 */
public class JavaSourceFromString extends SimpleJavaFileObject {

    /**
     * The source code of this "file".
     */
    final String code;

    /**
     * Constructs a new JavaSourceFromString.
     *
     * @param name the name of the compilation unit represented by this file
     * object
     * @param code the source code for the compilation unit represented by this
     * file object
     */
    JavaSourceFromString(String name, String code) {
        super(URI.create("string:///" + (name.endsWith(Kind.SOURCE.extension) ? name : name + Kind.SOURCE.extension)), Kind.SOURCE);
        this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }
}