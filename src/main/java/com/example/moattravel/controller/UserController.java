package com.example.moattravel.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.moattravel.entity.User;
import com.example.moattravel.form.UserEditForm;
import com.example.moattravel.security.UserDetailsImpl;
import com.example.moattravel.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 会員情報（マイページ）表示
    @GetMapping
    public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
        User user = userDetailsImpl.getUser();
        model.addAttribute("user", user);
        return "user/index";
    }

    // 会員情報編集画面表示
    @GetMapping("/edit")
    public String edit(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
        User user = userDetailsImpl.getUser();
        UserEditForm userEditForm = new UserEditForm(
            user.getId(),
            user.getName(),
            user.getFurigana(),
            user.getPostalCode(),
            user.getAddress(),
            user.getPhoneNumber(),
            user.getEmail()
        );

        model.addAttribute("userEditForm", userEditForm);

        return "user/edit";
    }

    // 会員情報更新処理
    @PostMapping("/update")
    public String update(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                         @ModelAttribute @Validated UserEditForm userEditForm,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        
        // ログイン中の自分のIDを強制的に設定（不正な書き換え防止）
        userEditForm.setId(userDetailsImpl.getUser().getId());

        if (bindingResult.hasErrors()) {
            return "user/edit";
        }

        userService.update(userEditForm);
        redirectAttributes.addFlashAttribute("successMessage", "会員情報を編集しました。");

        return "redirect:/user";
    }
}