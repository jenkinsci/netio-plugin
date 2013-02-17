package com.tngtech.internal.wrappers;

import java.io.InputStream;

public class Scanner {
    private java.util.Scanner scanner;

    public Scanner(InputStream inputStream) {
        scanner = new java.util.Scanner(inputStream);
    }

    public Scanner(java.util.Scanner scanner) {
        this.scanner = scanner;
    }

    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }

    public String nextLine() {
        return scanner.nextLine();
    }

    public void close() {
        scanner.close();
    }
}