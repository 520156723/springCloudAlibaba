package per.hqd.contentcenter.rocketmq;

import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import per.hqd.contentcenter.dao.rocketmqTransactionLogMapper.RocketmqTransactionLogMapper;
import per.hqd.contentcenter.domain.dto.content.ShareAuditDTO;
import per.hqd.contentcenter.domain.dto.messaging.UserAddBonusMsgDTO;
import per.hqd.contentcenter.domain.entity.rocketmqTransactionLogMapper.RocketmqTransactionLog;
import per.hqd.contentcenter.service.content.ShareService;

@RocketMQTransactionListener(txProducerGroup = "tx-add-bonus-group")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AddBonusTransactionListener implements RocketMQLocalTransactionListener {

    private final ShareService shareService;

    private final RocketmqTransactionLogMapper rocketmqTransactionLogMapper;

    /**
     *
     * @param msg {@link UserAddBonusMsgDTO}
     * @param arg {@link ShareAuditDTO}
     * @return 用于实现笔记中的3、4步，生产者执行本地事务和执行完后生产者再发送确认消息给MQServer
     */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        MessageHeaders headers = msg.getHeaders();
        String transactionId = (String)headers.get(RocketMQHeaders.TRANSACTION_ID);
        Integer shareId = (Integer) headers.get("shareId");
        try {
            this.shareService.auditByIdWithRocketMqLog(shareId, (ShareAuditDTO) arg, transactionId);
            // 如果到这一步服务挂了，需要下面checkLocalTransaction来查询事务状态
            return RocketMQLocalTransactionState.COMMIT;
        } catch (Exception e) {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }


    /**
     * 对应监听笔记中的第五步（实现消息回查）
     * 查rocketmq日志表来得到事务执行状态
     * @param msg
     * @return
     */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        MessageHeaders headers = msg.getHeaders();
        String transactionId = (String)headers.get(RocketMQHeaders.TRANSACTION_ID);
        // 等同于 select * from xxx where transaction_id=transactionId
        RocketmqTransactionLog transactionLog = this.rocketmqTransactionLogMapper.selectOne(
                RocketmqTransactionLog.builder()
                        .transactionId(transactionId)
                        .build()
        );
        if (transactionLog != null) {
        return RocketMQLocalTransactionState.COMMIT;
        }
        return RocketMQLocalTransactionState.ROLLBACK;
    }
}
