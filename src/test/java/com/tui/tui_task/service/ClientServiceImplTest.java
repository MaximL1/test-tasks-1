package com.tui.tui_task.service;

import com.tui.tui_task.data.dto.request.ClientDto;
import com.tui.tui_task.data.mapper.ClientMapper;
import com.tui.tui_task.data.model.Client;
import com.tui.tui_task.repository.ClientRepository;
import com.tui.tui_task.service.impl.ClientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ClientServiceImplTest {

	private ClientRepository clientRepository;
	private ClientMapper clientMapper;
	private ClientServiceImpl clientService;

	@BeforeEach
	void setUp() {
		clientRepository = mock(ClientRepository.class);
		clientMapper = mock(ClientMapper.class);
		clientService = new ClientServiceImpl(clientRepository, clientMapper);
	}

	@Test
	void shouldReturnExistingClient_WhenClientExistsAndDataIsUnchanged() {
		ClientDto dto = new ClientDto("John", "Doe", "1234567890", "john.doe@example.com");
		Client client = new Client();
		client.setFirstName("John");
		client.setLastName("Doe");
		client.setPhoneNumber("1234567890");

		when(clientRepository.findByEmail(dto.email())).thenReturn(Optional.of(client));

		Client result = clientService.getOrCreateClient(dto);

		assertThat(result).isEqualTo(client);
		verify(clientRepository, never()).save(any());
	}

	@Test
	void shouldUpdateClient_WhenClientExistsAndDataChanged() {
		ClientDto dto = new ClientDto("Jane", "Smith", "0987654321", "jane.smith@example.com");
		Client existingClient = new Client();
		existingClient.setFirstName("Janet");
		existingClient.setLastName("Smyth");
		existingClient.setPhoneNumber("1112223333");

		when(clientRepository.findByEmail(dto.email())).thenReturn(Optional.of(existingClient));
		when(clientMapper.toDesiredContacts(dto)).thenReturn("firstName: Jane, lastName: Smith, phoneNumber: 0987654321");

		Client result = clientService.getOrCreateClient(dto);

		assertThat(result.getDesiredContacts()).isEqualTo("firstName: Jane, lastName: Smith, phoneNumber: 0987654321");
		verify(clientRepository, never()).save(any());
	}

	@Test
	void shouldCreateNewClient_WhenClientDoesNotExist() {
		ClientDto dto = new ClientDto("Alice", "Wonderland", "9998887777", "alice@example.com");
		Client mappedClient = new Client();
		mappedClient.setEmail("alice@example.com");

		when(clientRepository.findByEmail(dto.email())).thenReturn(Optional.empty());
		when(clientMapper.dtoToClient(dto)).thenReturn(mappedClient);
		when(clientRepository.save(mappedClient)).thenReturn(mappedClient);

		Client result = clientService.getOrCreateClient(dto);

		assertThat(result.getEmail()).isEqualTo("alice@example.com");
		verify(clientRepository).save(mappedClient);
	}
}

