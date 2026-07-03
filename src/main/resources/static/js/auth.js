// Global Supabase Client Instance
let supabaseClient = null;
let supabaseInitError = null;

// Initialize Supabase Client dynamically from Backend Config API
async function initSupabase() {
    try {
        const response = await fetch('/api/config/supabase');
        if (!response.ok) {
            throw new Error("Unable to fetch Supabase configuration from server.");
        }
        const config = await response.json();
        
        if (config.supabaseUrl && config.supabaseKey) {
            supabaseClient = window.supabase.createClient(config.supabaseUrl, config.supabaseKey);
            setupAuthListener();
        } else {
            console.warn("Supabase keys are not configured on the server.");
        }
    } catch (error) {
        console.error("Initialization error:", error);
        supabaseInitError = error.message || error;
    }
}

// Setup Event Listener for Supabase Authentication State Changes
function setupAuthListener() {
    supabaseClient.auth.onAuthStateChange((event, session) => {
        if (session) {
            // Set access token cookie
            // Expire time matches session token lifetime (expires_in in seconds)
            const maxAge = session.expires_in;
            document.cookie = `access_token=${session.access_token}; path=/; max-age=${maxAge}; Secure; SameSite=Lax`;
            
            // If on login/signup page, redirect to dashboard on successful login
            const path = window.location.pathname;
            if (path === '/login' || path === '/signup' || path === '/') {
                window.location.href = '/dashboard';
            }
        } else {
            // Delete cookie
            document.cookie = 'access_token=; path=/; expires=Thu, 01 Jan 1970 00:00:00 UTC; Secure; SameSite=Lax';
            
            // Redirect unauthenticated users to login page if they are on a protected page
            const path = window.location.pathname;
            if (path !== '/login' && path !== '/signup' && path !== '/forgot-password' && path !== '/reset-password') {
                window.location.href = '/login';
            }
        }
    });
}

// Log out user
async function logoutUser() {
    if (supabaseClient) {
        try {
            await supabaseClient.auth.signOut();
        } catch (e) {
            console.error("Supabase signOut error:", e);
        }
    }
    // Double check cookie removal
    document.cookie = 'access_token=; path=/; expires=Thu, 01 Jan 1970 00:00:00 UTC; Secure; SameSite=Lax';
    window.location.href = '/login';
}

// Start Initialization when page loads
document.addEventListener('DOMContentLoaded', () => {
    initSupabase();
});
