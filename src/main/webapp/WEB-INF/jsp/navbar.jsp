<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle != null ? pageTitle : "XO Arcade"}</title>
    <!-- Bootstrap 5 CSS -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.2/font/bootstrap-icons.min.css">
    <!-- Google Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;600;800&display=swap" rel="stylesheet">
    <!-- Custom Style -->
    <link rel="stylesheet" href="/static/css/style.css">
    <!-- Supabase Client JS (Standard CDN) -->
    <script src="https://cdn.jsdelivr.net/npm/@supabase/supabase-js@2"></script>
</head>
<body>

<nav class="navbar navbar-expand-lg navbar-dark bg-dark-glass sticky-top">
    <div class="container">
        <a class="navbar-brand d-flex align-items-center" href="/dashboard">
            <span class="logo-icon me-2">🎮</span>
            <span class="logo-text">XO <span class="text-neon-cyan">ARCADE</span></span>
        </a>
        
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link ${activePage == 'dashboard' ? 'active' : ''}" href="/dashboard">
                        <i class="bi bi-grid-1x2-fill me-1"></i> Dashboard
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${activePage == 'friends' ? 'active' : ''}" href="/friends">
                        <i class="bi bi-people-fill me-1"></i> Friends
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${activePage == 'history' ? 'active' : ''}" href="/history">
                        <i class="bi bi-clock-history me-1"></i> Match History
                    </a>
                </li>
            </ul>
            
            <div class="d-flex align-items-center gap-3">
                <!-- Notifications Bell -->
                <div class="position-relative">
                    <a href="/notifications" class="text-light nav-icon-btn position-relative ${activePage == 'notifications' ? 'active-icon' : ''}">
                        <i class="bi bi-bell-fill fs-5"></i>
                        <span id="notification-badge" class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger d-none">
                            0
                        </span>
                    </a>
                </div>

                <!-- User Profile Dropdown -->
                <div class="dropdown">
                    <a class="d-flex align-items-center text-decoration-none dropdown-toggle text-light cursor-pointer" href="#" role="button" id="userDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                        <img id="nav-avatar" src="${currentUser.avatar != null ? currentUser.avatar : 'https://robohash.org/guest.png?set=set4'}" alt="Avatar" class="rounded-circle nav-avatar me-2">
                        <span class="d-none d-md-inline fw-semibold text-neon-pink" id="nav-username">${currentUser.username != null ? currentUser.username : 'Player'}</span>
                    </a>
                    <ul class="dropdown-menu dropdown-menu-end dropdown-menu-dark bg-dark-card border-neon-cyan" aria-labelledby="userDropdown">
                        <li>
                            <a class="dropdown-item py-2" href="/profile">
                                <i class="bi bi-person-circle me-2"></i> View Profile
                            </a>
                        </li>
                        <li><hr class="dropdown-divider border-secondary"></li>
                        <li>
                            <button class="dropdown-item py-2 text-danger" onclick="logoutUser()">
                                <i class="bi bi-box-arrow-right me-2"></i> Logout
                            </button>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</nav>

<div class="main-content container py-4">
