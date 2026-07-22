package com.jk1.service;

import com.jk1.entity.Campaign;
import com.jk1.entity.enums.CampaignType;
import com.jk1.entity.enums.Status;
import com.jk1.repository.CampaignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CampaignService {

    private final CampaignRepository campaignRepository;

    public List<Campaign> getAllCampaigns() {
        return campaignRepository.findAll();
    }

    public List<Campaign> getActiveCampaigns() {
        return campaignRepository.findByStatus(Status.ACTIVE);
    }

    public Optional<Campaign> getCampaign(Long id) {
        return campaignRepository.findById(id);
    }

    @Transactional
    public Campaign createCampaign(String name, CampaignType type, String content, LocalDateTime startDate, LocalDateTime endDate) {
        Campaign campaign = Campaign.builder()
                .name(name)
                .campaignType(type)
                .content(content)
                .startDate(startDate)
                .endDate(endDate)
                .build();
        campaign.setStatus(Status.ACTIVE);
        return campaignRepository.save(campaign);
    }

    @Transactional
    public void deleteCampaign(Long id) {
        campaignRepository.deleteById(id);
    }
}
