package ru.vavilov.notebook6.subEditor.model;

import java.util.Arrays;

public enum Language {
    ENGLISH(1L, "EN", "Английский", "English", "🇺🇸"),
    GERMAN(2L, "DE", "Немецкий", "Deutsch", "🇩🇪"),
    FRENCH(3L, "FR", "Французский", "Français", "🇫🇷"),
    SPANISH(4L, "ES", "Испанский", "Español", "🇪🇸"),
    ITALIAN(5L, "IT", "Итальянский", "Italiano", "🇮🇹"),
    PORTUGUESE(6L, "PT", "Португальский", "Português", "🇵🇹"),
    RUSSIAN(7L, "RU", "Русский", "Русский", "🇷🇺"),
    CHINESE(8L, "ZH", "Китайский", "中文", "🇨🇳"),
    JAPANESE(9L, "JA", "Японский", "日本語", "🇯🇵"),
    KOREAN(10L, "KO", "Корейский", "한국어", "🇰🇷"),
    ARABIC(11L, "AR", "Арабский", "العربية", "🇸🇦"),
    HINDI(12L, "HI", "Хинди", "हिन्दी", "🇮🇳"),
    TURKISH(13L, "TR", "Турецкий", "Türkçe", "🇹🇷"),
    DUTCH(14L, "NL", "Нидерландский", "Nederlands", "🇳🇱"),
    POLISH(15L, "PL", "Польский", "Polski", "🇵🇱");

    private final Long id;
    private final String code;
    private final String nameRu;
    private final String nameNative;
    private final String flag;

    Language(Long id, String code, String nameRu, String nameNative, String flag) {
        this.id = id;
        this.code = code;
        this.nameRu = nameRu;
        this.nameNative = nameNative;
        this.flag = flag;
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getNameRu() { return nameRu; }
    public String getNameNative() { return nameNative; }
    public String getFlag() { return flag; }

    public static Language getById(Long id) {
        return Arrays.stream(values())
            .filter(lang -> lang.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Неизвестный ID языка: " + id));
    }

    public static Language getByCode(String code) {
        return Arrays.stream(values())
            .filter(lang -> lang.getCode().equalsIgnoreCase(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Неизвестный код языка: " + code));
    }
}