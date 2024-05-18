package org.planningPoker.model;

import org.bson.codecs.pojo.annotations.BsonId;

public class Role {
    
    @BsonId
    private String roleId;
    private String role;
    private String permissions;
    
    public Role(String roleId, String role, String permissions) {
        this.roleId = roleId;
        this.role = role;
        this.permissions = permissions;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    
}
