package edu.byui.apj.storefront.tutorial101.accessdatajpa;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CustomerRepository  extends CrudRepository<Customer, Long> {

    List<Customer> findByLastName(String firstName);

    Customer findById(long id);

    List<Customer> findByFirstNameOrLastNameIgnoreCase(String firstName, String lastName);

}
