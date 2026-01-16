package com.ssafy.domain.attendance.entity;

import com.ssafy.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "session_memo")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SessionMemo extends BaseEntity {

}
