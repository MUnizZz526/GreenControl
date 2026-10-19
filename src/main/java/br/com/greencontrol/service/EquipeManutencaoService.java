package br.com.greencontrol.service;

import br.com.greencontrol.dto.EquipeRequestDTO;
import br.com.greencontrol.dto.EquipeResponseDTO;
import br.com.greencontrol.exception.RecursoNaoEncontradoException;
import br.com.greencontrol.model.EquipeManutencao;
import br.com.greencontrol.repository.EquipeManutencaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@org.springframework.validation.annotation.Validated
public class EquipeManutencaoService {

    private final EquipeManutencaoRepository equipeRepository;
    private final br.com.greencontrol.repository.TrechoRodoviaRepository trechos;

    public EquipeManutencaoService(EquipeManutencaoRepository equipeRepository, br.com.greencontrol.repository.TrechoRodoviaRepository trechos) {
        this.trechos = trechos;
        this.equipeRepository = equipeRepository;
    }

    @Transactional(readOnly = true)
    public List<EquipeResponseDTO> listarTodas() {
        return equipeRepository.findAll()
                .stream()
                .map(EquipeResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public EquipeResponseDTO buscarPorId(Long id) {
        EquipeManutencao equipe = buscarEntidadePorId(id);
        return EquipeResponseDTO.fromEntity(equipe);
    }

    @Transactional
    public EquipeResponseDTO criar(@jakarta.validation.Valid EquipeRequestDTO dto) {
        EquipeManutencao equipe = new EquipeManutencao(dto.nome(), dto.especialidade());
        EquipeManutencao salva = equipeRepository.save(equipe);
        return EquipeResponseDTO.fromEntity(salva);
    }

    @Transactional
    public EquipeResponseDTO atualizar(Long id, @jakarta.validation.Valid EquipeRequestDTO dto) {
        EquipeManutencao equipe = buscarEntidadePorId(id);
        equipe.setNome(dto.nome());
        equipe.setEspecialidade(dto.especialidade());
        EquipeManutencao atualizada = equipeRepository.save(equipe);
        return EquipeResponseDTO.fromEntity(atualizada);
    }

    @Transactional
    public void remover(Long id) {
        if (!equipeRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Equipe com ID " + id + " não encontrada para exclusão.");
        }
        if (!trechos.findByEquipeId(id).isEmpty()) throw new org.springframework.dao.DataIntegrityViolationException("Vínculo existente");
        equipeRepository.deleteById(id);
        equipeRepository.flush();
    }

    @Transactional(readOnly = true)
    public List<EquipeResponseDTO> buscarPorEspecialidade(String especialidade) {
        return equipeRepository.findByEspecialidadeContainingIgnoreCase(especialidade)
                .stream()
                .map(EquipeResponseDTO::fromEntity)
                .toList();
    }

    public EquipeManutencao buscarEntidadePorId(Long id) {
        return equipeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Equipe com ID " + id + " não encontrada."));
    }
}
