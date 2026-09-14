package com.monitoramento.camera.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CameraRequest {

    @NotBlank(message = "O nome é obrigatório.")
    private String name;

    private String description;

    @NotBlank(message = "O IP é obrigatório.")
    private String ip;

    @NotNull(message = "A porta HTTP é obrigatória.")
    private Integer httpPort;

    @NotNull(message = "A porta RTSP é obrigatória.")
    private Integer rtspPort;

    private String username;
    private String password;

    private Boolean active = true;
}
