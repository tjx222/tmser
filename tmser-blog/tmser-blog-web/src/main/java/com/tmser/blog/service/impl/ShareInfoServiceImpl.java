package com.tmser.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tmser.blog.model.dto.AttachmentDTO;
import com.tmser.blog.model.dto.ShareInfoDTO;
import com.tmser.blog.model.dto.VisitLogDTO;
import com.tmser.blog.model.entity.Attachment;
import com.tmser.blog.model.entity.ShareInfo;
import com.tmser.blog.model.entity.VisitLog;
import com.tmser.blog.model.params.ShareInfoParam;
import com.tmser.blog.repository.ShareInfoRepository;
import com.tmser.blog.repository.VisitLogRepository;
import com.tmser.blog.service.ShareInfoService;
import com.tmser.blog.service.VisitLogService;
import com.tmser.blog.service.base.AbstractCrudService;
import com.tmser.database.mybatis.MybatisPageHelper;
import com.tmser.model.page.Page;
import com.tmser.model.page.PageImpl;
import com.tmser.model.page.Pageable;
import com.tmser.model.sort.Sort;
import com.tmser.util.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * LogService implementation class
 *
 * @author ryanwang
 * @date 2019-03-14
 */
@Service
public class ShareInfoServiceImpl extends AbstractCrudService<ShareInfo, Integer> implements ShareInfoService {

    private final ShareInfoRepository shareInfoRepository;

    public ShareInfoServiceImpl(ShareInfoRepository shareInfoRepository) {
        super(shareInfoRepository);
        this.shareInfoRepository = shareInfoRepository;
    }

    @Override
    public Page<ShareInfoDTO> pageLatest(int top) {
        Assert.isTrue(top > 0, "Top number must not be less than 0");

        // Build page request
        Page<ShareInfo> latestPageable = PageImpl.of(0, top);
        Page<ShareInfoDTO> resultPage = PageImpl.of(0, top, Sort.by(Sort.Direction.DESC, "create_time"));
        // List all
        return resultPage.setContent(listAll(latestPageable).getContent()
                        .stream().map(log -> new ShareInfoDTO().<ShareInfoDTO>convertFrom(log)).collect(Collectors.toList()))
                .setTotal(latestPageable.getTotal());
    }

    @Override
    public Long sumTotalVisit() {
        return shareInfoRepository.countVisit();
    }

    @Override
    public ShareInfoDTO deleteById(Integer id) {
        ShareInfo shareInfo = shareInfoRepository.selectById(id);
        shareInfoRepository.logicDelete(id);
        return new ShareInfoDTO().<ShareInfoDTO>convertFrom(shareInfo);
    }

    @Override
    public List<ShareInfoDTO> deleteByIds(List<Integer> ids) {
        if(CollectionUtils.isEmpty(ids)){
            Collections.emptyList();
        }
        List<ShareInfo> shareInfoList = listAllByIds(ids);
        shareInfoRepository.logicDeleteByIds(ids);
        return shareInfoList.stream()
                .map(si -> new ShareInfoDTO().<ShareInfoDTO>convertFrom(si)).collect(Collectors.toList());
    }

    @Override
    public Page<ShareInfoDTO> pageDtosBy(Pageable pageable, ShareInfoParam shareInfoParam) {
        Assert.notNull(pageable, "Page info must not be null");
        ShareInfo model = new ShareInfo();
        model.setSign(shareInfoParam.getSign());
        model.setStartTime(shareInfoParam.getStartTime());
        QueryWrapper<ShareInfo> wrapper = new QueryWrapper<>(model);
        if (StringUtils.isNotBlank(shareInfoParam.getName())) {
            wrapper.likeRight("name", shareInfoParam.getName());
        }
        if (StringUtils.isNotBlank(shareInfoParam.getRemark())) {
            wrapper.likeRight("remark", shareInfoParam.getRemark());
        }

        // List all
        Page<ShareInfo> shareInfoPage = MybatisPageHelper.fillPageData(
                shareInfoRepository.selectPage(MybatisPageHelper.changeToMybatisPage(pageable), wrapper), pageable);

        Page<ShareInfoDTO> resultPage = PageImpl.of(shareInfoPage.getCurrent(), shareInfoPage.getSize(), shareInfoPage.getTotal());
        // Convert and return
        return resultPage.setContent(shareInfoPage.getContent()
                .stream()
                .map(shareInfo -> new ShareInfoDTO().<ShareInfoDTO> convertFrom(shareInfo)).collect(Collectors.toList()));
    }

    @Override
    public ShareInfoDTO getShareBySign(String sign) {
        ShareInfo shareInfo = shareInfoRepository.selectBySign(sign);
        if(Objects.isNull(shareInfo)){
            return null;
        }
       return new ShareInfoDTO().<ShareInfoDTO>convertFrom(shareInfo);
    }

    @Override
    public void updateVisitCount(Integer id) {
        shareInfoRepository.updateVisitCount(id);
    }
}
