package br.com.greencontrol.repository;

import br.com.greencontrol.model.EquipeManutencao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipeManutencaoRepository extends JpaRepository<EquipeManutencao, Long> {

    // Derived Queries - Geradas automaticamente pelo Spring Data JPA
    List<EquipeManutencao> findByNomeContainingIgnoreCase(String nome);

    List<EquipeManutencao> findByEspecialidadeContainingIgnoreCase(String especialidade);
}
