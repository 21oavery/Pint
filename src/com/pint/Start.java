package com.pint;

import com.pint.lib.Loader;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.file.Files;

public class Start {
    private static final int listenerPort = 2534;
    private static ServerSocket listenerSock;

    public static void main(String[] args) throws IOException {
        // Check if already running
        try {
            listenerSock = new ServerSocket(listenerPort, 0, InetAddress.getByAddress(new byte[] {127, 0, 0, 1}));
        } catch (BindException e) {
            // We're already running
            System.out.println("Already running pint, exiting");
            System.exit(0);
        }
        // Setup running directory
        System.setProperty("user.dir", Files.createTempDirectory("pint").toAbsolutePath().toString());
        // Load tasks
        Loader.search(new File("P:\\pint.d"));
    }
}