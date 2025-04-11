package br.com.asgard.service;

import br.com.asgard.domain.boundedcontext.agendador.Agendamento;

import java.util.Optional;
import java.util.UUID;

public interface AgendadorService {

    Optional<Agendamento> agendar(Agendamento agendamento);

    void excluir(UUID agendamentoId);
}
