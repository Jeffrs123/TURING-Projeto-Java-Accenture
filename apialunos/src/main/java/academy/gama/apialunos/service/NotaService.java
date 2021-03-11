package academy.gama.apialunos.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import academy.gama.apialunos.dto.response.MessageResponseDTO;
import academy.gama.apialunos.dto.resquest.NotaDTO;
import academy.gama.apialunos.entity.Aluno;
import academy.gama.apialunos.entity.Nota;
import academy.gama.apialunos.exception.FiledNotValidException;
import academy.gama.apialunos.exception.ItemNotFoundException;
import academy.gama.apialunos.mapper.NotaMapper;
import academy.gama.apialunos.repository.NotaRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class NotaService {

	private final NotaRepository notaRepository;
	
	private final NotaMapper notaMapper = NotaMapper.INSTANCE;
	
	public MessageResponseDTO create(NotaDTO itemDTO) {
		Nota itemToSave = notaMapper.toModel(itemDTO);
		itemToSave.resolverNotasEConceito();;
		Nota savedItem = notaRepository.save(itemToSave);
		return createMessageResponse(savedItem.getId(), "Nota created with ID ");
	}
	
	public List<NotaDTO> listAll() {
		List<Nota> todosItems = notaRepository.findAll();
		return todosItems.stream()
				.map(notaMapper::toDTO)
				.collect(Collectors.toList())
				;
	}
	
	public NotaDTO findById(Long id) throws ItemNotFoundException {
		Nota itemEncontrado = verifyIfExists(id);
		
		return notaMapper.toDTO(itemEncontrado);
	}
	
	public void delete (Long id) throws ItemNotFoundException {
		verifyIfExists(id);
		notaRepository.deleteById(id);
	}
	
	public MessageResponseDTO updateById(Long id, NotaDTO itemDTO ) throws ItemNotFoundException {
		verifyIfExists(id);
		
		Nota itemToUpdate = notaMapper.toModel(itemDTO);
		
		Nota updatedItem = notaRepository.save(itemToUpdate);
		
		return createMessageResponse(updatedItem.getId(), "Updated nota com ID ");
	}
	
	// Methods in Relations with Aluno
	
	public List<NotaDTO> listNotasByAlunoId(Aluno aluno) {
		List<Nota> todosItems = notaRepository.findByAluno(aluno);
		return todosItems.stream()
				.map(notaMapper::toDTO)
				.collect(Collectors.toList())
				;
	}
	
	public MessageResponseDTO createNotaByAlunoId(Aluno aluno, NotaDTO itemDTO) {
		Nota itemToSave = notaMapper.toModel(itemDTO);
		itemToSave.setAluno(aluno);
		itemToSave.resolverNotasEConceito();
		Nota savedItem = notaRepository.save(itemToSave);
		return createMessageResponse(savedItem.getId(), "Olá " + aluno.getNome() + ", nota criada com ID   ");
	}
	
	public MessageResponseDTO updateNotaByAlunoId(Aluno aluno, Long notaId, NotaDTO itemDTO ) throws ItemNotFoundException, FiledNotValidException {
		
		Nota notaToUpdate= verifyIfExists(notaId);
		
		verifyParameterAndNotePathId(notaId, itemDTO.getId());
		
		Nota itemToUpdate = resolverNotaDTO(notaToUpdate, itemDTO);
		itemToUpdate.resolverNotasEConceito();
		itemToUpdate.setAluno(aluno);
		
		Nota updatedItem = notaRepository.save(itemToUpdate);
		
		return createMessageResponse(updatedItem.getId(), "Updated nota com ID ");
	}
	
	private Nota resolverNotaDTO(Nota notaToUpdate, NotaDTO notaDTO) {
		NotaDTO notaDTOUpdated = new NotaDTO();
		
		notaDTOUpdated.setId(notaDTO.getId());
		
		if (notaDTO.getNota_apresentacao() == null)
			notaDTOUpdated.setNota_apresentacao( String.valueOf(notaToUpdate.getNota_apresentacao()));
		else 
			notaDTOUpdated.setNota_apresentacao(notaDTO.getNota_apresentacao());
		
		if (notaDTO.getNota_dois() == null)
			notaDTOUpdated.setNota_dois( String.valueOf(notaToUpdate.getNota_dois()));
		else 
			notaDTOUpdated.setNota_dois(notaDTO.getNota_dois());
		
		if (notaDTO.getNota_um() == null)
			notaDTOUpdated.setNota_um( String.valueOf(notaToUpdate.getNota_um()));
		else 
			notaDTOUpdated.setNota_um(notaDTO.getNota_um());
		
		if (notaDTO.getNota_trabalho() == null)
			notaDTOUpdated.setNota_trabalho( String.valueOf(notaToUpdate.getNota_trabalho()));
		else 
			notaDTOUpdated.setNota_trabalho(notaDTO.getNota_trabalho());
		
		return notaMapper.toModel(notaDTOUpdated);
	}
	
	private void verifyParameterAndNotePathId(Long notaId, Long notaIdDTO) throws FiledNotValidException {
		if ( notaId != notaIdDTO) {
			throw new FiledNotValidException("id no corpo da mensagem (" + notaIdDTO +") é diferente do objeto URL que tentas atualizar (" + notaId +").");
		}
	}
		
	private Nota verifyIfExists(Long id) throws ItemNotFoundException {
		return notaRepository.findById(id)
				.orElseThrow(() -> new ItemNotFoundException(id, "nota"));
	}
	
	private MessageResponseDTO createMessageResponse(Long id, String message) {
		return MessageResponseDTO
				.builder()
				.message(message.trim() + " "+ id)
				.build();
	}
	
}
