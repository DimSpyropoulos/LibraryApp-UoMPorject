package gr.uom.it1566.Library.dto;

import lombok.Data;


@Data
public class AuthenticationRequest {
    private String email;
    private String password;
}
