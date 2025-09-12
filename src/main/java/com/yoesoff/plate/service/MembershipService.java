package com.yoesoff.plate.service;

import com.yoesoff.plate.dto.MembershipDTO;
import com.yoesoff.plate.dto.MembershipRequestDTO;
import com.yoesoff.plate.entity.MembershipEntity;
import com.yoesoff.plate.entity.UserEntity;
import com.yoesoff.plate.enums.MembershipStatus;
import com.yoesoff.plate.enums.MembershipType;
import com.yoesoff.plate.enums.OrganizationType;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class MembershipService {

    public List<MembershipDTO> getUserMemberships(UserEntity user, MembershipStatus status, int page, int size) {
        StringBuilder query = new StringBuilder("member = ?1");

        if (status != null) {
            query.append(" and status = ?2");
        }

        List<MembershipEntity> memberships = MembershipEntity.find(query.toString(),
                        Sort.by("createdAt").descending(),
                        user, status)
                .page(page, size)
                .list();

        return memberships.stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<MembershipDTO> getOrganizationMembers(UserEntity organization, MembershipStatus status, int page, int size) {
        // Only organizations can view their members
        if (organization.organizationType == OrganizationType.PERSONAL) {
            throw new IllegalArgumentException("Only organizations can have members");
        }

        StringBuilder query = new StringBuilder("organization = ?1");

        if (status != null) {
            query.append(" and status = ?2");
        }

        List<MembershipEntity> memberships = MembershipEntity.find(query.toString(),
                        Sort.by("createdAt").descending(),
                        organization, status)
                .page(page, size)
                .list();

        return memberships.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional
    public MembershipDTO requestMembership(UserEntity member, MembershipRequestDTO request) {
        // Only personal users can become members
        if (member.organizationType != OrganizationType.PERSONAL) {
            throw new IllegalArgumentException("Only personal users can request memberships");
        }

        UserEntity organization = UserEntity.findById(request.organizationId);
        if (organization == null || organization.organizationType == OrganizationType.PERSONAL) {
            throw new IllegalArgumentException("Invalid organization");
        }

        // Check if membership already exists
        MembershipEntity existing = MembershipEntity.findByMemberAndOrganization(member, organization);
        if (existing != null && existing.status != MembershipStatus.CANCELLED) {
            throw new IllegalArgumentException("Membership already exists or is pending");
        }

        MembershipEntity membership = new MembershipEntity();
        membership.member = member;
        membership.organization = organization;
        membership.membershipType = request.membershipType;
        membership.status = MembershipStatus.PENDING;
        membership.startDate = request.startDate != null ? request.startDate : LocalDate.now();
        membership.endDate = request.endDate;
        membership.monthlyFee = request.monthlyFee;
        membership.memberNotes = request.memberNotes;
        membership.createdAt = LocalDateTime.now();

        membership.persist();
        return convertToDTO(membership);
    }

    @Transactional
    public MembershipDTO approveMembership(UUID membershipId, UserEntity organization, String organizationNotes) {
        MembershipEntity membership = MembershipEntity.findById(membershipId);
        if (membership == null || !membership.organization.equals(organization)) {
            return null;
        }

        if (membership.status != MembershipStatus.PENDING) {
            throw new IllegalArgumentException("Can only approve pending memberships");
        }

        membership.status = MembershipStatus.ACTIVE;
        membership.approvedAt = LocalDateTime.now();
        membership.organizationNotes = organizationNotes;
        membership.persist();

        return convertToDTO(membership);
    }

    @Transactional
    public MembershipDTO rejectMembership(UUID membershipId, UserEntity organization, String reason) {
        MembershipEntity membership = MembershipEntity.findById(membershipId);
        if (membership == null || !membership.organization.equals(organization)) {
            return null;
        }

        membership.status = MembershipStatus.CANCELLED;
        membership.cancelledAt = LocalDateTime.now();
        membership.organizationNotes = reason;
        membership.persist();

        return convertToDTO(membership);
    }

    @Transactional
    public MembershipDTO cancelMembership(UUID membershipId, UserEntity user, String reason) {
        MembershipEntity membership = MembershipEntity.findById(membershipId);
        if (membership == null) {
            return null;
        }

        // Either member or organization can cancel
        boolean canCancel = membership.member.equals(user) || membership.organization.equals(user);
        if (!canCancel) {
            return null;
        }

        membership.status = MembershipStatus.CANCELLED;
        membership.cancelledAt = LocalDateTime.now();

        if (membership.member.equals(user)) {
            membership.memberNotes = reason;
        } else {
            membership.organizationNotes = reason;
        }

        membership.persist();
        return convertToDTO(membership);
    }

    @Transactional
    public MembershipDTO suspendMembership(UUID membershipId, UserEntity organization, String reason) {
        MembershipEntity membership = MembershipEntity.findById(membershipId);
        if (membership == null || !membership.organization.equals(organization)) {
            return null;
        }

        membership.status = MembershipStatus.SUSPENDED;
        membership.suspendedAt = LocalDateTime.now();
        membership.organizationNotes = reason;
        membership.persist();

        return convertToDTO(membership);
    }

    @Transactional
    public MembershipDTO reactivateMembership(UUID membershipId, UserEntity organization) {
        MembershipEntity membership = MembershipEntity.findById(membershipId);
        if (membership == null || !membership.organization.equals(organization)) {
            return null;
        }

        if (membership.status == MembershipStatus.SUSPENDED) {
            membership.status = MembershipStatus.ACTIVE;
            membership.suspendedAt = null;
            membership.persist();
        }

        return convertToDTO(membership);
    }

    public boolean isMemberOf(UserEntity member, UserEntity organization) {
        MembershipEntity membership = MembershipEntity.findByMemberAndOrganization(member, organization);
        return membership != null && membership.isActive();
    }

    private MembershipDTO convertToDTO(MembershipEntity membership) {
        MembershipDTO dto = new MembershipDTO();
        dto.id = membership.id;
        dto.membershipType = membership.membershipType;
        dto.status = membership.status;
        dto.startDate = membership.startDate;
        dto.endDate = membership.endDate;
        dto.monthlyFee = membership.monthlyFee;
        dto.benefits = membership.benefits;
        dto.memberNotes = membership.memberNotes;
        dto.organizationNotes = membership.organizationNotes;
        dto.createdAt = membership.createdAt;
        dto.approvedAt = membership.approvedAt;
        dto.suspendedAt = membership.suspendedAt;
        dto.cancelledAt = membership.cancelledAt;

        // Member info
        dto.memberName = membership.member.getFullName();
        dto.memberUsername = membership.member.username;

        // Organization info
        dto.organizationName = membership.organization.getFullName();
        dto.organizationUsername = membership.organization.username;
        dto.organizationType = membership.organization.organizationType;

        return dto;
    }
}