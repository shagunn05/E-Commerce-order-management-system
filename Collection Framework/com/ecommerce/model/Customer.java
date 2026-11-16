

package com.ecommerce.model;

import jakarta.persistence.*;

@Entity
@Table(name = "customers")
public class Customer extends User {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CustomerTier tier = CustomerTier.REGULAR;

    protected Customer() {
        super();
    }

    public Customer(Long id, String name, String email) {
        super(id, name, email);
    }

    public CustomerTier getTier() {
        return tier;
    }

    public void setTier(CustomerTier tier) {
        if (tier == null) {
            throw new IllegalArgumentException(
                    "Customer tier cannot be null"
            );
        }
        this.tier = tier;
    }

    @Override
    public String getRole() {
        return "CUSTOMER";
    }
}
