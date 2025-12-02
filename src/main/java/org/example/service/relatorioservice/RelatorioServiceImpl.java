package org.example.service.relatorioservice;

import org.example.dto.EquipamentoContagemFalhasDTO;
import org.example.dto.FalhaDetalhadaDTO;
import org.example.dto.RelatorioParadaDTO;
import org.example.infraestrutura.repository.acaocorretiva.AcaoCorretivaRepositoryImpl;
import org.example.infraestrutura.repository.equipamento.EquipamentoRepositoryImpl;
import org.example.infraestrutura.repository.falha.FalhaRepositoryImpl;
import org.example.infraestrutura.repository.relatorio.RelatorioRepositoryImpl;
import org.example.model.Equipamento;
import org.example.model.Falha;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RelatorioServiceImpl implements RelatorioService{

    EquipamentoRepositoryImpl equipamentoRepository = new EquipamentoRepositoryImpl();
    FalhaRepositoryImpl falhaRepository = new FalhaRepositoryImpl();
    AcaoCorretivaRepositoryImpl acaoRepository = new AcaoCorretivaRepositoryImpl();
    RelatorioRepositoryImpl relatorioRepository = new RelatorioRepositoryImpl();


    @Override
    public List<RelatorioParadaDTO> gerarRelatorioTempoParada() throws SQLException {
        List<RelatorioParadaDTO> relatorioParada = new ArrayList<>();

        relatorioParada = relatorioRepository.gerarRelatorioTempoParada();

        return relatorioParada;
    }

    @Override
    public List<Equipamento> buscarEquipamentosSemFalhasPorPeriodo(LocalDate dataInicio, LocalDate datafim) throws SQLException {
        List<Equipamento> equipamentos = new ArrayList<>();

        equipamentos = relatorioRepository.buscarEquipamentosSemFalhasPorPeriodo(dataInicio, datafim);

        return equipamentos;
    }

    @Override
    public Optional<FalhaDetalhadaDTO> buscarDetalhesCompletosFalha(long falhaId) throws SQLException {
        List<FalhaDetalhadaDTO> falhasDetalhadas = new ArrayList<>();
        Falha falha = falhaRepository.buscarFalhaPorId(falhaId);
        if (falhaId <= 0) {
            throw new RuntimeException("Id de falha inválido");
        }
         return relatorioRepository.buscarDetalhesCompletosFalha(falhaId);

    }

    @Override
    public List<EquipamentoContagemFalhasDTO> gerarRelatorioManutencaoPreventiva(int contagemMinimaFalhas) throws SQLException {
        List<EquipamentoContagemFalhasDTO> relatorioManutencao = new ArrayList<>();

        if (contagemMinimaFalhas < 1) {
            throw new IllegalArgumentException("A contagem mínima de falhas deve ser maior que zero.");
        }

        relatorioManutencao = relatorioRepository.gerarRelatorioManutencaoPreventiva(contagemMinimaFalhas);

        return relatorioManutencao;
    }
}
