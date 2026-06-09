package com.skycareSolutions.skyCarePortal.scheduler;

import com.skycareSolutions.skyCarePortal.model.AgreementTemplateAsset;
import com.skycareSolutions.skyCarePortal.model.AgreementTemplateVersion;
import com.skycareSolutions.skyCarePortal.repository.AgreementTemplateAssetRepository;
import com.skycareSolutions.skyCarePortal.repository.AgreementTemplateVersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

@Component
public class AgreementAssetCleanupScheduler {

    @Autowired
    private AgreementTemplateAssetRepository assetRepository;

    @Autowired
    private AgreementTemplateVersionRepository versionRepository;

    @Scheduled(cron = "${agreement.asset-cleanup.cron:0 0 3 * * SUN}")
    public void cleanupOrphanAssets() {
        Set<String> referencedAssetIds = new HashSet<>();
        for (AgreementTemplateVersion version : versionRepository.findAll()) {
            String html = version.getContentHtml();
            if (html == null) {
                continue;
            }
            for (AgreementTemplateAsset asset : assetRepository.findAll()) {
                if (html.contains(asset.getId()) || html.contains("/agreement-templates/assets/" + asset.getId())) {
                    referencedAssetIds.add(asset.getId());
                }
            }
        }
        for (AgreementTemplateAsset asset : assetRepository.findAll()) {
            if (!referencedAssetIds.contains(asset.getId())) {
                File file = new File(asset.getFilePath());
                if (file.exists()) {
                    file.delete();
                }
                assetRepository.delete(asset);
            }
        }
    }
}
