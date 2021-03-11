package academy.gama.apialunos.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import academy.gama.apialunos.dto.response.MessageResponseDTO;
import academy.gama.apialunos.dto.resquest.AlunoDTO;
import academy.gama.apialunos.dto.resquest.NotaDTO;
import academy.gama.apialunos.entity.Aluno;
import academy.gama.apialunos.exception.FiledNotValidException;
import academy.gama.apialunos.exception.ItemNotFoundException;
import academy.gama.apialunos.mapper.AlunoMapper;
import academy.gama.apialunos.repository.AlunoRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class AlunoService {

	private final AlunoRepository alunoRepository;

	private final AlunoMapper alunoMapper = AlunoMapper.INSTANCE;

	public MessageResponseDTO create(AlunoDTO itemDTO) {

		Aluno itemToSave = alunoMapper.toModel(itemDTO);
		Aluno savedItem = alunoRepository.save(itemToSave);
		MessageResponseDTO resposta = new MessageResponseDTO("Aluno created with ID", savedItem.getId());

		return resposta;
	}

	public List<AlunoDTO> listAll(String nome) {
		
		List<Aluno> listaItems;
		
		if (nome == null)
			listaItems = alunoRepository.findAll();
		else {
			listaItems = alunoRepository.findByNome(nome);
		}
		
		System.out.println("pronto, agora vou retornar a lista");
		return listaItems.stream()
				.map(alunoMapper::toDTO)
				.collect(Collectors.toList())
				;
	}

	public AlunoDTO findById(Long id) throws ItemNotFoundException {
		Aluno itemEncontrado = verifyIfAlunoExists(id);

		return alunoMapper.toDTO(itemEncontrado);
	}

	public MessageResponseDTO delete (Long id) throws ItemNotFoundException {
		MessageResponseDTO resposta = null;
		verifyIfAlunoExists(id);
		
		try {
			alunoRepository.deleteById(id);
			resposta = new MessageResponseDTO("Deletado aluno com ID", id);
			System.out.println("resposta: " + resposta);
		} catch (Exception e) {
			System.err.println("[ERRO AO DELETAR]: " + e);
		}
		return resposta;
	}

	public MessageResponseDTO updateById(Long id, AlunoDTO itemDTO ) throws ItemNotFoundException, FiledNotValidException {

		MessageResponseDTO resposta = null;

		verifyIfAlunoExists(id);
		Aluno itemToUpdate = alunoBackToModel(itemDTO);
		
		try {
			Aluno updatedItem = alunoRepository.save(itemToUpdate);
			resposta = new MessageResponseDTO("Updated aluno com ID", updatedItem.getId());

		} catch (Exception e) {
			
			 throw new FiledNotValidException(e.getMessage());
		}
		return resposta;
	}
	
	private Aluno alunoBackToModel(AlunoDTO itemDTO) throws FiledNotValidException {
		return alunoMapper.toModel(itemDTO);
	}

	private Aluno verifyIfAlunoExists(Long id) throws ItemNotFoundException {
		return alunoRepository.findById(id)
				.orElseThrow(() -> new ItemNotFoundException(id, "aluno"));
	}
	
	// RELAÇÕES	>> NOTAS
	private final NotaService notaService;
		
	public List<NotaDTO> listNotasByAlunoId(Long id) throws ItemNotFoundException {
		Aluno alunoEndontrado = verifyIfAlunoExists(id);
		return notaService.listNotasByAlunoId(alunoEndontrado);
	}
	public MessageResponseDTO createNotaByAlunoId(Long id, NotaDTO itemDTO ) throws ItemNotFoundException, FiledNotValidException {
		Aluno aluno = verifyIfAlunoExists(id);
		return notaService.createNotaByAlunoId(aluno, itemDTO);
	}
	
	public MessageResponseDTO updateNotaByAlunoId(Long id, Long notaId, NotaDTO itemDTO ) throws ItemNotFoundException, FiledNotValidException {
		Aluno aluno = verifyIfAlunoExists(id);
		return notaService.updateNotaByAlunoId(aluno, notaId, itemDTO);
	}

}
