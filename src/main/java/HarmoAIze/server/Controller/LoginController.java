package HarmoAIze.server.Controller;

import HarmoAIze.server.Service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Login-controller", description = "로그인 및 회원가입 관련 처리를 위한 컨트롤러 입니다.") //클래스에 대한 설명을 할 수 있는 어노테이션이다.
public class LoginController {
    LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @Operation(summary = "로그인 API", description = "id와 pw를 받아 로그인을 시도합니다.")
    @GetMapping("/user/login")
    public String login(
            @Parameter(description = "사용자 id", required = true, example = "sunbi8534")
            @RequestParam String id,
            @Parameter(description = "사용자 비밀번호", required = true, example = "8534")
            @RequestParam String pw) {
        return loginService.checkUser(id, pw);
    }
}
