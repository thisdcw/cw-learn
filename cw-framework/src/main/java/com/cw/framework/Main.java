package com.cw.framework;

import cn.hutool.core.util.RandomUtil;
import com.cw.framework.supply.BaseReq;
import com.cw.framework.supply.OrderSubmitDTO;
import com.cw.framework.supply.SkuDTO;
import com.cw.framework.supply.SkuDetailDTO;
import com.cw.framework.supply.response.SkuDetail;
import com.cw.framework.utils.DateTimeUtils;
import com.cw.framework.utils.GsonUtils;
import com.cw.framework.utils.SignUtils;
import com.cw.framework.utils.SupplyUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author thisdcw
 * @date 2025年06月13日 16:03
 */
public class Main {

    public static final String APPSECRET = "iwfbRdK9LBJ3C4FfeR6BT6NSOeakN2aW";
    public static final String APPID = "173252412813";
    public static final String URL = "https://b.zhenxinhuixuan.com/open";


    public static void main(String[] args) {

        testOrder();

//        testSkuInfo();

    }

    public static void testOrder(){
        OrderSubmitDTO dto = new OrderSubmitDTO();
        dto.setRemark("测试");
        dto.setShipAreaCode("330000000000,330100000000,330112000000");
        List<SkuDTO> skuList = new ArrayList<>();
        SkuDTO sku = new SkuDTO();
        sku.setSku_id(4665547);
        sku.setSku_num(1);
        skuList.add(sku);
        dto.setSku_list(skuList);
        dto.setThird_sn("1123445");
        dto.setUser_address("浙江省杭州市临安区");
        dto.setUser_mobile("18296630779");
        dto.setUser_name("dcw");

        generateBaseParams(dto);
        String sign = SignUtils.createSign(dto, APPSECRET);
        dto.setSign(sign);

        SupplyUtils.post(URL + "/order/submit", GsonUtils.toJson(dto), SkuDetail.class);
    }

    public static void testSkuInfo(){
        SkuDetailDTO detailDTO = new SkuDetailDTO();
        detailDTO.setSku_id(2813083);
        detailDTO.setSpu_id(854003);

        Map<String, Object> map = generateBaseForm(detailDTO);
        String sign = SignUtils.createSign(detailDTO, APPSECRET);
        map.put("sku_id", detailDTO.getSku_id());
        map.put("spu_id", detailDTO.getSpu_id());
        map.put("sign", sign);

        SupplyUtils.get(URL + "/products/getSkuIdInfo", map, SkuDetail.class);
    }

    public static  <T extends BaseReq> void generateBaseParams(T dto) {
        dto.setAppId(APPID);
        dto.setTimestamp(DateTimeUtils.getTimeStamp());
        dto.setOnceString(String.valueOf(RandomUtil.randomInt(1, 10000)));
    }

    public static <T extends BaseReq> Map<String, Object> generateBaseForm(T dto) {
        dto.setAppId(APPID);
        dto.setTimestamp(DateTimeUtils.getTimeStamp());
        dto.setOnceString(String.valueOf(RandomUtil.randomInt(1, 10000)));
        String versions = dto.getVersions();

        Map<String, Object> map = new HashMap<>();
        map.put("appId", dto.getAppId());
        map.put("timestamp", dto.getTimestamp());
        map.put("onceString", dto.getOnceString());
        map.put("versions", versions);


        return map;
    }
}
