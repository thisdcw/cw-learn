package com.cw.framework.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.Map.Entry;

/**
 * @author thisdcw
 * @date 2025年06月05日 16:40
 */
public class WxPayV2Util {

    // 生成签名（MD5）
    public static String generateSignature(Map<String, String> data, String key) throws Exception {
        // 1. 排序
        List<String> sortedKeys = new ArrayList<>(data.keySet());
        Collections.sort(sortedKeys);

        // 2. 构建字符串
        StringBuilder sb = new StringBuilder();
        for (String k : sortedKeys) {
            String v = data.get(k);
            if (v != null && !v.trim().isEmpty() && !"sign".equals(k)) {
                sb.append(k).append("=").append(v).append("&");
            }
        }
        // 加上 API 密钥
        sb.append("key=").append(key);

        // 3. MD5 加密并转大写
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] digest = md5.digest(sb.toString().getBytes(StandardCharsets.UTF_8));

        StringBuilder result = new StringBuilder();
        for (byte b : digest) {
            result.append(String.format("%02X", b));
        }
        return result.toString();
    }

    // Map 转 XML
    public static String mapToXml(Map<String, String> data) throws Exception {
        Document document = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();
        Element root = document.createElement("xml");
        document.appendChild(root);

        for (Entry<String, String> entry : data.entrySet()) {
            Element field = document.createElement(entry.getKey());
            field.appendChild(document.createTextNode(entry.getValue()));
            root.appendChild(field);
        }

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        return writer.getBuffer().toString();
    }

    public static Map<String, String> xmlToMap(String xml) throws Exception {
        Map<String, String> result = new HashMap<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setExpandEntityReferences(false);
        // 防XXE
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xml)));
        NodeList nodeList = doc.getDocumentElement().getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node instanceof Element) {
                result.put(node.getNodeName(), node.getTextContent());
            }
        }
        return result;
    }


    public static String generateNonceStr() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
