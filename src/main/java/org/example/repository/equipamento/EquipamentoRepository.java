package org.example.repository.equipamento;

import org.example.model.Equipamento;

import java.sql.SQLException;

public interface EquipamentoRepository {

    Equipamento criarEquipamento(Equipamento equipamento) throws SQLException;

    Equipamento buscarEquipamentoPorId(Long id) throws SQLException;
}
