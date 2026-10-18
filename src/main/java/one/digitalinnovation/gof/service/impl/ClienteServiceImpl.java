package one.digitalinnovation.gof.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import one.digitalinnovation.gof.model.Cliente;
import one.digitalinnovation.gof.model.ClienteRepository;
import one.digitalinnovation.gof.model.Endereco;
import one.digitalinnovation.gof.model.EnderecoRepository;
import one.digitalinnovation.gof.service.ClienteNaoEncontradoException;
import one.digitalinnovation.gof.service.ClienteService;
import one.digitalinnovation.gof.service.ViaCepService;

/**
 * Implementação da <b>Strategy</b> {@link ClienteService}, a qual pode ser
 * injetada pelo Spring (via {@link Autowired}). Com isso, como essa classe é um
 * {@link Service}, ela será tratada como um <b>Singleton</b>.
 * 
 * @author falvojr
 */
@Service
public class ClienteServiceImpl implements ClienteService {

	// Singleton: Injetar os componentes do Spring com @Autowired.
	@Autowired
	private ClienteRepository clienteRepository;
	@Autowired
	private EnderecoRepository enderecoRepository;
	@Autowired
	private ViaCepService viaCepService;
	
	// Strategy: Implementar os métodos definidos na interface.
	// Facade: Abstrair integrações com subsistemas, provendo uma interface simples.

	@Override
	public Iterable<Cliente> buscarTodos() {
		// Buscar todos os Clientes.
		return clienteRepository.findAll();
	}

	@Override
	public Cliente buscarPorId(Long id) {
		// Buscar Cliente por ID.
		Optional<Cliente> cliente = clienteRepository.findById(id);
		return cliente.orElseThrow(() -> new ClienteNaoEncontradoException(id));
	}

	@Override
	public Cliente inserir(Cliente cliente) {
		return salvarClienteComCep(cliente);
	}

	@Override
	public Cliente atualizar(Long id, Cliente cliente) {
		if (cliente == null) {
			throw new IllegalArgumentException("Informe os dados do cliente.");
		}
		Cliente clienteExistente = buscarPorId(id);
		clienteExistente.setNome(cliente.getNome());
		clienteExistente.setEndereco(cliente.getEndereco());
		return salvarClienteComCep(clienteExistente);
	}

	@Override
	public void deletar(Long id) {
		// Deletar Cliente por ID.
		buscarPorId(id);
		clienteRepository.deleteById(id);
	}

	private Cliente salvarClienteComCep(Cliente cliente) {
		if (cliente == null || cliente.getEndereco() == null
				|| !StringUtils.hasText(cliente.getEndereco().getCep())) {
			throw new IllegalArgumentException("Informe o CEP do cliente.");
		}

		// Verificar se o Endereco do Cliente já existe (pelo CEP).
		String cepInformado = cliente.getEndereco().getCep().trim();
		if (!cepInformado.matches("\\d{5}-?\\d{3}")) {
			throw new IllegalArgumentException("O CEP deve conter 8 dígitos.");
		}
		String cep = cepInformado.replace("-", "");
		Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {
			// Caso não exista, integrar com o ViaCEP e persistir o retorno.
			Endereco novoEndereco = viaCepService.consultarCep(cep);
			if (novoEndereco == null || Boolean.TRUE.equals(novoEndereco.getErro())) {
				throw new IllegalArgumentException("CEP não encontrado: " + cep);
			}
			novoEndereco.setCep(cep);
			enderecoRepository.save(novoEndereco);
			return novoEndereco;
		});
		cliente.setEndereco(endereco);
		// Inserir Cliente, vinculando o Endereco (novo ou existente).
		return clienteRepository.save(cliente);
	}

}
