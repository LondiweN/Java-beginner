/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oracle.stcurr.ide.processors;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 *
 * @author mheimer
 */
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes("*")
public class FindMains extends AbstractProcessor {
    
    private Elements elementUtils;
    private Types typeUtils;
    private Messager messager;
    private TypeMirror stringTypeMirror;
    private List<String> classesWithMainMethods = new ArrayList<>();
    
    public List<String> getClassesWithMainMethods() {
        return classesWithMainMethods;
    }
    
    @Override
    public void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
        messager = processingEnvironment.getMessager();
        stringTypeMirror = elementUtils.getTypeElement(String.class.getName()).asType();
    }
    
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {
            for (Element element : roundEnv.getRootElements()) {
                classesWithMainMethods.addAll(containsMainMethod(element));
            }
        }
        return true;
    }
    
    public List<String> containsMainMethod(Element element) {
        List<String> outerTypesWithMain = new ArrayList<>();
        containsMainMethod(element, outerTypesWithMain);
        return outerTypesWithMain;
    }
    
    public void containsMainMethod(Element element, List<String> outerTypesWithMain) {
        switch (element.getKind()) {
            case CLASS:
            case ENUM:
                TypeElement typeElement = (TypeElement) element;
                String qName = typeElement.getQualifiedName().toString();
                for (Element e : typeElement.getEnclosedElements()) {
                    if (e.getKind() == ElementKind.METHOD) {
                        if (e.getSimpleName().toString().equals("main")) {
                            Set<Modifier> modifiers = e.getModifiers();
                            if (modifiers.contains(Modifier.PUBLIC) && modifiers.contains(Modifier.STATIC)) {
                                ExecutableElement method = (ExecutableElement) e;
                                List<? extends VariableElement> params = method.getParameters();
                                if (params.size() == 1 && params.get(0).asType().getKind() == TypeKind.ARRAY) {
                                    ArrayType arrayType = (ArrayType) params.get(0).asType();
                                    if (arrayType.getComponentType().equals(stringTypeMirror)) {
                                        outerTypesWithMain.add(qName);
                                    }
                                }
                            }
                        }
                    }
                }
                //fall through
            default:
                for(Element e : element.getEnclosedElements()) {
                    containsMainMethod(e, outerTypesWithMain);
                }
        }
    }
}
