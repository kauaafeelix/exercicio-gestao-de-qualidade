package org.example.service.equipamento;

import org.example.model.Equipamento;
import org.example.repository.equipamento.EquipamentoRepositoryImpl;

import java.sql.SQLException;

public class EquipamentoServiceImpl implements EquipamentoService{

    EquipamentoRepositoryImpl equipamentoRepository = new EquipamentoRepositoryImpl();

    @Override
    public Equipamento criarEquipamento(Equipamento equipamento) throws SQLException {
        Equipamento novoEquipamento = null;
        try{
           novoEquipamento = equipamentoRepository.criarEquipamento(equipamento);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return novoEquipamento;
    }

    @Override
    public Equipamento buscarEquipamentoPorId(Long id) throws SQLException {
        Equipamento equipamento = equipamentoRepository.buscarEquipamentoPorId(id);
        if (equipamento == null) {
            throw new RuntimeException("Equipamento não encontrado!");
        } else {
            return equipamento;
        }
    }
}
