package com.jk1.service;

import com.jk1.entity.Address;
import java.util.List;
import java.util.Optional;

public interface AddressService {
    Address save(Address address);
    Optional<Address> findById(Long id);
    List<Address> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);

    List<Address> findUserAddresses(String email);
    Address addAddress(String email, com.jk1.dto.request.AddressRequestDTO dto);
    Address updateAddress(String email, Long addressId, com.jk1.dto.request.AddressRequestDTO dto);
    void setDefaultAddress(String email, Long addressId);
    void deleteAddress(String email, Long addressId);
}
