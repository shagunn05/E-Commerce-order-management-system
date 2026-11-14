package com.managementsystem.model;

/**
 * Concrete StaffUser class representing administrators and library staff.
 */
public class StaffUser extends User {
    private String department;
    private String staffCode;

    public StaffUser(String userId, String name, String email, String department, String staffCode) {
        super(userId, name, email);
        this.department = department != null ? department.trim() : "Operations";
        this.staffCode = staffCode != null ? staffCode.trim() : "STAFF-" + userId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getStaffCode() {
        return staffCode;
    }

    @Override
    public String getRole() {
        return "STAFF";
    }

    @Override
    public String getUserSummary() {
        return String.format("Department: %s | Staff Code: %s", department, staffCode);
    }
}
