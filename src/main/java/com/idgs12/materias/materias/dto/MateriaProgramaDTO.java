package com.idgs12.materias.materias.dto;

import lombok.Data;

@Data
public class MateriaProgramaDTO {
    private int id;
    private String nombreMateria;
    private String nombrePrograma;
    private Long programaId;
    private boolean activo;
}
