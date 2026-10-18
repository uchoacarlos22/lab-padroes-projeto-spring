package one.digitalinnovation.gof.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import one.digitalinnovation.gof.model.Cliente;
import one.digitalinnovation.gof.model.ClienteRepository;
import one.digitalinnovation.gof.model.Endereco;
import one.digitalinnovation.gof.model.EnderecoRepository;
import one.digitalinnovation.gof.service.ClienteNaoEncontradoException;
import one.digitalinnovation.gof.service.ViaCepService;

@ExtendWith(MockitoExtension.class)
class ClienteServiceImplTests {

	@Mock
	private ClienteRepository clienteRepository;
	@Mock
	private EnderecoRepository enderecoRepository;
	@Mock
	private ViaCepService viaCepService;
	@InjectMocks
	private ClienteServiceImpl clienteService;

	@Test
	void deveNormalizarCepEConsultarViaCepAoInserir() {
		Cliente cliente = cliente("01001-000");
		Endereco enderecoConsultado = new Endereco();
		enderecoConsultado.setCep("01001000");
		when(enderecoRepository.findById("01001000")).thenReturn(Optional.empty());
		when(viaCepService.consultarCep("01001000")).thenReturn(enderecoConsultado);
		when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

		Cliente salvo = clienteService.inserir(cliente);

		assertEquals("01001000", salvo.getEndereco().getCep());
		verify(enderecoRepository).save(enderecoConsultado);
		verify(clienteRepository).save(cliente);
	}

	@Test
	void deveRejeitarCepInvalidoSemConsultarViaCep() {
		assertThrows(IllegalArgumentException.class, () -> clienteService.inserir(cliente("1234-567")));
	}

	@Test
	void deveInformarQuandoClienteNaoExiste() {
		when(clienteRepository.findById(42L)).thenReturn(Optional.empty());

		assertThrows(ClienteNaoEncontradoException.class, () -> clienteService.buscarPorId(42L));
	}

	@Test
	void naoDeveDeletarClienteInexistente() {
		when(clienteRepository.findById(42L)).thenReturn(Optional.empty());

		assertThrows(ClienteNaoEncontradoException.class, () -> clienteService.deletar(42L));
		verify(clienteRepository, never()).deleteById(42L);
	}

	private Cliente cliente(String cep) {
		Cliente cliente = new Cliente();
		cliente.setNome("Maria Silva");
		Endereco endereco = new Endereco();
		endereco.setCep(cep);
		cliente.setEndereco(endereco);
		return cliente;
	}
}
