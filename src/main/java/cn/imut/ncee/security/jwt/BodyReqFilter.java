package cn.imut.ncee.security.jwt;

import cn.imut.ncee.util.JSONUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * @Auth zhanglei
 * @Date 2023/2/26 21:31
 */
public class BodyReqFilter implements Filter {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String grateful =  request.getHeader("grateful");
        byte[] requestBody = null;
        if(StringUtils.isNotEmpty(grateful) && "true".equals(grateful)) {
            String charSetStr = request.getCharacterEncoding();
            if (null == charSetStr) {
                charSetStr = "UTF-8";
            }
            Charset charSet = Charset.forName(charSetStr);
            String requestBodyStr = StreamUtils.copyToString(request.getInputStream(), charSet);
            if(StringUtils.isNotEmpty(requestBodyStr) && requestBodyStr.contains("body")) {
                Map<String, String> map = JSONUtil.gsonToBean(requestBodyStr, Map.class);
                String value = map.get("body");
                if(StringUtils.isNotEmpty(value) && Base64.isBase64(value)) {
                    String resultStr = StringUtils.trimToNull(value).replaceAll(" +", "+");
                    requestBody = Base64.decodeBase64(resultStr);
                }else{
                    requestBody = new byte[0];
                }
            }else {
                requestBody = StreamUtils.copyToByteArray(request.getInputStream());
            }
            requestBodyStr = null;   //强制刷新为null
            log.info("requestBodyStr已经设置完毕. requestBodyStr is {}", requestBodyStr);
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
