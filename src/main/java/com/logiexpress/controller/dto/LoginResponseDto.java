package com.logiexpress.controller.dto;

public record LoginResponseDto(String token, String tipo, long expiraEnMs, String rol) {
}
