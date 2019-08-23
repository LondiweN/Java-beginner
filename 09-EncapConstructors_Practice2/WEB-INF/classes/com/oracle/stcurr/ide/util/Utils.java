/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.stcurr.ide.util;

import com.oracle.stcurr.ide.resources.json.Project.MemoryFile;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.tools.JavaFileObject;

/**
 *
 * @author mheimer
 */
public class Utils {

    public static Map<String, String> toMap(final String fileName, final String fileContent) {
        Map<String, String> fileMap = new HashMap<>();
        fileMap.put(fileName, fileContent);
        return fileMap;
    }

    public static Iterable<? extends JavaFileObject> getCompilationUnits(final Map<String, String> fileMap) {
        return new Iterable<JavaSourceFromString>() {
            final Set<Map.Entry<String, String>> entrySet = fileMap.entrySet();

            @Override
            public Iterator<JavaSourceFromString> iterator() {
                final Iterator<Map.Entry<String, String>> files = entrySet.iterator();
                return new Iterator<JavaSourceFromString>() {
                    @Override
                    public boolean hasNext() {
                        return files.hasNext();
                    }

                    @Override
                    public JavaSourceFromString next() {
                        Map.Entry<String, String> file = files.next();
                        return new JavaSourceFromString(file.getKey(), file.getValue());
                    }

                    @Override
                    public void remove() {
                        files.remove();
                    }
                };
            }
        };
    }
    
    public static Iterable<? extends JavaFileObject> getCompilationUnits(final List<MemoryFile> fileList) {
        return new Iterable<JavaSourceFromString>() {
            @Override
            public Iterator<JavaSourceFromString> iterator() {
                final Iterator<MemoryFile> files = fileList.iterator();
                return new Iterator<JavaSourceFromString>() {
                    @Override
                    public boolean hasNext() {
                        return files.hasNext();
                    }

                    @Override
                    public JavaSourceFromString next() {
                        MemoryFile file = files.next();
                        return new JavaSourceFromString(file.getName(), file.getText());
                    }

                    @Override
                    public void remove() {
                        files.remove();
                    }
                };
            }
        };
    }

    public static boolean recursiveDelete(File file) {
        if (file.isDirectory()) {
            File[] contents = file.listFiles();
            if (contents.length == 0) {
                return file.delete();
            } else {
                boolean result = true;
                for (File f : contents) {
                    result &= recursiveDelete(f);
                }
                return result & file.delete();
            }
        } else {
            return file.delete();
        }
    }
}
