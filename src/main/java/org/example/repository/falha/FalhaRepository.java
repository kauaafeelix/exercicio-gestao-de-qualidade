package org.example.repository.falha;

import org.example.model.Falha;

import java.util.List;

public interface FalhaRepository {

    Falha registrarNovaFalha(Falha falha) throws Exception;

    List<Falha> buscarFalhasCriticasAbertas() throws Exception;
}
