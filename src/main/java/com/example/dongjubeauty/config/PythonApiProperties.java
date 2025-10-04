package com.example.dongjubeauty.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * FastAPI 연결 설정 바인딩.
 * application.yml 의 python.api.* 값을 주입받습니다.
 */
@Data
@Validated
@ConfigurationProperties(prefix = "python.api")
public class PythonApiProperties {

    /** FastAPI 베이스 URL (예: http://localhost:7880) */
    @NotBlank
    private String baseUrl;
}
