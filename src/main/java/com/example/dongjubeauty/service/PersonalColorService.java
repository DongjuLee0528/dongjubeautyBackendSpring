package com.example.dongjubeauty.service;

import com.example.dongjubeauty.dto.AnalyzeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PersonalColorService {

    private static final ParameterizedTypeReference<Map<String, Object>> MAP_TYPE =
            new ParameterizedTypeReference<>() {};

    private final RestClient pythonRestClient;

    // ----- 공통 헤더/Accept 적용 -----
    private RestClient.RequestBodySpec withCommonHeaders(RestClient.RequestBodySpec spec,
                                                         String traceId,
                                                         String acceptLanguage) {
        if (traceId != null && !traceId.isBlank()) {
            spec = spec.header("X-Trace-Id", traceId);
        }
        if (acceptLanguage != null && !acceptLanguage.isBlank()) {
            spec = spec.header("Accept-Language", acceptLanguage);
        }
        return spec.accept(MediaType.APPLICATION_JSON);
    }

    // ---------- 퍼스널 컬러: JSON ----------
    public Map<String, Object> analyzeJson(AnalyzeRequest body,
                                           boolean debug,
                                           String traceId,
                                           String acceptLanguage) {
        RestClient.RequestBodySpec spec = pythonRestClient.post()
                .uri(uri -> uri.path("/analyze").queryParam("debug", debug).build())
                .contentType(MediaType.APPLICATION_JSON);

        return withCommonHeaders(spec, traceId, acceptLanguage)
                .body(body)
                .retrieve()
                .body(MAP_TYPE);
    }

    // ---------- 퍼스널 컬러: 파일 ----------
    public Map<String, Object> analyzeFile(MultipartFile file,
                                           boolean debug,
                                           boolean exifCorrection,
                                           String traceId,
                                           String acceptLanguage) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }

        MultipartBodyBuilder mb = new MultipartBodyBuilder();
        try {
            mb.part("file", file.getBytes())
                    .filename(file.getOriginalFilename() != null ? file.getOriginalFilename() : "upload.jpg")
                    .contentType(file.getContentType() != null
                            ? MediaType.parseMediaType(file.getContentType())
                            : MediaType.IMAGE_JPEG);
        } catch (IOException e) {
            throw new IllegalArgumentException("파일을 읽을 수 없습니다.", e);
        }

        RestClient.RequestBodySpec spec = pythonRestClient.post()
                .uri(uri -> uri.path("/analyze/file")
                        .queryParam("debug", debug)
                        .queryParam("exif_correction", exifCorrection)
                        .build())
                .contentType(MediaType.MULTIPART_FORM_DATA);

        return withCommonHeaders(spec, traceId, acceptLanguage)
                .body(mb.build())
                .retrieve()
                .body(MAP_TYPE);
    }

    // ---------- 얼굴형: JSON ----------
    public Map<String, Object> faceShapeJson(AnalyzeRequest body,
                                             boolean debug,
                                             String traceId,
                                             String acceptLanguage) {
        RestClient.RequestBodySpec spec = pythonRestClient.post()
                .uri(uri -> uri.path("/face-shape").queryParam("debug", debug).build())
                .contentType(MediaType.APPLICATION_JSON);

        return withCommonHeaders(spec, traceId, acceptLanguage)
                .body(body)
                .retrieve()
                .body(MAP_TYPE);
    }

    // ---------- 얼굴형: 파일 ----------
    public Map<String, Object> faceShapeFile(MultipartFile file,
                                             boolean debug,
                                             boolean exifCorrection,
                                             String traceId,
                                             String acceptLanguage) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }

        MultipartBodyBuilder mb = new MultipartBodyBuilder();
        try {
            mb.part("file", file.getBytes())
                    .filename(file.getOriginalFilename() != null ? file.getOriginalFilename() : "upload.jpg")
                    .contentType(file.getContentType() != null
                            ? MediaType.parseMediaType(file.getContentType())
                            : MediaType.IMAGE_JPEG);
        } catch (IOException e) {
            throw new IllegalArgumentException("파일을 읽을 수 없습니다.", e);
        }

        RestClient.RequestBodySpec spec = pythonRestClient.post()
                .uri(uri -> uri.path("/face-shape/file")
                        .queryParam("debug", debug)
                        .queryParam("exif_correction", exifCorrection)
                        .build())
                .contentType(MediaType.MULTIPART_FORM_DATA);

        return withCommonHeaders(spec, traceId, acceptLanguage)
                .body(mb.build())
                .retrieve()
                .body(MAP_TYPE);
    }
}
