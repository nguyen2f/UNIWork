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

            // SUPER_ADMIN có tất cả quyền
            SystemRole.SUPER_ADMIN, EnumSet.allOf(SystemPermission.class),

            // ADMIN cũng có tất cả quyền
            SystemRole.ADMIN, EnumSet.allOf(SystemPermission.class),

            // MANAGER cũng có tất cả quyền
            SystemRole.MANAGER, EnumSet.allOf(SystemPermission.class),

            SystemRole.EMPLOYEE, EnumSet.of(
                    SystemPermission.MANAGE_SYSTEM,
                    SystemPermission.MANAGE_REPORTS,
                    SystemPermission.MANAGE_NOTIFICATIONS,
                    SystemPermission.MANAGE_PROJECTS,
                    SystemPermission.MANAGE_PROJECT_MEMBERS,
                    SystemPermission.MANAGE_EVENTS,
                    SystemPermission.MANAGE_CHATS,
                    SystemPermission.MANAGE_COMMENTS,
                    SystemPermission.MANAGE_TASKS,
                    SystemPermission.MANAGE_USERS,
                    SystemPermission.MANAGE_STAGES
            )

    );

    public Set<SystemPermission> getPermission(SystemRole role) {
        return matrix.getOrDefault(role, Set.of());
    }
}
