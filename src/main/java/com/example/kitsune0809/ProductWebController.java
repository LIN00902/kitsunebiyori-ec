package com.example.kitsune0809;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class ProductWebController {

    @Autowired
    private ProductRepository productRepository;

    // 🌟 建立一個商品對照表（ID 對應 名稱與圖片），完全不需要 if-else！
    // 之後如果加新商品（例如 ID 5），只需要在這裡多加一行即可，非常乾淨好維護。
    private static final Map<Long, String[]> PRODUCT_PRESETS = Map.of(
        1L, new String[]{"│狐狸摩登花磚│光釉吸水杯墊", "hanaishi.jpg"},
        2L, new String[]{"│抱抱狐狸│狐狸造型迴紋針", "popular_product01.png"},
        3L, new String[]{"│燙金複製畫卡│", "popular_product02.png"},
        4L, new String[]{"│一狐好茶│霧透壓克力吊飾", "popular_product03.png"}
    );

    @GetMapping("/")
    public String viewProducts() {
        return "index";
    }

    @GetMapping("/story")
    public String storyPage() {
        return "story";
    }

    @GetMapping("/products")
    public String productsPage(Model model) {
        List<Product> products = productRepository.findAll();
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p, (existing, replacement) -> existing));
        model.addAttribute("productMap", productMap);
        return "products";
    }

    @GetMapping("/tennjikai")
    public String tennjikaiPage() {
        return "tennjikai";
    }

    @GetMapping("/user")
    public String userPage() {
        return "user";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {
        if ("admin".equals(username) && "0000".equals(password)) {
            session.setAttribute("isLoggedIn", true);
            return "redirect:/admin";
        } else {
            model.addAttribute("error", "ユーザーIDまたはパスワードが間違っています。");
            return "user";
        }
    }

    @GetMapping("/admin")
    public String adminPage(HttpSession session, Model model) {
        Boolean isLoggedIn = (Boolean) session.getAttribute("isLoggedIn");
        if (isLoggedIn == null || !isLoggedIn) {
            return "redirect:/user";
        }
        model.addAttribute("products", productRepository.findAll());
        return "admin";
    }

    // 8. 新增商品 (透過對照表自動帶入名稱與圖片，零 if-else)
    @PostMapping("/admin/products/add")
    public String addProduct(@RequestParam("id") Long id,
            @RequestParam("price") int price,
            @RequestParam("amount") int amount,
            @RequestParam(value = "text", required = false) String text) {
        
        Product product = productRepository.findById(id).orElse(new Product());
        product.setId(id);
        product.setPrice(price);
        product.setAmount(amount);
        product.setText(text);
        
        // 從對照表直接抓取名稱與圖片
        String[] preset = PRODUCT_PRESETS.get(id);
        if (preset != null) {
            product.setName(preset[0]);
            product.setImg(preset[1]);
        }
        
        productRepository.save(product);
        return "redirect:/admin";
    }

    @GetMapping("/admin/products/delete")
    public String deleteProduct(@RequestParam("id") Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        }
        return "redirect:/admin";
    }

    // 10. 更新商品
    @PostMapping("/admin/products/update")
    public String updateProduct(@RequestParam("id") Long id,
            @RequestParam("price") Integer price,
            @RequestParam("amount") Integer amount,
            @RequestParam(value = "text", required = false) String text) {

        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            product.setPrice(price);
            product.setAmount(amount);
            product.setText(text);
            
            // 更新時也確保對應名稱正確
            String[] preset = PRODUCT_PRESETS.get(id);
            if (preset != null) {
                product.setName(preset[0]);
                product.setImg(preset[1]);
            }

            productRepository.save(product);
        }
        return "redirect:/admin";
    }
}