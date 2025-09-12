package com.yoesoff.plate.dto;

import com.yoesoff.plate.enums.OrganizationType;
import com.yoesoff.plate.enums.UserRole;

public class UserSearchDTO {
    public String query; // Search term
    public UserRole role; // Filter by role
    public OrganizationType organizationType; // Filter by organization type
    public String city; // Filter by city
    public String discipline; // Filter by discipline (for fighters)
    public boolean friendsOnly; // Only search among friends
    public int page = 0;
    public int size = 20;
}