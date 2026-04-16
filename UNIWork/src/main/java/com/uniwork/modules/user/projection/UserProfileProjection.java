package com.uniwork.modules.user.projection;

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
    String getAvatarUrl();
    String getAvatarPublicId();

}
