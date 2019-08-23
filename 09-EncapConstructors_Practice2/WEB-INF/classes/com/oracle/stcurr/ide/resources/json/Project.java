/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.stcurr.ide.resources.json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author mheimer
 */
public class Project {

    public static class MemoryFile {

        private String name;
        private String text;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
    private int selectedFile = -1;
    private List<MemoryFile> files = new ArrayList<>();
    private String args;

    public int getSelectedFile() {
        return selectedFile;
    }

    public void setSelectedFile(int selectedFile) {
        this.selectedFile = selectedFile;
    }

    public MemoryFile[] getFiles() {
        return files.toArray(new MemoryFile[0]);
    }
    
    public void setFiles(MemoryFile[] files) {
        this.files.clear();
        this.files.addAll(Arrays.asList(files));
    }

    public void put(String fileName, String fileContent) {
        MemoryFile f = new MemoryFile();
        f.setName(fileName);
        f.setText(fileContent);
        files.add(f);
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    @Override
    public String toString() {
        return "Project:\n"
                + "\tselectedFile:" + selectedFile + "\n"
                + "\tArgs:" + args + "\n"
                + "\tfiles:" + files.toString();

    }
}
