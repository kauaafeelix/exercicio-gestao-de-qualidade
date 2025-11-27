package org.example.service.falha;

import org.example.model.Equipamento;
import org.example.model.Falha;
import org.example.infraestrutura.repository.equipamento.EquipamentoRepositoryImpl;
import org.example.infraestrutura.repository.falha.FalhaRepositoryImpl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FalhaServiceImpl implements FalhaService{

    FalhaRepositoryImpl falhaRepository = new FalhaRepositoryImpl();
    EquipamentoRepositoryImpl equipamentoRepository = new EquipamentoRepositoryImpl();


    @Override
    public Falha registrarNovaFalha(Falha falha) throws SQLException {
        Falha novaFalha = null;

        Equipamento equipamento = equipamentoRepository.buscarEquipamentoPorId(falha.getEquipamentoId());
        if (equipamento == null) {
            throw new IllegalArgumentException("Equipamento não encontrado!");
        }

        falha.setStatus("ABERTA");

        if ("CRITICA".equals(falha.getCriticidade())) {
            equipamentoRepository.atualizarStatusEquipamento(equipamento.getId(), "EM_MANUTENCAO");
            novaFalha = falhaRepository.registrarNovaFalha(falha);
            return novaFalha;
        }

        novaFalha = falhaRepository.registrarNovaFalha(falha);
        return novaFalha;
    }


    @Override
    public List<Falha> buscarFalhasCriticasAbertas() throws SQLException {
        List<Falha>falhas = new ArrayList<>();

        falhas = falhaRepository.buscarFalhasCriticasAbertas();

        return falhas;
    }
}
