package com.tmser.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tmser.blog.exception.AlreadyExistsException;
import com.tmser.blog.model.dto.LinkDTO;
import com.tmser.blog.model.entity.Link;
import com.tmser.blog.model.params.LinkParam;
import com.tmser.blog.model.vo.LinkTeamVO;
import com.tmser.blog.repository.LinkRepository;
import com.tmser.blog.service.LinkService;
import com.tmser.blog.service.base.AbstractCrudService;
import com.tmser.blog.utils.ServiceUtils;
import com.tmser.model.sort.Sort;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * LinkService implementation class
 *
 * @author ryanwang
 * @date 2019-03-14
 */
@Service
public class LinkServiceImpl extends AbstractCrudService<Link, Integer> implements LinkService {

    private final LinkRepository linkRepository;

    public LinkServiceImpl(LinkRepository linkRepository) {
        super(linkRepository);
        this.linkRepository = linkRepository;
    }

    @Override
    public @NonNull
    List<LinkDTO> listDtos(@NonNull Sort sort) {
        Assert.notNull(sort, "Sort info must not be null");

        return convertTo(listAll(sort));
    }

    @Override
    public @NonNull
    List<LinkTeamVO> listTeamVos(@NonNull Sort sort) {
        Assert.notNull(sort, "Sort info must not be null");

        // List all links
        List<LinkDTO> links = listDtos(sort);

        // Get teams
        Set<String> teams = ServiceUtils.fetchProperty(links, LinkDTO::getTeam);

        // Convert to team link list map (Key: team, value: link list)
        Map<String, List<LinkDTO>> teamLinkListMap =
                ServiceUtils.convertToListMap(teams, links, LinkDTO::getTeam);

        List<LinkTeamVO> result = new LinkedList<>();

        // Wrap link team vo list
        teamLinkListMap.forEach((team, linkList) -> {
            // Build link team vo
            LinkTeamVO linkTeamVO = new LinkTeamVO();
            linkTeamVO.setTeam(team);
            linkTeamVO.setLinks(linkList);

            // Add it to result
            result.add(linkTeamVO);
        });

        return result;
    }

    @Override
    public @NonNull
    List<LinkTeamVO> listTeamVosByRandom(@NonNull Sort sort) {
        Assert.notNull(sort, "Sort info must not be null");
        List<LinkDTO> links = listDtos(sort);
        Set<String> teams = ServiceUtils.fetchProperty(links, LinkDTO::getTeam);
        Map<String, List<LinkDTO>> teamLinkListMap =
                ServiceUtils.convertToListMap(teams, links, LinkDTO::getTeam);
        List<LinkTeamVO> result = new LinkedList<>();
        teamLinkListMap.forEach((team, linkList) -> {
            LinkTeamVO linkTeamVO = new LinkTeamVO();
            linkTeamVO.setTeam(team);
            Collections.shuffle(linkList);
            linkTeamVO.setLinks(linkList);
            result.add(linkTeamVO);
        });
        return result;
    }

    @Override
    public @NonNull
    Link createBy(@NonNull LinkParam linkParam) {
        Assert.notNull(linkParam, "Link param must not be null");

        // Check the name
        boolean exist = existByName(linkParam.getName());

        if (exist) {
            throw new AlreadyExistsException("友情链接 " + linkParam.getName() + " 已存在")
                    .setErrorData(linkParam.getName());
        }

        // Check the url
        exist = existByUrl(linkParam.getUrl());

        if (exist) {
            throw new AlreadyExistsException("友情链接 " + linkParam.getUrl() + " 已存在")
                    .setErrorData(linkParam.getUrl());
        }

        return create(linkParam.convertTo());
    }

    @Override
    public @NonNull
    Link updateBy(Integer id, @NonNull LinkParam linkParam) {
        Assert.notNull(id, "Id must not be null");
        Assert.notNull(linkParam, "Link param must not be null");

        // Check the name
        boolean exist = linkRepository.existsByNameAndIdNot(linkParam.getName(), id);
        if (exist) {
            throw new AlreadyExistsException("友情链接 " + linkParam.getName() + " 已存在")
                    .setErrorData(linkParam.getName());
        }

        // Check the url
        exist = linkRepository.existsByUrlAndIdNot(linkParam.getUrl(), id);
        if (exist) {
            throw new AlreadyExistsException("友情链接 " + linkParam.getUrl() + " 已存在")
                    .setErrorData(linkParam.getUrl());
        }

        Link link = getById(id);
        linkParam.update(link);

        return update(link);
    }

    @Override
    public boolean existByName(String name) {
        Assert.hasText(name, "Link name must not be blank");
        Link link = new Link();
        link.setName(name);

        return Optional.ofNullable(linkRepository.selectOne(new QueryWrapper<>(link))).isPresent();
    }

    @Override
    public boolean existByUrl(String url) {
        Assert.hasText(url, "Link url must not be blank");
        Link link = new Link();
        link.setUrl(url);

        return Optional.ofNullable(linkRepository.selectOne(new QueryWrapper<>(link))).isPresent();
    }

    @Override
    public List<String> listAllTeams() {
        return linkRepository.findAllTeams();
    }

    @Override
    public @NonNull
    List<Link> listAllByRandom() {
        List<Link> allLink = linkRepository.selectList(null);
        Collections.shuffle(allLink);
        return allLink;
    }

    @NonNull
    private List<LinkDTO> convertTo(@Nullable List<Link> links) {
        if (CollectionUtils.isEmpty(links)) {
            return Collections.emptyList();
        }

        return links.stream().map(link -> (LinkDTO) new LinkDTO().convertFrom(link))
                .collect(Collectors.toList());
    }
}
