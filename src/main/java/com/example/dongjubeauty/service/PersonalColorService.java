package com.example.dongjubeauty.service;

import com.example.dongjubeauty.dto.AnalyzeRequest;
import com.example.dongjubeauty.util.ImageConversionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.netty.http.client.HttpClient;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonalColorService {

    private static final ParameterizedTypeReference<Map<String, Object>> MAP_TYPE =
            new ParameterizedTypeReference<>() {};

    private final RestClient pythonRestClient;

    @Value("${python.api.base-url}")
    private String pyBaseUrl;

    private final WebClient webClient = WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(
                    HttpClient.create()
                            .responseTimeout(Duration.ofSeconds(60))
                            .compress(true)))
            .build();

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
    public ResponseEntity<String> analyzeFile(MultipartFile file,
                                              boolean debug,
                                              boolean exifCorrection,
                                              String traceId,
                                              String acceptLanguage) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"status\":\"error\",\"code\":\"INVALID_IMAGE\",\"message\":\"빈 파일입니다.\"}");
        }

        String url = UriComponentsBuilder.fromHttpUrl(pyBaseUrl)
                .path("/analyze/file")
                .queryParam("debug", debug)
                .queryParam("exif_correction", exifCorrection)
                .toUriString();

        MultipartBodyBuilder mb = new MultipartBodyBuilder();
        try {
            ImageConversionUtils.ConvertedImage converted = ImageConversionUtils.convertToJpegIfHeic(file).orElse(null);
            if (converted != null) {
                mb.part("file", converted.asResource())
                        .filename(converted.filename())
                        .contentType(converted.mediaType());
            } else {
                mb.part("file", file.getResource())
                        .filename(Optional.ofNullable(file.getOriginalFilename()).orElse("upload.bin"))
                        .contentType(MediaType.parseMediaType(
                                Optional.ofNullable(file.getContentType()).orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE)));
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"status\":\"error\",\"code\":\"INVALID_IMAGE\",\"message\":\"이미지를 변환할 수 없습니다.\"}");
        }

        WebClient.RequestHeadersSpec<?> req = webClient.post()
                .uri(url)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromMultipartData(mb.build()));

        // 프론트가 보낸 헤더를 파이썬으로 전달
        if (acceptLanguage != null && !acceptLanguage.isBlank()) {
            req = req.header(HttpHeaders.ACCEPT_LANGUAGE, acceptLanguage);
        }
        if (traceId != null && !traceId.isBlank()) {
            req = req.header("X-Trace-Id", traceId);
        }

        // ⬇️ retrieve/onStatus 로 예외 던지지 말고, 상태/본문 그대로 전달
        return req.exchangeToMono(resp ->
                        resp.bodyToMono(String.class).defaultIfEmpty("")
                            .map(body -> ResponseEntity.status(resp.statusCode())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(body))
                )
                .block();
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
            ImageConversionUtils.ConvertedImage converted = ImageConversionUtils.convertToJpegIfHeic(file).orElse(null);

            byte[] payload;
            String filename;
            MediaType mediaType;

            if (converted != null) {
                payload = converted.bytes();
                filename = converted.filename();
                mediaType = converted.mediaType();
            } else {
                payload = file.getBytes();
                filename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "upload.jpg";
                mediaType = file.getContentType() != null
                        ? MediaType.parseMediaType(file.getContentType())
                        : MediaType.IMAGE_JPEG;
            }

            mb.part("file", payload)
                    .filename(filename)
                    .contentType(mediaType);
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
