package com.ssafy.domain.meeting.entity;

import com.ssafy.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "meeting_summary")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MeetingSummary extends BaseEntity {

}
