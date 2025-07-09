package com.team03.ticketmon.concert.dto;

import com.team03.ticketmon.concert.domain.enums.ConcertStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

/*
 * Seller Concert DTO
 * 판매자용 콘서트 정보 전송 객체
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerConcertDTO {
	private Long concertId;
	private String title;
	private String artist;
	private String description;
	private Long sellerId;
	private String venueName;
	private String venueAddress;
	private LocalDate concertDate;
	private LocalTime startTime;
	private LocalTime endTime;
	private Integer totalSeats;
	private LocalDateTime bookingStartDate;
	private LocalDateTime bookingEndDate;
	private Integer minAge;
	private Integer maxTicketsPerUser;
	private ConcertStatus status;
	private String posterImageUrl;
	private String aiSummary;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
