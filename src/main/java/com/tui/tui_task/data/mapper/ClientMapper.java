package com.tui.tui_task.data.mapper;

import com.tui.tui_task.data.dto.request.ClientDto;
import com.tui.tui_task.data.model.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClientMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "desiredContacts", ignore = true)
	Client dtoToClient(ClientDto clientDto);

	ClientDto toClientDto(Client client);

	default String toDesiredContacts(ClientDto clientDto) {
		return String.format("firstName: %s, lastName: %s, phoneNumber: %s",
							 clientDto.firstName(),
							 clientDto.lastName(),
							 clientDto.phoneNumber());
	}
}
