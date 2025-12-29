package com.example.ecommerce_demo.controller;

import com.example.ecommerce_demo.dto.Result;
import com.example.ecommerce_demo.dto.UserDTO;
import com.example.ecommerce_demo.entity.BaseContext;
import com.example.ecommerce_demo.entity.Product;
import com.example.ecommerce_demo.entity.ShoppingCart;
import com.example.ecommerce_demo.entity.User;
import com.example.ecommerce_demo.service.ProductService;
import com.example.ecommerce_demo.service.ShoppingCartService;
import com.example.ecommerce_demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.apache.commons.codec.digest.DigestUtils;
import tools.jackson.core.ObjectReadContext;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@Slf4j
public class HomeController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private ShoppingCartService cartService;

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        // 1. 所有人都能看商品
        List<Product> products = productService.findAll();
        model.addAttribute("products", products);

        // 2. 检查是否已登录（key 必须和 login 中一致："user"）
        User currentUser = (User) session.getAttribute("user");
        boolean isLoggedIn = (currentUser != null);
        model.addAttribute("isLoggedIn", isLoggedIn);

        if (isLoggedIn) {
            model.addAttribute("currentUser", currentUser);
            BaseContext.setCurrentId(currentUser.getId());
        }

        return "home";
    }

    // HomeController.java
    @GetMapping("/home")
    public String home1(Model model, HttpSession session) {
        List<Product> products = productService.findAll();
        model.addAttribute("products", products);
        User currentUser = (User) session.getAttribute("user");
        boolean isLoggedIn = (currentUser != null);
        model.addAttribute("isLoggedIn", isLoggedIn);
        if(isLoggedIn) {
            model.addAttribute("currentUser", currentUser);
            BaseContext.setCurrentId(currentUser.getId());
            int cartCount = cartService.getCartItemCount(currentUser.getId());
            model.addAttribute("cartCount", cartCount);
        }
        return "home";
    }

    @GetMapping("/charset")
    @ResponseBody
    public String getCharset() {
        return "JVM Default Charset: " + java.nio.charset.Charset.defaultCharset();
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("error", "用户名或密码错误！");
        }
        if (logout != null) {
            model.addAttribute("logout", true);
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttrs) {

        User user = userService.findByUserName(username);
        String passwordHash = DigestUtils.md5Hex(password);
        if (user != null && passwordHash.equals(user.getPassword())) {
            // 2. 登录成功：保存到 session
            session.setAttribute("user", user);
            BaseContext.setCurrentId(user.getId());
            cartService.getOrCreate(user.getId());
            return "redirect:/home"; // 跳转首页
        } else {
            // 3. 登录失败：重定向回登录页 + error 参数
            return "redirect:/login?error";
        }
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        // 绑定空 UserDTO 供 Thymeleaf 表单使用
        model.addAttribute("userDTO", new UserDTO());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute UserDTO userDTO, RedirectAttributes redirectAttrs,
                           Model model,
                           BindingResult result) {
        if((userDTO.getEmail() == null || userDTO.getEmail().isEmpty())){
            model.addAttribute("error", "email can not be empty");
            return "register";
        }
        if((userDTO.getPassword() == null || userDTO.getPassword().isEmpty())){
            model.addAttribute("error", "password can not be empty");
            return "register";
        }
        if(userDTO.getUsername() == null || userDTO.getUsername().isEmpty()){
            model.addAttribute("error", "username can not be empty");
            return "register";
        }
        User user = userService.findByUserName(userDTO.getUsername());
        if(user != null){
            model.addAttribute("error", "username is exist");
            return "register";
        }
        user = userService.findByEmail(userDTO.getEmail());
        if(user != null){
            model.addAttribute("error", "email is exist");
            return "register";
        }
        try {
            String encodedPassword = DigestUtils.md5Hex(userDTO.getPassword());
            User user1 = User.builder().email(userDTO.getEmail()).password(encodedPassword).username(userDTO.getUsername()).build();
            user1.setCreatedAt(LocalDateTime.now());
            user1.setUpdatedAt(LocalDateTime.now());
            userService.save(user1);

            return "redirect:/login?registered";
        }catch (Exception e){
            log.error("error while saving user {}", e.getMessage());
            model.addAttribute("error", "error while saving user");
            return "register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        BaseContext.removeCurrentId();
        session.invalidate();
        return "redirect:/login?logout";
    }

    @PostMapping("/cart/add")
    public String addToCart(
            @RequestParam Long productId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("error", "请先登录");
            return "redirect:/login?redirect=/home";
        }

        // ✅ 存入数据库
        cartService.addToCart(currentUser.getId(), productId, 1);

        redirectAttributes.addFlashAttribute("success", "✅ 商品已加入购物车！");
        return "redirect:/home";
    }

    // 查看购物车页面
    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login?redirect=/cart";
        }
        ShoppingCart cart = cartService.getCartWithItems(currentUser.getId());

        BigDecimal total = cart.getItems().stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int cartCount = cartService.getCartItemCount(currentUser.getId());
        model.addAttribute("cart", cart);
        model.addAttribute("totalAmount", total);
        model.addAttribute("cartCount", cartCount);
        return "cart";
    }

    @PostMapping("/cart/remove")
    public String removeCartItem(@RequestParam Long cartItemId, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login?redirect=/cart";
        }

        cartService.removeCartItem(currentUser.getId(), cartItemId);
        return "redirect:/cart"; // 无需拼接 userId
    }
}
