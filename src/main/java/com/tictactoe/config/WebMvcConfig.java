package com.tictactoe.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("forward:/static/login.html");
        registry.addViewController("/signup").setViewName("forward:/static/signup.html");
        registry.addViewController("/dashboard").setViewName("forward:/static/dashboard.html");
        registry.addViewController("/friends").setViewName("forward:/static/friends.html");
        registry.addViewController("/history").setViewName("forward:/static/history.html");
        registry.addViewController("/notifications").setViewName("forward:/static/notifications.html");
        registry.addViewController("/profile").setViewName("forward:/static/profile.html");
        registry.addViewController("/game/join").setViewName("forward:/static/join-game.html");
        registry.addViewController("/game/waiting/{id}").setViewName("forward:/static/waiting-room.html");
        registry.addViewController("/game/board/{id}").setViewName("forward:/static/game-board.html");
        registry.addViewController("/").setViewName("forward:/static/dashboard.html");
    }
}
