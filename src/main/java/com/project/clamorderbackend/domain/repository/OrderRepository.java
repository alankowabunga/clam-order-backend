package com.project.clamorderbackend.domain.repository;

import com.project.clamorderbackend.domain.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Order entity.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Find order by public ID
     */
    Optional<Order> findByPublicId(String publicId);

    /**
     * Find orders by status
     */
    List<Order> findByStatus(Order.OrderStatus status);

    /**
     * Find orders created within a date range
     */
    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Find orders by district (for export)
     */
    List<Order> findByDistrictOrderByCreatedAtDesc(String district);

    /**
     * Find all orders ordered by creation date
     */
    @Query("SELECT o FROM Order o ORDER BY o.createdAt DESC")
    List<Order> findAllOrderByCreatedAtDesc();

    /**
     * Find orders by status and date range
     */
    List<Order> findByStatusAndCreatedAtBetween(
            Order.OrderStatus status, 
            LocalDateTime start, 
            LocalDateTime end);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt >= :startOfDay")
    long countOrdersCreatedToday(@Param("startOfDay") LocalDateTime startOfDay);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    long countByStatus(@Param("status") Order.OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.totalWeight), 0) FROM Order o WHERE o.createdAt >= :startOfDay")
    int sumTotalWeightToday(@Param("startOfDay") LocalDateTime startOfDay);

    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);

    Page<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
}
