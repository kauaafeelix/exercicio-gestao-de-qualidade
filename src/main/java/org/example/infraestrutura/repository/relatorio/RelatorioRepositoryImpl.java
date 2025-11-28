package org.example.infraestrutura.repository.relatorio;

import org.example.dto.EquipamentoContagemFalhasDTO;
import org.example.dto.FalhaDetalhadaDTO;
import org.example.dto.RelatorioParadaDTO;
import org.example.infraestrutura.database.Conexao;
import org.example.model.AcaoCorretiva;
import org.example.model.Equipamento;
import org.example.model.Falha;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RelatorioRepositoryImpl implements RelatorioRepository{


    @Override
    public List<RelatorioParadaDTO> gerarRelatorioTempoParada()  throws SQLException {
        List<RelatorioParadaDTO> relatorioParada = new ArrayList<>();

        String sql = """
                SELECT e.id AS equipamento_id,
                       e.nome AS equipamento_nome,
                       SUM(f.tempo_parada) AS tempo_total_parada
                FROM equipamentos e
                JOIN falhas f ON e.id = f.equipamento_id
                WHERE f.status = 'RESOLVIDA'
                GROUP BY e.id, e.nome
                """;

        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Long equipamentoId = rs.getLong("equipamento_id");
                String equipamentoNome = rs.getString("equipamento_nome");
                double tempoTotalParada = rs.getDouble("tempo_total_parada");

                RelatorioParadaDTO relatorio = new RelatorioParadaDTO(equipamentoId, equipamentoNome, tempoTotalParada);
                relatorioParada.add(relatorio);
            }
        }

        return relatorioParada;
    }

    @Override
    public List<Equipamento> buscarEquipamentosSemFalhasPorPeriodo(LocalDate dataInicio, LocalDate dataFim)  throws SQLException{
        List<Equipamento> equipamentosSemFalhas = new ArrayList<>();

        String sql = """
                SELECT e.id,
                       e.nome,
                       e.numeroDeSerie,
                       e.areaSetor,
                       e.statusOperacional
                FROM equipamentos e
                WHERE e.id NOT IN (
                    SELECT f.equipamento_id
                    FROM falhas f
                    WHERE f.data_hora_ocorrencia BETWEEN ? AND ?
                )
                """;

        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setObject(1, dataInicio.atStartOfDay());
            ps.setObject(2, dataFim.atTime(23, 59, 59));

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                Long id = rs.getLong("id");
                String nome = rs.getString("nome");
                String numeroDeSerie = rs.getString("numeroDeSerie");
                String areaSetor = rs.getString("areaSetor");
                String statusOperacional = rs.getString("statusOperacional");

                Equipamento equipamento = new Equipamento(id, nome, numeroDeSerie, areaSetor, statusOperacional);
                equipamentosSemFalhas.add(equipamento);
            }
        }

        return equipamentosSemFalhas;
    }

    @Override
    public Optional<FalhaDetalhadaDTO> buscarDetalhesCompletosFalha(long falhaId)  throws SQLException{
        String sql = """
                SELECT 
                    f.id as falha_id,
                    f.equipamento_id,
                    f.dataHoraOcorrencia,
                    f.descricao,
                    f.criticidade,
                    f.status,
                    f.tempoParadaHoras,
                    e.nome as nome_equipamento,
                    e.numeroDeSerie,
                    e.areaSetor,
                    e.statusOperacional,
                    ac.id as acao_id,
                    ac.descricao as acao_descricao,
                    ac.dataHoraExecucao
                FROM Falha f
                JOIN Equipamento e ON f.equipamentoId = e.id
                LEFT JOIN AcaoCorretiva ac ON f.id = ac.falhaId
                WHERE f.id = ?
                """;

        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, falhaId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()){

                Falha falha = new Falha(
                        rs.getLong("falha_id"),
                        rs.getLong("equipamento_id"),
                        rs.getObject("dataHoraOcorrencia", LocalDateTime.class),
                        rs.getString("descricao"),
                        rs.getString("criticidade"),
                        rs.getString("status"),
                        rs.getBigDecimal("tempoParadaHoras")
                );
                Equipamento equipamento = new Equipamento(
                        rs.getLong("equipamento_id"),
                        rs.getString("nome_equipamento"),
                        rs.getString("numeroDeSerie"),
                        rs.getString("areaSetor"),
                        rs.getString("statusOperacional")
                );
                List<String> acaoCorretiva = null;

                return Optional.of(new FalhaDetalhadaDTO(falha, equipamento, acaoCorretiva));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<EquipamentoContagemFalhasDTO> gerarRelatorioManutencaoPreventiva(int contagemMinimaFalhas)  throws SQLException{
        return List.of();
    }
}
