package per.hqd.contentcenter.domain.entity.rocketmqTransactionLogMapper;

import javax.persistence.*;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "rocketmq_transaction_log")
public class RocketmqTransactionLog {
    /**
     * id
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 事务id
     */
    @Column(name = "transaction_Id")
    private String transactionId;

    /**
     * 日志
     */
    private String log;
}