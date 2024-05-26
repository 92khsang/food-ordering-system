package com.food.ordering.system.order.service.dataaccess.customer.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_customer_m_view", schema = "customer")
public class CustomerEntity {

    @Id
    private UUID id;

}
