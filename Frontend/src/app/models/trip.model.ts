import { City } from './city.model';
import { RiskLevel, TripStatus } from './enums';
import { Forecast } from './forecast.model';
import { Recommendation } from './insights.model';

export interface TripStop {
  id: number;
  arrivalDate: string; // LocalDate YYYY-MM-DD
  departureDate: string; // LocalDate YYYY-MM-DD
  weatherSummary?: string;
  riskLevel?: RiskLevel;
  city?: City;
  cityId?: number;
  cityName?: string;
  country?: string;
}

export interface Trip {
  id: number;
  title: string;
  description: string;
  startDate: string; // LocalDate YYYY-MM-DD
  endDate: string; // LocalDate YYYY-MM-DD
  status: TripStatus;
  overallRisk?: RiskLevel;
  stops: TripStop[];
}

export interface TripRequest {
  title: string;
  description: string;
  startDate: string;
  endDate: string;
  status: TripStatus;
}

export interface TripStopRequest {
  cityId: number;
  cityName: string;
  country?: string;
  arrivalDate: string;
  departureDate: string;
}

export interface TripStopSummary {
  stop: TripStop;
  forecasts?: Forecast[];
  recommendations?: Recommendation[];
  riskLevel: RiskLevel;
  weatherSummary: string;
}

export interface TripSummaryResponse {
  trip: Trip;
  overallRiskLevel?: RiskLevel;
  stopsSummary: TripStopSummary[];
}
