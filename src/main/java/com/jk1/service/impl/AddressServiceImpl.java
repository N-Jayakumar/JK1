package com.jk1.service.impl;

import com.jk1.entity.Address;
import com.jk1.repository.AddressRepository;
import com.jk1.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.jk1.entity.User;
import com.jk1.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Override
    public Address save(Address address) {
        return addressRepository.save(address);
    }

    @Override
    public Optional<Address> findById(Long id) {
        return addressRepository.findById(id);
    }

    @Override
    public List<Address> findAll() {
        return addressRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        addressRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return addressRepository.existsById(id);
    }

    @Override
    public List<Address> findUserAddresses(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return addressRepository.findByUserId(user.getId());
    }

    @Override
    public Address addAddress(String email, com.jk1.dto.request.AddressRequestDTO dto) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Address> existing = addressRepository.findByUserId(user.getId());
        boolean isFirst = existing.isEmpty();

        Address address = Address.builder()
                .street(dto.getStreet())
                .city(dto.getCity())
                .state(dto.getState())
                .country(dto.getCountry())
                .zipCode(dto.getZipCode())
                .addressType(dto.getAddressType())
                .isDefault(isFirst || dto.isDefault())
                .user(user)
                .build();

        if (address.isDefault() && !isFirst) {
            existing.forEach(a -> {
                a.setDefault(false);
                addressRepository.save(a);
            });
        }

        return addressRepository.save(address);
    }

    @Override
    public Address updateAddress(String email, Long addressId, com.jk1.dto.request.AddressRequestDTO dto) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to address");
        }

        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setCountry(dto.getCountry());
        address.setZipCode(dto.getZipCode());
        address.setAddressType(dto.getAddressType());

        if (dto.isDefault() && !address.isDefault()) {
            List<Address> existing = addressRepository.findByUserId(user.getId());
            existing.forEach(a -> {
                a.setDefault(false);
                addressRepository.save(a);
            });
            address.setDefault(true);
        }

        return addressRepository.save(address);
    }

    @Override
    public void setDefaultAddress(String email, Long addressId) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to address");
        }

        List<Address> existing = addressRepository.findByUserId(user.getId());
        existing.forEach(a -> {
            a.setDefault(false);
            addressRepository.save(a);
        });

        address.setDefault(true);
        addressRepository.save(address);
    }

    @Override
    public void deleteAddress(String email, Long addressId) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to address");
        }

        addressRepository.delete(address);
        
        // If it was default, make another one default
        if (address.isDefault()) {
            List<Address> remaining = addressRepository.findByUserId(user.getId());
            if (!remaining.isEmpty()) {
                Address first = remaining.get(0);
                first.setDefault(true);
                addressRepository.save(first);
            }
        }
    }
}
