package com.hypermedia.library.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegistrationRequest {

    @NotEmpty(message = "The firstname field can not be null")
    @NotBlank(message = "The firstname field can not be null")
    private String firstname;
    @NotEmpty(message = "The lastname field can not be null")
    @NotBlank(message = "The lastname field can not be null")
    private String lastname;
    @NotEmpty(message = "The email field can not be null")
    @NotBlank(message = "The email field can not be null")
    @Email(message = "Email is not well formatted")
    private String email;
    @NotEmpty(message = "The password field can not be null")
    @NotBlank(message = "The password field can not be null")
    @Size(min = 8, message = "Password should be 8 characters long minimum")
    private String password;

}
