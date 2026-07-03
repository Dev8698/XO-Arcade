package com.tictactoe.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.security.Principal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticatedUser implements Principal, Serializable {
    private static final long serialVersionUID = 1L;

    private String id;        // Supabase UUID
    private String username;
    private String email;

    @Override
    public String getName() {
        return id;
    }
}
