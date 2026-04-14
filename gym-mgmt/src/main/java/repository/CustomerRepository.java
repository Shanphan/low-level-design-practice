package repository;

import entity.Customer;

import java.util.HashMap;
import java.util.Map;

public class CustomerRepository {

    private final Map<String, Customer> customers;

    public CustomerRepository() {
        this.customers = new HashMap<>();
    }

    public Customer saveOrUpdate(Customer customer) {
        return customers.put(customer.getId(), customer);
    }
    public void delete(Customer customer) {
        customers.remove(customer.getId());
    }
    public Customer getById(String id) {
        return customers.get(id);
    }



}
