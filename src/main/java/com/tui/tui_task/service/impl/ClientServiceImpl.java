package com.tui.tui_task.service.impl;

import java.util.Optional;

import com.tui.tui_task.data.dto.request.ClientDto;
import com.tui.tui_task.data.mapper.ClientMapper;
import com.tui.tui_task.data.model.Client;
import com.tui.tui_task.repository.ClientRepository;
import com.tui.tui_task.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

	//Add logging in required place
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientServiceImpl.class);

	private final ClientRepository clientRepository;
	private final ClientMapper clientMapper;

	@Override
	public Client getOrCreateClient(ClientDto clientDto) {
		Optional<Client> existingClient = clientRepository.findByEmail(clientDto.email());

		return existingClient
				.map(client -> updateClientIfNeeded(client, clientDto))
				.orElseGet(() -> createNewClient(clientDto));
	}

	private Client updateClientIfNeeded(Client client, ClientDto clientDto) {
		if (hasClientDataChanged(client, clientDto)) {
			client.setDesiredContacts(clientMapper.toDesiredContacts(clientDto));
		}
		return client;
	}

	private boolean hasClientDataChanged(Client client, ClientDto clientDto) {
		return !client.getPhoneNumber().equals(clientDto.phoneNumber()) ||
			   !client.getFirstName().equals(clientDto.firstName()) ||
			   !client.getLastName().equals(clientDto.lastName());
	}

	private Client createNewClient(ClientDto clientDto) {
		return clientRepository.save(clientMapper.dtoToClient(clientDto));
	}
}
