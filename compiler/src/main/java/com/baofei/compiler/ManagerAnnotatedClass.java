package com.baofei.compiler;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

/**
 * Created by baofei on 2016/9/30.
 */
public class ManagerAnnotatedClass {

    public final TypeElement typeElement;

    public ManagerAnnotatedClass(TypeElement typeElement) {
        this.typeElement = typeElement;
    }

    public TypeMirror getType() {
        return typeElement.asType();
    }

    public Name getClassName(){
        return typeElement.getSimpleName();
    }

    private List<ExecutableElement> getExecutableElement() {
        ArrayList<ExecutableElement> executableElements = new ArrayList<>();
        for (Element element : typeElement.getEnclosedElements()) {
            if (element.getKind() == ElementKind.METHOD ) {
                executableElements.add((ExecutableElement) element);
            }
        }
        return executableElements;
    }


    public List<ManagerAnnotatedMethod> getMethods() {
        List<ManagerAnnotatedMethod> managerAnnotatedMethods = new ArrayList<>();
        List<ExecutableElement> elements = getExecutableElement();
        for (ExecutableElement element : elements) {
            managerAnnotatedMethods.add(new ManagerAnnotatedMethod(element));
        }
        return managerAnnotatedMethods;
    }


    @Override
    public String toString() {
        return typeElement.getSimpleName() + "Manager";
    }
}
