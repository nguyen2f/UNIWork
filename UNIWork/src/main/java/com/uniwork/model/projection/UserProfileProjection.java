package com.uniwork.model.projection;

public interface UserProfileProjection {
    Long getUserId();
    String getName();
    String getPhone();
    String getEmail();
    String getBio();
    String getAddress();
    String getDepartment();
    Boolean getActive();
    String getSystemRole();

}
