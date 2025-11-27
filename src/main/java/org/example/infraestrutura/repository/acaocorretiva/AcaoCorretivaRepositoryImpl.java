package org.example.infraestrutura.repository.acaocorretiva;

import org.example.infraestrutura.database.Conexao;
import org.example.model.AcaoCorretiva;

import java.sql.*;

public class AcaoCorretivaRepositoryImpl implements AcaoCorretivaRepository {

    @Override
    public AcaoCorretiva registrarConclusaoDeAcao(AcaoCorretiva acao) throws SQLException {

        String sql = """
                INSERT INTO AcaoCorretiva (
                    falhaId,
                    dataHoraInicio,
                    dataHoraFim,
                    responsavel,
                    descricaoAcao )
                    VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, acao.getFalhaId());
            ps.setObject(2, acao.getDataHoraInicio());
            ps.setObject(3, acao.getDataHoraFim());
            ps.setString(4, acao.getResponsavel());
            ps.setString(5, acao.getDescricaoArea());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                Long idGerado = rs.getLong(1);
                acao.setId(idGerado);
            }
        }
        return acao;
    }
}
