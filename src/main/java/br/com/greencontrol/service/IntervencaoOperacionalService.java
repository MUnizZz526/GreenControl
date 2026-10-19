package br.com.greencontrol.service;

import br.com.greencontrol.dto.IntervencaoRequestDTO;
import br.com.greencontrol.dto.IntervencaoResponseDTO;
import br.com.greencontrol.exception.RecursoNaoEncontradoException;
import br.com.greencontrol.model.IntervencaoOperacional;
import br.com.greencontrol.model.TrechoRodovia;
import br.com.greencontrol.repository.IntervencaoOperacionalRepository;
import br.com.greencontrol.repository.TrechoRodoviaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@org.springframework.validation.annotation.Validated
public class IntervencaoOperacionalService {

    private final IntervencaoOperacionalRepository intervencaoRepository;
    private final TrechoRodoviaRepository trechoRepository;

    public IntervencaoOperacionalService(IntervencaoOperacionalRepository intervencaoRepository,
                                         TrechoRodoviaRepository trechoRepository) {
        this.intervencaoRepository = intervencaoRepository;
        this.trechoRepository = trechoRepository;
    }

    @Transactional(readOnly = true)
    public List<IntervencaoResponseDTO> listarTodas() {
        return intervencaoRepository.findAll()
                .stream()
                .map(IntervencaoResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public IntervencaoResponseDTO buscarPorId(Long id) {
        IntervencaoOperacional intervencao = buscarEntidadePorId(id);
        return IntervencaoResponseDTO.fromEntity(intervencao);
    }

    @Transactional
    public IntervencaoResponseDTO criar(@jakarta.validation.Valid IntervencaoRequestDTO dto) {
        TrechoRodovia trecho = trechoRepository.findById(dto.idTrecho())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Trecho com ID " + dto.idTrecho() + " não encontrado."));

        LocalDate data = dto.dataIntervencao() != null ? dto.dataIntervencao() : LocalDate.now();

        trecho.setNivelVegetacao(br.com.greencontrol.model.EfeitoIntervencao.de(dto.tipoIntervencao()).aplicar(trecho.getNivelVegetacao()));

        IntervencaoOperacional intervencao = new IntervencaoOperacional(
                dto.tipoIntervencao(),
                data,
                trecho
        );

        IntervencaoOperacional salva = intervencaoRepository.save(intervencao);
        return IntervencaoResponseDTO.fromEntity(salva);
    }

    @Transactional
    public IntervencaoResponseDTO atualizar(Long id, @jakarta.validation.Valid IntervencaoRequestDTO dto) {
        IntervencaoOperacional intervencao = buscarEntidadePorId(id);
        TrechoRodovia trecho = trechoRepository.findById(dto.idTrecho())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Trecho com ID " + dto.idTrecho() + " não encontrado."));

        intervencao.setTipoIntervencao(dto.tipoIntervencao());
        if (dto.dataIntervencao() != null) {
            intervencao.setDataIntervencao(dto.dataIntervencao());
        }
        intervencao.setTrecho(trecho);

        IntervencaoOperacional atualizada = intervencaoRepository.save(intervencao);
        return IntervencaoResponseDTO.fromEntity(atualizada);
    }

    @Transactional
    public void remover(Long id) {
        if (!intervencaoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Intervenção com ID " + id + " não encontrada para exclusão.");
        }
        intervencaoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<IntervencaoResponseDTO> buscarPorTipo(String tipo) {
        return intervencaoRepository.findByTipoIntervencaoIgnoreCase(tipo)
                .stream()
                .map(IntervencaoResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<IntervencaoResponseDTO> buscarPorTrecho(Long trechoId) {
        return intervencaoRepository.findByTrechoId(trechoId)
                .stream()
                .map(IntervencaoResponseDTO::fromEntity)
                .toList();
    }

    public IntervencaoOperacional buscarEntidadePorId(Long id) {
        return intervencaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Intervenção com ID " + id + " não encontrada."));
    }
}
