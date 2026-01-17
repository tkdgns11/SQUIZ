package com.ssafy.domain.attendance.entity;

import com.ssafy.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "session_memo")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SessionMemo extends BaseEntity {
}
