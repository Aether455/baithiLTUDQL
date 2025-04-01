package com.example.baithicuoiki.service;

import com.example.baithicuoiki.model.Customer;
import com.example.baithicuoiki.model.User;
import com.example.baithicuoiki.repository.CustomerRepository;
import com.example.baithicuoiki.repository.IUserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final IUserRepository iUserRepository;

    public boolean existsByPhone(String phone) {
        return customerRepository.existsByPhone(phone);
    }

    public List<Customer> getAllCustomers(){
        return customerRepository.findAll();
    }


    public Optional<Customer> getCustomerByUserId(Long userId){
        return customerRepository.findByUserId(userId);
    }

    public Customer saveCustomer(Customer customer){
        if (customer.getFullName() == null || customer.getAddress() == null) {
            throw new IllegalArgumentException("Customer name and address must not be null.");
        }
        // Kiểm tra số điện thoại trùng lặp
        if (customerRepository.existsByPhone(customer.getPhone())) {
            throw new IllegalStateException("Số điện thoại đã tồn tại!");
        }
        return customerRepository.save(customer);
    }

    public Customer updateCustomer(@NonNull Customer customer){
        Customer existingCustomer = customerRepository.findById(customer.getId())
                .orElseThrow(()-> new IllegalStateException("Customer with ID " + customer.getId() + " does not exist."));
        if (customer.getUser() != null){
            Optional<User> user = iUserRepository.findById(customer.getUser().getId());
            if (user.isPresent()){
                existingCustomer.setUser(user.get());
            }if (user.isPresent()) {
                existingCustomer.setUser(user.get());
            } else {
                throw new IllegalStateException("User with ID " + customer.getUser().getId() + " does not exist.");
            }

        }

        // Cập nhật tất cả các trường có thể thay đổi
        if (customer.getFullName() != null) existingCustomer.setFullName(customer.getFullName());
        if (customer.getAddress() != null) existingCustomer.setAddress(customer.getAddress());
        if (customer.getPhone() != null && !customer.getPhone().equals(existingCustomer.getPhone())) {
            if (customerRepository.existsByPhone(customer.getPhone())) {
                throw new IllegalStateException("Số điện thoại đã tồn tại!");
            }
            existingCustomer.setPhone(customer.getPhone());
        }



        return customerRepository.save(existingCustomer);
    }

    public void deleteCustomerById(Long id){
        if (!customerRepository.existsById(id)) {
            throw new IllegalStateException("Customer with ID " + id + " does not exist.");
        }
        customerRepository.deleteById(id);
    }

    public Customer getCustomerById(Long id){
        return customerRepository.getReferenceById(id);
    }
}
