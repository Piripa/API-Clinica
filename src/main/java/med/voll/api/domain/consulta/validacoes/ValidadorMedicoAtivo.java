package med.voll.api.domain.consulta.validacoes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.validation.ValidationException;
import med.voll.api.domain.ValidacaoExcepetion;
import med.voll.api.domain.consulta.DadosAgendamentoConsulta;
import med.voll.api.medico.MedicoRepository;

@Component
public class ValidadorMedicoAtivo implements ValidadorAgendamentoDeConsulta {
	
	@Autowired
	private MedicoRepository repository;
	
	public void validar(DadosAgendamentoConsulta dados) {
		if(dados.idMedico() == null) {
			return;
		}
		var medicoEstaAtivo = repository.findAtivoById(dados.idMedico());
		if(!medicoEstaAtivo) {
			throw new ValidacaoExcepetion("Consulta não pode ser agendada com o médico");
		}
	}
}
