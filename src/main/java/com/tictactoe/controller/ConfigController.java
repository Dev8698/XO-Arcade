package com.tictactoe.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.anon.key}")
    private String supabaseKey;

    @GetMapping("/supabase")
    public ResponseEntity<Map<String, String>> getSupabaseConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("supabaseUrl", supabaseUrl);
        config.put("supabaseKey", supabaseKey);
        return ResponseEntity.ok(config);
    }
}
