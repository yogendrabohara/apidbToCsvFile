package com.example.demo;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface DataService {
    ByteArrayInputStream convertAllDataToCsv(MultipartFile file) throws IOException;


    InputStreamResource generateCsvFromData();

    FileSystemResource generateCsvFile();
}
