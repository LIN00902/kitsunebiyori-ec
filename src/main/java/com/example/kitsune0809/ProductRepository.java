package com.example.kitsune0809;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//這是一個標籤（註解），告訴 Spring Boot：
// 「這是一個專門用來跟資料庫溝通的元件（Repository）請幫我管理它。」
// 意思：宣告這是一個 Interface（介面），名字叫 ProductRepository。在 Java 裡面，操作資料庫的工具通常會用介面來寫。//
@Repository

// Product：告訴 JPA 這個 Repository 是專門用來管 Product 這張資料表（Model）的。
//Long：告訴 JPA 這張表的 ID 型態是 Long。
//有了它，你的 Controller 就可以輕鬆呼叫它來新增、查詢、修改、刪除資料庫裡的商品//
public interface ProductRepository extends JpaRepository<Product, Long> {
    // 必須要有一行這個，Spring Data JPA 才會認得用名字找資料！
    Product findByName(String name);
}