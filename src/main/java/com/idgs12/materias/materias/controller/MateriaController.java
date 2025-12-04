package com.idgs12.materias.materias.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.idgs12.materias.materias.dto.MateriaDTO;
import com.idgs12.materias.materias.entity.MateriaEntity;
import com.idgs12.materias.materias.services.MateriaService;

@RestController
@RequestMapping("/materias")
@CrossOrigin(origins = "*")
public class MateriaController {

    @Autowired
    private MateriaService materiaService;

    @PostMapping
    public ResponseEntity<MateriaEntity> crear(@RequestBody MateriaDTO materiaDTO) {
        MateriaEntity materia = materiaService.crearMateria(materiaDTO);
        return ResponseEntity.ok(materia);
    }

    @GetMapping("/all")
    public ResponseEntity<List<MateriaDTO>> findAll() {
        List<MateriaDTO> materias = materiaService.findAll();
        return ResponseEntity.ok(materias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MateriaDTO> findById(@PathVariable Integer id) {
        MateriaDTO materia = materiaService.findById(id);
        if (materia != null) {
            return ResponseEntity.ok(materia);
        }
        return ResponseEntity.notFound().build();
    }
}
