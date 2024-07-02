package com.hypermedia.library.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticationRequest {

    @NotEmpty(message = "The email field can not be null")
    @NotBlank(message = "The email field can not be null")
    @Email(message = "Email is not well formatted")
    private String email;
    @NotEmpty(message = "The password field can not be null")
    @NotBlank(message = "The password field can not be null")
    private String password;

}
