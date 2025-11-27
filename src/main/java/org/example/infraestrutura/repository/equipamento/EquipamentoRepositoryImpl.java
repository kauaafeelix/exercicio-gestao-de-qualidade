package org.example.infraestrutura.repository.equipamento;

import org.example.database.Conexao;
import org.example.model.Equipamento;

import java.sql.*;

public class EquipamentoRepositoryImpl implements EquipamentoRepository {

    @Override
    public Equipamento criarEquipamento(Equipamento equipamento) throws SQLException {

        String sql = """
                INSERT INTO Equipamento (
                    nome,
                    numeroDeSerie,
                    areaSetor,
                    statusOperacional
                ) VALUES (?, ?, ?, 'OPERACIONAL')
                """;

        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, equipamento.getNome());
            ps.setString(2, equipamento.getNumeroDeSerie());
            ps.setString(3, equipamento.getAreaSetor());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                Long idGerado = rs.getLong(1);
                equipamento.setId(idGerado);
            }
            return equipamento;
        }
    }

    @Override
    public Equipamento buscarEquipamentoPorId(Long id) throws SQLException {

        String sql = """
                SELECT 
                    id,
                    nome,
                    numeroDeSerie,
                    areaSetor,
                    statusOperacional
                FROM Equipamento
                WHERE id = ?
                """;
        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Long equipamentoId = rs.getLong("id");
                String nome = rs.getString("nome");
                String numeroDeSerie = rs.getString("numeroDeSerie");
                String areaSetor = rs.getString("areaSetor");
                String statusOperacional = rs.getString("statusOperacional");
                return new Equipamento(equipamentoId, nome, numeroDeSerie, areaSetor, statusOperacional);
            }
    }
        return null;
    }

    @Override
    public void atualizarStatusEquipamento(Long idEquipamento, String status) throws SQLException {
        String sql = """
                UPDATE Equipamento
                SET statusOperacional = ?
                WHERE id = ?
                """;

        try(Connection conn = Conexao.conectar();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "EM_MANUTENCAO");
            ps.setLong(2, idEquipamento);
            ps.executeUpdate();
        }
    }



}
