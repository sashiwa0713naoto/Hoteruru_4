package com.example.moattravel.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.moattravel.entity.House;
import com.example.moattravel.entity.Reservation;
import com.example.moattravel.entity.User;
import com.example.moattravel.form.ReservationRegisterForm;
import com.example.moattravel.repository.HouseRepository;
import com.example.moattravel.repository.ReservationRepository;
import com.example.moattravel.repository.UserRepository;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final HouseRepository houseRepository;
    private final UserRepository userRepository;

    public ReservationService(ReservationRepository reservationRepository, 
                              HouseRepository houseRepository, 
                              UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.houseRepository = houseRepository;
        this.userRepository = userRepository;
    }

    public Integer calculateAmount(String checkinDate, String checkoutDate, Integer price) {
        LocalDate checkin = LocalDate.parse(checkinDate);
        LocalDate checkout = LocalDate.parse(checkoutDate);
        long nights = ChronoUnit.DAYS.between(checkin, checkout);
        int amount = price * (int) nights;
        return amount;
    }

    @Transactional
    public void create(ReservationRegisterForm reservationRegisterForm) {
        Reservation reservation = new Reservation();
        
        House house = houseRepository.getReferenceById(reservationRegisterForm.getHouseId());
        User user = userRepository.getReferenceById(reservationRegisterForm.getUserId());
        
        reservation.setHouse(house);
        reservation.setUser(user);
        reservation.setCheckinDate(LocalDate.parse(reservationRegisterForm.getCheckinDate()));
        reservation.setCheckoutDate(LocalDate.parse(reservationRegisterForm.getCheckoutDate()));
        reservation.setNumberOfPeople(reservationRegisterForm.getNumberOfPeople());
        reservation.setAmount(reservationRegisterForm.getAmount());
        
        reservationRepository.save(reservation);
    }

    // 管理者用：すべての予約をリストで取得するメソッド（エラー対策）
    public List<Reservation> findAllReservations() {
        return reservationRepository.findAll();
    }

    // 管理者用：すべての予約をページング付きで取得するメソッド
    public Page<Reservation> findAllReservations(Pageable pageable) {
        return reservationRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    // 会員用：特定のユーザーの予約を取得するメソッド
    public Page<Reservation> findByUserOrderByCreatedAtDesc(User user, Pageable pageable) {
        return reservationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }
    
    @Transactional
    public void delete(Integer id) {
        reservationRepository.deleteById(id);
    }
}