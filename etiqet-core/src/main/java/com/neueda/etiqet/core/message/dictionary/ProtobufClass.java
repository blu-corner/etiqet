package com.neueda.etiqet.core.message.dictionary;

public class ProtobufClass {

    private String packagePath;
    private String javaOuterClassName;
    private String className;

    public ProtobufClass(String packagePath, String javaOuterClassName, String className) {
        this.packagePath = packagePath;
        this.javaOuterClassName = javaOuterClassName;
        this.className = className;
    }

    public String getPackagePath() {
        return packagePath;
    }

    public String getJavaOuterClassName() {
        return javaOuterClassName;
    }

    public String getClassName() {
        return className;
    }
}
