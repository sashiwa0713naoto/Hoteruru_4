package com.example.moattravel.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.moattravel.entity.Reservation;
import com.example.moattravel.repository.ReservationRepository;

@Controller
@RequestMapping("/admin/reservations")
public class AdminReservationController {
    
    private final ReservationRepository reservationRepository;

    public AdminReservationController(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    // 予約一覧表示
    @GetMapping
    public String index(@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.DESC) Pageable pageable, Model model) {
        Page<Reservation> reservationPage = reservationRepository.findAllByOrderByCreatedAtDesc(pageable);
        
        model.addAttribute("reservationPage", reservationPage);
        
        return "admin/reservations/index";
    }

    // 予約編集画面表示
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable(name = "id") Integer id, Model model) {
        Reservation reservation = reservationRepository.getReferenceById(id);
        
        model.addAttribute("reservation", reservation);
        
        return "admin/reservations/edit";
    }

    // 予約更新処理
    @PostMapping("/{id}/update")
    public String update(@PathVariable(name = "id") Integer id, @ModelAttribute Reservation reservation, RedirectAttributes redirectAttributes) {
        reservation.setId(id);
        reservationRepository.save(reservation);
        
        redirectAttributes.addFlashAttribute("successMessage", "予約情報を編集しました。");
        
        return "redirect:/admin/reservations";
    }

    // 予約削除処理
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        reservationRepository.deleteById(id);
        
        redirectAttributes.addFlashAttribute("successMessage", "予約を削除しました。");
        
        return "redirect:/admin/reservations";
    }
}