package br.com.greencontrol.service;

import br.com.greencontrol.dto.TrechoRequestDTO;
import br.com.greencontrol.dto.TrechoResponseDTO;
import br.com.greencontrol.exception.RecursoNaoEncontradoException;
import br.com.greencontrol.exception.RegraDeNegocioException;
import br.com.greencontrol.model.EquipeManutencao;
import br.com.greencontrol.model.TrechoRodovia;
import br.com.greencontrol.repository.TrechoRodoviaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@org.springframework.validation.annotation.Validated
public class TrechoRodoviaService {

    private final TrechoRodoviaRepository trechoRepository;
    private final EquipeManutencaoService equipeService;
    private final br.com.greencontrol.repository.IntervencaoOperacionalRepository intervencoes;

    public TrechoRodoviaService(TrechoRodoviaRepository trechoRepository, EquipeManutencaoService equipeService, br.com.greencontrol.repository.IntervencaoOperacionalRepository intervencoes) {
        this.intervencoes = intervencoes;
        this.trechoRepository = trechoRepository;
        this.equipeService = equipeService;
    }

    @Transactional(readOnly = true)
    public List<TrechoResponseDTO> listarTodos() {
        return trechoRepository.findAll()
                .stream()
                .map(TrechoResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public TrechoResponseDTO buscarPorId(Long id) {
        TrechoRodovia trecho = buscarEntidadePorId(id);
        return TrechoResponseDTO.fromEntity(trecho);
    }

    @Transactional
    public TrechoResponseDTO criar(@jakarta.validation.Valid TrechoRequestDTO dto) {
        validarRegrasDominio(dto);

        EquipeManutencao equipe = null;
        if (dto.idEquipe() != null) {
            equipe = equipeService.buscarEntidadePorId(dto.idEquipe());
        }

        TrechoRodovia trecho = new TrechoRodovia();
        trecho.setQuilometroInicial(dto.quilometroInicial());
        trecho.setQuilometroFinal(dto.quilometroFinal());
        trecho.setNivelVegetacao(dto.nivelVegetacao());
        trecho.setTipoClima(dto.tipoClima());
        trecho.setEquipe(equipe);

        TrechoRodovia salvo = trechoRepository.save(trecho);
        return TrechoResponseDTO.fromEntity(salvo);
    }

    @Transactional
    public TrechoResponseDTO atualizar(Long id, @jakarta.validation.Valid TrechoRequestDTO dto) {
        validarRegrasDominio(dto);

        TrechoRodovia trecho = buscarEntidadePorId(id);
        trecho.setQuilometroInicial(dto.quilometroInicial());
        trecho.setQuilometroFinal(dto.quilometroFinal());
        trecho.setNivelVegetacao(dto.nivelVegetacao());
        trecho.setTipoClima(dto.tipoClima());

        if (dto.idEquipe() != null) {
            EquipeManutencao equipe = equipeService.buscarEntidadePorId(dto.idEquipe());
            trecho.setEquipe(equipe);
        } else {
            trecho.setEquipe(null);
        }

        TrechoRodovia atualizado = trechoRepository.save(trecho);
        return TrechoResponseDTO.fromEntity(atualizado);
    }

    @Transactional
    public void remover(Long id) {
        if (!trechoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Trecho com ID " + id + " não encontrado para exclusão.");
        }
        if (!intervencoes.findByTrechoId(id).isEmpty()) throw new org.springframework.dao.DataIntegrityViolationException("Vínculo existente");
        trechoRepository.deleteById(id);
        trechoRepository.flush();
    }

    @Transactional
    public TrechoResponseDTO simularCrescimento(Long id) {
        TrechoRodovia trecho = buscarEntidadePorId(id);
        trecho.simularCrescimentoDoPeriodo();
        if (trecho.getNivelVegetacao() > 999999.99) {
            throw new RegraDeNegocioException("Crescimento excede o limite de armazenamento da vegetação.");
        }
        TrechoRodovia atualizado = trechoRepository.save(trecho);
        return TrechoResponseDTO.fromEntity(atualizado);
    }

    @Transactional(readOnly = true)
    public List<TrechoResponseDTO> buscarPorNivelMinimo(Double minimo) {
        return trechoRepository.findByNivelVegetacaoGreaterThanEqual(minimo)
                .stream()
                .map(TrechoResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TrechoResponseDTO> buscarPorClima(String clima) {
        return trechoRepository.findByTipoClimaIgnoreCase(clima)
                .stream()
                .map(TrechoResponseDTO::fromEntity)
                .toList();
    }

    public TrechoRodovia buscarEntidadePorId(Long id) {
        return trechoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Trecho com ID " + id + " não encontrado."));
    }

    private void validarRegrasDominio(TrechoRequestDTO dto) {
        if (!Double.isFinite(dto.quilometroInicial()) || !Double.isFinite(dto.quilometroFinal()) || !Double.isFinite(dto.nivelVegetacao())) throw new RegraDeNegocioException("Valores devem ser finitos.");
        if (dto.quilometroInicial() < 0) {
            throw new RegraDeNegocioException("Quilômetro inicial não pode ser negativo.");
        }
        if (dto.quilometroFinal() < 0) {
            throw new RegraDeNegocioException("Quilômetro final não pode ser negativo.");
        }
        if (dto.quilometroFinal() <= dto.quilometroInicial()) {
            throw new RegraDeNegocioException("O quilômetro final (" + dto.quilometroFinal() +
                    ") deve ser estritamente maior que o quilômetro inicial (" + dto.quilometroInicial() + ").");
        }
        if (dto.nivelVegetacao() < 0) {
            throw new RegraDeNegocioException("O nível de vegetação não pode ser negativo.");
        }
    }
}
