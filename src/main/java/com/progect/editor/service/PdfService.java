package com.progect.editor.service;

import com.progect.editor.dto.PdfCustomCutRequest;
import com.progect.editor.dto.PdfCutRequest;
import com.progect.editor.exception.EmptyFileException;
import com.progect.editor.exception.InvalidPageRangeException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class PdfService {

    public ResponseEntity<byte []> cut(MultipartFile file, PdfCutRequest pdfCutRequest) throws IOException {
        if (file.isEmpty())
        {
            throw new EmptyFileException("The file must not be empty");
        }
        PDDocument pdf;
        pdf=Loader.loadPDF(file.getBytes());
        int startPage=pdfCutRequest.getFromPage();
        int endPage=pdfCutRequest.getToPage();
        int totalPages=pdf.getNumberOfPages();

        if (startPage < 1 || endPage > totalPages || startPage > endPage) {
            throw new InvalidPageRangeException(
                    "Invalid page range! File contains " + totalPages + " pages."
            );
        }
        int first=startPage-1;
        int end=endPage-1;

        PDDocument newDoc= new PDDocument();


        for (int i=first;i<=end;i++)
        {
            newDoc.addPage(pdf.getPage(i));
        }
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();

        newDoc.save(outputStream);
        byte[] fileBytes=outputStream.toByteArray();

        pdf.close();
        newDoc.close();
        outputStream.close();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"cut.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(fileBytes);
    }

    public ResponseEntity<byte[]> cutCustom(MultipartFile file, PdfCustomCutRequest request) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new EmptyFileException("The file must not be empty");
        }

        try (PDDocument pdf = Loader.loadPDF(file.getBytes())) {
            int totalPages = pdf.getNumberOfPages();

            Set<Integer> targetPages = parsePageRanges(request.getPages(), totalPages);

            try (PDDocument newDoc = new PDDocument();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

                for (int pageNum : targetPages) {
                    newDoc.addPage(pdf.getPage(pageNum - 1)); // 0-based index
                }

                newDoc.save(outputStream);
                byte[] fileBytes = outputStream.toByteArray();

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"custom_cut.pdf\"")
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(fileBytes);
            }
        }
    }

    private Set<Integer> parsePageRanges(String rawRanges, int totalPages) {
        Set<Integer> pageNumbers = new LinkedHashSet<>();
        String[] parts = rawRanges.split(",");

        for (String part : parts) {
            part = part.trim();
            if (part.isEmpty()) continue;

            try {
                if (part.contains("-")) {
                    String[] range = part.split("-");
                    int start = Integer.parseInt(range[0].trim());
                    int end = Integer.parseInt(range[1].trim());

                    validatePageNumber(start, totalPages);
                    validatePageNumber(end, totalPages);

                    if (start > end) {
                        throw new InvalidPageRangeException("Start page (" + start + ") cannot be greater than end page (" + end + ")");
                    }

                    for (int i = start; i <= end; i++) {
                        pageNumbers.add(i);
                    }
                } else {
                    int page = Integer.parseInt(part);
                    validatePageNumber(page, totalPages);
                    pageNumbers.add(page);
                }
            } catch (NumberFormatException e) {
                throw new InvalidPageRangeException("Invalid format in range: '" + part + "'");
            }
        }

        if (pageNumbers.isEmpty()) {
            throw new InvalidPageRangeException("No valid pages specified");
        }

        return pageNumbers;
    }

    private void validatePageNumber(int page, int totalPages) {
        if (page < 1 || page > totalPages) {
            throw new InvalidPageRangeException("Page " + page + " is out of bounds! Document has " + totalPages + " pages.");
        }
    }

    }

