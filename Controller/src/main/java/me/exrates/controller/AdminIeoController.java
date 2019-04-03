package me.exrates.controller;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.ieo.IEODetailsUpdateDto;
import me.exrates.model.enums.IEODetailsStatus;
import me.exrates.service.IEOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Log4j2
@Controller
public class AdminIeoController {

    private final IEOService ieoService;

    @Autowired
    public AdminIeoController(IEOService ieoService) {
        this.ieoService = ieoService;
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/ieo/page", method = GET)
    public String getIeoPage(Model model) {
        model.addAttribute("statuses", IEODetailsStatus.values());
        return "admin/ieo";
    }


    @RequestMapping(value = "/2a8fy7b07dxe44/ieo/all", method = GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Collection<IEODetailsUpdateDto> getAllIeos() {
        return ieoService.findAll()
                .stream()
                .map(IEODetailsUpdateDto::new)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/ieo", method = POST, consumes =  MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity createIeo(IEODetailsUpdateDto dto) {
        return new ResponseEntity(dto, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/ieo/{id}", method = PUT, consumes =  MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity updateIeo(@PathVariable("id") Integer id, IEODetailsUpdateDto dto) {
        return ResponseEntity.ok(dto);
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/ieo/revert/{id}", method = POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity revertIeo(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(null);
    }



}
