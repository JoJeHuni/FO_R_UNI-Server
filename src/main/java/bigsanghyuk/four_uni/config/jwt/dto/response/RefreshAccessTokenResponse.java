package bigsanghyuk.four_uni.config.jwt.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RefreshAccessTokenResponse {

    private String accessToken;
}
