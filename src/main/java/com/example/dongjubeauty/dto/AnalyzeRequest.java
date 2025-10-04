package com.example.dongjubeauty.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnalyzeRequest {

    /** FastAPI는 snake_case 권장, 레거시 camelCase도 허용 */
    @NotBlank
    @JsonProperty("image_base64")      // 직렬화 이름
    @JsonAlias({"imageBase64"})        // 역직렬화 허용(레거시 키)
    private String imageBase64;

    @Valid
    private Options options;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Options {
        /** traceId/trace_id 모두 수용 */
        @JsonProperty("traceId")
        @JsonAlias({"trace_id"})
        private String traceId;

        /** EXIF 회전 보정: 기본 true, exifCorrection(레거시)도 허용 */
        @JsonProperty("exif_correction")
        @JsonAlias({"exifCorrection"})
        private Boolean exifCorrection = Boolean.TRUE;

        /** 디버그(랜드마크 오버레이 등) */
        @JsonProperty("debug")
        private Boolean debug;
    }
}
