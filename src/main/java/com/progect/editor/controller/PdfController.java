package com.progect.editor.controller;

import com.progect.editor.dto.PdfCustomCutRequest;
import com.progect.editor.dto.PdfCutRequest;
import com.progect.editor.service.PdfService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/pdf")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PdfController {
    private final PdfService pdfService;

    @PostMapping("/cut")
    public ResponseEntity<byte[]> cut(
            @RequestParam MultipartFile file,
            @Valid @ModelAttribute PdfCutRequest pdfCutRequest
            ) throws IOException {
        return pdfService.cut(file, pdfCutRequest);
    }

    @PostMapping("/cut-custom")
    public ResponseEntity<byte[]> cutCustom(
            @RequestPart("file") MultipartFile file,
            @Valid @ModelAttribute("pages") PdfCustomCutRequest request) throws IOException {
        return pdfService.cutCustom(file, request);
    }
}
