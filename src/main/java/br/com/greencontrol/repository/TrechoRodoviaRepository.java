package br.com.greencontrol.repository;

import br.com.greencontrol.model.TrechoRodovia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrechoRodoviaRepository extends JpaRepository<TrechoRodovia, Long> {

    // Derived Query exigida: busca trechos com vegetação acima de determinado limiar
    List<TrechoRodovia> findByNivelVegetacaoGreaterThanEqual(Double minimo);

    // Derived Query por tipo de clima
    List<TrechoRodovia> findByTipoClimaIgnoreCase(String tipoClima);

    // Derived Query por equipe responsável
    List<TrechoRodovia> findByEquipeId(Long equipeId);
}
