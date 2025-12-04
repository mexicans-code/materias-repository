package com.idgs12.materias.materias.entity;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "programa_materia")
public class ProgramaMateriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Long programaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_materia", nullable = false)
    private MateriaEntity materia;
}
