package service;

import entity.Customer;
import repository.CustomerRepository;

public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer addCustomer(Customer customer) {
        return customerRepository.saveOrUpdate(customer);
    }

    public Customer getCustomer(String id) {
        Customer customer = customerRepository.getById(id);
        if (customer == null) {
            throw new RuntimeException("Customer not found: " + id);
        }
        return customer;
    }
}
