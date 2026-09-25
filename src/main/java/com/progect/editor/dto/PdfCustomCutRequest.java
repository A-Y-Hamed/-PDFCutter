package com.progect.editor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PdfCustomCutRequest {
    @NotBlank(message = "{ranges.required}")
    private String pages;
}
