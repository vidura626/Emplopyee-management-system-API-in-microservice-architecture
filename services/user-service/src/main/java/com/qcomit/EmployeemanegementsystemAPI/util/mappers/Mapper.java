package com.qcomit.EmployeemanegementsystemAPI.util.mappers;

import com.qcomit.EmployeemanegementsystemAPI.dto.EmployeeDto;
import com.qcomit.EmployeemanegementsystemAPI.dto.UserDto;
import com.qcomit.EmployeemanegementsystemAPI.entity.User;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@org.mapstruct.Mapper(componentModel = "spring")
public interface Mapper {
    Mapper INSTANCE = Mappers.getMapper(Mapper.class);

//    @Mapping(source = "firstName", target = "name.firstName")
//    @Mapping(source = "lastName", target = "name.lastName")
//    @Mapping(source = "role", target = "user.role.role")
//    @Mapping(source = "username", target = "user.username")
//    @Mapping(source = "password", target = "user.password")
//    Employee toEntity(EmployeeDto dto);
//
//    @InheritInverseConfiguration
//    EmployeeDto toDto(Employee entity);
//
//    List<EmployeeDto> toDto(List<Employee> all);


    @Mapping(source = "role", target = "role.role")
    User toUserEntity(EmployeeDto dto);

    @Mapping(source = "role.role", target = "role")
    UserDto toDto(User entity);

    @InheritInverseConfiguration
    User toEntity(UserDto dto);

    List<UserDto> toDto(List<User> all);

//    AddressDto toDto(Address entity);
//
//    Address toEntity(AddressDto dto);
//
//    Contact toEntity(ContactDto dto);
//
//    ContactDto toDto(Contact entity);
//
//    List<Contact> toContactEntity(List<ContactDto> dto);
//
//    List<ContactDto> toContactDto(List<Contact> entity);
}
