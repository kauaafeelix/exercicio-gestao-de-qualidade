package org.example.repository.equipamento;

import org.example.model.Equipamento;

public interface EquipamentoRepository {

    Equipamento criarEquipamento(Equipamento equipamento) throws Exception;

    Equipamento buscarEquipamentoPorId(Long id) throws Exception;
}
