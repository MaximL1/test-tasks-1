package com.tui.tui_task.repository;

import java.util.Optional;

import com.tui.tui_task.data.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {

	Optional<Client> findByEmail(String email);
}
