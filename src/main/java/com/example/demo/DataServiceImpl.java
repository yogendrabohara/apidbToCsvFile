package com.example.demo;

import com.opencsv.CSVWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



@Service
public class DataServiceImpl implements DataService {
    private static final Logger logger = LoggerFactory.getLogger(DataServiceImpl.class);

    @Autowired
    private final DataFeignInterface dataFeign;

    public DataServiceImpl(DataFeignInterface dataFeign) {
        this.dataFeign = dataFeign;
    }

    @Override
    public ByteArrayInputStream convertAllDataToCsv(MultipartFile file) throws IOException {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(outputStream));

        Iterator<Row> rowIterator = sheet.iterator();

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            int lastColumn = row.getLastCellNum();


            if (lastColumn < 0) {
                continue;
            }

            String[] data = new String[lastColumn];

            for (int i = 0; i < lastColumn; i++) {
                Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                data[i] = getCellValueAsString(cell);
            }

            csvWriter.writeNext(data);
        }

        csvWriter.flush();
        return new ByteArrayInputStream(outputStream.toByteArray());
    }



    private String getCellValueAsString(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> DateUtil.isCellDateFormatted(cell)
                    ? cell.getDateCellValue().toString()
                    : String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }

    @Override
    public InputStreamResource generateCsvFromData() {
        List<EntityDto> posts = dataFeign.getData();  // ensure getData() returns List<EntityDto>

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             OutputStreamWriter osWriter = new OutputStreamWriter(out);
             CSVWriter writer = new CSVWriter(osWriter)) {

            writer.writeNext(new String[]{"Id", "Title", "Body"});

            for (EntityDto post : posts) {
                writer.writeNext(new String[]{
                        String.valueOf(post.getId()),
                        post.getTitle(),
                        post.getBody()
                });
            }

            writer.flush();
            return new InputStreamResource(new ByteArrayInputStream(out.toByteArray()));

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate CSV", e);
        }
    }

    @Override
    public FileSystemResource generateCsvFile() {
        List<EntityDto> posts = dataFeign.getData();
        try {
            File tempFile = File.createTempFile("data", ".csv");
            try(CSVWriter csvWriter = new CSVWriter(new FileWriter(tempFile))) {
                csvWriter.writeNext(new String[] {"Id", "Title", "Body"});
                for(EntityDto post: posts) {
                    csvWriter.writeNext(new String[] {
                            String.valueOf(post.getId()),
                            sanitize(post.getTitle()),
                            sanitize(post.getBody())
                    });
                }
                csvWriter.flush();

            }
            tempFile.deleteOnExit();
            return  new FileSystemResource(tempFile);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private String sanitize(String value) {
        if(value == null) return "";
        if(value.startsWith("=") || value.startsWith("+") || value.startsWith("-") || value.startsWith("@")) {
            return "'" + value;
        }
        return value;
    }

}
