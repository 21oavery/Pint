package com.pint.lib;

import org.objectweb.asm.*;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class Loader {
    private static URLClassLoader loader = (URLClassLoader) Loader.class.getClassLoader();
    private static Method addURLMethod;
    static {
        try {
            addURLMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addURLMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void search(File f) {
        search(f, 512);
    }

    private static void search(File f, int searchCnt) {
        if (!f.exists()) return;
        if (f.isDirectory()) {
            if (searchCnt == 0) return;
            searchCnt--;
            File[] list = f.listFiles();
            if (list != null) {
                for (File f2 : list) {
                    search(f2, searchCnt);
                }
            }
        } else {
            try {
                System.out.println(f);
                loadJar(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static final ClassVisitor loadVisitor = new ClassVisitor(Opcodes.ASM7) {
        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor superVis = super.visitMethod(access, name, descriptor, signature, exceptions);
            System.out.println("method: " + name);
            return new MethodVisitor(Opcodes.ASM7) {
                @Override
                public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                    System.out.println(descriptor);
                    return super.visitAnnotation(descriptor, visible);
                }
            };
        }
    };

    private static ClassVisitor createModuleBuilder(ClassVisitor superVisitor, Consumer<LoadedModule> callback) {
        return new ClassVisitor(Opcodes.ASM7, superVisitor) {
            @Override
            public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                AnnotationVisitor vis = super.visitAnnotation(descriptor, visible);
                if (descriptor.equals("Lcom/pint/api/Module")) {
                    return new AnnotationVisitor(Opcodes.ASM7, vis) {
                        private boolean isInvalid;
                        private String name;
                        private int maxV;
                        private int minV;
                        private int bugV;
                        private final ArrayList<String> deps = new ArrayList<>();
                        private final ArrayList<String> optDeps = new ArrayList<>();

                        @Override
                        public void visit(String name, Object value) {
                            super.visit(name, value);
                            if (isInvalid) return;
                            System.out.println(name + ":" + value.toString());
                            switch (name) {
                                case "name":
                                    if (!(value instanceof String) || ((String) value).matches("[^\\w-.]")) {
                                        System.err.println("[ERROR] Module name must be a String and contain only alphanumeric characters, periods, and dashes");
                                        isInvalid = false;
                                        return;
                                    }
                                    name = (String) value;
                                    break;
                                case "maxV":
                                    int v;
                                    if (!(value instanceof Integer) || ((v = ((Integer) value)) < 0)) {
                                        System.err.println("[ERROR] Module major version must be positive integer");
                                        isInvalid = false;
                                        return;
                                    }
                                    maxV = v;
                                    break;
                                case "minV":
                                    if (!(value instanceof Integer) || ((v = ((Integer) value)) < 0)) {
                                        System.err.println("[ERROR] Module minor version must be positive integer");
                                        isInvalid = false;
                                        return;
                                    }
                                    minV = v;
                                    break;
                                case "bugV":
                                    if (!(value instanceof Integer) || ((v = ((Integer) value)) < 0)) {
                                        System.err.println("[ERROR] Module bug version must be positive integer");
                                        isInvalid = false;
                                        return;
                                    }
                                    bugV = v;
                                    break;
                                case "deps":

                                case "optDeps":
                            }
                        }

                        @Override
                        public void visitEnd() {
                            super.visitEnd();
                            if (isInvalid) return;
                            callback.accept(new LoadedModule());
                        }
                    };
                } else return vis;
            }
        };
    }

    private static void loadJar(File f) throws IOException {
        ZipFile zipF;
        try {
            zipF = new ZipFile(f);
        } catch (ZipException e) {
            return;
        }
        ZipEntry zipE;
        Enumeration<? extends ZipEntry> entries = zipF.entries();
        while (entries.hasMoreElements()) {
            zipE = entries.nextElement();
            String name = zipE.getName();
            if (name.endsWith(".class")) {
                System.out.println(name);
                ClassReader r = new ClassReader(zipF.getInputStream(zipE));
                r.accept(loadVisitor, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            }
        }
    }
}