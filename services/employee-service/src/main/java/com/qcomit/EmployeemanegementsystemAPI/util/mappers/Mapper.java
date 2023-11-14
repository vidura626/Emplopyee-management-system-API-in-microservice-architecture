package com.qcomit.EmployeemanegementsystemAPI.util.mappers;

import com.qcomit.EmployeemanegementsystemAPI.dto.EmployeeDto;
import com.qcomit.EmployeemanegementsystemAPI.dto.embedded.AddressDto;
import com.qcomit.EmployeemanegementsystemAPI.dto.embedded.ContactDto;
import com.qcomit.EmployeemanegementsystemAPI.entity.Employee;
import com.qcomit.EmployeemanegementsystemAPI.entity.embedded.Address;
import com.qcomit.EmployeemanegementsystemAPI.entity.embedded.Contact;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@org.mapstruct.Mapper(componentModel = "spring")
public interface Mapper {
    Mapper INSTANCE = Mappers.getMapper(Mapper.class);

    @Mapping(source = "firstName", target = "name.firstName")
    @Mapping(source = "lastName", target = "name.lastName")
//    @Mapping(source = "role", target = "user.role.role")
//    @Mapping(source = "username", target = "user.username")
//    @Mapping(source = "password", target = "user.password")
    Employee toEntity(EmployeeDto dto);

    @InheritInverseConfiguration
    EmployeeDto toDto(Employee entity);

    List<EmployeeDto> toDto(List<Employee> all);

    AddressDto toDto(Address entity);

    Address toEntity(AddressDto dto);

    Contact toEntity(ContactDto dto);

    ContactDto toDto(Contact entity);

    List<Contact> toContactEntity(List<ContactDto> dto);

    List<ContactDto> toContactDto(List<Contact> entity);
}
