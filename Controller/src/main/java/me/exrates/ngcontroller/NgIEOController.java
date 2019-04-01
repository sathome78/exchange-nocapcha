package me.exrates.ngcontroller;

import me.exrates.model.dto.ieo.ClaimDto;
import me.exrates.model.ngModel.response.ResponseModel;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequestMapping("/api/private/v2/ieo/")
@RestController
public class NgIEOController {

    @GetMapping("/{ieoName}")
    public ResponseModel<?> getInfo(@PathVariable String ieoName) {
        return new ResponseModel<>();
    }

    @PostMapping("/claim", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseModel<?> saveClaim(@RequestBody @Valid ClaimDto claimDto) {


    }


    private String getPrincipalEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
