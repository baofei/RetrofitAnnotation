package com.baofei.compiler;

import com.baofei.annotation.Extra;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

/**
 * Created by baofei on 2016/9/30.
 */
public class ManagerAnnotatedMethod {

    public final ExecutableElement executableElement;

    public boolean hasExtraTag = false;

    private String mParameterNames;

    public ManagerAnnotatedMethod(ExecutableElement executableElement) {
        this.executableElement = executableElement;
        hasExtraTag = executableElement.getAnnotation(Extra.class) != null;
        // System.out.println("------------->>>autoLogin:" + autoLogin);
    }


    public Name getMethodName() {
        return executableElement.getSimpleName();
    }

    public TypeMirror getReturnType() {
        return executableElement.getReturnType();
    }

    private List<VariableElement> getParameters() {
        ArrayList<VariableElement> cariableElements = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        for (Element element : executableElement.getParameters()) {
            //System.out.println("--------->>" + element.getKind() + "," + element.getSimpleName());
            if (element.getKind() == ElementKind.PARAMETER) {
                cariableElements.add((VariableElement) element);
                sb.append(element.getSimpleName());
                sb.append(",");
            }
        }
        if (sb.length() > 0) {
            mParameterNames = sb.toString();
            mParameterNames = mParameterNames.substring(0, mParameterNames.length() - 1);
        }
        //System.out.println("--------->>mParameterName:" + mParameterNames);
        return cariableElements;
    }

    public ArrayList<ParameterSpec> getParameterSpecs() {
        ArrayList<ParameterSpec> parameters = new ArrayList<>();
        List<VariableElement> p = getParameters();
        for (VariableElement ve : p) {
            parameters.add(ParameterSpec.builder(ClassName.get(ve.asType()), ve.getSimpleName().toString()).build());
        }
        return parameters;
    }

    public String getParameterNames() {
        return mParameterNames == null ? "" : mParameterNames;
    }

    public String getTypeArgument() {
        return ((DeclaredType) getReturnType()).getTypeArguments().get(0).toString();
       }

    @Override
    public String toString() {
        List<VariableElement> parameterss = getParameters();
        StringBuffer sb = new StringBuffer();
        for (VariableElement ve : parameterss) {
            sb.append(ve.asType().toString() + " " + ve.getSimpleName());
        }
        //DeclaredType type = ((DeclaredType) getReturnType());
        // System.out.println("----------------->>>" + type.getTypeArguments().toString());
        return "ManagerAnnotatedMethod[" +
                " public " +
                getReturnType().toString() + " " +
                executableElement.getSimpleName() + "(" +
                sb.toString() +
                ")]";
    }
}
