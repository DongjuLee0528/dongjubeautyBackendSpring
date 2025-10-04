package com.example.dongjubeauty.web;

import com.example.dongjubeauty.dto.AnalyzeRequest;
import com.example.dongjubeauty.service.PersonalColorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Objects;

import static com.example.dongjubeauty.util.LocalizationUtils.addKoreanLabels;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class PersonalColorController {

    private final PersonalColorService service;

    /** 응답 바디의 traceId를 헤더(X-Trace-Id)로도 실어 반환 */
    private ResponseEntity<Map<String, Object>> withTraceHeader(Map<String, Object> res) {
        String traceId = res != null ? Objects.toString(res.get("traceId"), null) : null;
        HttpHeaders headers = new HttpHeaders();
        if (traceId != null && !traceId.isBlank()) {
            headers.add("X-Trace-Id", traceId);
        }
        return ResponseEntity.ok().headers(headers).body(res);
    }

    // ----- 퍼스널 컬러: JSON -----
    @PostMapping(
            value = "/analyze",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Map<String, Object>> analyzeJson(
            @Valid @RequestBody AnalyzeRequest body,
            @RequestParam(defaultValue = "false") boolean debug,
            @RequestHeader(value = "X-Trace-Id", required = false) String traceId,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage
    ) {
        Map<String, Object> res = addKoreanLabels(service.analyzeJson(body, debug, traceId, acceptLanguage));
        return withTraceHeader(res);
    }

    // ----- 퍼스널 컬러: 파일 -----
    @PostMapping(
            value = "/analyze/file",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Map<String, Object>> analyzeFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam(defaultValue = "false") boolean debug,
            @RequestParam(name = "exif_correction", defaultValue = "true") boolean exifCorrection,
            @RequestHeader(value = "X-Trace-Id", required = false) String traceId,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage
    ) {
        if (file == null || file.isEmpty()) {
            return withTraceHeader(Map.of("status", "error", "code", "INVALID_IMAGE", "message", "빈 파일입니다."));
        }
        Map<String, Object> res = addKoreanLabels(service.analyzeFile(file, debug, exifCorrection, traceId, acceptLanguage));
        return withTraceHeader(res);
    }

    // ----- 얼굴형: JSON -----
    @PostMapping(
            value = "/face-shape",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Map<String, Object>> faceShapeJson(
            @Valid @RequestBody AnalyzeRequest body,
            @RequestParam(defaultValue = "false") boolean debug,
            @RequestHeader(value = "X-Trace-Id", required = false) String traceId,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage
    ) {
        Map<String, Object> res = addKoreanLabels(service.faceShapeJson(body, debug, traceId, acceptLanguage));
        return withTraceHeader(res);
    }

    // ----- 얼굴형: 파일 -----
    @PostMapping(
            value = "/face-shape/file",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Map<String, Object>> faceShapeFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam(defaultValue = "false") boolean debug,
            @RequestParam(name = "exif_correction", defaultValue = "true") boolean exifCorrection,
            @RequestHeader(value = "X-Trace-Id", required = false) String traceId,
            @RequestHeader(value = "Accept-Language", required = false) String acceptLanguage
    ) {
        if (file == null || file.isEmpty()) {
            return withTraceHeader(Map.of("status", "error", "code", "INVALID_IMAGE", "message", "빈 파일입니다."));
        }
        Map<String, Object> res = addKoreanLabels(service.faceShapeFile(file, debug, exifCorrection, traceId, acceptLanguage));
        return withTraceHeader(res);
    }
}
