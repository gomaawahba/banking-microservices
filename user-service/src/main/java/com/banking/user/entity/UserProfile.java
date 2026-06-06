package com.banking.user.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    private String fullName;
    private String nationality;
    private String city;
    private String country;
}