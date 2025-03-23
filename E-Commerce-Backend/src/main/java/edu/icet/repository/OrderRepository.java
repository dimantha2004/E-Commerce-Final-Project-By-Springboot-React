package edu.icet.repository;

import edu.icet.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order,Long> {

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItemList WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);
}
