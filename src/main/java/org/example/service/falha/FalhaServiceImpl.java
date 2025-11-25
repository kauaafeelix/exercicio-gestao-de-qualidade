package org.example.service.falha;

import org.example.model.Falha;
import org.example.repository.falha.FalhaRepositoryImpl;

import java.sql.SQLException;
import java.util.List;

public class FalhaServiceImpl implements FalhaService{

    FalhaRepositoryImpl falhaRepository = new FalhaRepositoryImpl();

    @Override
    public Falha registrarNovaFalha(Falha falha) throws SQLException {
        return null;
    }

    @Override
    public List<Falha> buscarFalhasCriticasAbertas() throws SQLException {
        return List.of();
    }
}
