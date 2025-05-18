package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/data")
public class DataController {

    @Autowired
    private DataServiceImpl dataService;

    public DataController(DataServiceImpl dataService) {
        this.dataService = dataService;
    }

    @GetMapping("/csv")
    public ResponseEntity<InputStreamResource> getPostsCsv() {
        InputStreamResource resource = dataService.generateCsvFromData();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=posts.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }

    //better approach
    @GetMapping("/csvfile")
    public ResponseEntity<FileSystemResource> downloadCsv() {
        FileSystemResource resource = dataService.generateCsvFile();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=posts.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }



    @PostMapping("/upload")
    public ResponseEntity<byte[]> convertAllExcelDataToCsv(@RequestParam("file") MultipartFile file) throws IOException {
        ByteArrayInputStream csvStream = dataService.convertAllDataToCsv(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=all_data.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvStream.readAllBytes());
    }
}
