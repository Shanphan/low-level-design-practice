package service;

import exceptions.DuplicateReservationException;
import manager.ProductMgr;
import manager.ReservationMgr;
import entity.Product;
import entity.Reservation;
import entity.ReservationStatus;
import exceptions.ProductNotAvailableException;
import exceptions.ProductNotFoundException;
import exceptions.ReservationNotFoundException;

public class ReservationService {

    private final ReservationMgr reservationMgr;
    private final ProductMgr productMgr;

    public ReservationService(ReservationMgr reservationMgr, ProductMgr productMgr) {
        this.reservationMgr = reservationMgr;
        this.productMgr = productMgr;
    }

    public Reservation reserve(String productId, String userId, int q) {

        Product product = productMgr.findById(productId);
        if(product == null) {
            throw new ProductNotFoundException("Product not found with product Id " + productId);
        }

        product.getRowLock().lock();
        try {
            if(reservationMgr.findReservationByProductIdAndUserId(userId, productId) != null) {
                throw new DuplicateReservationException("User " + userId + " already has a pending reservation for product " + productId);
            }
            int availableQ = product.getTotalQuantity() - product.getReserveQuantity();
            if(q > availableQ) {
                throw new ProductNotAvailableException("Product not available " + product.getName());
            }

            Reservation reservation = new Reservation(productId, userId, q);
            product.setReserveQuantity(product.getReserveQuantity() + q);
            productMgr.save(product);
            reservationMgr.save(reservation);

            return reservation;
        } finally {
            product.getRowLock().unlock();
        }

    }

    public void confirm (String resId) {
        Reservation reservation = reservationMgr.findById(resId);

        if(reservation == null) {
            throw  new ReservationNotFoundException("No reservation found with id " + resId);
        }

        Product product = productMgr.findById(reservation.getProductId());
        product.getRowLock().lock();
        try {
            if(!reservation.getReservationStatus().canTransitionTo(ReservationStatus.CONFIRMED)) {
                throw new IllegalStateException("Cannot go from " + reservation.getReservationStatus().name() + " to "
                        + ReservationStatus.CONFIRMED.name());
            }
            product.setTotalQuantity(product.getTotalQuantity() - reservation.getQuantity());
            product.setReserveQuantity(product.getReserveQuantity() - reservation.getQuantity());
            reservation.setReservationStatus(ReservationStatus.CONFIRMED);

            productMgr.save(product);
            reservationMgr.save(reservation);
        } finally {
            product.getRowLock().unlock();
        }


    }

    public void cancel (String resId) {

        Reservation reservation = reservationMgr.findById(resId);
        if(reservation == null) {
            throw  new ReservationNotFoundException("No reservation found with id " + resId);
        }

        Product product = productMgr.findById(reservation.getProductId());
        product.getRowLock().lock();
        try {
            if(!reservation.getReservationStatus().canTransitionTo(ReservationStatus.CANCELLED)) {
                throw new IllegalStateException("Cannot go from " + reservation.getReservationStatus().name() + " to " + ReservationStatus.CANCELLED.name());
            }
            product.setReserveQuantity(product.getReserveQuantity() - reservation.getQuantity());
            reservation.setReservationStatus(ReservationStatus.CANCELLED);

            productMgr.save(product);
            reservationMgr.save(reservation);
        } finally {
            product.getRowLock().unlock();
        }



    }


}
