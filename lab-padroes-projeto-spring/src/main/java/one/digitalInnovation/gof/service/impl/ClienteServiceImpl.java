
 package one.digitalInnovation.gof.service.impl;

import org.springframework.stereotype.Service;
import java.util.Optional;
import one.digitalInnovation.gof.model.Cliente;
import one.digitalInnovation.gof.model.ClienteRepository;
import one.digitalInnovation.gof.model.Endereco;
import one.digitalInnovation.gof.model.EnderecoRepository;
import one.digitalInnovation.gof.service.ClienteService;
import one.digitalInnovation.gof.service.ViaCepService;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implementação da <b>Strategy</b> {@link ClienteService}, a qual pode ser
 * injetada pelo Spring(Via {@link Autowired}). Com isso, essa classe é um 
 * {@link Service}, ela será tratada como um <b>Singleton</b>
 * 
 * 
 * @author https://github.com/alexaniatoma
 *
 */

@Service
public class ClienteServiceImpl implements ClienteService {
	
	//Singleton: Injetar os comportamentos do Spring com @Autowired.
	@Autowired
	private ClienteRepository clienteRepository;
	@Autowired
	private EnderecoRepository enderecoRepository;
	@Autowired
	private ViaCepService viaCepService;
	
	//Strategy: Implementar os métodos definidos na interface.
	//Facade: Abstrair integrações com subsistemas, provendo uma interface simples.
	
	@Override
	public Iterable<Cliente>buscarTodos(){
		// Buscar todos os clientes. 
		
		return clienteRepository.findAll();
	}

	@Override
	public Cliente buscarPorId(Long id) {
		// Buscar Cliente por Id.
		
		Optional<Cliente> cliente = clienteRepository.findById(id);
		return cliente.get();
	}

	@Override
	public void inserir(Cliente cliente) {
		salvarClienteComCep(cliente);
		
	}
	

	private void salvarClienteComCep(Cliente cliente) {
		// Verificar se o Endereco do Cliente já existe (pelo CEP).
		String cep = cliente.getEndereco().getCep();
		Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {
			// Caso não exita, integrar com o ViaCEP e persisitir o retorno.
			Endereco novoEndereco = viaCepService.consultarCep(cep);
			enderecoRepository.save(novoEndereco);
			return novoEndereco;
		});
		cliente.setEndereco(endereco);
		// Inserir Cliente, vinculando o Endereco (novo ou existente).
		clienteRepository.save(cliente);
	}

	@Override
	public void atualizar(Long id, Cliente cliente) {
		//Buscar Cliente por ID, caso exista:
		Optional<Cliente> clienteBd = clienteRepository.findById(id);
		if(clienteBd.isPresent()) {
			salvarClienteComCep(cliente);
			
		}
		
	}

	@Override
	public void deletar(Long id) {
		// Deletar Cliente por ID
		clienteRepository.deleteById(id);
		
	}

}
