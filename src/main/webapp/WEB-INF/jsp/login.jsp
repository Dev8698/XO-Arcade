<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - XO Arcade</title>
    <!-- Bootstrap 5 CSS -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.2/font/bootstrap-icons.min.css">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;600;800&display=swap" rel="stylesheet">
    <!-- Custom Style -->
    <link rel="stylesheet" href="/static/css/style.css">
    <!-- Supabase Client JS -->
    <script src="https://cdn.jsdelivr.net/npm/@supabase/supabase-js@2"></script>
</head>
<body class="d-flex align-items-center justify-content-center min-vh-100 py-5">

<div class="container" style="max-width: 450px;">
    
    <!-- BRAND HEADER -->
    <div class="text-center mb-4">
        <h1 class="logo-text m-0 fs-2">
            🎮 XO <span class="text-neon-cyan">ARCADE</span>
        </h1>
        <p class="text-muted-custom mt-2">Enter the arena and challenge your friends</p>
    </div>

    <!-- LOGIN FORM CARD -->
    <div class="card bg-dark-card p-4 border-neon-cyan pulse-glow" id="login-container">
        <h3 class="fw-bold mb-3 text-neon-cyan text-center">PLAYER LOGIN</h3>
        
        <!-- Error Alert -->
        <div class="alert alert-danger d-none" id="login-error" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2"></i>
            <span id="login-error-msg">Failed to login.</span>
        </div>
        
        <!-- Success Alert (e.g., password reset sent) -->
        <div class="alert alert-success d-none" id="login-success" role="alert">
            <i class="bi bi-check-circle-fill me-2"></i>
            <span id="login-success-msg">Check your email for details.</span>
        </div>

        <form id="loginForm" onsubmit="handleLogin(event)">
            <div class="mb-3">
                <label for="email" class="form-label text-muted-custom">Email Address</label>
                <div class="input-group">
                    <span class="input-group-text bg-dark-glass border-0 text-muted"><i class="bi bi-envelope-fill"></i></span>
                    <input type="email" class="form-control form-control-dark" id="email" required placeholder="name@domain.com">
                </div>
            </div>
            
            <div class="mb-3">
                <label for="password" class="form-label text-muted-custom">Password</label>
                <div class="input-group">
                    <span class="input-group-text bg-dark-glass border-0 text-muted"><i class="bi bi-lock-fill"></i></span>
                    <input type="password" class="form-control form-control-dark" id="password" required placeholder="••••••••">
                </div>
            </div>

            <div class="d-flex justify-content-between align-items-center mb-4">
                <div class="form-check">
                    <input class="form-check-input bg-dark-glass border-secondary" type="checkbox" id="rememberMe">
                    <label class="form-check-label text-muted-custom small" for="rememberMe">
                        Remember Me
                    </label>
                </div>
                <a href="#" onclick="toggleForgotPassword(true)" class="text-neon-pink text-decoration-none small">Forgot Password?</a>
            </div>

            <button type="submit" class="btn btn-neon-cyan w-100 py-2 d-flex align-items-center justify-content-center gap-2" id="loginBtn">
                <span>ENTER ARENA</span>
                <div class="spinner-border spinner-border-sm d-none" id="loginSpinner" role="status"></div>
            </button>
        </form>

        <div class="text-center mt-4">
            <span class="text-muted-custom small">New to the grid? </span>
            <a href="/signup" class="text-neon-cyan text-decoration-none small fw-semibold">CREATE AN ACCOUNT</a>
        </div>
    </div>

    <!-- FORGOT PASSWORD CARD (HIDDEN BY DEFAULT) -->
    <div class="card bg-dark-card p-4 border-neon-pink d-none" id="forgot-container">
        <h3 class="fw-bold mb-3 text-neon-pink text-center">FORGOT PASSWORD</h3>
        <p class="text-muted-custom text-center small mb-4">Enter your email and we'll send you a password reset link.</p>
        
        <form id="forgotForm" onsubmit="handleForgotPassword(event)">
            <div class="mb-4">
                <label for="forgot-email" class="form-label text-muted-custom">Email Address</label>
                <div class="input-group">
                    <span class="input-group-text bg-dark-glass border-0 text-muted"><i class="bi bi-envelope-fill"></i></span>
                    <input type="email" class="form-control form-control-dark" id="forgot-email" required placeholder="name@domain.com">
                </div>
            </div>

            <button type="submit" class="btn btn-neon-pink w-100 py-2 d-flex align-items-center justify-content-center gap-2" id="forgotBtn">
                <span>SEND RESET LINK</span>
                <div class="spinner-border spinner-border-sm d-none" id="forgotSpinner" role="status"></div>
            </button>
            
            <button type="button" onclick="toggleForgotPassword(false)" class="btn btn-outline-secondary w-100 mt-2 text-muted-custom">
                BACK TO LOGIN
            </button>
        </form>
    </div>
</div>

<script src="/static/js/auth.js?v=2"></script>
<script>
    // Toggle forgot password section
    function toggleForgotPassword(show) {
        document.getElementById('login-error').classList.add('d-none');
        document.getElementById('login-success').classList.add('d-none');
        if (show) {
            document.getElementById('login-container').classList.add('d-none');
            document.getElementById('forgot-container').classList.remove('d-none');
        } else {
            document.getElementById('login-container').classList.remove('d-none');
            document.getElementById('forgot-container').classList.add('d-none');
        }
    }

    // Handle Login submission
    async function handleLogin(event) {
        event.preventDefault();
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        const rememberMe = document.getElementById('rememberMe').checked;
        
        const errorAlert = document.getElementById('login-error');
        const spinner = document.getElementById('loginSpinner');
        const submitBtn = document.getElementById('loginBtn');
        
        errorAlert.classList.add('d-none');
        spinner.classList.remove('d-none');
        submitBtn.disabled = true;
        
        if (!supabaseClient || !supabaseClient.auth) {
            errorAlert.classList.remove('d-none');
            let msg = "Auth server not reachable. Check configuration.";
            if (typeof supabaseInitError !== 'undefined' && supabaseInitError) {
                msg += " (Error: " + supabaseInitError + ")";
            }
            document.getElementById('login-error-msg').innerText = msg;
            spinner.classList.add('d-none');
            submitBtn.disabled = false;
            return;
        }

        try {
            const { data, error } = await supabaseClient.auth.signInWithPassword({
                email: email,
                password: password
            });

            if (error) {
                throw error;
            }

            // Successfully authenticated, onAuthStateChange in auth.js will set cookie and redirect
        } catch (err) {
            errorAlert.classList.remove('d-none');
            document.getElementById('login-error-msg').innerText = err.message || "Invalid email or password.";
            spinner.classList.add('d-none');
            submitBtn.disabled = false;
        }
    }

    // Handle Forgot Password submission
    async function handleForgotPassword(event) {
        event.preventDefault();
        const email = document.getElementById('forgot-email').value;
        
        const spinner = document.getElementById('forgotSpinner');
        const submitBtn = document.getElementById('forgotBtn');
        
        spinner.classList.remove('d-none');
        submitBtn.disabled = true;

        try {
            const { error } = await supabaseClient.auth.resetPasswordForEmail(email, {
                redirectTo: window.location.origin + '/login'
            });

            if (error) {
                throw error;
            }

            // Success
            toggleForgotPassword(false);
            const successAlert = document.getElementById('login-success');
            successAlert.classList.remove('d-none');
            document.getElementById('login-success-msg').innerText = "Password reset email sent! Check your inbox.";
        } catch (err) {
            alert("Error sending reset email: " + err.message);
        } finally {
            spinner.classList.add('d-none');
            submitBtn.disabled = false;
        }
    }
</script>
</body>
</html>
