/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.stcurr.ide.ast.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mheimer
 */
public class CompilationUnitNode {
    
    private String packageName;
    private String fileName;
    
    private List<ImportNode> importNodes = new ArrayList<>();

    public List<ImportNode> getImportNodes() {
        return importNodes;
    }

    public void setImportNodes(List<ImportNode> importNodes) {
        this.importNodes = importNodes;
    }
    
    public void addImportNode(ImportNode importNode) {
        importNodes.add(importNode);
    }

    /**
     * @return void if default package;
     */
    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    
}
