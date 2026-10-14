package br.com.greencontrol.repository;

import br.com.greencontrol.model.IntervencaoOperacional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntervencaoOperacionalRepository extends JpaRepository<IntervencaoOperacional, Long> {

    // Derived Query exigida: busca intervenções por tipo (ex: RocadaMecanizada, Pulverizacao)
    List<IntervencaoOperacional> findByTipoIntervencaoIgnoreCase(String tipoIntervencao);

    // Derived Query por trecho monitorado
    List<IntervencaoOperacional> findByTrechoId(Long trechoId);
}
