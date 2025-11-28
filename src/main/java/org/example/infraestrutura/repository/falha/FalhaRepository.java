package org.example.infraestrutura.repository.falha;

import org.example.model.Falha;

import java.sql.SQLException;
import java.util.List;

public interface FalhaRepository {

    Falha registrarNovaFalha(Falha falha) throws SQLException;

    List<Falha> buscarFalhasCriticasAbertas() throws SQLException;

    Falha buscarFalhaPorId(long id) throws SQLException;

    void atualizarStatusFalha(long id, String status) throws SQLException;
}
