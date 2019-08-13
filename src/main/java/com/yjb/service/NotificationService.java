package com.yjb.service;

import com.yjb.dto.NotificationDTO;
import com.yjb.dto.PaginationDTO;
import com.yjb.dto.QuestionDTO;
import com.yjb.enums.NotificationStatusEnum;
import com.yjb.enums.NotificationTypeEnum;
import com.yjb.exception.CustomizeErrorCode;
import com.yjb.exception.CustomizeException;
import com.yjb.mapper.NotificationMapper;
import com.yjb.model.*;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    NotificationMapper notificationMapper;

    /**
     * 查询通知
     * @param id
     * @param page
     * @param size
     * @return
     */
    public PaginationDTO list(Long id, Integer page, Integer size) {
        NotificationExample example1 = new NotificationExample();
        example1.createCriteria()
                .andReceiverEqualTo(id);
        long totalCount = notificationMapper.countByExample(example1);
        long totalPage = totalCount % size != 0 ? totalCount / size + 1 : totalCount / size;
        if (page < 1) page = 1;
        if (page > totalPage) page = Math.toIntExact(totalPage);

        NotificationExample example = new NotificationExample();
        example.createCriteria().andReceiverEqualTo(id);
        example.setOrderByClause("gmt_create desc");
        RowBounds rowBounds = new RowBounds((page-1)*size, size);
        List<Notification> notifications = notificationMapper.selectByExampleWithRowbounds(example, rowBounds);
        List<NotificationDTO> notificationDTOS = new ArrayList<>();
        for (Notification notification : notifications) {
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification, notificationDTO);
            notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notificationDTO.getType()));
            notificationDTOS.add(notificationDTO);
        }

        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<>(page, size, totalCount);

        paginationDTO.setData(notificationDTOS);
        return paginationDTO;
    }

    /**
     * 计算未读数
     * @param id
     * @return
     */
    public Long unReadCount(Long id) {
        NotificationExample example = new NotificationExample();
        example.createCriteria()
                .andStatusEqualTo(NotificationStatusEnum.UNREAD.getStatus())
                .andReceiverEqualTo(id);
        return notificationMapper.countByExample(example);
    }

    /**
     * 读取具体通知
     * @param id
     * @param user
     * @return
     */
    public NotificationDTO read(Long id, User user) {
        Notification notification = notificationMapper.selectByPrimaryKey(id);
        if (notification == null) {
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        }
        if (notification.getReceiver() != user.getId()) {
            throw new CustomizeException(CustomizeErrorCode.READ_NOTIFICATION_FAIL);
        }
        //更新成已读状态
        Notification notificationUpdate = new Notification();
        notificationUpdate.setId(notification.getId());
        notificationUpdate.setStatus(NotificationStatusEnum.READ.getStatus());
        notificationMapper.updateByPrimaryKeySelective(notificationUpdate);

        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification, notificationDTO);
        notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notificationDTO.getType()));
        return notificationDTO;
    }


}
