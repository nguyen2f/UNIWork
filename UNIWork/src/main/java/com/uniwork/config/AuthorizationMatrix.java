package com.uniwork.config;

import com.uniwork.model.enumuration.SystemPermission;
import com.uniwork.model.enumuration.SystemRole;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Component
public class AuthorizationMatrix {

    private static final Map<SystemRole, Set<SystemPermission>> matrix = Map.of(

            SystemRole.SUPER_ADMIN, EnumSet.allOf(SystemPermission.class),

            SystemRole.ADMIN, EnumSet.of(SystemPermission.BASIC_ACCESS,
                    SystemPermission.USE_CHAT,
                    SystemPermission.RECEIVE_NOTIFICATION,
                    SystemPermission.CREATE_PROJECT,
                    SystemPermission.VIEW_ALL_PROJECTS,
                    SystemPermission.MANAGE_ALL_PROJECTS,
                    SystemPermission.VIEW_PROJECT_REPORT,
                    SystemPermission.VIEW_SYSTEM_REPORT,
                    SystemPermission.MANAGE_USERS,
                    SystemPermission.ASSIGN_SYSTEM_ROLE
            ),

            SystemRole.MANAGER, EnumSet.of(
                    SystemPermission.BASIC_ACCESS,
                    SystemPermission.USE_CHAT,
                    SystemPermission.RECEIVE_NOTIFICATION,
                    SystemPermission.CREATE_PROJECT,
                    SystemPermission.VIEW_PROJECT_REPORT,
                    SystemPermission.VIEW_SYSTEM_REPORT
            ),

            SystemRole.EMPLOYEE, EnumSet.of(
                    SystemPermission.BASIC_ACCESS,
                    SystemPermission.USE_CHAT,
                    SystemPermission.RECEIVE_NOTIFICATION
            )
    );

    public Set<SystemPermission> getPermission(SystemRole role) {
        return matrix.getOrDefault(role, Set.of());
    }
}
