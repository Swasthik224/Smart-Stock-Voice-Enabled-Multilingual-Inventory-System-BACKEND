package com.retailbilling.dto;

public class AuthResponse {
    private String token;
    private UserData user;

    // Manual All-ArgsConstructor
    public AuthResponse(String token, UserData user) {
        this.token = token;
        this.user = user;
    }

    // Getters and Setters
    public String getToken() { 
        return token; 
    }
    
    public void setToken(String token) { 
        this.token = token; 
    }

    public UserData getUser() { 
        return user; 
    }
    
    public void setUser(UserData user) { 
        this.user = user; 
    }

    // Nested Static Helper Class for User Identity Context
    public static class UserData {
        private String username;
        private String email;
        private String role; // Flattened single string representation

        public UserData(String username, String email, String role) {
            this.username = username;
            this.email = email;
            this.role = role;
        }

        public String getUsername() { 
            return username; 
        }
        
        public void setUsername(String username) { 
            this.username = username; 
        }

        public String getEmail() { 
            return email; 
        }
        
        public void setEmail(String email) { 
            this.email = email; 
        }

        public String getRole() { 
            return role; 
        }
        
        public void setRole(String role) { 
            this.role = role; 
        }
    }
}