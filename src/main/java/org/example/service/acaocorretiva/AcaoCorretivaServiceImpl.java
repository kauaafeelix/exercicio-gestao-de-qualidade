package org.example.service.acaocorretiva;

import org.example.infraestrutura.repository.acaocorretiva.AcaoCorretivaRepositoryImpl;
import org.example.infraestrutura.repository.equipamento.EquipamentoRepositoryImpl;
import org.example.infraestrutura.repository.falha.FalhaRepositoryImpl;
import org.example.model.AcaoCorretiva;
import org.example.model.Falha;

import java.sql.SQLException;

public class AcaoCorretivaServiceImpl implements AcaoCorretivaService {

    AcaoCorretivaRepositoryImpl acaoCorretivaRepository = new AcaoCorretivaRepositoryImpl();
    FalhaRepositoryImpl falhaRepository = new FalhaRepositoryImpl();
    EquipamentoRepositoryImpl equipamentoRepository = new EquipamentoRepositoryImpl();


    @Override
    public AcaoCorretiva registrarConclusaoDeAcao(AcaoCorretiva acao) throws SQLException {
        AcaoCorretiva novaAcao = null;

        Falha falha = falhaRepository.buscarFalhaPorId(acao.getFalhaId());
        if (falha == null) {
            throw new RuntimeException("Falha não encontrada!");
        }


        if ("CRITICA".equals(falha.getCriticidade())) {
            equipamentoRepository.atualizarStatusEquipamento(falha.getEquipamentoId(), "OPERACIONAL");
            novaAcao = acaoCorretivaRepository.registrarConclusaoDeAcao(acao);
            falhaRepository.atualizarStatusFalha(falha.getId(), "RESOLVIDA");
            return novaAcao;

        }
        novaAcao = acaoCorretivaRepository.registrarConclusaoDeAcao(acao);
        falhaRepository.atualizarStatusFalha(falha.getId(), "RESOLVIDA");
        return novaAcao;
    }
}
