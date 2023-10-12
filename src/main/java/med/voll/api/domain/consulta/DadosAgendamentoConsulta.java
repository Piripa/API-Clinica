package med.voll.api.domain.consulta;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import med.voll.api.medico.Especialidade;

//Trecho de código suprimido

public record DadosAgendamentoConsulta(
     Long idMedico,

     @NotNull
     Long idPaciente,

     @NotNull
     @Future
     LocalDateTime data, //formato americano "ano-mes-diaThora-minuto-segundo"

     Especialidade especialidade
     ) {
}
