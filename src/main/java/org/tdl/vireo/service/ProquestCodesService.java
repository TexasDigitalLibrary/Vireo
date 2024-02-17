package org.tdl.vireo.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

@Service
public class ProquestCodesService {

    private final Logger logger = LoggerFactory.getLogger(ProquestCodesService.class);

    private final Map<String, Map<String, String>> codes;

    @Autowired
    private ResourcePatternResolver resourcePatternResolver;

    ProquestCodesService() {
        codes = new HashMap<String, Map<String, String>>();
    }

    @PostConstruct
    void init() throws IOException {
        loadProquestLanguageCodes();
        loadProquestDegreeCodes();
        loadProquestSubjectCodes();
    }

    public Map<String, String> getCodes(String key) {
        checkCreateCodes(key);
        return Collections.unmodifiableMap(codes.get(key));
    }

    private void loadProquestLanguageCodes() throws IOException {
        logger.info("Loading proquest language codes");
        setCodes("languages", getProquestCodes("language_codes.xls"));
    }

    private void loadProquestDegreeCodes() throws IOException {
        logger.info("Loading proquest degree codes");
        setCodes("degrees", getProquestCodes("degree_codes.xls"));
    }

    private void loadProquestSubjectCodes() throws IOException {
        logger.info("Loading proquest subjects codes");
        setCodes("subjects", getProquestCodes("umi_subjects.xls"));
    }

    private void setCodes(String key, Map<String, String> codes) {
        checkCreateCodes(key);
        this.codes.put(key, codes);
    }

    private void checkCreateCodes(String key) {
        if (this.codes.get(key) == null) {
            this.codes.put(key, new HashMap<String, String>());
        }
    }

    private Map<String, String> getProquestCodes(String xslFileName) throws IOException {
        Map<String, String> proquestCodes = new HashMap<String, String>();

        Resource resource = resourcePatternResolver.getResource("classpath:/proquest/" + xslFileName);

        try (
            InputStream file = resource.getInputStream();
            HSSFWorkbook workbook = new HSSFWorkbook(file);
        ) {
            HSSFSheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                String code = null, description = "";

                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {

                    Cell cell = cellIterator.next();
                    if (cell.getCellType() == CellType.STRING) {
                        String cellValue = cell.getStringCellValue();
                        if (code == null) {
                            code = cellValue;
                        } else {
                            description = cellValue;
                        }
                    }
                }

                proquestCodes.put(code, description);
            }
        }

        return proquestCodes;
    }

}
