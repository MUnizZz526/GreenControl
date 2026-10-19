package br.com.greencontrol.repository;

import br.com.greencontrol.model.RelatorioPrioridade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RelatorioPrioridadeRepository extends JpaRepository<RelatorioPrioridade, Long> {

    // Derived Query exigida: consulta relatórios históricos dentro de um período
    List<RelatorioPrioridade> findByDataGeracaoBetween(LocalDateTime inicio, LocalDateTime fim);

    List<RelatorioPrioridade> findByDataGeracaoGreaterThanEqualAndDataGeracaoLessThan(LocalDateTime inicio, LocalDateTime fim);

    // Derived Query: ordenação decrescente por data de geração
    List<RelatorioPrioridade> findAllByOrderByDataGeracaoDesc();
}
