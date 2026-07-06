package com.tictactoe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class MultiplayerTicTacToeApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(MultiplayerTicTacToeApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(MultiplayerTicTacToeApplication.class, args);
    }
}
