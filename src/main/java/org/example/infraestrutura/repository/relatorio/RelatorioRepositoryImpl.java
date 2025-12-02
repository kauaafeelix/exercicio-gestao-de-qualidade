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
                      SUM(COALESCE(f.tempoParadaHoras, 0)) AS tempo_total_parada
                FROM Equipamento e
                JOIN Falha f ON e.id = f.equipamentoId
                GROUP BY e.id, e.nome
                                                           
            """;

        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Long equipamentoId = rs.getLong("equipamento_id");
                    String equipamentoNome = rs.getString("equipamento_nome");
                    double tempoTotalParada = rs.getDouble("tempo_total_parada");

                    RelatorioParadaDTO relatorio = new RelatorioParadaDTO(equipamentoId, equipamentoNome, tempoTotalParada);
                    relatorioParada.add(relatorio);
                }
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
                FROM Equipamento e
                WHERE e.id NOT IN (
                    SELECT f.equipamentoId
                    FROM Falha f
                    WHERE f.dataHoraOcorrencia BETWEEN ? AND ?
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
    public Optional<FalhaDetalhadaDTO> buscarDetalhesCompletosFalha(long falhaId) throws SQLException {
        String sql = """
            SELECT 
                f.id as falha_id,
                f.equipamentoId,
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
                ac.descricaoAcao as acao_descricao,
                ac.dataHoraInicio as acao_data_inicio,
                ac.dataHoraFim as acao_data_fim,
                ac.responsavel as acao_responsavel
            FROM Falha f
            JOIN Equipamento e ON f.equipamentoId = e.id
            LEFT JOIN AcaoCorretiva ac ON f.id = ac.falhaId
            WHERE f.id = ?
            """;

        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, falhaId);

            ResultSet rs = ps.executeQuery();

            Falha falha = null;
            Equipamento equipamento = null;
            List<String> acoesCorretivas = new ArrayList<>();

            while (rs.next()) {
                if (falha == null) {
                    falha = new Falha(
                            rs.getLong("falha_id"),
                            rs.getLong("equipamentoId"),
                            rs.getObject("dataHoraOcorrencia", LocalDateTime.class),
                            rs.getString("descricao"),
                            rs.getString("criticidade"),
                            rs.getString("status"),
                            rs.getBigDecimal("tempoParadaHoras")
                    );
                }
                if (equipamento == null) {
                    equipamento = new Equipamento(
                            rs.getLong("equipamentoId"),
                            rs.getString("nome_equipamento"),
                            rs.getString("numeroDeSerie"),
                            rs.getString("areaSetor"),
                            rs.getString("statusOperacional")
                    );
                }
                String acaoDescricao = rs.getString("acao_descricao");
                if (acaoDescricao != null) {
                    acoesCorretivas.add(acaoDescricao);
                }
            }

            if (falha != null && equipamento != null) {
                return Optional.of(new FalhaDetalhadaDTO(falha, equipamento, acoesCorretivas));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<EquipamentoContagemFalhasDTO> gerarRelatorioManutencaoPreventiva(int contagemMinimaFalhas) throws SQLException {
        List<EquipamentoContagemFalhasDTO> relatorio = new ArrayList<>();

        String sql = """
        SELECT e.id AS equipamento_id,
               e.nome AS equipamento_nome,
               COUNT(f.id) AS total_falhas
        FROM Equipamento e
        JOIN Falha f ON e.id = f.equipamentoId
        GROUP BY e.id, e.nome
        HAVING COUNT(f.id) >= ?
        """;

        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, contagemMinimaFalhas);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Long equipamentoId = rs.getLong("equipamento_id");
                String equipamentoNome = rs.getString("equipamento_nome");
                int totalFalhas = rs.getInt("total_falhas");

                EquipamentoContagemFalhasDTO dto = new EquipamentoContagemFalhasDTO(equipamentoId, equipamentoNome, totalFalhas);
                relatorio.add(dto);
            }
        }

        return relatorio;
    }
}
