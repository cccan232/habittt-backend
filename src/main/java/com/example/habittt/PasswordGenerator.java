package com.example.habittt;

import cn.hutool.crypto.digest.BCrypt;

public class PasswordGenerator {

    public static void main(String[] args) {
        String password = "admin123";
        String hashed = BCrypt.hashpw(password);
        System.out.println("原始密码: " + password);
        System.out.println("加密后: " + hashed);

        // 验证
        boolean matches = BCrypt.checkpw(password, hashed);
        System.out.println("验证结果: " + matches);

        System.out.println("\n请在数据库中执行以下SQL:");
        System.out.println("INSERT INTO admin (username, password, email, status) VALUES ('admin', '" + hashed + "', 'admin@habittt.com', '正常');");
    }
}
