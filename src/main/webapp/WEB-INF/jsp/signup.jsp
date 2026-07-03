<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign Up - XO Arcade</title>
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
            <p class="text-muted-custom mt-2">Initialize your player card to join the grid</p>
        </div>

        <!-- SIGNUP FORM CARD -->
        <div class="card bg-dark-card p-4 border-neon-cyan pulse-glow">
            <h3 class="fw-bold mb-3 text-neon-cyan text-center">CREATE ACCOUNT</h3>

            <!-- Error Alert -->
            <div class="alert alert-danger d-none" id="signup-error" role="alert">
                <i class="bi bi-exclamation-triangle-fill me-2"></i>
                <span id="signup-error-msg">Failed to create account.</span>
            </div>

            <!-- Success Alert (Email Verification) -->
            <div class="alert alert-success d-none" id="signup-success" role="alert">
                <i class="bi bi-envelope-check-fill me-2"></i>
                <span>Account created! Please check your email to verify your account.</span>
            </div>

            <form id="signupForm" onsubmit="handleSignup(event)">
                <div class="mb-3">
                    <label for="username" class="form-label text-muted-custom">Username</label>
                    <div class="input-group">
                        <span class="input-group-text bg-dark-glass border-0 text-muted"><i
                                class="bi bi-person-fill"></i></span>
                        <input type="text" class="form-control form-control-dark" id="username" required minlength="3"
                            maxlength="20" placeholder="ChallengerName">
                    </div>
                </div>

                <div class="mb-3">
                    <label for="email" class="form-label text-muted-custom">Email Address</label>
                    <div class="input-group">
                        <span class="input-group-text bg-dark-glass border-0 text-muted"><i
                                class="bi bi-envelope-fill"></i></span>
                        <input type="email" class="form-control form-control-dark" id="email" required
                            placeholder="name@domain.com">
                    </div>
                </div>

                <div class="mb-3">
                    <label for="password" class="form-label text-muted-custom">Password</label>
                    <div class="input-group">
                        <span class="input-group-text bg-dark-glass border-0 text-muted"><i
                                class="bi bi-lock-fill"></i></span>
                        <input type="password" class="form-control form-control-dark" id="password" required
                            minlength="6" placeholder="••••••••">
                    </div>
                </div>

                <div class="mb-4">
                    <label for="confirmPassword" class="form-label text-muted-custom">Confirm Password</label>
                    <div class="input-group">
                        <span class="input-group-text bg-dark-glass border-0 text-muted"><i
                                class="bi bi-lock-fill"></i></span>
                        <input type="password" class="form-control form-control-dark" id="confirmPassword" required
                            minlength="6" placeholder="••••••••">
                    </div>
                </div>

                <button type="submit"
                    class="btn btn-neon-cyan w-100 py-2 d-flex align-items-center justify-content-center gap-2"
                    id="signupBtn">
                    <span>CREATE ACCOUNT</span>
                    <div class="spinner-border spinner-border-sm d-none" id="signupSpinner" role="status"></div>
                </button>
            </form>

            <div class="text-center mt-4">
                <span class="text-muted-custom small">Already registered? </span>
                <a href="/login" class="text-neon-cyan text-decoration-none small fw-semibold">LOG IN</a>
            </div>
        </div>
    </div>

    <script src="/static/js/auth.js?v=2"></script>
    <script>
        async function handleSignup(event) {
            event.preventDefault();

            const username = document.getElementById('username').value.trim();
            const email = document.getElementById('email').value.trim();
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;

            const errorAlert = document.getElementById('signup-error');
            const successAlert = document.getElementById('signup-success');
            const spinner = document.getElementById('signupSpinner');
            const submitBtn = document.getElementById('signupBtn');

            errorAlert.classList.add('d-none');
            successAlert.classList.add('d-none');

            // Passwords Match validation
            if (password !== confirmPassword) {
                errorAlert.classList.remove('d-none');
                document.getElementById('signup-error-msg').innerText = "Passwords do not match.";
                return;
            }

            spinner.classList.remove('d-none');
            submitBtn.disabled = true;

            if (!supabaseClient || !supabaseClient.auth) {
                errorAlert.classList.remove('d-none');
                let msg = "Auth server not reachable. Check configuration.";
                if (typeof supabaseInitError !== 'undefined' && supabaseInitError) {
                    msg += " (Error: " + supabaseInitError + ")";
                }
                document.getElementById('signup-error-msg').innerText = msg;
                spinner.classList.add('d-none');
                submitBtn.disabled = false;
                return;
            }

            try {
                const { data, error } = await supabaseClient.auth.signUp({
                    email: email,
                    password: password,
                    options: {
                        data: {
                            username: username
                        }
                    }
                });

                if (error) {
                    throw error;
                }

                // check if user needs to verify email (standard flow if auto-confirm is off)
                if (data.user && data.session === null) {
                    successAlert.classList.remove('d-none');
                    document.getElementById('signupForm').reset();
                } else if (data.session) {
                    // Auto login occurred, auth.js listener will handle redirect
                }
            } catch (err) {
                errorAlert.classList.remove('d-none');
                document.getElementById('signup-error-msg').innerText = err.message || "Failed to create account.";
            } finally {
                spinner.classList.add('d-none');
                submitBtn.disabled = false;
            }
        }
    </script>
</body>

</html>