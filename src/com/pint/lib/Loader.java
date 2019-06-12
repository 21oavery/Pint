package com.pint.lib;

import org.objectweb.asm.*;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

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
            } catch (IllegalAccessException | InvocationTargetException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static int getMagic(InputStream in, byte[] out) throws IOException {
        int sig = 0;
        int r;
        for (int i = 0; i < 4; i++) {
            r = in.read();
            if (r == -1) {
                in.close();
                throw new IOException("Unexpected EOF before end of magic");
            }
            out[i] = (byte) r;
            sig <<= 8;
            sig |= ((byte) r);
        }
        return sig;
    }

    private static InputStream stitch(InputStream in, byte[] magic) {
        return new SequenceInputStream(new ByteArrayInputStream(magic), in);
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

    private static void loadJar(File f) throws InvocationTargetException, IllegalAccessException, IOException {
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