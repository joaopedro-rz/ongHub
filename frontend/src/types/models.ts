export type CampaignSummary = {
  id: number
  title: string
  status: string
  ngoId: number
  ngoName: string
  category: string | null
  urgent: boolean
  coverImageUrl: string | null
  startDate: string
  endDate: string
  financialGoal: string | null
}

export type CampaignItem = {
  id: number
  itemName: string
  category: string | null
  quantityNeeded: number
  quantityReceived: number
  unit: string | null
}

export type CampaignUpdate = {
  id: number
  title: string
  body: string
  createdAt: string
}

export type CampaignDetail = {
  id: number
  title: string
  description: string
  financialGoal: string | null
  startDate: string
  endDate: string
  coverImageUrl: string | null
  status: string
  urgent: boolean
  category: string | null
  ngoId: number
  ngoName: string
  items: CampaignItem[]
  updates: CampaignUpdate[]
}

export type NgoSummary = {
  id: number
  name: string
  status: string
  categoryId: number | null
  city: string | null
  state: string | null
}

export type NgoAddress = {
  id: number
  street: string | null
  number: string | null
  complement: string | null
  neighborhood: string | null
  city: string | null
  state: string | null
  country: string | null
  postalCode: string | null
  latitude: number | null
  longitude: number | null
}

export type NgoDetail = {
  id: number
  name: string
  cnpj: string | null
  description: string | null
  phone: string | null
  website: string | null
  email: string | null
  logoUrl: string | null
  socialLinks: string | null
  certifications: string | null
  status: string
  managerUserId: number | null
  categoryId: number | null
  address: NgoAddress | null
}

export type AdminDashboard = {
  ngos: number
  users: number
  campaigns: number
  donations: number
  volunteerApplications: number
}

export type NgoDashboard = {
  donationsCount: number
  activeCampaigns: number
  approvedVolunteers: number
  confirmedFinancialTotal: string
}

export type DonorDashboard = {
  donationsCount: number
  confirmedFinancialTotal: string
  supportedNgos: number
}

export type DonationRow = {
  id: number
  donationType: string
  status: string
  campaignId: number
  campaignTitle: string | null
  amount: string | null
  paymentMethod: string | null
  proofUrl: string | null
  materialDescription: string | null
  quantity: number | null
  campaignItemId: number | null
  notes: string | null
  confirmedAt: string | null
  createdAt: string
  receiptNumber: string | null
}

export type OpportunitySummary = {
  id: number
  ngoId: number
  ngoName: string
  title: string
  slotsAvailable: number
  startDate: string
  endDate: string
}

export type ScheduleSlot = {
  id: number
  slotStart: string
  slotEnd: string
  title: string | null
  volunteerUserId: number | null
}

export type OpportunityDetailPublic = {
  summary: OpportunitySummary
  description: string | null
  skillsRequired: string | null
  hoursPerWeek: number | null
  schedules: ScheduleSlot[]
}
