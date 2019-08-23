/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.stcurr.ide.ast.scanners;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import static com.sun.source.tree.Tree.Kind.CLASS;
import static com.sun.source.tree.Tree.Kind.ENUM;
import com.sun.source.util.TreePathScanner;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 *
 * @author mheimer
 */
public class FindMainMethodsScanner extends TreePathScanner<Void, Void> {

    private String currentFileName = null;
    private String currentPackage = null;
    private Deque<String> currentClassName = new LinkedList<>();
    private com.sun.source.util.JavacTask javacTask;
    private com.sun.source.util.Trees trees;
    private javax.lang.model.util.Elements elements;
    private javax.lang.model.util.Types types;
    private TypeMirror stringTypeMirror;
    private Map<String,List<String>> classesWithMainMethods = new HashMap<>();

    public FindMainMethodsScanner(com.sun.source.util.JavacTask javacTask) {
        this.javacTask = javacTask;
        trees = com.sun.source.util.Trees.instance(javacTask);
        elements = javacTask.getElements();
        types = javacTask.getTypes();
        stringTypeMirror = javacTask.getElements().getTypeElement(String.class.getName()).asType();
    }

    public Map<String,List<String>> getClassesWithMainMethods() {
        return classesWithMainMethods;
    }

    @Override
    public Void visitCompilationUnit(CompilationUnitTree cut, Void p) {
        currentFileName = cut.getSourceFile().getName();
        ExpressionTree et = cut.getPackageName();
        if (et != null && et.getKind() == Tree.Kind.IDENTIFIER) {
            IdentifierTree packageName = (IdentifierTree) et;
            currentPackage = packageName.getName().toString();
        } else {
            currentPackage = null;
        }
        return super.visitCompilationUnit(cut, p);
    }

    @Override
    public Void visitClass(ClassTree ct, Void p) {
        //classesWithMainMethods.addAll(containsMainMethod());
        switch (ct.getKind()) {
            case CLASS:
            case ENUM:
                currentClassName.add(ct.getSimpleName().toString());
                List<? extends Tree> members = ct.getMembers();
                for (Tree member : members) {
                    switch (member.getKind()) {
                        case CLASS:
                        case ENUM:
                            ClassTree nestedClass = (ClassTree) member;
                            Set<Modifier> flags = nestedClass.getModifiers().getFlags();
                            if (flags.contains(Modifier.STATIC) && flags.contains(Modifier.PUBLIC)) {
                                scan(member, null);
                            }
                            break;
                        case METHOD:
                            scan(member, null);
                            break;
                    }
                }
                currentClassName.pollLast();
        }
        return null;
    }

    @Override
    public Void visitMethod(MethodTree mt, Void p) {
        ExecutableElement methodElement = (ExecutableElement) trees.getElement(getCurrentPath());
        if (methodElement.getSimpleName().toString().equals("main")) {
            Set<Modifier> modifiers = methodElement.getModifiers();
            if (modifiers.contains(Modifier.PUBLIC) && modifiers.contains(Modifier.STATIC)) {
                List<? extends VariableElement> params = methodElement.getParameters();
                if (params.size() == 1 && params.get(0).asType().getKind() == TypeKind.ARRAY) {
                    ArrayType arrayType = (ArrayType) params.get(0).asType();
                    if (types.isSameType(arrayType.getComponentType(), stringTypeMirror)) {
                        StringBuilder sb = new StringBuilder();
                        if (currentPackage != null) {
                            sb.append(currentPackage);
                            sb.append(".");
                        }
                        Iterator<String> i = currentClassName.iterator();
                        while (i.hasNext()) {
                            sb.append(i.next());
                            if (i.hasNext()) {
                                sb.append("$");
                            }
                        }
                        List<String> classesInFileWithMain = classesWithMainMethods.get(currentFileName);
                        if(classesInFileWithMain == null) {
                            classesInFileWithMain = new ArrayList<String>();
                            classesWithMainMethods.put(currentFileName, classesInFileWithMain);
                        }
                        classesInFileWithMain.add(sb.toString());
                    }
                }
            }
        }


//        if (!(mt.getName().toString().equals("main"))) {
//            return null;
//        }
//
//        boolean isStatic = false;
//        boolean isPublic = false;
//        for (Modifier mod : mt.getModifiers().getFlags()) {
//            if (mod == Modifier.PUBLIC) {
//                isPublic = true;
//            }
//            if (mod == Modifier.STATIC) {
//                isStatic = true;
//            }
//        }
//        if (!(isStatic && isPublic)) {
//            return null;
//        }
//
//        Tree returnTree = mt.getReturnType();
//        if (!(returnTree.getKind() == Tree.Kind.PRIMITIVE_TYPE && ((PrimitiveTypeTree) returnTree).getPrimitiveTypeKind() == TypeKind.VOID)) {
//            return null;
//        }
//        System.out.println("public static void main");
//
//        List<? extends VariableTree> params = mt.getParameters();
//        if (params.size() != 1) {
//            return null;
//        }
//        Tree type = params.get(0).getType();
//        if (!(type.getKind() == Tree.Kind.ARRAY_TYPE)) {
//            return null;
//        }
//        ArrayTypeTree arrayType = (ArrayTypeTree) type;
//        Tree arrayElementType = arrayType.getType();
//        System.out.println("Element type: " + arrayElementType.getKind());
//
//        //IDENTIFIER or MEMBER_SELECT
//        if (arrayElementType.getKind() == Tree.Kind.IDENTIFIER) {
//            IdentifierTree identTree = (IdentifierTree) arrayElementType;
//            String typeName = identTree.getName().toString();
//            System.out.println("Type: " + typeName);
//        }
//
//
//
//        //only looking at methods in classes and enums, get enclosing type, convert to element and get qualified name

        return null;
    }
}
