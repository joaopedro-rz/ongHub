package com.onghub.api.dto.dashboard;

import java.math.BigDecimal;

public final class DashboardApiDtos {

    private DashboardApiDtos() {}

    public record AdminSummary(long ngos, long users, long campaigns, long donations, long volunteerApplications) {}

    public record NgoSummary(long donationsCount, long activeCampaigns, long approvedVolunteers, BigDecimal confirmedFinancialTotal) {}

    public record DonorSummary(long donationsCount, BigDecimal confirmedFinancialTotal, long supportedNgos) {}
}
