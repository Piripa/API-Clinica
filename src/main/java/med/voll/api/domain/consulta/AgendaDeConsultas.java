package med.voll.api.domain.consulta;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import med.voll.api.domain.ValidacaoExcepetion;
import med.voll.api.domain.consulta.validacoes.ValidadorAgendamentoDeConsulta;
import med.voll.api.domain.consulta.validacoes.ValidadorCancelamentoDeConsulta;
import med.voll.api.medico.Medico;
import med.voll.api.medico.MedicoRepository;
import med.voll.api.paciente.PacienteRepository;

@Service
public class AgendaDeConsultas {
	//executa regras de negócios e validações
	
	@Autowired
	private ConsultaRepository consultaRepository;
	
	@Autowired
	private MedicoRepository medicoRepository;
	
	@Autowired
	private PacienteRepository pacienteRepository;
	
	@Autowired
	private List<ValidadorAgendamentoDeConsulta> validadores;
	
	@Autowired
	private List<ValidadorCancelamentoDeConsulta> validadoresCancelamento;
	
	public DadosDetalhamentoConsulta agendar(DadosAgendamentoConsulta dados) {
		
		if(!pacienteRepository.existsById(dados.idPaciente())) {
			throw new ValidacaoExcepetion("Id do paciente informado não existe!");
		}
		
		if(dados.idMedico()!=null && !medicoRepository.existsById(dados.idMedico())) {
			throw new ValidacaoExcepetion("Id do medico informado não existe!");
		}
		
		/**
		 * Implementações de regra de negócio
		 * Design Pattern - Strategy
		 * Principios Solid: Single Responsability Principle, Open-Closed Principle,Dependency Inversion Principle
		 */
		
		validadores.forEach(v -> v.validar(dados));
		
		var paciente = pacienteRepository.findById(dados.idPaciente()).get();
		var medico = escolherMedico(dados); //devolve um opcional
		if(medico == null) {
			throw new ValidacaoExcepetion("Não existe médico disponível nessa data");
		}
		var consulta = new Consulta(null,medico,paciente,dados.data(),null);
		consultaRepository.save(consulta);
		
		return new DadosDetalhamentoConsulta(consulta);
	}
	
	//escolher um médico aleatório no horario disponível
	private Medico escolherMedico(DadosAgendamentoConsulta dados) {
		if (dados.idMedico() != null) {
			return medicoRepository.getReferenceById(dados.idMedico());
		}
		if(dados.especialidade() == null) {
			throw new ValidacaoExcepetion("Especialidade é obrigatório quando o médico não for escolhido;");
			
		}
		
		return medicoRepository.escolherMedicoAleatorioLivreNaData(dados.especialidade(),dados.data());
	}

	public void cancelar(DadosCancelamentoConsulta dados) {
	    if (!consultaRepository.existsById(dados.idConsulta())) {
	        throw new ValidacaoExcepetion("Id da consulta informado não existe!");
	    }
	    
	    validadoresCancelamento.forEach(v -> v.validar(dados));

	    var consulta = consultaRepository.getReferenceById(dados.idConsulta());
	    consulta.cancelar(dados.motivo());
	}
}
