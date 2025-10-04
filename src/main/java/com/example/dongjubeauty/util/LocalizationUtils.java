package com.example.dongjubeauty.util;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class LocalizationUtils {
    private LocalizationUtils() {}

    // 소문자 키로 정규화한 테이블(케이스 인센서티브 매핑)
    private static final Map<String, String> SHAPE_KO = Map.ofEntries(
            Map.entry("oval", "계란형"),
            Map.entry("oblong", "긴형"),             // long face
            Map.entry("long", "긴형"),
            Map.entry("round", "둥근형"),
            Map.entry("square", "사각형"),
            Map.entry("heart", "하트형"),
            Map.entry("diamond", "다이아몬드형"),
            Map.entry("triangle", "삼각형"),
            Map.entry("inverted triangle", "역삼각형"),
            Map.entry("inverted_triangle", "역삼각형")
    );

    private static final Map<String, String> SEASON_KO = Map.of(
            "spring", "봄 웜",
            "summer", "여름 쿨",
            "autumn", "가을 웜",
            "fall",   "가을 웜",    // 호환
            "winter", "겨울 쿨"
    );

    private static final Map<String, String> TONE_KO = Map.of(
            "warm", "웜",
            "cool", "쿨"
    );

    /** 한국어 라벨을 추가합니다(무조건 추가). null 입력 시 빈 결과를 반환합니다. */
    public static Map<String, Object> addKoreanLabels(Map<String, Object> payload) {
        if (payload == null) {
            Map<String, Object> out = new LinkedHashMap<>();
            out.put("status", "error");
            out.put("code", "EMPTY_RESPONSE");
            out.put("message_ko", "서버 응답이 비어 있습니다.");
            return out;
        }
        Map<String, Object> out = new LinkedHashMap<>(payload);

        // face_shape 또는 shape
        Object shape = out.getOrDefault("face_shape", out.get("shape"));
        if (shape instanceof String s) {
            String ko = lookupInsensitive(SHAPE_KO, s);
            out.put("face_shape_ko", ko);
        }

        Object season = out.get("season");
        if (season instanceof String s) {
            String ko = lookupInsensitive(SEASON_KO, s);
            out.put("season_ko", ko);
        }

        Object tone = out.get("tone");
        if (tone instanceof String s) {
            String ko = lookupInsensitive(TONE_KO, s);
            out.put("tone_ko", ko);
        }

        Object status = out.get("status");
        if ("ok".equals(status)) {
            out.putIfAbsent("message_ko", "분석이 완료되었습니다.");
        } else if (out.get("code") instanceof String code) {
            out.put("message_ko", switch (code) {
                case "NO_FACE"      -> "얼굴을 찾지 못했습니다. 정면 사진으로 다시 시도해 주세요.";
                case "LOW_QUALITY"  -> "이미지 품질이 낮아 분석할 수 없습니다.";
                case "INVALID_IMAGE"-> "이미지 데이터가 올바르지 않습니다.";
                default             -> "분석 중 오류가 발생했습니다.";
            });
        }
        return out;
    }

    /**
     * 한국어 사용자가 아닐 때는 원본 그대로 반환하는 버전.
     * e.g. Accept-Language 헤더를 그대로 넘겨 사용하세요.
     */
    public static Map<String, Object> addKoreanLabels(Map<String, Object> payload, String acceptLanguage) {
        if (acceptLanguage == null) return addKoreanLabels(payload); // 기본 한국어 추가
        String lang = acceptLanguage.toLowerCase(Locale.ROOT);
        // "ko", "ko-KR", "ko_KR" 등 처리
        if (lang.startsWith("ko")) {
            return addKoreanLabels(payload);
        }
        // 한국어가 아니면 라벨 추가 없이 원본(또는 null 방어) 그대로 반환
        return payload != null ? payload : Map.of("status", "error", "code", "EMPTY_RESPONSE");
    }

    // -------- helpers --------
    private static String lookupInsensitive(Map<String, String> table, String key) {
        if (key == null) return null;
        String k = key.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
        return table.getOrDefault(k, key); // 없으면 원문 유지
    }
}
