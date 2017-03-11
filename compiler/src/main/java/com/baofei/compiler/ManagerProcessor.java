package com.baofei.compiler;

import com.baofei.annotation.Manager;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by baofei on 2016/9/30.
 */
@AutoService(Processor.class)
public class ManagerProcessor extends AbstractProcessor {

    private static final String PACKAGE_CALLBACK = "com.baofei.framework.net";

    private static final String PACKAGE_MANAGER = ".manager";

    private static final String SUFFIX = "Manager";

    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(Manager.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        // System.out.println("---------------------getSupportedSourceVersion---------------------" + SourceVersion.latestSupported());
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        List<ManagerAnnotatedClass> classLsit = new ArrayList<>();
        for (Element element : roundEnv.getElementsAnnotatedWith(Manager.class)) {
            TypeElement typeElement = (TypeElement) element;
            if (!isValidInterface(typeElement)) {
                return true;
            }
            classLsit.add(new ManagerAnnotatedClass(typeElement));
        }
        try {
            for (ManagerAnnotatedClass manager : classLsit) {

                generate(manager);
            }
            //generateManagerUtil(classLsit);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 构建完整class
     *
     * @param generateClass
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void generate(ManagerAnnotatedClass generateClass) throws IOException, ClassNotFoundException {
        if (null == generateClass) {
            return;
        }

        String packageName = getPackageName(processingEnv.getElementUtils(), generateClass.typeElement);
        TypeSpec typeSpec = generateClass(generateClass, packageName);

        JavaFile javaFile = JavaFile.builder(packageName, typeSpec).build();
        //javaFile.writeTo(processingEnv.getFiler());
        Filer filer = processingEnv.getFiler();
        javaFile.writeTo(filer);
    }


    private boolean isValidInterface(TypeElement element) {
        return element.getKind() == ElementKind.INTERFACE;
    }

    private String getPackageName(Elements elementUtils, TypeElement type) {
        PackageElement pkg = elementUtils.getPackageOf(type);
        System.out.println(pkg.getQualifiedName().toString());
        return pkg.getQualifiedName().toString() + PACKAGE_MANAGER;
        // return PACKAGE_MANAGER;
    }

    /**
     * 构建class
     *
     * @param generateClass
     * @param packageName
     * @return
     * @throws ClassNotFoundException
     */
    public TypeSpec generateClass(ManagerAnnotatedClass generateClass, String packageName) throws ClassNotFoundException {
        String fileName = generateClass.getClassName().toString() + SUFFIX;
        String fieldName = "s" + fileName;
        TypeSpec.Builder builder = TypeSpec.classBuilder(fileName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .superclass(ParameterizedTypeName.get(ClassName.get("com.baofei.library.retrofit.manager", "BaseManager"), TypeName.get(generateClass.getType())));

        ClassName className = ClassName.get(packageName, fileName);
        //构建单例
        builder.addField(FieldSpec.builder(className, fieldName)
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .build());
        builder.addMethod(generateInstancer(fieldName, className));

        //builder.addMethod(generateConstructor());
        //构建方法
        List<ManagerAnnotatedMethod> methods = generateClass.getMethods();
        for (ManagerAnnotatedMethod method : methods) {
            builder.addMethod(generateMtehod(method));
            if (method.hasExtraTag) {
                builder.addMethod(generateMtehodExtraTag(method));
            }
        }
        return builder.build();
    }


    private static final String MTEHOD = "call.enqueue(new $T($T$L))";
    private static final String MTEHOD_HAS_EXTRATAG = "call.enqueue(new $T($T$L, $L))";

    /**
     * 构建manager类的方法
     *
     * @param method
     * @return
     */
    public MethodSpec generateMtehod(ManagerAnnotatedMethod method) {
        return MethodSpec.methodBuilder(method.getMethodName().toString())
                //.addJavadoc("@return " + method.getReturnType())
                .addModifiers(Modifier.PUBLIC)
                .addParameters(method.getParameterSpecs())
                .addStatement("$T call = getService().$L($L)",
                        ParameterizedTypeName.get(ClassName.get("retrofit2", "Call"), ClassName.bestGuess(method.getTypeArgument())),
                        method.getMethodName().toString(),
                        method.getParameterNames())
                .addStatement(MTEHOD,
                        ParameterizedTypeName.get(ClassName.get("com.baofei.library.callback", "ApCallBack"), ClassName.bestGuess(method.getTypeArgument())),
                        ClassName.bestGuess(method.getTypeArgument()),
                        ".class")
                .addStatement("return call")
                .returns(ParameterizedTypeName.get(ClassName.get("retrofit2", "Call"), ClassName.bestGuess(method.getTypeArgument())))
                .build();
    }

    /**
     * 构建manager类的方法
     *
     * @param method
     * @return
     */
    public MethodSpec generateMtehodExtraTag(ManagerAnnotatedMethod method) {
        return MethodSpec.methodBuilder(method.getMethodName().toString())
                //.addJavadoc("@return " + method.getReturnType())
                .addModifiers(Modifier.PUBLIC)
                .addParameters(method.getParameterSpecs())
                .addParameter(ParameterSpec.builder(TypeName.OBJECT, "extra").build())
                .addStatement("$T call = getService().$L($L)",
                        ParameterizedTypeName.get(ClassName.get("retrofit2", "Call"), ClassName.bestGuess(method.getTypeArgument())),
                        method.getMethodName().toString(),
                        method.getParameterNames())
                .addStatement(MTEHOD_HAS_EXTRATAG,
                        ParameterizedTypeName.get(ClassName.get("com.baofei.library.callback", "ApCallBack"), ClassName.bestGuess(method.getTypeArgument())),
                        ClassName.bestGuess(method.getTypeArgument()),
                        ".class", "extra")
                .addStatement("return call")
                .returns(ParameterizedTypeName.get(ClassName.get("retrofit2", "Call"), ClassName.bestGuess(method.getTypeArgument())))
                .build();
    }


    public MethodSpec generateConstructor() {
        return MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addStatement("super()")
                .build();
    }

    /**
     * 构建单例getInstance方法
     *
     * @param fileName
     * @param className
     * @return
     */
    public MethodSpec generateInstancer(String fileName, ClassName className) {
        return MethodSpec.methodBuilder("getInstance")
                .addModifiers(Modifier.PUBLIC, Modifier.SYNCHRONIZED, Modifier.STATIC)
                .beginControlFlow("if ($N == null) ", fileName)
                .addStatement("$N = new $T()", fileName, className)
                .endControlFlow()
                .addStatement("return $N", fileName)
                .returns(className)
                .build();
    }

}
