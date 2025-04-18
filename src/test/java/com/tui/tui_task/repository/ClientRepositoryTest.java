package com.tui.tui_task.repository;

import java.util.Optional;

import com.tui.tui_task.data.model.Client;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ClientRepositoryTest {

	@Autowired
	private ClientRepository clientRepository;

	@Test
	void testFindByEmail_shouldReturnClient() {
		Client client = new Client();
		client.setFirstName("John");
		client.setLastName("Doe");
		client.setEmail("john.doe@example.com");
		client.setPhoneNumber("1234567890");

		clientRepository.save(client);

		Optional<Client> found = clientRepository.findByEmail("john.doe@example.com");

		assertTrue(found.isPresent());
		assertEquals("John", found.get().getFirstName());
		assertEquals("Doe", found.get().getLastName());
	}

	@Test
	void testFindByEmail_shouldReturnEmpty() {
		Optional<Client> found = clientRepository.findByEmail("nonexistent@example.com");

		assertFalse(found.isPresent());
	}
}

