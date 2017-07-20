package com.hjzgg.stateless.common.esapi;

import java.io.IOException;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;
import org.owasp.esapi.PreparedString;
import org.owasp.esapi.codecs.CSSCodec;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.DB2Codec;
import org.owasp.esapi.codecs.HTMLEntityCodec;
import org.owasp.esapi.codecs.JavaScriptCodec;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.codecs.OracleCodec;
import org.owasp.esapi.codecs.PercentCodec;
import org.owasp.esapi.codecs.UnixCodec;
import org.owasp.esapi.codecs.VBScriptCodec;
import org.owasp.esapi.codecs.WindowsCodec;
import org.owasp.esapi.codecs.XMLEntityCodec;
import org.owasp.esapi.errors.EncodingException;

/**
 * 
 * <p>
 * <b>本类主要完成以下功能：</b>
 * <p>
 * @version 1.0
 * @time 2016年4月23日 下午4:09:59
 */
public class IYCPEncoder {

    private static final Encoder encoder = ESAPI.encoder();

    private static final char HTML_PLACEHODER = '?';

    /**
     * SQL 编码公共函数
     */
    public static String sqlEncode(String inputString, DatabaseCodec dbcodec) {
        return encoder.encodeForSQL(dbcodec.codec(), inputString);
    }


    /**
     * 处理preapredStatement sql
     *
     * @param sqlTemplate
     * @param paras
     * @param dbcodec
     * @return
     */
    public static String sqlPreparedString(String sqlTemplate, String[] paras, DatabaseCodec dbcodec) {
        PreparedString sqlPreparedString = new PreparedString(sqlTemplate, dbcodec.codec());
        for (int i = 0; i < paras.length; i++) {
            sqlPreparedString.set(i + 1, paras[i]);
        }

        return sqlPreparedString.toString();
    }


    /**
     * HTML 转码公共函数，包括HTML、CSS、JavaScript、URL
     */
    public String htmlEncode(String inputString) {
        return encoder.encodeForHTML(inputString);
    }

    //HTML属性需要与HTML采用不同的编码方法
    public String htmlAttributeEncode(String inputString) {
        return encoder.encodeForHTMLAttribute(inputString);
    }

    /**
     * css 编码
     *
     * @param inputString
     * @return
     */
    public String cssEncode(String inputString) {
        return encoder.encodeForCSS(inputString);
    }

    /**
     * js 编码
     *
     * @param inputString
     * @return
     */
    public String javaScriptEncode(String inputString) {
        return encoder.encodeForJavaScript(inputString);
    }

    /**
     * url 编码
     *
     * @param inputString
     * @return
     */
    public String urlEncode(String inputString) throws Exception {
        try {
            return encoder.encodeForURL(inputString);
        } catch (EncodingException e) {
            // 将EncodingException转换成Exception，隔离ESAPI Exception。
            // 遵循Law of Demeter，或最少知识原则（Least Knowledge Principle)
            throw new Exception(e);
        }
    }


    /**
     * url解码
     *
     * @param url
     * @return
     * @throws Exception
     */
    public static String urlDecode(String url) throws Exception {
        try {
            return encoder.decodeFromURL(url);
        } catch (EncodingException e) {
            throw new Exception(e);
        }
    }


    /**
     * XML encoding转码公共函数
     *
     * @param inputString
     * @return 转码后的字符串
     */
    public String xmlEncode(String inputString) {
        return encoder.encodeForXML(inputString);
    }

    /**
     * xml attribute 编码
     *
     * @param inputString
     * @return
     */
    public String xmlAttributeEncode(String inputString) {
        return encoder.encodeForXMLAttribute(inputString);
    }

    /**
     * 处理客户端负责场景脚本编码问题。在HTML客户端脚本包括以下几类：即HTML、CSS、JavaScript、URL等。
     * 不同的脚本由不同的解析器解析，不同的解析器有不同的关键字集合。因此需要采用不同的编码器进行编码。
     *
     * @param strTemplate 模板字符串
     * @param paras       参数数据
     * @param codecs      编码器数据
     * @param placeholder 模板字符串中的占位符
     * @return 转码并格式化之后的字符串
     */
    public String webPreparedString(String strTemplate, String[] paras, Codec[] codecs, char placeholder) {
        PreparedString clientSidePreparedString = new PreparedString(strTemplate, placeholder, TextCodec.HTML.codec());
        for (int i = 0; i < paras.length; i++) {
            clientSidePreparedString.set(i + 1, paras[i], codecs[i]);
        }
        return clientSidePreparedString.toString();
    }

    /**
     * 使用缺省的占位符“？”
     *
     * @param strTemplate
     * @param paras
     * @param codecs
     * @return
     */
    public String webPreparedString(String strTemplate, String[] paras, Codec[] codecs) {
        return webPreparedString(strTemplate, paras, codecs, HTML_PLACEHODER);
    }

    /**
     * 处理客户端负责场景脚本编码问题。在HTML客户端脚本包括以下几类：即HTML、CSS、JavaScript、URL等。
     * 不同的脚本由不同的解析器解析，不同的解析器有不同的关键字集合。因此需要采用不同的编码器进行编码。
     *
     * @param strTemplate 模板字符串
     * @param paras       参数数据
     * @param codecs      编码器数据
     * @param placeholder 模板字符串中的占位符
     * @return 转码并格式化之后的字符串
     */
    public String webPreparedString(String strTemplate, String[] paras, TextCodec[] codecs, char placeholder) {
        PreparedString clientSidePreparedString = new PreparedString(strTemplate, placeholder, TextCodec.HTML.codec());
        for (int i = 0; i < paras.length; i++) {
            clientSidePreparedString.set(i + 1, paras[i], codecs[i].codec());
        }
        return clientSidePreparedString.toString();
    }

    /**
     * 使用缺省的占位符“？”
     *
     * @param strTemplate
     * @param paras
     * @param codecs
     * @return
     */
    public String webPreparedString(String strTemplate, String[] paras, TextCodec[] codecs) {
        return webPreparedString(strTemplate, paras, codecs, HTML_PLACEHODER);
    }

    /**
     * 使用缺省的占位符“？”
     *
     * @param strTemplate
     * @param param
     * @param codec
     * @return
     */
    public String webPreparedString(String strTemplate, String param, TextCodec codec) {
        return webPreparedString(strTemplate, new String[]{param}, new TextCodec[]{codec}, HTML_PLACEHODER);
    }

    /**
     * base64编码
     *
     * @param data
     * @return
     */
    public String encodeForBase64(byte[] data) {
        return encoder.encodeForBase64(data, false);
    }

    /**
     * base64解码
     *
     * @param text
     * @return
     * @throws IOException
     */
    public byte[] decodeFromBase64(String text) throws IOException {
        return encoder.decodeFromBase64(text);
    }

    public enum DatabaseCodec {
        ORACLE(new OracleCodec()),
        MYSQL_ANSI(new MySQLCodec(MySQLCodec.Mode.ANSI)),
        MYSQL_STANDARD(new MySQLCodec(MySQLCodec.Mode.ANSI)),
        DB2(new DB2Codec());

        private Codec codec;

        private DatabaseCodec(Codec codec) {
            this.codec = codec;
        }

        public Codec codec() {
            return codec;
        }
    }

    public enum TextCodec {
        CSS(new CSSCodec()),
        HTML(new HTMLEntityCodec()),
        JS(new JavaScriptCodec()),
        PERCENT(new PercentCodec()),
        XML(new XMLEntityCodec()),
        UNIX(new UnixCodec()),
        WINDOWS(new WindowsCodec()),
        VB(new VBScriptCodec());

        private Codec codec;

        private TextCodec(Codec codec) {
            this.codec = codec;
        }

        public Codec codec() {
            return codec;
        }
    }

}
