package org.example.repository.acaocorretiva;

import org.example.model.AcaoCorretiva;

public interface AcaoCorretivaRepository {

    AcaoCorretiva registrarConclusaoDeAcao(AcaoCorretiva acao) throws Exception;
}
