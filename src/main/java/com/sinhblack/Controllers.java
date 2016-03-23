package com.sinhblack;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class Controllers{

    public static String clientId = "1090406087772-ncvrist0dgffpho7ifnaj0f3p7fmqf9t.apps.googleusercontent.com";
    public static String clientScrete = "xzYVdxVXbfUQKy3yngCa1ohE";
    public static String callbackUrl = "http://localhost:8080/dangnhap/google/callback";
    public static String scope = "https://www.googleapis.com/auth/userinfo.email+" +
                                 "https://www.googleapis.com/auth/userinfo.profile";
    public static String getCodeUrl = "https://accounts.google.com/o/oauth2/auth?" +
            "redirect_uri=" + callbackUrl + "&response_type=code" +
            "&client_id=" + clientId + "&scope=" +scope +
            "&approval_prompt=force&access_type=offline";

    @RequestMapping(value = "/dangnhap/google", method = RequestMethod.GET)
    public void yecauDangNhap(HttpServletResponse response) throws Exception{
        response.sendRedirect(getCodeUrl);
    }

    private String code = "";
    ResponseEntity<String> info;
    @RequestMapping(value = "/dangnhap/google/callback")
    public void getCode(HttpServletRequest request, HttpServletResponse response){
        code = request.getParameter("code");
        System.out.println(code);
        try{
            info = getAccessTokenAndInfo(code);
            response.sendRedirect("/profile");
        }catch (Exception e){
            
        }
    }

    public ResponseEntity<String> getAccessTokenAndInfo(String code) throws Exception{
        String getAccessTokenUrl = "https://www.googleapis.com/oauth2/v3/token";
        String getInfoUrl = "https://www.googleapis.com/plus/v1/people/me";

        RestTemplate template = new RestTemplate();

        MultiValueMap<String, String> payload = new LinkedMultiValueMap<String, String>();
        payload.add("code", code);
        payload.add("redirect_uri", callbackUrl);
        payload.add("client_id", clientId);
        payload.add("client_secret", clientScrete);
        payload.add("scope", "");
        payload.add("grant_type", "authorization_code");

        ResponseToken result = template.postForObject(getAccessTokenUrl, payload, ResponseToken.class);
        String accessToken = result.getAccess_token();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> userInfo = template.exchange(getInfoUrl, HttpMethod.GET, entity, String.class);
        //System.out.println(userInfo);
        return userInfo;
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public ResponseEntity<String> getInfo(){
        return info;
    }
}
