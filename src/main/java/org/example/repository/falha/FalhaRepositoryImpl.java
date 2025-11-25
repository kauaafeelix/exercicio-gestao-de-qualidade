package org.example.repository.falha;

import org.example.database.Conexao;
import org.example.model.Falha;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FalhaRepositoryImpl implements FalhaRepository{

    @Override
    public Falha registrarNovaFalha(Falha falha) throws SQLException {

        String sql = """
                INSERT INTO Falha (
                equipamentoId,
                dataHoraOcorrencia,
                descricao,
                criticidade,
                status,
                tempoParadaHoras
                ) VALUES (?, ?, ?, ?, 'ABERTA', ?)
                """;

        try(Connection conn = Conexao.conectar();
            PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, falha.getEquipamentoId());
            ps.setObject(2, falha.getDataHoraOcorrencia());
            ps.setString(3, falha.getDescricao());
            ps.setString(4, falha.getCriticidade());
            ps.setBigDecimal(5, falha.getTempoParadaHoras());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()){
                Long idGerado = rs.getLong(1);
                falha.setId(idGerado);
            }
        }
        return falha;
    }

    @Override
    public List<Falha> buscarFalhasCriticasAbertas() throws SQLException {
        List<Falha> falhas = new ArrayList<>();

        String sql = """
                SELECT 
                    id,
                    equipamentoId,
                    dataHoraOcorrencia,
                    descricao,
                    criticidade,
                    status,
                    tempoParadaHoras
                    FROM Falha
                    WHERE criticidade = 'CRITICA' AND status = 'ABERTA'
                """;

        try(Connection conn = Conexao.conectar();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                Long idFalha = rs.getLong("id");
                Long equipamentoId = rs.getLong("equipamentoId");
                LocalDateTime dataHoraOcorrencia = rs.getObject("dataHoraOcorrencia", LocalDateTime.class);
                String descricao = rs.getString("descricao");
                String criticidade = rs.getString("criticidade");
                String status = rs.getString("status");
                BigDecimal tempoParadaHoras = rs.getBigDecimal("tempoParadaHoras");

                Falha falha = new Falha(idFalha, equipamentoId, dataHoraOcorrencia, descricao, criticidade, status, tempoParadaHoras);
                falhas.add(falha);
            }
        }
        return falhas;
    }
}
