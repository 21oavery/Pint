package com.pint.lib;

import org.objectweb.asm.*;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
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

    private static final ClassVisitor loadVisitor = new ClassVisitor(Opcodes.ASM7) {@Override
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

    private static ClassVisitor createModuleBuilder() {
        boolean shouldMake;
        ClassVisitor c = new ClassVisitor(Opcodes.ASM7) {
            @Override
            public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                AnnotationVisitor vis = super.visitAnnotation(descriptor, visible);
                if (descriptor.equals("Lcom/pint/api/Module")) {
                    return new AnnotationVisitor(Opcodes.ASM7, vis) {
                        private String name;
                        private int maxV;
                        private int minV;
                        private int bugV;

                        @Override
                        public void visit(String name, Object value) {
                            System.out.println(name + ":" + value.toString());
                            super.visit(name, value);
                        }

                        @Override
                        public void visitEnd() {
                            super.visitEnd();
                        }
                    };
                } else return vis;
            }
        }
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