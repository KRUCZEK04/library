package com.example.projektApi.service;

import com.example.projektApi.model.*;
import com.example.projektApi.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public ReservationService(ReservationRepository reservationRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Reservation reserveBook(Long bookId, String username) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Książka nie istnieje"));

        if (book.getStatus() != BookStatus.AVAILABLE) {
            throw new RuntimeException("Książka niedostępna");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Błąd użytkownika"));

        book.setStatus(BookStatus.RESERVED);
        bookRepository.save(book);

        Reservation reservation = new Reservation();
        reservation.setBook(book);
        reservation.setUser(user);

        return reservationRepository.save(reservation);
    }

    @Transactional
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Rezerwacja nie istnieje"));

        Book book = reservation.getBook();
        book.setStatus(BookStatus.AVAILABLE);
        bookRepository.save(book);

        reservation.setActive(false);
        reservationRepository.save(reservation);
    }
}
