package org.example.infraestrutura.repository.equipamento;

import org.example.model.Equipamento;

import java.sql.SQLException;

public interface EquipamentoRepository {

    Equipamento criarEquipamento(Equipamento equipamento) throws SQLException;

    Equipamento buscarEquipamentoPorId(Long id) throws SQLException;

    void atualizarStatusEquipamento(Long equipamento, String status) throws SQLException;
}
