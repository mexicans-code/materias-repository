package com.idgs12.materias.materias.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.idgs12.materias.materias.repository.MateriaRepository;
import com.idgs12.materias.materias.FeignClient.ProgramaFeignClient;
import com.idgs12.materias.materias.entity.MateriaEntity;
import com.idgs12.materias.materias.entity.ProgramaMateriaEntity;
import com.idgs12.materias.materias.dto.MateriaDTO;
import com.idgs12.materias.materias.dto.ProgramaEducativoDTO;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MateriaService {

    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired
    private ProgramaFeignClient programaFeignClient;

    // Crear materia
    @Transactional
    public MateriaEntity crearMateria(MateriaDTO materiaDTO) {
        ProgramaEducativoDTO programa = programaFeignClient.getProgramaById(materiaDTO.getProgramaId());
        if (programa == null) {
            throw new RuntimeException("El programa educativo no existe");
        }

        MateriaEntity materia = new MateriaEntity();
        materia.setNombre(materiaDTO.getNombre());
        materia.setActivo(true);

        ProgramaMateriaEntity relacion = new ProgramaMateriaEntity();
        relacion.setProgramaId(materiaDTO.getProgramaId());
        relacion.setMateria(materia);

        ArrayList<ProgramaMateriaEntity> relaciones = new ArrayList<>();
        relaciones.add(relacion);
        materia.setProgramaMaterias(relaciones);

        return materiaRepository.save(materia);
    }

    // Listar todas las materias
    @Transactional
    public List<MateriaDTO> findAll() {
        List<MateriaEntity> materias = materiaRepository.findAll();

        List<Long> idsProgramas = materias.stream()
                .flatMap(m -> m.getProgramaMaterias().stream())
                .map(pm -> pm.getProgramaId())
                .distinct()
                .collect(Collectors.toList());

        List<ProgramaEducativoDTO> programas = programaFeignClient.obtenerProgramasPorIds(idsProgramas);

        Map<Long, ProgramaEducativoDTO> programaMap = new HashMap<>();
        if (programas != null) {
            for (ProgramaEducativoDTO p : programas) {
                programaMap.put(Long.valueOf(p.getId()), p);
            }
        }

        List<MateriaDTO> resultado = new ArrayList<>();
        for (MateriaEntity materia : materias) {
            MateriaDTO dto = new MateriaDTO();
            dto.setId(materia.getId()); // ✅ AGREGADO
            dto.setNombre(materia.getNombre());
            dto.setActivo(materia.isActivo());

            if (materia.getProgramaMaterias() != null && !materia.getProgramaMaterias().isEmpty()) {
                ProgramaMateriaEntity relacion = materia.getProgramaMaterias().get(0);
                dto.setProgramaId(relacion.getProgramaId());

                ProgramaEducativoDTO programa = programaMap.get(relacion.getProgramaId());
                if (programa != null) {
                    dto.setNombrePrograma(programa.getNombre());
                } else {
                    dto.setNombrePrograma("Sin programa");
                }
            }
            resultado.add(dto);
        }

        return resultado;
    }

    // ✅ Obtener materia por ID (para Feign Client)
    public MateriaDTO findById(Integer id) {
        MateriaEntity materia = materiaRepository.findById(id).orElse(null);

        if (materia == null) {
            return null;
        }

        MateriaDTO dto = new MateriaDTO();
        dto.setId(materia.getId());
        dto.setNombre(materia.getNombre());
        dto.setActivo(materia.isActivo());

        if (materia.getProgramaMaterias() != null && !materia.getProgramaMaterias().isEmpty()) {
            ProgramaMateriaEntity relacion = materia.getProgramaMaterias().get(0);
            dto.setProgramaId(relacion.getProgramaId());

            try {
                ProgramaEducativoDTO programa = programaFeignClient.getProgramaById(relacion.getProgramaId());
                if (programa != null) {
                    dto.setNombrePrograma(programa.getNombre());
                }
            } catch (Exception e) {
                dto.setNombrePrograma("Sin programa");
            }
        }

        return dto;
    }
}
