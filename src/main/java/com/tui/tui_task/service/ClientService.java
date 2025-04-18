package com.tui.tui_task.service;

import com.tui.tui_task.data.dto.request.ClientDto;
import com.tui.tui_task.data.model.Client;

public interface ClientService {

	Client getOrCreateClient(ClientDto clientDto);
}
