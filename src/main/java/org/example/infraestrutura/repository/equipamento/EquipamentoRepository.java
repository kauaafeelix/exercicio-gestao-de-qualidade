package org.example.infraestrutura.repository.equipamento;

import org.example.model.Equipamento;

import java.sql.SQLException;

public interface EquipamentoRepository {

    Equipamento criarEquipamento(Equipamento equipamento) throws SQLException;

    Equipamento buscarEquipamentoPorId(long id) throws SQLException;

    void atualizarStatusEquipamento(long equipamento, String status) throws SQLException;
}
