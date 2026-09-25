package com.progect.editor.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PdfCutRequest {
    @NotNull(message = "{firstNumber.required}")
    private Integer fromPage;
    @NotNull(message = "{lastNumber.required}")
    private Integer toPage;
    
}
