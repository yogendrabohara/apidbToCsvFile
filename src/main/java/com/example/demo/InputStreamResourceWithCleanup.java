package com.example.demo;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class InputStreamResourceWithCleanup extends InputStreamResource {
    private final File file;

    public InputStreamResourceWithCleanup(File file) throws FileNotFoundException {
        super(new FileInputStream(file));
        this.file = file;
    }




}
