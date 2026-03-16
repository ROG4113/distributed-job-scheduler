package com.aniket.distributed_job_scheduler.entities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import com.aniket.distributed_job_scheduler.model.JobStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Job {
    @Id
    @UuidGenerator
    @GeneratedValue(generator = "UUID")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
    private UUID id;

    private String payload;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    private LocalDateTime  scheduledTime;

    private String jobType;

    @Builder.Default
    private Integer retryCount=0;

    @Builder.Default
    @Column
    private Integer maxRetries=3;

    @Version
    private Integer version;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    @JsonManagedReference // This is the parent and will get serialized(avoiding jackson loop bug)
    private List<JobExecution> executions;
}
