package com.xcz.common.core.utils.ip;

import com.xcz.common.core.utils.StringUtils;
import lombok.Data;

/**
 * IpLocationInfo
 * @date 2023/3/15
 */
@Data
public class IpLocationInfo {
    private String country;
    private String region;
    private String province;
    private String city;
    private String isp;

    public String getLocationInfo() {
        return StringUtils.format("{}-{}-{}-{}", country, province, city, isp);
    }
}
